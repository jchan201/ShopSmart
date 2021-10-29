package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerPaymentsBinding;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class CustomerPaymentsActivity extends AppCompatActivity implements Serializable {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    PaymentMethod[] paymentMethods;
    int index = 0;
    int total = 0;
    private CustomerPaymentsBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerPaymentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");

            boolean paymentSuccess = currIntent.getBooleanExtra("EXTRA_UPDATE_PAYMENT_SUCCESS", false);
            if (paymentSuccess)
                Toast.makeText(CustomerPaymentsActivity.this, "Successfully update payment method.", Toast.LENGTH_SHORT).show();

            boolean addSuccess = currIntent.getBooleanExtra("EXTRA_ADD_PAYMENT_SUCCESS", false);
            if (addSuccess)
                Toast.makeText(CustomerPaymentsActivity.this, "Successfully add new payment method.", Toast.LENGTH_SHORT).show();
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

                binding.queryFullName.setText(user.getFirstName() + " " + user.getMiddleInitial() + " " + user.getLastName());

                paymentMethods = user.getPaymentMethods().toArray(new PaymentMethod[0]);
                total = paymentMethods.length;
                binding.queryTotalIndex.setText(Integer.toString(total));
                binding.queryCardIndex.setText(Integer.toString(index + 1));

                if (index == 0 && total == 0) {
                    binding.customerPaymentView.setVisibility(View.GONE);
                    binding.queryCardNum.setVisibility(View.GONE);
                    binding.queryCardName.setVisibility(View.GONE);
                    binding.queryCardNum3.setVisibility(View.GONE);
                    binding.queryExpDate.setVisibility(View.GONE);
                    binding.buttonEdit.setVisibility(View.GONE);
                    binding.buttonRemove.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                } else {
                    if (index + 1 == total) {
                        binding.buttonPrev.setVisibility(View.INVISIBLE);
                        binding.buttonNext.setVisibility(View.INVISIBLE);
                    }

                    if (index + 1 < total) {
                        binding.buttonPrev.setVisibility(View.INVISIBLE);
                    }
                    displayCardInfo(paymentMethods[index]);
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.buttonPrev.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                binding.buttonNext.setVisibility(View.VISIBLE);
                binding.queryCardIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);

                if (index == 0) {
                    binding.buttonPrev.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.buttonNext.setOnClickListener(view -> {
            if (index < total) {
                index += 1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.queryCardIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);

                if (index + 1 == total) {
                    binding.buttonNext.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.buttonAdd.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(CustomerPaymentsActivity.this, CustomerPaymentAddActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            intentToProfile.putExtra("EXTRA_PMETHOD_EDIT", false);
            startActivity(intentToProfile);
        });

        binding.buttonEdit.setOnClickListener(view -> {

            PaymentMethod pMethod = new PaymentMethod();
            pMethod.setName(paymentMethods[index].getName());
            pMethod.setCardNumber(paymentMethods[index].getCardNumber());
            pMethod.setExpiry(paymentMethods[index].getExpiry());
            pMethod.setSecurityCode(paymentMethods[index].getSecurityCode());

            Address bAddress = new Address();
            bAddress.setCountry(paymentMethods[index].getBillingAddress().getCountry());
            bAddress.setCity(paymentMethods[index].getBillingAddress().getCity());
            bAddress.setProvince(paymentMethods[index].getBillingAddress().getProvince());
            bAddress.setPostalCode(paymentMethods[index].getBillingAddress().getPostalCode());
            bAddress.setAddress1(paymentMethods[index].getBillingAddress().getAddress1());
            bAddress.setAddress2(paymentMethods[index].getBillingAddress().getAddress2());

            Intent intentAdd = new Intent(CustomerPaymentsActivity.this, CustomerPaymentAddActivity.class);

            intentAdd.putExtra("EXTRA_PASS", userPass);
            intentAdd.putExtra("EXTRA_EMAIL", userEmail);
            intentAdd.putExtra("EXTRA_UPDATE_INDEX", index);

            intentAdd.putExtra("EXTRA_PMETHOD", pMethod);
            intentAdd.putExtra("EXTRA_PMETHOD_ADDRESS", bAddress);

            intentAdd.putExtra("EXTRA_PMETHOD_PHONE", user.getPhone());

            intentAdd.putExtra("EXTRA_PMETHOD_EDIT", true);

            intentAdd.putExtra("EXTRA_INDEX", index);

            realm.close();
            startActivity(intentAdd);

            //Intent intentToProfile = new Intent(CustomerPaymentsActivity.this, CustomerPaymentAddActivity.class);
            //intentToProfile.putExtra("EXTRA_PASS", userPass);
            //intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            //intentToProfile.putExtra("EXTRA_PMETHOD_EDIT", true);
//
            ////Address pMethodAddress = paymentMethods[index].getBillingAddress();
//
            //intentToProfile.putExtra("EXTRA_PMETHOD", paymentMethods[index]);
            ////intentToProfile.putExtra("EXTRA_PMETHOD_ADDRESS", pMethodAddress);
//
            //startActivity(intentToProfile);
        });

        binding.btnBack2.setOnClickListener(view -> {
            realm.close();
            Intent intentToBack = new Intent(CustomerPaymentsActivity.this, CustomerManageProfileActivity.class);
            intentToBack.putExtra("EXTRA_PASS", userPass);
            intentToBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToBack);
        });
    }

    private void displayCardInfo(PaymentMethod paymentMethod) {
        binding.queryCardNum.setText("***" + paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length() - 4));
        binding.queryCardName.setText(paymentMethod.getName().toUpperCase());
        binding.queryExpDate.setText(paymentMethod.getExpiry());
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