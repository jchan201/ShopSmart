package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerManageProfileBinding;

import io.realm.RealmResults;

public class CustomerManageProfileActivity extends AppCompatActivity {
    private CustomerManageProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerManageProfileBinding.inflate(getLayoutInflater());
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
                if (user != null) binding.queryFullName.setText(user.getEmail());
            }
        });

        binding.accountButton.setOnClickListener(view ->
                startActivity(new Intent(CustomerManageProfileActivity.this, CustomerProfileActivity.class)));

        binding.passwordButton.setOnClickListener(view ->
                startActivity(new Intent(CustomerManageProfileActivity.this, CustomerPasswordActivity.class)));

        binding.paymentButton.setOnClickListener(view ->
                startActivity(new Intent(CustomerManageProfileActivity.this, CustomerPaymentsActivity.class)));

        /*binding.orderButtonManage.setOnClickListener(view ->
                startActivity(new Intent(CustomerManageProfileActivity.this, ???????????????????)));*/

        binding.cancelButtonManage.setOnClickListener(view ->
                startActivity(new Intent(CustomerManageProfileActivity.this, CustomerDashboardActivity.class)));
    }
}
