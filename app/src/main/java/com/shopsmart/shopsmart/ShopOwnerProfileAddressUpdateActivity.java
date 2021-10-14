package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDetailUpdateProfileActivityBinding;
import com.shopsmart.shopsmart.databinding.ShopownerProfileUpdateAddressActivityBinding;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerProfileAddressUpdateActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private DatePickerDialog datePickerDialog;
    private ShopownerProfileUpdateAddressActivityBinding binding;

    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    private Realm realm;

    AppUser user;

    Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileUpdateAddressActivityBinding.inflate(getLayoutInflater());
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

                address = user.getAddress();

                int index = 0;
                String[] provinces = getResources().getStringArray(R.array.provinces);
                for(int i = 0; i < provinces.length; i++){
                    if(provinces[i].equals(address.getProvince())){
                        index = i;
                        break;
                    }
                }

                binding.textCity.setText(address.getCity());
                binding.spinnerProvince.setSelection(index);
                binding.textZipCode.setText(address.getPostalCode());
                binding.textAddLine1.setText(address.getAddress1());
                binding.textAddLine2.setText(address.getAddress2());

            }
            else{
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnCancel.setOnClickListener(view -> {
                    realm.close();
                    Intent intentToProfile = new Intent(ShopOwnerProfileAddressUpdateActivity.this, ShopOwnerProfileAddressActivity.class);
                    intentToProfile.putExtra("EXTRA_PASS", userPass);
                    intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(intentToProfile);
                }
        );

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                updateAddress();
                realm.close();
                Intent intentToProfile = new Intent(ShopOwnerProfileAddressUpdateActivity.this, ShopOwnerProfileAddressActivity.class);
                intentToProfile.putExtra("EXTRA_PASS", userPass);
                intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(intentToProfile);
            }
        });
    }

    private void updateAddress() {
        Address newAddress = new Address();
        newAddress.setCity(binding.textCity.getText().toString());
        newAddress.setProvince(binding.spinnerProvince.getSelectedItem().toString());
        newAddress.setPostalCode(binding.textZipCode.getText().toString());
        newAddress.setAddress1(binding.textAddLine1.getText().toString());
        newAddress.setAddress2(binding.textAddLine2.getText().toString());
        newAddress.setCountry(address.getCountry());

        realm.executeTransaction(transactionRealm -> {
            user.addOrUpdateAddress(newAddress);
        });
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.textCity.getText().toString().isEmpty()) {
            this.binding.textCity.setError("City cannot be empty");
            valid = false;
        }

        if (this.binding.textZipCode.getText().toString().isEmpty()) {
            this.binding.textZipCode.setError("Postal code cannot be empty");
            valid = false;
        }

        if(!this.binding.textZipCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")){
            this.binding.textZipCode.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }

        if (this.binding.textAddLine1.getText().toString().isEmpty()) {
            this.binding.textAddLine1.setError("Address Line 1 cannot be empty");
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