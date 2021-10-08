package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerSignupActivity3Binding;

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

    boolean success = true;
    String errorMsg;

    App app;

    Realm backgroundRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerSignupActivity3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        binding.edtTextExpiryDateYear.setText(Integer.toString(year));

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //cancel go back sign up selection page
        binding.buttonCancel.setOnClickListener(view -> {
            startActivity(new Intent(ShopOwnerSignupActivity3.this, ShopOwnerSignupActivity2.class));
        });

        binding.buttonNext.setOnClickListener(view -> {
            if(validation()){
                createUser();
            }
        });
    }

    private boolean validation(){
        boolean valid = true;

        if(this.binding.edtTextCardNum.getText().toString().isEmpty()){
            this.binding.edtTextCardNum.setError("Card number cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextCCV.getText().toString().isEmpty()){
            this.binding.edtTextCCV.setError("CCV cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextBillCity.getText().toString().isEmpty()){
            this.binding.edtTextBillCity.setError("City cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextBillPostalCode.getText().toString().isEmpty()){
            this.binding.edtTextBillPostalCode.setError("Postal Code cannot be empty");
            valid = false;
        }

        if(!this.binding.edtTextBillPostalCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")){
            this.binding.edtTextBillPostalCode.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }

        if(this.binding.edtTextBillAdd1.getText().toString().isEmpty()){
            this.binding.edtTextBillAdd1.setError("Address cannot be empty");
            valid = false;
        }

        return valid;
    }

    private PaymentMethod createPaymentMethod(){
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setCardNumber(this.binding.edtTextCardNum.getText().toString());
        paymentMethod.setExpiry(this.binding.edtTextExpiryDateMonth.getSelectedItem().toString()+"/"+this.binding.edtTextExpiryDateYear.getText().toString());
        paymentMethod.setSecurityCode(this.binding.edtTextCCV.getText().toString());

        Address billAddress = new Address();
        billAddress.setAddress1(this.binding.edtTextBillAdd1.getText().toString());
        billAddress.setAddress2(this.binding.edtTextBillAdd2.getText().toString());
        billAddress.setCity(this.binding.edtTextBillCity.getText().toString());
        billAddress.setProvince(this.binding.spinnerProvince.getSelectedItem().toString());
        billAddress.setPostalCode(this.binding.edtTextBillPostalCode.getText().toString());
        billAddress.setCountry("Canada");

        paymentMethod.setBillingAddress(billAddress);
        return paymentMethod;
    }

    private void createUser(){
        AppUser appUser = new AppUser();

        appUser.setEmail(userEmail);
        appUser.setFirstName(userFName);
        appUser.setMiddleInitial(userMName);
        appUser.setLastName(userLName);
        appUser.setPhone(userPhone);
        appUser.addAddress(userAddress);
        appUser.setBirthdate(userDOB);
        appUser.setUserType("Owner");

        appUser.addPaymentMethod(createPaymentMethod());

        Calendar calendarDOB = Calendar.getInstance();
        calendarDOB.setTime(userDOB);
        Calendar calendar = Calendar.getInstance();
        appUser.setAge(calendar.get(Calendar.YEAR) - calendarDOB.get(Calendar.YEAR));

        // Create user in database
        app.getEmailPassword().registerUserAsync(appUser.getEmail(), userPass, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully registered user.");
                errorMsg = "Successfully registered user";
            } else {
                Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
                success = false;
                errorMsg = "Failed to register user: " + it.getError().getErrorMessage();
            }
            Intent backToStartUpScreen = new Intent(ShopOwnerSignupActivity3.this, StartupActivity.class);
            backToStartUpScreen.putExtra("EXTRA_SIGNUP_SUCCESS", success);
            backToStartUpScreen.putExtra("EXTRA_ERROR_MSG", errorMsg);
            startActivity(backToStartUpScreen);
        });

        // Create AppUser with associated User
        Credentials credentials = Credentials.emailPassword(appUser.getEmail(), userPass);
        app.loginAsync(credentials, result -> {
            Log.e("SSS", "In async: " + app.currentUser().getId());
            SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart").build();
            backgroundRealm = Realm.getInstance(config);

            backgroundRealm.executeTransactionAsync(transactionRealm -> {
                // insert the user
                transactionRealm.insert(appUser);
            });

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
        backgroundRealm.close(); // Close the realm.
    }
}