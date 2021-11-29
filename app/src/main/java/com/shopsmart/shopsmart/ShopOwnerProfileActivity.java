package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfilePageActivityBinding;

public class ShopOwnerProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopownerProfilePageActivityBinding binding = ShopownerProfilePageActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnPersonalInfo.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileActivity.this, ShopOwnerProfileDetailActivity.class)));
        binding.btnAddresses.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileActivity.this, ShopOwnerProfileAddressActivity.class)));
        binding.btnPayments.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileActivity.this, ShopOwnerProfilePaymentsActivity.class)));
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileActivity.this, ShopOwnerDashboardActivity.class)));
    }
}