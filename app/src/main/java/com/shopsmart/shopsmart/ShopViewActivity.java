package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.shopsmart.shopsmart.databinding.ShopViewActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopViewActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    List<ObjectId> shopIds;
    ArrayList<Shop> shops;
    Shop shop;
    int index = 0;
    int total = 0;
    TabLayout tabLayout;
    ViewPager2 view2;
    FragmentAdapter adapter;
    private ShopViewActivityBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopViewActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());
        this.currIntent = this.getIntent();
        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.index = currIntent.getIntExtra("EXTRA_INDEX", index);
        }

        FragmentManager fm = getSupportFragmentManager();
        tabLayout = binding.tabLayout;
        view2 = binding.viewPager;
        adapter = new FragmentAdapter(fm, getLifecycle(), userEmail, userPass, index);
        view2.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                view2.setCurrentItem(position);
                if (position == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("EXTRA_USER", userEmail);
                    bundle.putString("EXTRA_PASS", userPass);
                    bundle.putInt("EXTRA_INDEX", index);
                    adapter.first.setArguments(bundle);
                } else if (position == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putString("EXTRA_USER", userEmail);
                    bundle.putString("EXTRA_PASS", userPass);
                    bundle.putInt("EXTRA_INDEX", index);
                    adapter.second.setArguments(bundle);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        view2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.index = currIntent.getIntExtra("EXTRA_INDEX", index);
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                realm = Realm.getInstance(config);

                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
                for (AppUser u : users) {
                    if (u.getEmail().equals(userEmail)) {
                        user = u;
                    }
                }
                RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
                shopIds = user.getShops();
                shops = new ArrayList<>();
                for (Shop s : allShops) {
                    for (ObjectId o : shopIds) {
                        if (s.getId().equals(o))
                            shops.add(s);
                    }
                }

                total = shops.size();

                if (index >= 0) {
                    shop = shops.get(index);
                    displayShopInfo();
                }
                realm.close();
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });
    }

    private void displayShopInfo() {
        binding.queryShopName.setText(shop.getName());
        binding.queryShopDescription.setText(shop.getDesc());
        binding.queryShopPhone.setText(shop.getPhone());
        binding.queryShopWebsite.setText(shop.getWebsite());
        binding.queryShopEmail.setText(shop.getEmail());
        Bundle bundle = new Bundle();
        bundle.putString("EXTRA_USER", userEmail);
        bundle.putString("EXTRA_PASS", userPass);
        bundle.putInt("EXTRA_INDEX", index);
        adapter.first.setArguments(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHome:
                realm.close();
                Intent listIntent = new Intent(ShopViewActivity.this, ShopListActivity.class);
                listIntent.putExtra("EXTRA_EMAIL", userEmail);
                listIntent.putExtra("EXTRA_PASS", userPass);
                startActivity(listIntent);
                break;

            case R.id.menuPrev:
                realm.close();
                Intent prevIntent = new Intent(ShopViewActivity.this, ShopListActivity.class);
                prevIntent.putExtra("EXTRA_PASS", userPass);
                prevIntent.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(prevIntent);
                break;
//            case R.id.Profile:
//                realm.close();
//                Intent settingsIntent = new Intent(CustomerDashboardActivity.this, CustomerManageProfileActivity.class);
//                settingsIntent.putExtra("EXTRA_EMAIL", userEmail);
//                settingsIntent.putExtra("EXTRA_PASS", userPass);
//                Toast.makeText(CustomerDashboardActivity.this, "Profile", Toast.LENGTH_SHORT).show();
//                startActivity(settingsIntent);
//                finish();
//                break;
            case R.id.LogOut:
                realm.close();
                Intent dashboardIntent = new Intent(ShopViewActivity.this, StartupActivity.class);
                startActivity(dashboardIntent);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        // Log out.
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("LOGOUT", "Successfully logged out.");
            } else {
                Log.e("LOGOUT", "Failed to log out, error: " + result.getError());
            }
        });
    }
}