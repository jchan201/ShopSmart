package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivityDriverSignup2Binding;
import com.shopsmart.shopsmart.databinding.ActivityDriverSignup3Binding;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class DriverSignup3Activity extends AppCompatActivity implements View.OnClickListener {
    private final String PARTITION = "ShopSmart";
    ActivityDriverSignup3Binding binding;
    Intent currIntent;
    Address userAddress;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityDriverSignup3Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userAddress = (Address)this.currIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ");
        }

        this.binding.btnCancel.setOnClickListener(this);
        this.binding.btnFinish.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.btn_cancel: {
                    // Go back to login page
                    Intent mainIntent = new Intent(this, StartupActivity.class);
                    startActivity(mainIntent);
                    break;
                }
                case R.id.btn_finish: {
                    // Validate data
                    if (this.validateData()) {
                        // Create User and AppUser Object
                        this.createUser();

                        // Go to Dashboard
                        Intent dashboardIntent = new Intent(this, ShopOwnerDashboardActivity.class);
                        startActivity(dashboardIntent);
                    }
                    break;
                }
            } // end of switch
        }
    }

    private void createUser() {
        AppUser appUser = new AppUser();
        String password = this.currIntent.getStringExtra("EXTRA_PASSWORD");

        BankInformation bankInformation = new BankInformation();
        bankInformation.setAccountNumber(this.binding.editAccountNumber.getText().toString());
        bankInformation.setInstitutionNumber(this.binding.editInstitutionNumber.getText().toString());
        bankInformation.setTransitNumber(this.binding.editTransitNumber.getText().toString());

        appUser.setEmail(this.currIntent.getStringExtra("EXTRA_EMAIL"));
        appUser.setFirstName(this.currIntent.getStringExtra("EXTRA_FNAME"));
        appUser.setMiddleInitial(this.currIntent.getStringExtra("EXTRA_MNAME"));
        appUser.setLastName(this.currIntent.getStringExtra("EXTRA_LNAME"));
        appUser.setPhone(this.currIntent.getStringExtra("EXTRA_PHONE"));
        appUser.addAddress(this.userAddress);
        // TO-DO: NEED TO ADD PAYMENT INFORMATION

        // Create user in database
        app.getEmailPassword().registerUserAsync(appUser.getEmail(), password, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully registered user.");
            } else {
                Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
            }
        });

        // Create AppUser with associated User
        Credentials credentials = Credentials.emailPassword(appUser.getEmail(), password);
        app.loginAsync(credentials, result -> {
            Log.e("SSS", "In async: " + app.currentUser().getId());
            SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
            Realm backgroundRealm = Realm.getInstance(config);

            backgroundRealm.executeTransaction(transactionRealm -> {
                // insert the user
                transactionRealm.insert(appUser);
            });
        });
    }

    private boolean validateData() {
        boolean validUser = true;

        if (this.binding.editAccountNumber.getText().toString().isEmpty()) {
            this.binding.editAccountNumber.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editInstitutionNumber.getText().toString().isEmpty()) {
            this.binding.editInstitutionNumber.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editTransitNumber.getText().toString().isEmpty()) {
            this.binding.editTransitNumber.setError("Cannot be empty");
            validUser = false;
        }

        return validUser;
    }
} // end of class