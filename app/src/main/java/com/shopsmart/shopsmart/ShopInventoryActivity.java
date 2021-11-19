package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopInventoryActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopInventoryActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    List<ObjectId> shopIds;
    ArrayList<Shop> shops;
    Address address;
    List<ObjectId> productIds;
    ArrayList<Product> products;

    Shop shop;
    int index = 0;
    int total = 0;
    private ShopInventoryActivityBinding binding;
    private App app;
    private Realm realm;
    SyncConfiguration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopInventoryActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.index = currIntent.getIntExtra("EXTRA_INDEX", index);

            boolean addProductSuccess = currIntent.getBooleanExtra("EXTRA_ADD_PRODUCT_SUCCESS", false);
            if (addProductSuccess)
                Toast.makeText(ShopInventoryActivity.this, "Successfully add product to shop.", Toast.LENGTH_SHORT).show();

        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).allowWritesOnUiThread(true).build();
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
                    productIds = shop.getProducts();

                    RealmResults<Product> allProducts = realm.where(Product.class).findAll();
                    products = new ArrayList<Product>();

                    for(Product p : allProducts){
                        for(ObjectId o : productIds){
                            if(p.getId().equals(o)){
                                products.add(p);
                            }
                        }
                    }

                    ListAdapter listAdapter = new ListAdapter(this, products, userEmail, userPass, app, shop);

                    binding.lstProducts.setAdapter(listAdapter);
                }

            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnAddProduct.setOnClickListener(view -> {
            if(realm != null) {
                if (!realm.isClosed()) {
                    realm.close();
                }
            }
            Intent intentToProfile = new Intent(ShopInventoryActivity.this, ProductAddActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            intentToProfile.putExtra("EXTRA_INDEX", index);
            startActivity(intentToProfile);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}