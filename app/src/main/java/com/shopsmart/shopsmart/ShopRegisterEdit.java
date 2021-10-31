package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivityBinding;

public class ShopRegisterEdit extends AppCompatActivity {
    String userEmail;
    String userPass;
    private ShopRegisterActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = this.getIntent();
        if (currIntent != null) {
            userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

        binding.btnCancel.setOnClickListener(view -> startActivity(new Intent(ShopRegisterEdit.this, ShopListActivity.class)
                .putExtra("EXTRA_EMAIL", userPass)
                .putExtra("EXTRA_PASS", userPass)));

        binding.btnNext.setOnClickListener(view -> {
            if (validation()) {
                Intent nextSignUpScreen = new Intent(ShopRegisterEdit.this, ShopRegister2.class);
                nextSignUpScreen.putExtra("EXTRA_NAME", binding.edtTextShopName.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_DESC", binding.edtTextDesc.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_EMAIL", binding.edtTextEmail.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_PHONE", binding.edtTextPhoneNum.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_WEBSITE", binding.edtTextWebsite.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_PASS", userPass);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(nextSignUpScreen);
            }
        });
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.edtTextShopName.getText().toString().isEmpty()) {
            this.binding.edtTextShopName.setError("Shop name cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextDesc.getText().toString().isEmpty()) {
            this.binding.edtTextDesc.setError("Shop Description cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextEmail.getText().toString().isEmpty()) {
            this.binding.edtTextEmail.setError("Shop Email cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextPhoneNum.getText().toString().isEmpty()) {
            this.binding.edtTextPhoneNum.setError("Shop phone number cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextWebsite.getText().toString().isEmpty()) {
            this.binding.edtTextWebsite.setError("Shop website link cannot be empty");
            valid = false;
        }

        return valid;
    }
}