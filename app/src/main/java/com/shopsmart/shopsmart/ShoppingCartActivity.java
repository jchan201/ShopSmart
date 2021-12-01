package com.shopsmart.shopsmart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerShoppingCartBinding;

import java.util.ArrayList;

import io.realm.RealmResults;

public class ShoppingCartActivity extends AppCompatActivity {
    private CustomerShoppingCartBinding binding;
    private ShoppingCartListAdapter shoppingCartListAdapter;
    private ArrayList<ProductItem> productItemArrayList;
    private AppUser user;
    private double subTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerShoppingCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email))
                        user = u;
                }

                productItemArrayList = new ArrayList<>();
                for(ProductItem p : user.getShoppingCart()){
                    productItemArrayList.add(p);
                }

                shoppingCartListAdapter = new ShoppingCartListAdapter(this, productItemArrayList, user);
                binding.listShoppingCart.setAdapter(shoppingCartListAdapter);

                calculateAndSetSubTotal();
            }
        });


//        binding.btnDeleteAll.setOnClickListener(view -> {
//            ShopSmartApp.realm.executeTransaction(transactionRealm -> {
//                RealmResults<Order> deleteOrders = ShopSmartApp.realm.where(Order.class).equalTo("cust_id", user.getId()).findAll();
//                deleteOrders.deleteAllFromRealm();
//
//                user.removeAllOrders();
//            });
//
//            orderListAdapter.notifyDataSetChanged();
//        });

//        binding.btnBack.setOnClickListener(view ->
//                startActivity(new Intent(ShoppingCartActivity.this, CustomerManageProfileActivity.class)));
    }

    public void calculateAndSetSubTotal(){

        if(!user.getShoppingCart().isEmpty()) {
            for (ProductItem p : user.getShoppingCart()) {
                Product product = ShopSmartApp.realm.where(Product.class).equalTo("_id", p.getProductId()).findFirst();
                subTotal += product.getPrice() * p.getQuantity();
            }
        }

        binding.tvSubtotal.setText(Double.toString(subTotal));
    }
}