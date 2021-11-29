package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerSignupActivity2Binding;

public class ShopOwnerSignupActivity2 extends AppCompatActivity {
    private ShopownerSignupActivity2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerSignupActivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent currIntent = getIntent();

        binding.buttonCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerSignupActivity2.this, ShopOwnerSignupActivity.class)));
        binding.buttonNext.setOnClickListener(view -> {
            if (validation()) {
                Address address = new Address(binding.edtTextAdd1.getText().toString(),
                        binding.edtTextAdd2.getText().toString(), "Canada",
                        binding.spinnerProvince.getSelectedItem().toString(),
                        binding.edtTextCity.getText().toString(),
                        binding.edtTextZip.getText().toString());
                Intent nextSignUpScreen = new Intent(ShopOwnerSignupActivity2.this, ShopOwnerSignupActivity3.class);
                nextSignUpScreen.putExtra("EXTRA_ADDRESS_OBJ", address);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", currIntent.getStringExtra("EXTRA_EMAIL"));
                nextSignUpScreen.putExtra("EXTRA_PASSWORD", currIntent.getStringExtra("EXTRA_PASSWORD"));
                nextSignUpScreen.putExtra("EXTRA_FNAME", currIntent.getStringExtra("EXTRA_FNAME"));
                nextSignUpScreen.putExtra("EXTRA_MNAME", currIntent.getStringExtra("EXTRA_MNAME"));
                nextSignUpScreen.putExtra("EXTRA_LNAME", currIntent.getStringExtra("EXTRA_LNAME"));
                nextSignUpScreen.putExtra("EXTRA_DOB", currIntent.getStringExtra("EXTRA_DOB"));
                nextSignUpScreen.putExtra("EXTRA_PHONE", binding.edtTextPhoneNum.getText().toString());
                startActivity(nextSignUpScreen);
            }
        });
    }

    private boolean validation() {
        boolean valid = true;
        if (binding.edtTextCity.getText().toString().isEmpty()) {
            binding.edtTextCity.setError("City cannot be empty");
            valid = false;
        }
        if (binding.edtTextZip.getText().toString().isEmpty()) {
            binding.edtTextZip.setError("Postal Code cannot be empty");
            valid = false;
        }
        if (!binding.edtTextZip.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            binding.edtTextZip.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }
        if (binding.edtTextAdd1.getText().toString().isEmpty()) {
            binding.edtTextAdd1.setError("Address Line 1 cannot be empty");
            valid = false;
        }
        if (binding.edtTextPhoneNum.getText().toString().isEmpty()) {
            binding.edtTextPhoneNum.setError("Phone cannot be empty");
            valid = false;
        }
        return valid;
    }
}