package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ProductAddActivityBinding;
import com.shopsmart.shopsmart.databinding.ShopRegisterActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ProductAddActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    List<ObjectId> shopIds;
    ArrayList<Shop> shops;
    Product product;
    Address address;

    Shop shop;
    int index = 0;
    int total = 0;
    private ProductAddActivityBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProductAddActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.index = currIntent.getIntExtra("EXTRA_INDEX", index);
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

                if(index >= 0){
                    shop = shops.get(index);
                    address = shop.getAddress();
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnCancel.setOnClickListener(view -> startActivity(new Intent(ProductAddActivity.this, ShopListActivity.class)
                .putExtra("EXTRA_EMAIL", userEmail)
                .putExtra("EXTRA_PASS", userPass)));

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                product.setName(binding.edtTextProductName.getText().toString());
                product.setDesc(binding.edtTextDesc.getText().toString());
                product.setPrice(Integer.parseInt(binding.edtTextPrice.getText().toString()));

                //TODO set product type
//                product.setProductType();

                shop.addProduct(product);

                Intent nextSignUpScreen = new Intent(ProductAddActivity.this, ShopRegister2.class);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", userEmail);
                nextSignUpScreen.putExtra("EXTRA_PASS", userPass);
                startActivity(nextSignUpScreen);
            }
        });
    }

    private boolean validation() {
        boolean valid = true;

        return valid;
    }
}