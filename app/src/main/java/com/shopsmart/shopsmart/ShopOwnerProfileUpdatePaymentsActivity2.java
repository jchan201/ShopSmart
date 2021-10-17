package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileAddPaymentActivity2Binding;
import com.shopsmart.shopsmart.databinding.ShopownerProfileUpdatePaymentActivity2Binding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerProfileUpdatePaymentsActivity2 extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ShopownerProfileUpdatePaymentActivity2Binding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    private Realm realm;

    AppUser user;
    PaymentMethod paymentMethod;

    int updateIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileUpdatePaymentActivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.paymentMethod = (PaymentMethod) this.currIntent.getSerializableExtra("EXTRA_PAYMENT_METHOD_OBJ");
            this.updateIndex = currIntent.getIntExtra("EXTRA_UPDATE_INDEX", updateIndex);
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

                int index = 0;
                String[] provinces = getResources().getStringArray(R.array.provinces);
                for(int i = 0; i < provinces.length; i++){
                    if(provinces[i].equals(paymentMethod.getBillingAddress().getProvince())){
                        index = i;
                        break;
                    }
                }

                binding.textCity.setText(paymentMethod.getBillingAddress().getCity());
                binding.spinnerProvince.setSelection(index);
                binding.textZipCode.setText(paymentMethod.getBillingAddress().getPostalCode());
                binding.textAddLine1.setText(paymentMethod.getBillingAddress().getAddress1());
                binding.textAddLine2.setText(paymentMethod.getBillingAddress().getAddress2());

            }
            else{
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnSave.setOnClickListener(view -> {
            if(validation()) {
                addPaymentMethod();
                realm.close();
                Intent intentToProfile = new Intent(ShopOwnerProfileUpdatePaymentsActivity2.this, ShopOwnerProfilePaymentsActivity.class);
                intentToProfile.putExtra("EXTRA_PASS", userPass);
                intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                intentToProfile.putExtra("EXTRA_UPDATE_PAYMENT_SUCCESS", true);
                startActivity(intentToProfile);
            }
        });

        binding.btnBack.setOnClickListener(view -> {
            realm.close();
            Intent intentToBack = new Intent(ShopOwnerProfileUpdatePaymentsActivity2.this, ShopOwnerProfileAddPaymentsActivity1.class);
            intentToBack.putExtra("EXTRA_PASS", userPass);
            intentToBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToBack);
        });
    }

    private void addPaymentMethod() {
        Address billingAddress = new Address();
        billingAddress.setCity(binding.textCity.getText().toString());
        billingAddress.setProvince(binding.spinnerProvince.getSelectedItem().toString());
        billingAddress.setPostalCode(binding.textZipCode.getText().toString());
        billingAddress.setAddress1(binding.textAddLine1.getText().toString());
        billingAddress.setAddress2(binding.textAddLine2.getText().toString());
        billingAddress.setCountry("Canada");

        paymentMethod.setBillingAddress(billingAddress);

        realm.executeTransaction(transactionRealm -> {
            user.updatePaymentMethod(paymentMethod,updateIndex);
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