package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileDeletePaymentConfirmationActivityBinding;

import io.realm.RealmResults;

public class ShopOwnerProfileDeletePaymentsConfirmActivity extends AppCompatActivity {
    private ShopownerProfileDeletePaymentConfirmationActivityBinding binding;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileDeletePaymentConfirmationActivityBinding.inflate(getLayoutInflater());
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
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(transactionRealm ->
                                user.removePaymentMethod(getIntent().getIntExtra("EXTRA_REMOVE_INDEX", 0)));
                        Intent intentToProfile = new Intent(ShopOwnerProfileDeletePaymentsConfirmActivity.this, ShopOwnerProfilePaymentsActivity.class);
                        intentToProfile.putExtra("EXTRA_DELETE_PAYMENT_SUCCESS", true);
                        startActivity(intentToProfile);
                    }
                }));
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileDeletePaymentsConfirmActivity.this, ShopOwnerProfilePaymentsActivity.class)));
    }
}