package com.shopsmart.shopsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class OrderListAdapter extends ArrayAdapter<Order> {
    private ArrayList<Order> orderArrayList;
    private AppUser appUser;

    public OrderListAdapter(Context context, ArrayList<Order> orderArrayList) {
        super(context, 0, orderArrayList);
    }

    public OrderListAdapter(Context context, ArrayList<Order> orderArrayList, AppUser appUser) {
        super(context, 0, orderArrayList);
        this.appUser = appUser;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_list_item, parent, false);
        Order order = orderArrayList.get(position);
        TextView orderDate = convertView.findViewById(R.id.orderDate);
        TextView orderTotal = convertView.findViewById(R.id.orderTotal);
        Button deleteBtn = convertView.findViewById(R.id.btnDelete);
        Button viewBtn = convertView.findViewById(R.id.btnView);

        orderDate.setText(order.getDate().toString());
        orderTotal.setText(Double.toString(order.getSubtotal() + order.getTax()));
        deleteBtn.setTag(position);
        viewBtn.setTag(position);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag();
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        Order deleteOrder = ShopSmartApp.realm.where(Order.class)
                                .equalTo("_id", appUser.getOrders().get(pos)).findFirst();
                        deleteOrder.deleteFromRealm();
                        appUser.removeOrder(pos);
                        orderArrayList.remove(pos);
                        OrderListAdapter.this.notifyDataSetChanged();
                    }
                });
            }
        });
        return convertView;
    }
}
