package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfilePageActivityBinding;

public class ShopOwnerProfileActivity extends AppCompatActivity {
    Intent currIntent;

    String userEmail;
    String userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShopownerProfilePageActivityBinding binding = ShopownerProfilePageActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

        binding.btnPersonalInfo.setOnClickListener(view -> {
            Intent intentToProfile = new Intent(ShopOwnerProfileActivity.this, ShopOwnerProfileDetailActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
        });

        binding.btnBack.setOnClickListener(view -> {
            Intent intentToProfile = new Intent(ShopOwnerProfileActivity.this, ShopOwnerDashboardActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
        });
    }
}