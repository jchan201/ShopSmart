package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileAddPaymentActivity2Binding;

import io.realm.RealmResults;

public class ShopOwnerProfileAddPaymentsActivity2 extends AppCompatActivity {
    private ShopownerProfileAddPaymentActivity2Binding binding;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileAddPaymentActivity2Binding.inflate(getLayoutInflater());
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

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                Address billingAddress = new Address();
                billingAddress.setCity(binding.textCity.getText().toString());
                billingAddress.setProvince(binding.spinnerProvince.getSelectedItem().toString());
                billingAddress.setPostalCode(binding.textZipCode.getText().toString());
                billingAddress.setAddress1(binding.textAddLine1.getText().toString());
                billingAddress.setAddress2(binding.textAddLine2.getText().toString());
                billingAddress.setCountry("Canada");


                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                            PaymentMethod paymentMethod = (PaymentMethod) getIntent().getSerializableExtra("EXTRA_PAYMENT_METHOD_OBJ");
                            paymentMethod.setBillingAddress(billingAddress);
                            user.addPaymentMethod(paymentMethod);
                        });
                        startActivity(new Intent(ShopOwnerProfileAddPaymentsActivity2.this, ShopOwnerProfilePaymentsActivity.class)
                                .putExtra("EXTRA_ADD_PAYMENT_SUCCESS", true));
                    }
                });
            }
        });
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileAddPaymentsActivity2.this, ShopOwnerProfileAddPaymentsActivity1.class)));
    }

    private boolean validation() {
        boolean valid = true;
        if (binding.textCity.getText().toString().isEmpty()) {
            binding.textCity.setError("City cannot be empty");
            valid = false;
        }
        if (binding.textZipCode.getText().toString().isEmpty()) {
            binding.textZipCode.setError("Postal code cannot be empty");
            valid = false;
        }
        if (!binding.textZipCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            binding.textZipCode.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }
        if (binding.textAddLine1.getText().toString().isEmpty()) {
            binding.textAddLine1.setError("Address Line 1 cannot be empty");
            valid = false;
        }
        return valid;
    }
}