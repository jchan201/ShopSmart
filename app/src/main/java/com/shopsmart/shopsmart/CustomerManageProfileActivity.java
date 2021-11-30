package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerManageProfileBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class CustomerManageProfileActivity extends AppCompatActivity {
    //CustomerManageProfileBinding binding;
    private final String PARTITION = "ShopSmart";
    Intent currentIntent;
    String userEmail;
    String userPass;
    AppUser user;
    private CustomerManageProfileBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerManageProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        this.currentIntent = this.getIntent();

        if (this.currentIntent != null) {
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");
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
                binding.queryFullName.setText(user.getEmail());
            }
        });

        binding.accountButton.setOnClickListener(view -> {
            realm.close();
            Intent intentProfile = new Intent(CustomerManageProfileActivity.this, CustomerProfileActivity.class);
            intentProfile.putExtra("EXTRA_EMAIL", userEmail);
            intentProfile.putExtra("EXTRA_PASS", userPass);
            startActivity(intentProfile);
        });

        binding.paymentButton.setOnClickListener(view -> {
            realm.close();
            Intent intentProfile = new Intent(CustomerManageProfileActivity.this, CustomerPaymentsActivity.class);
            intentProfile.putExtra("EXTRA_EMAIL", userEmail);
            intentProfile.putExtra("EXTRA_PASS", userPass);
            startActivity(intentProfile);
        });

        binding.cancelButtonManage.setOnClickListener(view -> {
            realm.close();
            Intent intentBack = new Intent(CustomerManageProfileActivity.this, CustomerDashboardActivity.class);
            intentBack.putExtra("EXTRA_EMAIL", userEmail);
            intentBack.putExtra("EXTRA_PASS", userPass);
            startActivity(intentBack);
        });

        binding.orderButtonManage.setOnClickListener(view -> {
            realm.close();
            Intent intentBack = new Intent(CustomerManageProfileActivity.this, OrderListActivity.class);
            intentBack.putExtra("EXTRA_EMAIL", userEmail);
            intentBack.putExtra("EXTRA_PASS", userPass);
            startActivity(intentBack);
        });

        binding.passwordButton.setOnClickListener(view -> {
            realm.close();
            Intent intentPassword = new Intent(CustomerManageProfileActivity.this, CustomerPasswordActivity.class);
            intentPassword.putExtra("EXTRA_EMAIL", userEmail);
            intentPassword.putExtra("EXTRA_PASS", userPass);
            startActivity(intentPassword);
        });
    }

    //@Override
    //public void onClick(View view) {
    //    if (view != null) {
    //        switch (view.getId()) {
    //            case R.id.cancelButton: {
    //                Intent mainIntent = new Intent(this, StartupActivity.class);
    //                startActivity(mainIntent);
    //                break;
    //            }
    //            case R.id.nextButton: {
    //                if (this.validateData()) {
    //                    //this.writeDataUser();
    //                } else {
    //                    Toast.makeText(getApplicationContext(), "Both Passwords must be the same", Toast.LENGTH_SHORT).show();
    //                }
    //            }
    //        }
    //    }
    //}

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

    //private void writeDataUser() {
    //    Intent CRegister2 = new Intent(this, CustomerRegistrationActivity2.class);
    //    //CRegister2.putExtra("EXTRA_EMAIL", this.binding.email.getText().toString());
    //    //CRegister2.putExtra("EXTRA_PASSWORD", this.binding.password1.getText().toString());
    //    startActivity(CRegister2);
    //}
}
