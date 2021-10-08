package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfilePageActivityBinding;

public class ShopOwnerProfileActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;

    String userEmail;
    String userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.shopsmart.shopsmart.databinding.ShopownerProfilePageActivityBinding binding = ShopownerProfilePageActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

//        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(ShopOwnerProfileActivity.this, ));
//            }
//        });
//
//        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                realm.close();
//                startActivity(new Intent(ShopOwnerProfileActivity.this, StartupActivity.class));
//                finish();
//            }
//        });
    }
}