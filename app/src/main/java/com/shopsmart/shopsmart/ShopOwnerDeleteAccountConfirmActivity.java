package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDeleteAccountActivityBinding;

import io.realm.RealmResults;

public class ShopOwnerDeleteAccountConfirmActivity extends AppCompatActivity {
    private ShopownerDeleteAccountActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDeleteAccountActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnConfirm.setOnClickListener(view -> {
            if (validation()) {
                deleteAccount();
            }
        });
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerDeleteAccountConfirmActivity.this, ShopOwnerDashboardActivity.class)));
    }

    private void deleteAccount() {
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                    RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                    AppUser user = null;
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getEmail().equals(ShopSmartApp.email)) {
                            user = users.get(i);
                        }
                    }

                    if (user != null) {

                        //remove all orders
                        if (!user.getOrders().isEmpty()) {
                            user.getOrders().deleteAllFromRealm();
                            user.removeAllOrders();
                        }

                        //remove all products and shops
                        if (!user.getShops().isEmpty()) {
                            for (int i = 0; i < user.getShops().size(); i++) {
                                Shop deleteShop = transactionRealm.where(Shop.class).equalTo("_id", user.getShops().get(i)).findFirst();
                                deleteShop.getProducts().deleteAllFromRealm();
                                deleteShop.removeAllProduct();
                            }

                            user.getShops().deleteAllFromRealm();
                            user.removeAllShop();
                        }

                        //remove said user
                        user.deleteFromRealm();
                    }
                });
                ShopSmartApp.logout();
                Intent intentToProfile = new Intent(ShopOwnerDeleteAccountConfirmActivity.this, StartupActivity.class);
                intentToProfile.putExtra("EXTRA_DELETE_PAYMENT_SUCCESS", true);
                startActivity(intentToProfile);
                finish(); // prevent going back to this activity process
            }
        });
    }

    private boolean validation() {
        boolean valid = true;
        if (binding.editTextPassword.getText().toString().isEmpty()) {
            binding.editTextPassword.setError("Password cannot be empty");
            valid = false;
        }
        if (!binding.editTextPassword.getText().toString().equals(ShopSmartApp.password)) {
            binding.editTextPassword.setError("Incorrect password");
            valid = false;
        }
        return valid;
    }
}