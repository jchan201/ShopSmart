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
    private final String PARTITION = "ShopSmart";
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
    private ShopownerSignupActivity3Binding binding;

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
            this.userAddress = (Address) this.currIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ");
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
        binding.buttonCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerSignupActivity3.this, ShopOwnerSignupActivity2.class)));

        binding.buttonNext.setOnClickListener(view -> {
            if (validation()) createUser();
        });
    }

    private boolean validation() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        boolean valid = true;

        if (this.binding.edtTextCardName.getText().toString().isEmpty()) {
            this.binding.edtTextCardName.setError("Name on card cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextCardNum.getText().toString().isEmpty()) {
            this.binding.edtTextCardNum.setError("Card number cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextCCV.getText().toString().isEmpty()) {
            this.binding.edtTextCCV.setError("CCV cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextExpiryDateYear.getText().toString().isEmpty()) {
            this.binding.edtTextExpiryDateYear.setError("Expire year cannot be empty");
            valid = false;
        }

        if (Integer.parseInt(this.binding.edtTextExpiryDateYear.getText().toString()) < year) {
            this.binding.edtTextExpiryDateYear.setError("Card expired");
            valid = false;
        }

        if (this.binding.edtTextBillCity.getText().toString().isEmpty()) {
            this.binding.edtTextBillCity.setError("City cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextBillPostalCode.getText().toString().isEmpty()) {
            this.binding.edtTextBillPostalCode.setError("Postal Code cannot be empty");
            valid = false;
        }

        if (!this.binding.edtTextBillPostalCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            this.binding.edtTextBillPostalCode.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }

        if (this.binding.edtTextBillAdd1.getText().toString().isEmpty()) {
            this.binding.edtTextBillAdd1.setError("Address cannot be empty");
            valid = false;
        }

        return valid;
    }

    private PaymentMethod createPaymentMethod() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(this.binding.edtTextCardName.getText().toString());
        paymentMethod.setCardNumber(this.binding.edtTextCardNum.getText().toString());
        paymentMethod.setExpiry(this.binding.edtTextExpiryDateMonth.getSelectedItem().toString() + "/" + this.binding.edtTextExpiryDateYear.getText().toString());
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

    private void createUser() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendarDOB = Calendar.getInstance();
        calendarDOB.setTime(userDOB);
        AppUser appUser = new AppUser("Owner", userFName, userMName, userLName,
                calendar.get(Calendar.YEAR) - calendarDOB.get(Calendar.YEAR), userEmail, userPhone, userDOB);
        appUser.addPaymentMethod(createPaymentMethod());
        appUser.addOrUpdateAddress(userAddress);

        // Create user in database
        app.getEmailPassword().registerUserAsync(appUser.getEmail(), userPass, it -> {
            Intent loginScreen = new Intent(ShopOwnerSignupActivity3.this, StartupActivity.class);
            if (it.isSuccess()) {
                Log.i(PARTITION, "Successfully registered user.");

                // Create AppUser with associated User
                Credentials credentials = Credentials.emailPassword(appUser.getEmail(), userPass);
                app.loginAsync(credentials, result -> {
                    Log.e("SSS", "In async: " + app.currentUser().getId());
                    SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart")
                            .allowWritesOnUiThread(true) // allow synchronous writes
                            .build();
                    Realm backgroundRealm = Realm.getInstance(config);
                    backgroundRealm.executeTransaction(transactionRealm -> {
                        transactionRealm.insert(appUser);
                    });
                    backgroundRealm.close();
                });
                loginScreen.putExtra("EXTRA_SIGNUP_SUCCESS", true);
            } else {
                Log.e(PARTITION, "Failed to register user: " + it.getError().getErrorMessage());
            }
            startActivity(loginScreen);
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