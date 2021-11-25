package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.shopsmart.shopsmart.databinding.CustomerDashboard1Binding;

import org.bson.types.ObjectId;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;


public class CustomerDashboardActivity extends AppCompatActivity implements MapsFragment.FragmentMapsListener {
    private final String PARTITION = "ShopSmart";
    Intent currentIntent;
    String userEmail;
    String userPass;
    String userAddress;
    String shopAddress;
    AppUser user;
    ArrayList<Shop> shops;
    Fragment fragment;

    TextView shopNameText;
    TextView shopPhoneText;
    TextView shopEmailText;
    TextView shopAddressText;

    String[] shopsId2;
    String[] shopNames;

    String[] shopAddressPCode;

    int numShops;

    private CustomerDashboard1Binding binding;
    private App app;
    private Realm realm;

    @Override
    public void onInputMapSent(CharSequence input) {
//        String shopId;
        boolean shopIdBool = false;
        ImageView imgView = (ImageView) findViewById(R.id.imageViewShop);

        int getInt = -1;
        Log.i("HELLO INPUT", input.toString());
        for(int x = 0; x < numShops && !shopIdBool; x++){
            if(input.toString().equals(shopsId2[x])){
//                shopId = input.toString();
                shopIdBool = true;
                getInt = x;
            }
        }

        if(shopIdBool){
            shopNameText = (TextView) findViewById(R.id.shopNameView);
            shopPhoneText = (TextView) findViewById(R.id.shopPhoneView);
            shopEmailText = (TextView) findViewById(R.id.shopEmailView);
            shopAddressText = (TextView) findViewById(R.id.shopAddressView);

            shopNameText.setVisibility(View.VISIBLE);
            shopPhoneText.setVisibility(View.VISIBLE);
            shopEmailText.setVisibility(View.VISIBLE);
            shopAddressText.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.VISIBLE);

            shopNameText.setText("Name: " + shops.get(getInt).getName());
            shopPhoneText.setText("Phone: " + shops.get(getInt).getPhone());
            shopEmailText.setText("Email: " + shops.get(getInt).getEmail());
            shopAddressText.setText("Address: " + shops.get(getInt).getAddress().getAddress1() + " " + shops.get(getInt).getAddress().getAddress2());
        }

        else{
            shopNameText.setVisibility(View.GONE);
            shopPhoneText.setVisibility(View.GONE);
            shopEmailText.setVisibility(View.GONE);
            shopAddressText.setVisibility(View.GONE);
            imgView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onViewMapSent(int idx){
        realm.close();
        Intent intentShopView = new Intent(CustomerDashboardActivity.this, ShopViewActivity.class);
        intentShopView.putExtra("EXTRA_EMAIL", userEmail);
        intentShopView.putExtra("EXTRA_PASS", userPass);
        intentShopView.putExtra("EXTRA_INDEX", idx);
        intentShopView.putExtra("EXTRA_USER_CUSTOMER", true);
        startActivity(intentShopView);
        finish();
//        Intent settingsIntent = new Intent(CustomerDashboardActivity.this, CustomerManageProfileActivity.class);
//        settingsIntent.putExtra("EXTRA_EMAIL", userEmail);
//        settingsIntent.putExtra("EXTRA_PASS", userPass);
//        Toast.makeText(CustomerDashboardActivity.this, "Profile", Toast.LENGTH_SHORT).show();
//        startActivity(settingsIntent);
//        finish();
//        break;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerDashboard1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragment = new MapsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        this.currentIntent = this.getIntent();

        if (this.currentIntent != null) {
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                realm = Realm.getInstance(config);

                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                for (int x = 0; x < users.size(); x++) {
                    if (users.get(x).getEmail().equals(userEmail)) {
                        user = users.get(x);
                    }
                }
                binding.userName.setText(user.getEmail());

                RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
                shops = new ArrayList<>();
                RealmList<String> shopsId;

                String shopString = "";

                int x = 0;
                for(Shop s : allShops){
                    shops.add(s);
//                    shopsId.get(x) = shops.get(x++).getId();
                    x++;
                }

                shopsId2 = new String[x];
                shopNames = new String[x];

                shopAddressPCode = new String[x];
                numShops = x;

                for(int n = 0; n < x; n++){
                    shopsId2[n] = String.valueOf(shops.get(n).getId());
                    shopNames[n] = shops.get(n).getName();
                    shopAddressPCode[n] = shops.get(n).getAddress().getPostalCode();
                    shopAddress = shops.get(n).getAddress().getAddress1();
                    shopAddress = shopAddress + " " + shops.get(n).getAddress().getAddress2();

                    shopString = shopString + "Name: " + shops.get(n).getName() + "\n";
                    shopString = shopString + "Description: " + shops.get(n).getDesc() + "\n";
                    shopString = shopString + "Phone: " + shops.get(n).getPhone() +  "\n" + "\n";

                }

                shopNameText = (TextView) findViewById(R.id.shopNameView);
                shopPhoneText = (TextView) findViewById(R.id.shopPhoneView);
                shopEmailText = (TextView) findViewById(R.id.shopEmailView);
                shopAddressText = (TextView) findViewById(R.id.shopAddressView);

//                shopText.setText(shopString);

//                shopNameText.setText("Shop Name: ");
//                shopPhoneText.setText(" ");
//                shopEmailText.setText(" ");
//                shopAddressText.setText(" ");

                shopNameText.setVisibility(View.GONE);
                shopPhoneText.setVisibility(View.GONE);
                shopEmailText.setVisibility(View.GONE);
                shopAddressText.setVisibility(View.GONE);

                userAddress = user.getAddress().getPostalCode();

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

//                ft.remove(fragment);
//              TEST
                Fragment newInstance = recreateFragment(fragment);
                ft.add(R.id.rlMaps, newInstance);

                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", userEmail);
                bundle.putString("USERPASS", userPass);
                bundle.putInt("NUMSHOPS", numShops);
                bundle.putStringArray("SHOPID", shopsId2);
                bundle.putStringArray("SHOPNAMES", shopNames);
                bundle.putStringArray("SHOPPCODES", shopAddressPCode);
                bundle.putString("USERADDRESS", userAddress);

//                bundle.putInt("EXTRA_NUMSHOPS", numShops);
//                bundle.putStringArray("EXTRA_SHOPID", shopsId2);
//                bundle.putStringArray("EXTRA_SHOPNAMES", shopNames);
//                bundle.putStringArray("EXTRA_SHOPADDRESSPCODES", shopAddressPCode);

                newInstance.setArguments(bundle);

                ft.replace(R.id.rlMaps, newInstance).commit();



//                RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
//                shopIds = user.getShops();
//                shops = new ArrayList<>();
//                for (Shop s : allShops) {
//                    for (ObjectId o : shopIds) {
//                        if (s.getId().equals(o))
//                            shops.add(s);
//                    }
//                }

            }
        });
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
            case R.id.Profile:
                realm.close();
                Intent settingsIntent = new Intent(CustomerDashboardActivity.this, CustomerManageProfileActivity.class);
                settingsIntent.putExtra("EXTRA_EMAIL", userEmail);
                settingsIntent.putExtra("EXTRA_PASS", userPass);
                Toast.makeText(CustomerDashboardActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                startActivity(settingsIntent);
                finish();
                break;
            case R.id.LogOut:
                realm.close();
                Intent dashboardIntent = new Intent(CustomerDashboardActivity.this, StartupActivity.class);
                startActivity(dashboardIntent);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("LOGOUT", "Successfully logged out.");
            } else {
                Log.e("LOGOUT", "Failed to log out, error: " + result.getError());
            }
        });
    }

    private Fragment recreateFragment(Fragment f)
    {
        try {
            Fragment.SavedState savedState = getSupportFragmentManager().saveFragmentInstanceState(f);

            Fragment newInstance = f.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);

            return newInstance;
        }
        catch (Exception e) // InstantiationException, IllegalAccessException
        {
            throw new RuntimeException("Cannot reinstantiate fragment " + f.getClass().getName(), e);
        }
    }
}
