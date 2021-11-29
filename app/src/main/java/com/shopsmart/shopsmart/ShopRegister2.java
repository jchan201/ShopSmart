package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivity2Binding;

public class ShopRegister2 extends AppCompatActivity {
    private ShopRegisterActivity2Binding binding;
    private String shopName;
    private String shopDesc;
    private String shopEmail;
    private String shopPhone;
    private String shopWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        if (currIntent != null) {
            shopName = currIntent.getStringExtra("EXTRA_NAME");
            shopDesc = currIntent.getStringExtra("EXTRA_DESC");
            shopEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            shopPhone = currIntent.getStringExtra("EXTRA_PHONE");
            shopWebsite = currIntent.getStringExtra("EXTRA_WEBSITE");
        }
        binding.btnNext.setOnClickListener(view -> {
            if (validation()) {
                Address address = new Address(binding.textAddLine1.getText().toString(),
                        binding.textAddLine2.getText().toString(),
                        "Canada",
                        binding.spinnerProvince.getSelectedItem().toString(),
                        binding.textCity.getText().toString(),
                        binding.textZipCode.getText().toString());
                Intent nextSignUpScreen = new Intent(ShopRegister2.this, ShopRegister3.class);
                nextSignUpScreen.putExtra("EXTRA_ADDRESS_OBJ", address);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", shopEmail);
                nextSignUpScreen.putExtra("EXTRA_NAME", shopName);
                nextSignUpScreen.putExtra("EXTRA_DESC", shopDesc);
                nextSignUpScreen.putExtra("EXTRA_PHONE", shopPhone);
                nextSignUpScreen.putExtra("EXTRA_WEBSITE", shopWebsite);
                startActivity(nextSignUpScreen);
            }
        });
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopRegister2.this, ShopRegister.class)));
    }

    private boolean validation() {
        boolean valid = true;
        if (binding.textCity.getText().toString().isEmpty()) {
            binding.textCity.setError("City cannot be empty");
            valid = false;
        }
        if (binding.textZipCode.getText().toString().isEmpty()) {
            binding.textZipCode.setError("Postal Code cannot be empty");
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