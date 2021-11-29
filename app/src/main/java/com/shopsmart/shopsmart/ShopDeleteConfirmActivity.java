package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopDeleteConfirmationActivityBinding;

import io.realm.RealmResults;

public class ShopDeleteConfirmActivity extends AppCompatActivity {
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopDeleteConfirmationActivityBinding binding =
                ShopDeleteConfirmationActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email))
                        user = users.get(i);
                }
            }
        });
        binding.btnConfirm.setOnClickListener(view ->
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                            int index = getIntent().getIntExtra("EXTRA_REMOVE_INDEX", 0);
                            Shop deleteShop = transactionRealm.where(Shop.class).equalTo("_id", user.getShops().get(index)).findFirst();
                            deleteShop.deleteFromRealm();
                            user.removeShop(index);
                        });
                        Intent intentToProfile = new Intent(ShopDeleteConfirmActivity.this, ShopListActivity.class);
                        intentToProfile.putExtra("EXTRA_DELETE_SHOP_SUCCESS", true);
                        startActivity(intentToProfile);
                    }
                }));
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopDeleteConfirmActivity.this, ShopListActivity.class)));
    }
}