package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDashboardActivityBinding;
import com.shopsmart.shopsmart.databinding.ShopownerProfilePageActivityBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerProfileActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ShopownerProfilePageActivityBinding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfilePageActivityBinding.inflate(getLayoutInflater());
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
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToProfile = new Intent(ShopOwnerProfileActivity.this, ShopOwnerDashboardActivity.class);
                intentToProfile.putExtra("EXTRA_PASS", userPass);
                intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(intentToProfile);
                finish();
            }
        });
    }
}