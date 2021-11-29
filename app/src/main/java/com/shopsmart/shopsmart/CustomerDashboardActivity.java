package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shopsmart.shopsmart.databinding.CustomerDashboard1Binding;

import io.realm.RealmResults;

public class CustomerDashboardActivity extends AppCompatActivity implements MapsFragment.FragmentMapsListener {
    private CustomerDashboard1Binding binding;
    private RealmResults<Shop> allShops;
    private int numShops;
    private String[] shopIds;
    private String[] shopNames;
    private String[] shopAddressPCode;

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

                allShops = ShopSmartApp.realm.where(Shop.class).findAll();
                numShops = allShops.size();
                shopIds = new String[numShops];
                shopNames = new String[numShops];
                shopAddressPCode = new String[numShops];

                StringBuilder shopString = new StringBuilder();
                for (int n = 0; n < numShops; n++) {
                    shopIds[n] = String.valueOf(allShops.get(n).getId());
                    shopNames[n] = allShops.get(n).getName();
                    shopAddressPCode[n] = allShops.get(n).getAddress().getPostalCode();

                    shopString.append("Name: ").append(allShops.get(n).getName()).append("\n");
                    shopString.append("Description: ").append(allShops.get(n).getDesc()).append("\n");
                    shopString.append("Phone: ").append(allShops.get(n).getPhone()).append("\n").append("\n");
                }

                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", ShopSmartApp.email);
                bundle.putString("USERPASS", ShopSmartApp.password);
                bundle.putInt("NUMSHOPS", numShops);
                bundle.putStringArray("SHOPID", shopIds);
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
        else if (id == R.id.LogOut) {
            ShopSmartApp.logout();
            startActivity(new Intent(CustomerDashboardActivity.this, StartupActivity.class));
        }
        return true;
    }

    @Override
    public void onInputMapSent(CharSequence input) {
        ImageView imgView = findViewById(R.id.imageViewShop);
        boolean shopIdBool = false;

        int getInt = -1;
        for (int x = 0; x < numShops && !shopIdBool; x++) {
            if (input.toString().equals(shopIds[x])) {
                shopIdBool = true;
                getInt = x;
            }
        }

        TextView shopNameText = findViewById(R.id.shopNameView);
        TextView shopPhoneText = findViewById(R.id.shopPhoneView);
        TextView shopEmailText = findViewById(R.id.shopEmailView);
        TextView shopAddressText = findViewById(R.id.shopAddressView);
        if (shopIdBool) {
            shopNameText.setVisibility(View.VISIBLE);
            shopPhoneText.setVisibility(View.VISIBLE);
            shopEmailText.setVisibility(View.VISIBLE);
            shopAddressText.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.VISIBLE);

            shopNameText.setText("Name: " + allShops.get(getInt).getName());
            shopPhoneText.setText("Phone: " + allShops.get(getInt).getPhone());
            shopEmailText.setText("Email: " + allShops.get(getInt).getEmail());
            shopAddressText.setText("Address: " + allShops.get(getInt).getAddress().getAddress1() + " " + allShops.get(getInt).getAddress().getAddress2());
        } else {
            shopNameText.setVisibility(View.GONE);
            shopPhoneText.setVisibility(View.GONE);
            shopEmailText.setVisibility(View.GONE);
            shopAddressText.setVisibility(View.GONE);
            imgView.setVisibility(View.GONE);

        }
    }

    @Override
    public void onViewMapSent(int idx) {
        Intent intent = new Intent(CustomerDashboardActivity.this, ShopViewActivity.class);
        intent.putExtra("EXTRA_INDEX", idx);
        startActivity(intent);
    }

    private Fragment recreateFragment(Fragment f) {
        try {
            Fragment.SavedState savedState = getSupportFragmentManager().saveFragmentInstanceState(f);
            Fragment newInstance = f.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);
            return newInstance;
        } catch (Exception e) { // InstantiationException, IllegalAccessException
            throw new RuntimeException("Cannot reinstantiate fragment " + f.getClass().getName(), e);
        }
    }
}
