package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.OrderListActivityBinding;
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

public class OrderListActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    List<ObjectId> orderIds;
    ArrayList<Order> orders;
    OrderListAdapter orderListAdapter;

    private OrderListActivityBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OrderListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
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

                RealmResults<Order> allOrders = realm.where(Order.class).findAll();
                orderIds = user.getOrders();
                orders = new ArrayList<>();
                for(Order o : allOrders){
                    for(ObjectId oid : orderIds){
                        if(o.getId().equals(oid)){
                            orders.add(o);
                        }
                    }
                }

                orderListAdapter = new OrderListAdapter(this, orders, userEmail, userPass, app, user);

                binding.lstOrders.setAdapter(orderListAdapter);

            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnDeleteAll.setOnClickListener(view -> {
            realm.executeTransaction(transactionRealm -> {
                RealmResults<Order> deleteOrders = realm.where(Order.class).equalTo("cust_id", user.getId()).findAll();
                deleteOrders.deleteAllFromRealm();

                user.removeAllOrders();
            });

            orderListAdapter.notifyDataSetChanged();
        });

        binding.btnBack.setOnClickListener(view -> {
            killActivity();
            Intent intentToProfile = new Intent(OrderListActivity.this, ShopListActivity.class);
            intentToProfile.putExtra("EXTRA_PASS", userPass);
            intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToProfile);
        });
    }

    public void killActivity(){
        if(realm != null) {
            if (!realm.isClosed()) {
                realm.close();
            }
        }
    }
}