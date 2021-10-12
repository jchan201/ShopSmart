package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    boolean success = true;
    String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.shopsmart.shopsmart.databinding.ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        //com.shopsmart.shopsmart.databinding.ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.success = currIntent.getBooleanExtra("EXTRA_SIGNUP_SUCCESS", true);
            this.errorMessage = currIntent.getStringExtra("EXTRA_ERROR_MSG");
        }

        if(!success){
            binding.textError.setText("Failed to register user: "+errorMessage);
        }

        binding.btnCustomer.setOnClickListener(view -> {
            // Go to Customer Signup Activity
            // replace the ???
            //startActivity(new Intent(SignupActivity.this, CustomerDashboardActivity.class));
            startActivity(new Intent(SignupActivity.this, CustomerRegistrationActivity1.class));
        });

        binding.btnShopOwner.setOnClickListener(view -> {
            // Go to Shop Owner Signup Activity
            // replace the ???
            startActivity(new Intent(SignupActivity.this, ShopOwnerSignupActivity.class));
        });
    }
}