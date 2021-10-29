package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDeleteAccountActivityBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerDeleteAccountConfirmActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    private ShopownerDeleteAccountActivityBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDeleteAccountActivityBinding.inflate(getLayoutInflater());
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
                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).allowWritesOnUiThread(true).build();
                realm = Realm.getInstance(config);

                // Retrieve all users in the Realm.
                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                // Find the AppUser
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(userEmail)) {
                        user = users.get(i);
                    }
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnConfirm.setOnClickListener(view -> {
            if (validation()) {
                deleteAccount();
                realm.close();
                Intent intentToProfile = new Intent(ShopOwnerDeleteAccountConfirmActivity.this, StartupActivity.class);
                intentToProfile.putExtra("EXTRA_DELETE_PAYMENT_SUCCESS", true);
                startActivity(intentToProfile);
            }
        });

        binding.btnCancel.setOnClickListener(view -> {
            realm.close();
            Intent intentToBack = new Intent(ShopOwnerDeleteAccountConfirmActivity.this, ShopOwnerDashboardActivity.class);
            intentToBack.putExtra("EXTRA_PASS", userPass);
            intentToBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToBack);
        });
    }

    private void deleteAccount() {
        realm.executeTransaction(transactionRealm -> {
            AppUser deleteUser = transactionRealm.where(AppUser.class).equalTo("_id", user.getId()).findFirst();
            deleteUser.deleteFromRealm();
        });
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.editTextPassword.getText().toString().isEmpty()) {
            this.binding.editTextPassword.setError("Password cannot be empty");
            valid = false;
        }

        if (!this.binding.editTextPassword.getText().toString().equals(userPass)) {
            this.binding.editTextPassword.setError("Incorrect password");
            valid = false;
        }

        return valid;
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