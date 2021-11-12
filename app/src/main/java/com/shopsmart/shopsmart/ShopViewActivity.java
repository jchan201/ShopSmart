package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.shopsmart.shopsmart.databinding.ShopViewActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
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
    Address address;
    int index = 0;
    int total = 0;
    private ShopViewActivityBinding binding;
    private App app;
    private Realm realm;

    TabLayout tabLayout;
    ViewPager2 view2;
    FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopViewActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tabLayout = findViewById(R.id.tab_layout);
        view2 = findViewById(R.id.viewPager);

        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        view2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                view2.setCurrentItem(position);
                if (position == 0) {
                    Address address = shop.getAddress();
                    String address1 = address.getAddress1();
                    String address2 = address.getAddress2();
                    String pCode = address.getPostalCode();

                    RealmList<String> sTimes = shop.getStartTimes();
                    RealmList<String> cTimes = shop.getEndTimes();
                    RealmList<String> sRegexTimes = shop.getStartTimes();
                    RealmList<String> cRegexTimes = shop.getEndTimes();

                    String regex = "^[c]$";

                    String[] daysOpen = new String[7];
                    String[] daysClosed = new String[7];

                    for(int x = 0; x < 7; x++){
                        if(!sRegexTimes.get(x).matches(regex)){
                            daysOpen[x] = sTimes.get(x);
                        }
                        else{
                            daysOpen[x] = sTimes.get(x);
                            daysOpen[x] = "CLOSED";
                        }
                    }

                    for(int x = 0; x < 7; x++){
                        if(!cRegexTimes.get(x).matches(regex)){
                            daysClosed[x] = " - " + cTimes.get(x);
                        }
                        else{
                            daysClosed[x] = cTimes.get(x);
                            daysClosed[x] = " ";
                        }
                    }

                    FragmentTransaction ft = fm.beginTransaction();

                    Bundle bundle = new Bundle();
                    bundle.putString("SHOP_ADDRESS1", address1);
                    bundle.putString("SHOP_ADDRESS2", address2);
                    bundle.putString("PCODE", pCode);

                    bundle.putStringArray("DAYSOPEN", daysOpen);
                    bundle.putStringArray("DAYSCLOSED", daysClosed);

                    bundle.putString("EXTRA_USER", userEmail);
                    bundle.putString("EXTRA_PASS", userPass);

                    FirstFragment fragment = new FirstFragment();
                    fragment.setArguments(bundle);

                    ft.replace(R.id.flFirstFragment, fragment).commit();
                }
                else if (position == 1) {
                    Credentials credentials = Credentials.emailPassword(userEmail, userPass);
                    app.loginAsync(credentials, result -> {
                        if (result.isSuccess()) {
                            Log.v("LOGIN", "Successfully authenticated using email and password.");

                            SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                            realm = Realm.getInstance(config);

                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();

                            Bundle bundle = new Bundle();
                            bundle.putString("EXTRA_USER", userEmail);
                            bundle.putString("EXTRA_PASS", userPass);

                            //TODO: Need to work on products query and send it to second fragment
                            /*RealmList<ObjectId> productIds = shop.getProducts();
                            RealmResults<Product> allProducts = realm.where(Product.class).findAll();
                            ArrayList<Product> products = new ArrayList<>();
                            for (ObjectId id : productIds) {
                                for (Product p : allProducts) {
                                    if (p.getId().equals(id)) {
                                        products.add(p);
                                        break;
                                    }
                                }
                            }*/
                            //bundle.putParcelableArrayList("PRODUCT_IDS", productIds);

                            SecondFragment fragment2 = new SecondFragment();
                            fragment2.setArguments(bundle);
                            ft.replace(R.id.flSecondFragment, fragment2).commit();
                        } else {
                            Log.v("LOGIN", "Failed to authenticate using email and password.");
                        }
                    });
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

                if(index >= 0){
                    shop = shops.get(index);
                    displayShopInfo();
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });
    }

    private void displayShopInfo(){
        binding.queryShopName.setText(shop.getName());
        binding.queryShopDescription.setText(shop.getDesc());
        binding.queryShopPhone.setText(shop.getPhone());
        binding.queryShopWebsite.setText(shop.getWebsite());
        binding.queryShopEmail.setText(shop.getEmail());

        Address address = shop.getAddress();
        String address1 = address.getAddress1();
        String address2 = address.getAddress2();
        String pCode = address.getPostalCode();

        RealmList<String> sTimes = shop.getStartTimes();
        RealmList<String> cTimes = shop.getEndTimes();
        RealmList<String> sRegexTimes = shop.getStartTimes();
        RealmList<String> cRegexTimes = shop.getEndTimes();

        String regex = "^[c]$";

        String[] daysOpen = new String[7];
        String[] daysClosed = new String[7];

        for(int x = 0; x < 7; x++){
            if(!sRegexTimes.get(x).matches(regex)){
                daysOpen[x] = sTimes.get(x);
            }
            else{
                daysOpen[x] = sTimes.get(x);
                daysOpen[x] = "CLOSED";
            }
        }

        for(int x = 0; x < 7; x++){
            if(!cRegexTimes.get(x).matches(regex)){
                daysClosed[x] = " - " + cTimes.get(x);
            }
            else{
                daysClosed[x] = cTimes.get(x);
                daysClosed[x] = " ";
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("SHOP_ADDRESS1", address1);
        bundle.putString("SHOP_ADDRESS2", address2);
        bundle.putString("PCODE", pCode);

        bundle.putStringArray("DAYSOPEN", daysOpen);
        bundle.putStringArray("DAYSCLOSED", daysClosed);

        bundle.putString("EXTRA_USER", userEmail);
        bundle.putString("EXTRA_PASS", userPass);

        FirstFragment fragment = new FirstFragment();
        fragment.setArguments(bundle);

        ft.replace(R.id.flFirstFragment, fragment).commit();

//        String MondayOpen = sTimes.get(0);
//        String TuesdayOpen = sTimes.get(1);
//        String WednesdayOpen = sTimes.get(2);
//        String ThursdayOpen = sTimes.get(3);
//        String FridayOpen = sTimes.get(4);
//        String SaturdayOpen = sTimes.get(5);
//        String SundayOpen = sTimes.get(6);
//
//        RealmList<String> cTimes = shop.getEndTimes();
//        String MondayC = cTimes.get(0);
//        String TuesdayC = cTimes.get(1);
//        String WednesdayC = cTimes.get(2);
//        String ThursdayC = cTimes.get(3);
//        String FridayC = cTimes.get(4);
//        String SaturdayC = cTimes.get(5);
//        String SundayC = cTimes.get(6);

//        for(int x = 0; x < 7; x++){
//            bundle.putString("DAYSOPEN" + x, daysOpen[x]);
//            bundle.putString("DAYSCLOSED" + x, daysClosed[x]);
//        }

//        bundle.putString("MONDAYOPEN", MondayOpen);
//        bundle.putString("TUESDAYOPEN", TuesdayOpen);
//        bundle.putString("WEDNESDAYOPEN", WednesdayOpen);
//        bundle.putString("THURSDAYOPEN", ThursdayOpen);
//        bundle.putString("FRIDAYOPEN", FridayOpen);
//        bundle.putString("SATURDAYOPEN", SaturdayOpen);
//        bundle.putString("SUNDAYOPEN", SundayOpen);
//
//        bundle.putString("MONDAYC", MondayC);
//        bundle.putString("TUESDAYC", TuesdayC);
//        bundle.putString("WEDNESDAYC", WednesdayC);
//        bundle.putString("THURSDAYC", ThursdayC);
//        bundle.putString("FRIDAYOC", FridayC);
//        bundle.putString("SATURDAYC", SaturdayC);
//        bundle.putString("SUNDAYC", SundayC);

//        Fragment f = SecondFragment.newInstance(address1, address2, pCode);
//
//        FragmentTransaction ft = getFragmentManager().beginTransaction();//.replace(R.id.tab_layout,new SecondFragment());
//        ft.commit();

//        address1.setText(address.getAddress1());
        //View tab1 = View.inflate(getBaseContext(), R.layout.)
        //TextView address1 = (TextView) vi

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
/*
        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.index = currIntent.getIntExtra("EXTRA_INDEX", index);

//            boolean addSuccess = currIntent.getBooleanExtra("EXTRA_REGISTER_SHOP_SUCCESS", false);
//            if (addSuccess)
//                Toast.makeText(ShopListActivity.this, "Successfully register new shop to your name.", Toast.LENGTH_SHORT).show();
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
                binding.textShopTotal.setText(Integer.toString(total));
                if (total == 0) {
                    binding.textShopIndex.setText(Integer.toString(index));
                } else {
                    binding.textShopIndex.setText(Integer.toString(index + 1));
                }

                if (index == 0 && total == 0) {
                    binding.singleShopView.setVisibility(View.GONE);
                    binding.textShopName.setVisibility(View.GONE);
                    binding.btnView.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                } else {
                    binding.singleShopView.setVisibility(View.VISIBLE);
                    binding.textShopName.setVisibility(View.VISIBLE);
                    binding.btnView.setVisibility(View.VISIBLE);
                    binding.buttonPrev.setVisibility(View.VISIBLE);
                    binding.buttonNext.setVisibility(View.VISIBLE);
                    if (index + 1 == total)
                        binding.buttonNext.setVisibility(View.GONE);
                    if (index == 0) {
                        binding.buttonPrev.setVisibility(View.GONE);
                    }
                    displayCardInfo(shops.get(index));
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.buttonPrev.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                binding.buttonNext.setVisibility(View.VISIBLE);
                binding.textShopIndex.setText(Integer.toString(index + 1));
                displayCardInfo(shops.get(index));

                if (index == 0) {
                    binding.buttonPrev.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.buttonNext.setOnClickListener(view -> {
            if (index < total) {
                index += 1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.textShopIndex.setText(Integer.toString(index + 1));
                displayCardInfo(shops.get(index));

                if (index + 1 == total) {
                    binding.buttonNext.setVisibility(View.INVISIBLE);
                }
            }
        });

//        binding.btnView.setOnClickListener(view -> {
//            realm.close();
//            Intent intentToProfile = new Intent(ShopListActivity.this, ShopOwnerProfileDeletePaymentsConfirmActivity.class);
//            intentToProfile.putExtra("EXTRA_PASS", userPass);
//            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
//            intentToProfile.putExtra("EXTRA_REMOVE_INDEX", index);
//            startActivity(intentToProfile);
//        });

        binding.btnView.setOnClickListener(view -> {
            realm.close();
//            Intent intentToView = new Intent(ShopListActivity.this,)
        });

        binding.btnAdd.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(ShopViewActivity.this, ShopRegister.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
        });

        binding.btnBack.setOnClickListener(view -> {
            realm.close();
            Intent intentToBack = new Intent(ShopViewActivity.this, ShopOwnerDashboardActivity.class);
            intentToBack.putExtra("EXTRA_PASS", userPass);
            intentToBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToBack);
        });
    }

    private void displayCardInfo(Shop shop) {
        binding.textShopName.setText(shop.getName());
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
*/