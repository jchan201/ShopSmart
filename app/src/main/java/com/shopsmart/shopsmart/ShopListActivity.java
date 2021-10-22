package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopListActivityBinding;
import com.shopsmart.shopsmart.databinding.ShopownerProfilePaymentsActivityBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopListActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ShopListActivityBinding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    private Realm realm;

    AppUser user;
    Shop[] shops;

    int index = 0;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");

            boolean addSuccess = currIntent.getBooleanExtra("EXTRA_REGISTER_SHOP_SUCCESS", false);
            if (addSuccess)
                Toast.makeText(ShopListActivity.this, "Successfully register new shop to your name.", Toast.LENGTH_SHORT).show();
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

                shops = user.getShops().toArray(new Shop[0]);
                total = shops.length;
                binding.textShopTotal.setText(Integer.toString(total));
                if(total == 0){
                    binding.textShopIndex.setText(Integer.toString(index));
                }
                else{
                    binding.textShopIndex.setText(Integer.toString(index+1));
                }

                if(index == 0 && total == 0){
                    binding.singleShopView.setVisibility(View.GONE);
                    binding.textShopName.setVisibility(View.GONE);
                    binding.btnView.setVisibility(View.GONE);
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
                    displayCardInfo(shops[index]);
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
                binding.textShopIndex.setText(Integer.toString(index+1));
                displayCardInfo(shops[index]);

                if(index == 0){
                    binding.buttonPrev.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.buttonNext.setOnClickListener(view -> {
            if(index < total){
                index+=1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.textShopIndex.setText(Integer.toString(index+1));
                displayCardInfo(shops[index]);

                if(index+1 == total){
                    binding.buttonNext.setVisibility(View.INVISIBLE);
                }
            }
        });

//        binding.btnView.setOnClickListener(view -> {
//            realm.close();
//            Intent intentToProfile = new Intent(ShopListActivity.this, ShopOwnerProfileDeletePaymentsConfirmActivity.class);
//            intentToProfile.putExtra("EXTRA_PASS", userPass);
//            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
//            intentToProfile.putExtra("EXTRA_REMOVE_INDEX", index);
//            startActivity(intentToProfile);
//        });

        binding.btnAdd.setOnClickListener(view -> {
            realm.close();
            Intent intentToProfile = new Intent(ShopListActivity.this, ShopRegister.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
        });

        binding.btnBack.setOnClickListener(view -> {
            realm.close();
            Intent intentToBack = new Intent(ShopListActivity.this, ShopOwnerDashboardActivity.class);
            intentToBack.putExtra("EXTRA_PASS", userPass);
            intentToBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToBack);
        });
    }

    private void displayCardInfo(Shop shop){
        binding.textShopName.setText(shop.getName());
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