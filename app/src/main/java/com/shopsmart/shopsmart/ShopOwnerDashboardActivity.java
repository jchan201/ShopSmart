package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDashboardActivityBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerDashboardActivity extends AppCompatActivity {
    private ShopownerDashboardActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDashboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email)) {
                        user = users.get(i);
                    }
                }
                if (user != null) binding.textUsername.setText(user.getEmail());
            }
        });

        binding.btnDelete.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerDashboardActivity.this, ShopOwnerDeleteAccountConfirmActivity.class)));

        binding.btnShopList.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerDashboardActivity.this, ShopListActivity.class)));

        binding.btnProfile.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerDashboardActivity.this, ShopOwnerProfileActivity.class)));

        binding.btnLogout.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerDashboardActivity.this, StartupActivity.class)));
    }
}