package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
