package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopInventoryActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ShopInventoryActivity extends AppCompatActivity {
    private ShopInventoryActivityBinding binding;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopInventoryActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        if (currIntent != null) {
            index = currIntent.getIntExtra("EXTRA_INDEX", 0);
            boolean addProductSuccess = currIntent.getBooleanExtra("EXTRA_ADD_PRODUCT_SUCCESS", false);
            boolean updateProductSuccess = currIntent.getBooleanExtra("EXTRA_UPDATE_PRODUCT_SUCCESS", false);
            if (addProductSuccess)
                Toast.makeText(ShopInventoryActivity.this, "Successfully add product to shop.", Toast.LENGTH_SHORT).show();
            else if (updateProductSuccess)
                Toast.makeText(ShopInventoryActivity.this, "Successfully update product info.", Toast.LENGTH_SHORT).show();
        }
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email)) {
                        user = u;
                    }
                }
                RealmResults<Shop> allShops = ShopSmartApp.realm.where(Shop.class).findAll();
                List<ObjectId> shopIds = user.getShops();
                ArrayList<Shop> shops = new ArrayList<>();
                for (Shop s : allShops) {
                    for (ObjectId o : shopIds) {
                        if (s.getId().equals(o))
                            shops.add(s);
                    }
                }
                if (index >= 0) {
                    Shop shop = shops.get(index);
                    RealmResults<Product> allProducts = ShopSmartApp.realm.where(Product.class).findAll();
                    List<ObjectId> productIds = shop.getProducts();
                    ArrayList<Product> products = new ArrayList<>();
                    for (Product p : allProducts) {
                        for (ObjectId o : productIds) {
                            if (p.getId().equals(o)) {
                                products.add(p);
                            }
                        }
                    }
                    ListAdapter listAdapter = new ListAdapter(this, products, shop);
                    binding.lstProducts.setAdapter(listAdapter);
                }

            }
        });
        binding.btnAddProduct.setOnClickListener(view -> {
            Intent intentToProfile = new Intent(ShopInventoryActivity.this, ProductAddActivity.class);
            intentToProfile.putExtra("EXTRA_INDEX", index);
            startActivity(intentToProfile);
        });
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopInventoryActivity.this, ShopListActivity.class)));
    }
}