package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivity2Binding;

public class ShopRegisterEdit2 extends AppCompatActivity {
    String userEmail;
    String userPass;
    String shopName;
    String shopDesc;
    String shopEmail;
    String shopPhone;
    String shopWebsite;
    private ShopRegisterActivity2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = this.getIntent();
        if (currIntent != null) {
            this.shopName = currIntent.getStringExtra("EXTRA_NAME");
            this.shopDesc = currIntent.getStringExtra("EXTRA_DESC");
            this.shopEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.shopPhone = currIntent.getStringExtra("EXTRA_PHONE");
            this.shopWebsite = currIntent.getStringExtra("EXTRA_WEBSITE");
            userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

        binding.btnNext.setOnClickListener(view -> {
            if (validation()) {
                //Create Address Object
                Address address = new Address(binding.textAddLine1.getText().toString(),
                        binding.textAddLine2.getText().toString(), "Canada",
                        binding.spinnerProvince.getSelectedItem().toString(),
                        binding.textCity.getText().toString(),
                        binding.textZipCode.getText().toString());

                Intent nextSignUpScreen = new Intent(ShopRegisterEdit2.this, ShopRegister3.class);
                nextSignUpScreen.putExtra("EXTRA_ADDRESS_OBJ", address);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", shopEmail);
                nextSignUpScreen.putExtra("EXTRA_NAME", shopName);
                nextSignUpScreen.putExtra("EXTRA_DESC", shopDesc);
                nextSignUpScreen.putExtra("EXTRA_PHONE", shopPhone);
                nextSignUpScreen.putExtra("EXTRA_WEBSITE", shopWebsite);
                nextSignUpScreen.putExtra("EXTRA_PASS", userPass);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(nextSignUpScreen);
            }
        });

        binding.btnBack.setOnClickListener(view -> startActivity(new Intent(ShopRegisterEdit2.this, ShopRegister.class)
                .putExtra("EXTRA_EMAIL", userPass)
                .putExtra("EXTRA_PASS", userPass)));
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.textCity.getText().toString().isEmpty()) {
            this.binding.textCity.setError("City cannot be empty");
            valid = false;
        }

        if (this.binding.textZipCode.getText().toString().isEmpty()) {
            this.binding.textZipCode.setError("Postal Code cannot be empty");
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