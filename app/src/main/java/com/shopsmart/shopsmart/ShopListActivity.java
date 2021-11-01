package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopListActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopListActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    List<ObjectId> shopIds;
    ArrayList<Shop> shops;
    int index = 0;
    int total = 0;
    private ShopListActivityBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Realm.init(this);

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

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                realm = Realm.getInstance(config);

                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
                for (AppUser u : users) {
                    if (u.getEmail().equals(userEmail)) {
                        user = u;
                    }
                }
                RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
                shopIds = user.getShops();
                shops = new ArrayList<>();
                for (Shop s : allShops) {
                    for (ObjectId o : shopIds) {
                        if (s.getId().equals(o))
                            shops.add(s);
                    }
                }

                total = shops.size();
                binding.textShopTotal.setText(Integer.toString(total));
                if (total == 0) {
                    binding.textShopIndex.setText(Integer.toString(index));
                } else {
                    binding.textShopIndex.setText(Integer.toString(index + 1));
                }

                if (index == 0 && total == 0) {
                    binding.singleShopView.setVisibility(View.GONE);
                    binding.textShopName.setVisibility(View.GONE);
                    binding.btnView.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                } else {
                    binding.singleShopView.setVisibility(View.VISIBLE);
                    binding.textShopName.setVisibility(View.VISIBLE);
                    binding.btnView.setVisibility(View.VISIBLE);
                    binding.btnEdit.setVisibility(View.VISIBLE);
                    binding.buttonPrev.setVisibility(View.VISIBLE);
                    binding.buttonNext.setVisibility(View.VISIBLE);
                    if (index + 1 == total)
                        binding.buttonNext.setVisibility(View.GONE);
                    if (index == 0) {
                        binding.buttonPrev.setVisibility(View.GONE);
                    }
                    displayCardInfo(shops.get(index));
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.buttonPrev.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                binding.buttonNext.setVisibility(View.VISIBLE);
                binding.textShopIndex.setText(Integer.toString(index + 1));
                displayCardInfo(shops.get(index));

                if (index == 0) {
                    binding.buttonPrev.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.buttonNext.setOnClickListener(view -> {
            if (index < total) {
                index += 1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.textShopIndex.setText(Integer.toString(index + 1));
                displayCardInfo(shops.get(index));

                if (index + 1 == total) {
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

        binding.btnView.setOnClickListener(view -> {
            realm.close();
            Intent intentToView = new Intent(ShopListActivity.this, ShopViewActivity.class);
            intentToView.putExtra("EXTRA_EMAIL", userEmail);
            intentToView.putExtra("EXTRA_PASS", userPass);
            intentToView.putExtra("EXTRA_INDEX", index);
            startActivity(intentToView);
        });

        binding.btnEdit.setOnClickListener(view -> {
            realm.close();
            Intent intentToEdit = new Intent(ShopListActivity.this, ShopRegisterEdit.class);
            intentToEdit.putExtra("EXTRA_EMAIL", userEmail);
            intentToEdit.putExtra("EXTRA_PASS", userPass);
            intentToEdit.putExtra("EXTRA_INDEX", index);
            startActivity(intentToEdit);
        });

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

    private void displayCardInfo(Shop shop) {
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