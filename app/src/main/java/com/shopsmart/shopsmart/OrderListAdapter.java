package com.shopsmart.shopsmart;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class OrderListAdapter extends ArrayAdapter<Order> {
    String userEmail;
    String userPassword;
    private final String PARTITION = "ShopSmart";
    private App app;
    private Realm realm;
    AppUser appUser;
    ArrayList<Order> orderArrayList;

    public OrderListAdapter(Context context, ArrayList<Order> orderArrayList){
        super(context, 0, orderArrayList);
    }

    public OrderListAdapter(Context context, ArrayList<Order> orderArrayList, String userEmail, String userPassword, App app, AppUser appUser){
        super(context, 0, orderArrayList);
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.app = app;
        this.appUser = appUser;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Order order = orderArrayList.get(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_list_item, parent, false);
        }

        TextView orderDate = (TextView) convertView.findViewById(R.id.orderDate);
        TextView orderTotal = (TextView) convertView.findViewById(R.id.orderTotal);
        Button deleteBtn = (Button) convertView.findViewById(R.id.btnDelete);
        Button viewBtn = (Button) convertView.findViewById(R.id.btnView);

        orderDate.setText(order.getDate().toString());
        orderTotal.setText(Double.toString(order.getSubtotal()+order.getTax()));
        deleteBtn.setTag(position);
        viewBtn.setTag(position);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();

                Credentials credentials = Credentials.emailPassword(userEmail, userPassword);
                app.loginAsync(credentials, result -> {
                    if (result.isSuccess()) {
                        Log.v("LOGIN", "Successfully authenticated using email and password.");

                        // Open a Synced Realm for asynchronous transactions.
                        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).allowWritesOnUiThread(true).build();
                        realm = Realm.getInstance(config);

                        deleteOrder(position);
                        orderArrayList.remove(position);
                        OrderListAdapter.this.notifyDataSetChanged();
                        realm.close();

                    } else {
                        Log.v("LOGIN", "Failed to authenticate using email and password.");
                    }
                });
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();

                if(realm != null) {
                    if (!realm.isClosed()) {
                        realm.close();
                    }
                }

//                Intent nextScreen = new Intent(getContext(), ProductViewActivity.class);
//                nextScreen.putExtra("EXTRA_PASS", userPassword);
//                nextScreen.putExtra("EXTRA_EMAIL", userEmail);
//                nextScreen.putExtra("EXTRA_ORDER_ID", orderArrayList.get(position).getId());
//                ((ShopInventoryActivity)getContext()).killActivity();
//                getContext().startActivity(nextScreen);
            }
        });

        return convertView;
    }

    private void deleteOrder(int index) {
        realm.executeTransaction(transactionRealm -> {
            Order deleteOrder = transactionRealm.where(Order.class).equalTo("_id", appUser.getOrders().get(index)).findFirst();
            deleteOrder.deleteFromRealm();

            appUser.removeOrder(index);
        });
    }
}
