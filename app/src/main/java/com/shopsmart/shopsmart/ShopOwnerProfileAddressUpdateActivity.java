package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileUpdateAddressActivityBinding;

import io.realm.RealmResults;

public class ShopOwnerProfileAddressUpdateActivity extends AppCompatActivity {
    private ShopownerProfileUpdateAddressActivityBinding binding;
    private Address newAddress;
    private Address address;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileUpdateAddressActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email)) {
                        user = users.get(i);
                    }
                }
                String[] provinces = getResources().getStringArray(R.array.provinces);
                address = user.getAddress();
                int index = 0;
                for (int i = 0; i < provinces.length; i++) {
                    if (provinces[i].equals(address.getProvince())) {
                        index = i;
                        break;
                    }
                }
                binding.textCity.setText(address.getCity());
                binding.spinnerProvince.setSelection(index);
                binding.textZipCode.setText(address.getPostalCode());
                binding.textAddLine1.setText(address.getAddress1());
                binding.textAddLine2.setText(address.getAddress2());
            }
        });
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileAddressUpdateActivity.this, ShopOwnerProfileAddressActivity.class))
        );
        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                newAddress = new Address();
                newAddress.setCity(binding.textCity.getText().toString());
                newAddress.setProvince(binding.spinnerProvince.getSelectedItem().toString());
                newAddress.setPostalCode(binding.textZipCode.getText().toString());
                newAddress.setAddress1(binding.textAddLine1.getText().toString());
                newAddress.setAddress2(binding.textAddLine2.getText().toString());
                newAddress.setCountry(address.getCountry());
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(transactionRealm ->
                                user.addOrUpdateAddress(newAddress));
                        startActivity(new Intent(ShopOwnerProfileAddressUpdateActivity.this, ShopOwnerProfileAddressActivity.class));
                    }
                });
            }
        });
    }

    private boolean validation() {
        boolean valid = true;
        if (this.binding.textCity.getText().toString().isEmpty()) {
            this.binding.textCity.setError("City cannot be empty");
            valid = false;
        }
        if (this.binding.textZipCode.getText().toString().isEmpty()) {
            this.binding.textZipCode.setError("Postal code cannot be empty");
            valid = false;
        }
        if (!this.binding.textZipCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            this.binding.textZipCode.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }
        if (this.binding.textAddLine1.getText().toString().isEmpty()) {
            this.binding.textAddLine1.setError("Address Line 1 cannot be empty");
            valid = false;
        }
        return valid;
    }
}