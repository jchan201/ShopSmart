package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivitySignupBinding;
import com.shopsmart.shopsmart.databinding.ShopownerDashboardActivityBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerDashboardActivity extends AppCompatActivity {
    private ShopownerDashboardActivityBinding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    App app;

    Realm backgroundRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDashboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASSWORD");
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            Log.e("SSS", "In async: " + app.currentUser().getId());
            SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart").build();
            backgroundRealm = Realm.getInstance(config);
        });

        // Retrieve all users in the Realm.
        RealmResults<AppUser> users = backgroundRealm.where(AppUser.class).findAll();
        AppUser user = null;

        // Find the AppUser
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail() == userEmail) {
                user = users.get(i);
            }
        }

        binding.textUsername.setText(user.getEmail());

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