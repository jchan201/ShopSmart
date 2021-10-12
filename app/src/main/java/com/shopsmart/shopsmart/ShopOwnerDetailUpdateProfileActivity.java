package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDetailUpdateProfileActivityBinding;
import com.shopsmart.shopsmart.databinding.ShopownerSignupActivityBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerDetailUpdateProfileActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private DatePickerDialog datePickerDialog;
    private ShopownerDetailUpdateProfileActivityBinding binding;

    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    private Realm realm;

    AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDetailUpdateProfileActivityBinding.inflate(getLayoutInflater());
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
//                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart")
                        .allowWritesOnUiThread(true) // allow synchronous writes
                        .build();
                realm = Realm.getInstance(config);

                // Retrieve all users in the Realm.
                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                // Find the AppUser
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(userEmail)) {
                        user = users.get(i);
                    }
                }
                binding.textFName.setText(user.getFirstName());
                binding.textMName.setText(user.getMiddleInitial());
                binding.textLName.setText(user.getLastName());

                Calendar cal = Calendar.getInstance();
                cal.setTime(user.getBirthdate());
                int calYear = cal.get(Calendar.YEAR);
                int calMonth = cal.get(Calendar.MONTH);
                int calDay = cal.get(Calendar.DAY_OF_MONTH);

                // Date picker
                binding.datePickerButton.setText(makeDateString(calDay, calMonth, calYear));
                DatePickerDialog.OnDateSetListener dateSetListener =
                        (datePicker, year, month, day) -> binding.datePickerButton.setText(makeDateString(day, month, year));
                datePickerDialog = new DatePickerDialog(this, dateSetListener, 2000, 0, 1);
            }
        });

        binding.btnCancel.setOnClickListener(view -> {
                realm.close();
                Intent intentToProfile = new Intent(ShopOwnerDetailUpdateProfileActivity.this, ShopOwnerProfileDetailActivity.class);
                intentToProfile.putExtra("EXTRA_PASS", userPass);
                intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(intentToProfile);
            }
        );

        binding.btnSave.setOnClickListener(view -> {
            if(validation()){
                updateUser();
                realm.close();
                Intent intentToProfile = new Intent(ShopOwnerDetailUpdateProfileActivity.this, ShopOwnerProfileDetailActivity.class);
                intentToProfile.putExtra("EXTRA_PASS", userPass);
                intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(intentToProfile);
            }
        });
    }

    private void updateUser(){
        AppUser updateUser = new AppUser();
        updateUser.setFirstName(binding.textFName.getText().toString());
        updateUser.setMiddleInitial(binding.textMName.getText().toString());
        updateUser.setLastName(binding.textLName.getText().toString());

        updateUser.setUserType(user.getUserType());

        try {
            updateUser.setBirthdate(new SimpleDateFormat("MMM dd yyyy").parse(binding.datePickerButton.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        Calendar calendarDOB = Calendar.getInstance();
        calendarDOB.setTime(updateUser.getBirthdate());

        updateUser.setAge(calendar.get(Calendar.YEAR) - calendarDOB.get(Calendar.YEAR));
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());

        for(int i = 0; i < user.getAddresses().size(); i++){
            updateUser.addAddress(user.getAddresses().get(0));
        }

        for(int i = 0; i < user.getPaymentMethods().size(); i++){
            updateUser.addPaymentMethod(user.getPaymentMethods().get(0));
        }

//        user.setFirstName(binding.textFName.getText().toString());
//        user.setMiddleInitial(binding.textMName.getText().toString());
//        user.setLastName(binding.textLName.getText().toString());
//        try {
//            user.setBirthdate(new SimpleDateFormat("MMM dd yyyy").parse(binding.datePickerButton.getText().toString()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        realm.executeTransaction(transactionRealm -> {
            transactionRealm.insertOrUpdate(updateUser);
            AppUser removeUser = transactionRealm.where(AppUser.class).equalTo("_id", user.getId()).findFirst();
            removeUser.deleteFromRealm();
            removeUser = null;
        });
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.textFName.getText().toString().isEmpty()) {
            this.binding.textFName.setError("First name cannot be empty");
            valid = false;
        }

        if (this.binding.textLName.getText().toString().isEmpty()) {
            this.binding.textLName.setError("Last name cannot be empty");
            valid = false;
        }

        return valid;
    }

    // Date Functions
    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 0:
                return "JAN";
            case 1:
                return "FEB";
            case 2:
                return "MAR";
            case 3:
                return "APR";
            case 4:
                return "MAY";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AUG";
            case 8:
                return "SEP";
            case 9:
                return "OCT";
            case 10:
                return "NOV";
            case 11:
                return "DEC";
            default:
                return "";
        }
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
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