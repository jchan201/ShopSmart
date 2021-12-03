package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivityCustomerShoppingCartBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;

import io.realm.RealmResults;

public class CustomerShoppingCartActivity extends AppCompatActivity {
    private ActivityCustomerShoppingCartBinding binding;
    private ShoppingCartListAdapter shoppingCartListAdapter;
    private ArrayList<ObjectId> uniqueShops;
    private AppUser user;
    private boolean boolCrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerShoppingCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        boolCrap = currIntent.getBooleanExtra("EXTRA_BOOLCRAP", false);
        if (boolCrap) finish();
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email))
                        user = u;
                }
                binding.queryFullName.setText(user.getEmail());
                ArrayList<ProductItem> shoppingCart = new ArrayList<>(user.getShoppingCart());
                if (!shoppingCart.isEmpty())
                    binding.buttonCheckout.setEnabled(true);
                uniqueShops = new ArrayList<>();
                double subtotal = 0;
                for (ProductItem p : shoppingCart) {
                    Product product = ShopSmartApp.realm.where(Product.class).equalTo("_id", p.getProductId()).findFirst();
                    if (!uniqueShops.contains(product.getShopId()))
                        uniqueShops.add(product.getShopId());
                    subtotal += product.getPrice() * p.getQuantity();
                }
                shoppingCartListAdapter = new ShoppingCartListAdapter(this, shoppingCart, user, subtotal);
                binding.listShoppingCart.setAdapter(shoppingCartListAdapter);
                binding.tvSubtotal.setText(String.format("%.2f", subtotal));
            }
        });
        binding.buttonCheckout.setOnClickListener(view -> {
            Intent intentToNext = new Intent(CustomerShoppingCartActivity.this, CustomerCheckoutActivity.class);
            intentToNext.putExtra("EXTRA_UNIQUESHOPPES", uniqueShops.size());
            intentToNext.putExtra("EXTRA_TOTAL", Double.parseDouble(binding.tvSubtotal.getText().toString()));
            intentToNext.putExtra("EXTRA_NUMITEMS", user.getShoppingCart().size());
            startActivity(intentToNext);
        });
        binding.btnBack.setOnClickListener(view -> finish());
    }

    public void updateSubtotal(double subtotal) {
        binding.tvSubtotal.setText(String.format("%.2f", subtotal));
    }

    public void removeShopFromList(ObjectId shopId) {
        uniqueShops.remove(shopId);
    }

    public void toggleCheckout() {
        binding.buttonCheckout.setEnabled(!binding.buttonCheckout.isEnabled());
    }
}