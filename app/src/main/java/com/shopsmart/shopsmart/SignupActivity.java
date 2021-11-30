package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Intent
        Intent currIntent = this.getIntent();
        if (currIntent != null) {
            boolean success = currIntent.getBooleanExtra("EXTRA_SIGNUP_SUCCESS", true);
            if (!success)
                binding.textError.setText("Failed to register user: " + currIntent.getStringExtra("EXTRA_ERROR_MSG"));
        }

        binding.btnCustomer.setOnClickListener(view -> {
            // Go to Customer Signup Activity
            startActivity(new Intent(SignupActivity.this, CustomerRegistrationActivity1.class));
            finish();
        });

        binding.btnShopOwner.setOnClickListener(view -> {
            // Go to Shop Owner Signup Activity
            startActivity(new Intent(SignupActivity.this, ShopOwnerSignupActivity.class));
            finish();
        });

        binding.btnBack.setOnClickListener(view -> {
            // Go back to Login
            startActivity(new Intent(SignupActivity.this, StartupActivity.class));
            finish();
        });
    }
}