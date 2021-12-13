package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileUpdatePaymentActivity2Binding;

import io.realm.RealmResults;

public class ShopOwnerProfileUpdatePaymentsActivity2 extends AppCompatActivity {
    private ShopownerProfileUpdatePaymentActivity2Binding binding;
    private PaymentMethod paymentMethod;
    private Address billingAddress;
    private int updateIndex;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileUpdatePaymentActivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        paymentMethod = (PaymentMethod) currIntent.getSerializableExtra("EXTRA_PAYMENT_METHOD_OBJ");
        billingAddress = (Address) currIntent.getSerializableExtra("EXTRA_BILLING_ADDRESS_OBJ");
        updateIndex = currIntent.getIntExtra("EXTRA_UPDATE_INDEX", 0);

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email))
                        user = users.get(i);
                }
                int index = 0;
                String[] provinces = getResources().getStringArray(R.array.provinces);
                for (int i = 0; i < provinces.length; i++) {
                    if (provinces[i].equals(billingAddress.getProvince())) {
                        index = i;
                        break;
                    }
                }
                binding.textCity.setText(billingAddress.getCity());
                binding.spinnerProvince.setSelection(index);
                binding.textZipCode.setText(billingAddress.getPostalCode());
                binding.textAddLine1.setText(billingAddress.getAddress1());
                binding.textAddLine2.setText(billingAddress.getAddress2());
            }
        });

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                billingAddress.setCity(binding.textCity.getText().toString());
                billingAddress.setProvince(binding.spinnerProvince.getSelectedItem().toString());
                billingAddress.setPostalCode(binding.textZipCode.getText().toString());
                billingAddress.setAddress1(binding.textAddLine1.getText().toString());
                billingAddress.setAddress2(binding.textAddLine2.getText().toString());
                billingAddress.setCountry("Canada");
                paymentMethod.setBillingAddress(billingAddress);
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(transactionRealm ->
                                user.updatePaymentMethod(paymentMethod, updateIndex));
                        Intent intentToProfile = new Intent(ShopOwnerProfileUpdatePaymentsActivity2.this, ShopOwnerProfilePaymentsActivity.class);
                        intentToProfile.putExtra("EXTRA_UPDATE_PAYMENT_SUCCESS", true);
                        startActivity(intentToProfile);
                    }
                });
            }
        });
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileUpdatePaymentsActivity2.this, ShopOwnerProfileUpdatePaymentsActivity1.class)));
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