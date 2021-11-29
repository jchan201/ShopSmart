package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivityBinding;

public class ShopRegister extends AppCompatActivity {
    private ShopRegisterActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopRegister.this, ShopListActivity.class)));

        binding.btnNext.setOnClickListener(view -> {
            if (validation()) {
                Intent nextSignUpScreen = new Intent(ShopRegister.this, ShopRegister2.class);
                nextSignUpScreen.putExtra("EXTRA_NAME", binding.edtTextShopName.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_DESC", binding.edtTextDesc.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_EMAIL", binding.edtTextEmail.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_PHONE", binding.edtTextPhoneNum.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_WEBSITE", binding.edtTextWebsite.getText().toString());
                startActivity(nextSignUpScreen);
            }
        });
    }

    private boolean validation() {
        boolean valid = true;
        if (binding.edtTextShopName.getText().toString().isEmpty()) {
            binding.edtTextShopName.setError("Shop name cannot be empty");
            valid = false;
        }
        if (binding.edtTextDesc.getText().toString().isEmpty()) {
            binding.edtTextDesc.setError("Shop Description cannot be empty");
            valid = false;
        }
        if (binding.edtTextEmail.getText().toString().isEmpty()) {
            binding.edtTextEmail.setError("Shop Email cannot be empty");
            valid = false;
        }
        if (binding.edtTextPhoneNum.getText().toString().isEmpty()) {
            binding.edtTextPhoneNum.setError("Shop phone number cannot be empty");
            valid = false;
        }
        if (binding.edtTextWebsite.getText().toString().isEmpty()) {
            binding.edtTextWebsite.setError("Shop website link cannot be empty");
            valid = false;
        }
        return valid;
    }
}