package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDashboardActivityBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerDashboardActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ShopownerDashboardActivityBinding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    private Realm realm;

    AppUser user;

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
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                // Open a Synced Realm for asynchronous transactions.
                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                realm = Realm.getInstance(config);

                // Retrieve all users in the Realm.
                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                // Find the AppUser
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(userEmail)) {
                        user = users.get(i);
                    }
                }
                binding.textUsername.setText(user.getEmail());
            }
        });

        binding.btnShopList.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(ShopOwnerDashboardActivity.this, ShopListActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
            finish();
        });

        binding.btnProfile.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(ShopOwnerDashboardActivity.this, ShopOwnerProfileActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
            finish();
        });

        binding.btnLogout.setOnClickListener(view -> {
            realm.close();
            startActivity(new Intent(ShopOwnerDashboardActivity.this, StartupActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        // Log out.
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("LOGOUT", "Successfully logged out.");
            } else {
                Log.e("LOGOUT", "Failed to log out, error: " + result.getError());
            }
        });
    }
}