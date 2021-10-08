package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ActivitySignupBinding binding;
    Intent currIntent;
    boolean success = true;
    String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
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

        binding.btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to Customer Signup Activity
                // replace the ???
                startActivity(new Intent(SignupActivity.this, CustomerRegistrationActivity1.class));
            }
        });

        binding.btnShopOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to Shop Owner Signup Activity
                // replace the ???
                startActivity(new Intent(SignupActivity.this, ShopOwnerSignupActivity.class));
            }
        });
    }
}