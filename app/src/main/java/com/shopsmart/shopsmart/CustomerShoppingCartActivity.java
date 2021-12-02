package com.shopsmart.shopsmart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivityCustomerShoppingCartBinding;

import java.util.ArrayList;

import io.realm.RealmResults;

public class CustomerShoppingCartActivity extends AppCompatActivity {
    private ActivityCustomerShoppingCartBinding binding;
    private ShoppingCartListAdapter shoppingCartListAdapter;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerShoppingCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email))
                        user = u;
                }
                ArrayList<ProductItem> shoppingCart = new ArrayList<>(user.getShoppingCart());
                double subtotal = 0;
                for (ProductItem p : shoppingCart) {
                    Product product = ShopSmartApp.realm.where(Product.class).equalTo("_id", p.getProductId()).findFirst();
                    subtotal += product.getPrice() * p.getQuantity();
                }
                shoppingCartListAdapter = new ShoppingCartListAdapter(this, shoppingCart, user, subtotal);
                binding.listShoppingCart.setAdapter(shoppingCartListAdapter);
                binding.tvSubtotal.setText(Double.toString(subtotal));
            }
        });
    }

    public void updateSubtotal(double subtotal) {
        binding.tvSubtotal.setText(Double.toString(subtotal));
    }
}