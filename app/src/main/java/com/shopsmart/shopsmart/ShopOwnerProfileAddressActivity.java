package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileAddressActivityBinding;

import io.realm.RealmResults;

public class ShopOwnerProfileAddressActivity extends AppCompatActivity {
    private ShopownerProfileAddressActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileAddressActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email))
                        user = users.get(i);
                }
                Address address = user.getAddress();
                binding.textCity.setText(address.getCity());
                binding.textProvince.setText(address.getProvince());
                binding.textZipCode.setText(address.getPostalCode());
                binding.textAddLine1.setText(address.getAddress1());
                binding.textAddLine2.setText(address.getAddress2());
                binding.textCountry.setText(address.getCountry());
            }
        });
        if (getIntent().getBooleanExtra("EXTRA_UPDATE_ADDRESS_SUCCESS", false))
            Toast.makeText(ShopOwnerProfileAddressActivity.this, "Successfully update address.", Toast.LENGTH_SHORT).show();
        binding.btnUpdate.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileAddressActivity.this, ShopOwnerProfileAddressUpdateActivity.class)));
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileAddressActivity.this, ShopOwnerProfileActivity.class)));
    }
}