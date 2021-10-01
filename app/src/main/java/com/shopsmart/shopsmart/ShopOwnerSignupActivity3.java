package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerSignupActivity3Binding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerSignupActivity3 extends AppCompatActivity {
    private ShopownerSignupActivity3Binding binding;
    Intent currIntent;

    Address userAddress;
    String userFName;
    String userMName;
    String userLName;
    String userEmail;
    String userPass;
    Date userDOB;
    String userPhone;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerSignupActivity3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userAddress = (Address)this.currIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ");
            this.userFName = currIntent.getStringExtra("EXTRA_FNAME");
            this.userMName = currIntent.getStringExtra("EXTRA_MNAME");
            this.userLName = currIntent.getStringExtra("EXTRA_LNAME");
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASSWORD");
            this.userPhone = currIntent.getStringExtra("EXTRA_PHONE");
            try {
                this.userDOB = new SimpleDateFormat("MMM dd yyyy").parse(currIntent.getStringExtra("EXTRA_DOB"));
//                this.userDOB = (Date)currIntent.getSerializableExtra("EXTRA_DOB2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //cancel go back sign up selection page
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopOwnerSignupActivity3.this, ShopOwnerSignupActivity2.class));
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation()){
                    createUser();
                    startActivity(new Intent(ShopOwnerSignupActivity3.this, ShopOwnerDashboardActivity.class));
                }
            }
        });
    }

    private boolean validation(){
        boolean valid = true;

        if(this.binding.edtTextAccNum.getText().toString().isEmpty()){
            this.binding.edtTextAccNum.setError("Account number cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextIntNum.getText().toString().isEmpty()){
            this.binding.edtTextIntNum.setError("Institution number cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextTransNum.getText().toString().isEmpty()){
            this.binding.edtTextTransNum.setError("Transit number cannot be empty");
            valid = false;
        }

        return valid;
    }

    private void createBankInfo(){
        BankInformation bankInfo = new BankInformation();
        bankInfo.setAccountNumber(this.binding.edtTextAccNum.getText().toString());
        bankInfo.setInstitutionNumber(this.binding.edtTextIntNum.getText().toString());
        bankInfo.setTransitNumber(this.binding.edtTextTransNum.getText().toString());
    }

    private void createUser(){
        AppUser appUser = new AppUser();

        createBankInfo();

        appUser.setEmail(userEmail);
        appUser.setFirstName(userFName);
        appUser.setMiddleInitial(userMName);
        appUser.setLastName(userLName);
        appUser.setPhone(userPhone);
        appUser.addAddress(userAddress);
        appUser.setBirthdate(userDOB);

        Calendar calendarDOB = Calendar.getInstance();
        calendarDOB.setTime(userDOB);
        Calendar calendar = Calendar.getInstance();
        appUser.setAge(calendar.get(Calendar.YEAR) - calendarDOB.get(Calendar.YEAR));

        // Create user in database
        app.getEmailPassword().registerUserAsync(appUser.getEmail(), userPass, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully registered user.");
            } else {
                Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
            }
        });

        // Create AppUser with associated User
        Credentials credentials = Credentials.emailPassword(appUser.getEmail(), userPass);
        app.loginAsync(credentials, result -> {
            Log.e("SSS", "In async: " + app.currentUser().getId());
            SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart").build();
            Realm backgroundRealm = Realm.getInstance(config);

            backgroundRealm.executeTransaction(transactionRealm -> {
                // insert the user
                transactionRealm.insert(appUser);
            });
        });
    }
}