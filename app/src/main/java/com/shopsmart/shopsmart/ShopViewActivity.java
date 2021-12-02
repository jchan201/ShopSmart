package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.shopsmart.shopsmart.databinding.ActivityCustomerShoppingCartBinding;
import com.shopsmart.shopsmart.databinding.ShopViewActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ShopViewActivity extends AppCompatActivity {
    private ShopViewActivityBinding binding;
    private FragmentAdapter adapter;
    private TabLayout tabLayout;
    private boolean userType;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopViewActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        index = getIntent().getIntExtra("EXTRA_INDEX", index);
        userType = getIntent().getBooleanExtra("CUSTOMER", false);
        tabLayout = binding.tabLayout;
        ViewPager2 view2 = binding.viewPager;
        adapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), index);
        view2.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                view2.setCurrentItem(position);
                Bundle bundle = new Bundle();
                bundle.putInt("EXTRA_INDEX", index);
                if (position == 0) adapter.first.setArguments(bundle);
                else if (position == 1) adapter.second.setArguments(bundle);
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

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<Shop> allShops = ShopSmartApp.realm.where(Shop.class).findAll();
                ArrayList<Shop> shops;
                if (userType) { // customer
                    shops = new ArrayList<>(allShops);
                } else { // shop owner
                    RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                    AppUser user = null;
                    for (AppUser u : users) {
                        if (u.getEmail().equals(ShopSmartApp.email)) {
                            user = u;
                        }
                    }
                    List<ObjectId> shopIds = user.getShops();
                    shops = new ArrayList<>();
                    for (Shop s : allShops) {
                        for (ObjectId o : shopIds) {
                            if (s.getId().equals(o))
                                shops.add(s);
                        }
                    }
                }
                if (index >= 0)
                    displayShopInfo(shops.get(index));
            }
        });
    }

    private void displayShopInfo(Shop shop) {
        binding.queryShopName.setText(shop.getName());
        binding.queryShopDescription.setText(shop.getDesc());
        binding.queryShopPhone.setText(shop.getPhone());
        binding.queryShopWebsite.setText(shop.getWebsite());
        binding.queryShopEmail.setText(shop.getEmail());
        Bundle bundle = new Bundle();
        bundle.putInt("EXTRA_INDEX", index);
        adapter.first.setArguments(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userType) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuHome || id == R.id.menuPrev)
            startActivity(new Intent(ShopViewActivity.this, CustomerDashboardActivity.class));
        else if (id == R.id.Profile)
            startActivity(new Intent(ShopViewActivity.this, CustomerManageProfileActivity.class));
        else if (id == R.id.LogOut)
            startActivity(new Intent(ShopViewActivity.this, StartupActivity.class));
        else if (id == R.id.ShoppingCart)
            startActivity(new Intent(ShopViewActivity.this, ActivityCustomerShoppingCartBinding.class));
        return true;
    }
}