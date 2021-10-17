package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfilePaymentsActivityBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerProfilePaymentsActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ShopownerProfilePaymentsActivityBinding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    private Realm realm;

    AppUser user;
    PaymentMethod[] paymentMethods;

    int index = 0;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfilePaymentsActivityBinding.inflate(getLayoutInflater());
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
                Toast.makeText(ShopOwnerProfilePaymentsActivity.this, "Successfully update payment method.", Toast.LENGTH_SHORT).show();

            boolean addSuccess = currIntent.getBooleanExtra("EXTRA_ADD_PAYMENT_SUCCESS", false);
            if (addSuccess)
                Toast.makeText(ShopOwnerProfilePaymentsActivity.this, "Successfully add new payment method.", Toast.LENGTH_SHORT).show();

            boolean deleteSuccess = currIntent.getBooleanExtra("EXTRA_DELETE_PAYMENT_SUCCESS", false);
            if (deleteSuccess)
                Toast.makeText(ShopOwnerProfilePaymentsActivity.this, "Successfully remove payment method.", Toast.LENGTH_SHORT).show();

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

                paymentMethods = user.getPaymentMethods().toArray(new PaymentMethod[0]);
                total = paymentMethods.length;
                binding.textPaymentTotal.setText(Integer.toString(total));
                if(total == 0){
                    binding.textPaymentIndex.setText(Integer.toString(index));
                }
                else{
                    binding.textPaymentIndex.setText(Integer.toString(index+1));
                }

                if(index == 0 && total == 0){
                    binding.singlePaymentView.setVisibility(View.GONE);
                    binding.textCardNum.setVisibility(View.GONE);
                    binding.textCardName.setVisibility(View.GONE);
                    binding.textCardExpireTitle.setVisibility(View.GONE);
                    binding.textCardExpire.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnRemove.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                }
                else{
                    if(index+1 == total){
                        binding.buttonPrev.setVisibility(View.INVISIBLE);
                        binding.buttonNext.setVisibility(View.INVISIBLE);
                    }

                    if(index+1 < total){
                        binding.buttonPrev.setVisibility(View.INVISIBLE);
                    }
                    displayCardInfo(paymentMethods[index]);
                }
            }
            else{
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.buttonPrev.setOnClickListener(view -> {
            if(index > 0){
                index-=1;
                binding.buttonNext.setVisibility(View.VISIBLE);
                binding.textPaymentIndex.setText(Integer.toString(index+1));
                displayCardInfo(paymentMethods[index]);

                if(index == 0){
                    binding.buttonPrev.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.buttonNext.setOnClickListener(view -> {
            if(index < total){
                index+=1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.textPaymentIndex.setText(Integer.toString(index+1));
                displayCardInfo(paymentMethods[index]);

                if(index+1 == total){
                    binding.buttonNext.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.btnRemove.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileDeletePaymentsConfirmActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            intentToProfile.putExtra("EXTRA_REMOVE_INDEX", index);
            startActivity(intentToProfile);
        });

        binding.btnEdit.setOnClickListener(view -> {
            //cuz you can't pass paymentmethods[index]
//            PaymentMethod paymentMethod = new PaymentMethod(paymentMethods[index].getName(),
//                                              paymentMethods[index].getCardNumber(),
//                                              paymentMethods[index].getExpiry(),
//                                              paymentMethods[index].getSecurityCode(),
//                                              paymentMethods[index].getBillingAddress());

            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setName(paymentMethods[index].getName());
            paymentMethod.setCardNumber(paymentMethods[index].getCardNumber());
            paymentMethod.setExpiry(paymentMethods[index].getExpiry());
            paymentMethod.setSecurityCode(paymentMethods[index].getSecurityCode());

            realm.close();
            Intent intentToProfile = new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileUpdatePaymentsActivity1.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            intentToProfile.putExtra("EXTRA_UPDATE_INDEX", index);
            intentToProfile.putExtra("EXTRA_PAYMENT_METHOD_OBJ", paymentMethod);
            startActivity(intentToProfile);
        });

        binding.btnAdd.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileAddPaymentsActivity1.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
        });

        binding.btnAdd.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileAddPaymentsActivity1.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
        });

        binding.btnBack.setOnClickListener(view -> {
            realm.close();
            Intent intentToBack = new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileActivity.class);
            intentToBack.putExtra("EXTRA_PASS", userPass);
            intentToBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToBack);
        });
    }

    private void displayCardInfo(PaymentMethod paymentMethod){
        binding.textCardNum.setText("***"+paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length()-4));
        binding.textCardName.setText(paymentMethod.getName().toUpperCase());
        binding.textCardExpire.setText(paymentMethod.getExpiry());
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