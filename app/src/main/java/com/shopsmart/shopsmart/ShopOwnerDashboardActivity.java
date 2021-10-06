package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivitySignupBinding;
import com.shopsmart.shopsmart.databinding.ShopownerDashboardActivityBinding;

public class ShopOwnerDashboardActivity extends AppCompatActivity {
    private ShopownerDashboardActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDashboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        binding.btnCustomer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Go to Customer Signup Activity
//                // replace the ???
//                //startActivity(new Intent(SignupActivity.this, ???));
//            }
//        });
//
//        binding.btnShopOwner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Go to Shop Owner Signup Activity
//                // replace the ???
//                startActivity(new Intent(ShopOwnerDashboardActivity.this, ShopOwnerSignupActivity.class));
//            }
//        });
    }
}