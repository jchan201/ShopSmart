package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shopsmart.shopsmart.databinding.CustomerDashboard1Binding;

import io.realm.RealmResults;

public class CustomerDashboardActivity extends AppCompatActivity {
    private CustomerDashboard1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerDashboard1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Fragment fragment = new MapsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (int x = 0; x < users.size(); x++) {
                    if (users.get(x).getEmail().equals(ShopSmartApp.email)) {
                        user = users.get(x);
                    }
                }
                if (user != null) binding.userName.setText(user.getEmail());

                RealmResults<Shop> allShops = ShopSmartApp.realm.where(Shop.class).findAll();
                int size = allShops.size();
                String[] shopsId2 = new String[size];
                String[] shopNames = new String[size];
                String[] shopAddressPCode = new String[size];

                StringBuilder shopString = new StringBuilder();
                for(int n = 0; n < size; n++){
                    shopsId2[n] = String.valueOf(allShops.get(n).getId());
                    shopNames[n] = allShops.get(n).getName();
                    shopAddressPCode[n] = allShops.get(n).getAddress().getPostalCode();

                    shopString.append("Name: ").append(allShops.get(n).getName()).append("\n");
                    shopString.append("Description: ").append(allShops.get(n).getDesc()).append("\n");
                    shopString.append("Phone: ").append(allShops.get(n).getPhone()).append("\n").append("\n");
                }
                TextView shopText = (TextView) findViewById(R.id.shopInfoText);
                shopText.setText(shopString.toString());

                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", ShopSmartApp.email);
                bundle.putString("USERPASS", ShopSmartApp.password);
                bundle.putInt("NUMSHOPS", size);
                bundle.putStringArray("SHOPID", shopsId2);
                bundle.putStringArray("SHOPNAMES", shopNames);
                bundle.putStringArray("SHOPPCODES", shopAddressPCode);
                bundle.putString("USERADDRESS", user.getAddress().getPostalCode());

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment newInstance = recreateFragment(fragment);
                ft.add(R.id.rlMaps, newInstance);
                newInstance.setArguments(bundle);
                ft.replace(R.id.rlMaps, newInstance).commit();
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
        int id = item.getItemId();
        if (id == R.id.Profile)
            startActivity(new Intent(CustomerDashboardActivity.this, CustomerManageProfileActivity.class));
        else if (id == R.id.LogOut)
            startActivity(new Intent(CustomerDashboardActivity.this, StartupActivity.class));
        return true;
    }

    @Override
    public void onInputMapSent(CharSequence input) {
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
    }

    private Fragment recreateFragment(Fragment f) {
        try {
            Fragment.SavedState savedState = getSupportFragmentManager().saveFragmentInstanceState(f);
            Fragment newInstance = f.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);
            return newInstance;
        }
        catch (Exception e) { // InstantiationException, IllegalAccessException
            throw new RuntimeException("Cannot reinstantiate fragment " + f.getClass().getName(), e);
        }
    }
}
