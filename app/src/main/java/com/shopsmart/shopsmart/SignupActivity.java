package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ActivitySignupBinding binding;
    boolean success = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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