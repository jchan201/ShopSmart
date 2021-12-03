package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.OrderListActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class OrderListActivity extends AppCompatActivity {
    private OrderListActivityBinding binding;
    private OrderListAdapter orderListAdapter;
    private List<ObjectId> orderIds;
    private ArrayList<Order> orders;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OrderListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email))
                        user = u;
                }
                RealmResults<Order> allOrders = ShopSmartApp.realm.where(Order.class).findAll();
                orderIds = user.getOrders();
                orders = new ArrayList<>();
                for (Order o : allOrders) {
                    for (ObjectId oid : orderIds) {
                        if (o.getId().equals(oid)) {
                            orders.add(o);
                        }
                    }
                }
                orderListAdapter = new OrderListAdapter(this, orders, user);
                binding.lstOrders.setAdapter(orderListAdapter);
            }
        });
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(OrderListActivity.this, CustomerManageProfileActivity.class)));
    }
}