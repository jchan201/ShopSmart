package com.shopsmart.shopsmart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ProductViewAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Product> products;
    private final String PARTITION = "ShopSmart";
    private final String userEmail, userPass;
    private final App app;
    private Realm realm;
    private final AppUser appUser;

    ProductViewAdapter(Context context, ArrayList<Product> products, String userEmail,
                       String userPass, App app, AppUser appUser) {
        this.context = context;
        this.products = products;
        this.userEmail = userEmail;
        this.userPass = userPass;
        this.app = app;
        this.appUser = appUser;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.product_view_list_item, viewGroup, false);

        Product product = products.get(i);
        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtDescription = view.findViewById(R.id.txtDescription);
        TextView txtPrice = view.findViewById(R.id.txtPrice);
        TextView txtStock = view.findViewById(R.id.txtStock);
        txtName.setText(product.getName());
        txtDescription.setText(product.getDesc());
        txtPrice.setText(String.format("Price: $%.2f", product.getPrice()));
        if (product.getStock() > 0)
            txtStock.setText(String.format("%d in Stock", product.getStock()));
        else txtStock.setText("None in Stock");
        if (!appUser.getUserType().equals("Owner")) {
            view.findViewById(R.id.btnAddToCart).setOnClickListener(view1 -> {
                Credentials credentials = Credentials.emailPassword(userEmail, userPass);
                app.loginAsync(credentials, result -> {
                    if (result.isSuccess()) {
                        Log.v("LOGIN", "Successfully authenticated using email and password.");
                        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(),
                                PARTITION).allowWritesOnUiThread(true).build();
                        realm = Realm.getInstance(config);
                        realm.executeTransaction(realm ->
                                appUser.addShoppingItem(new ProductItem(product.getId(), 1)));
                        Toast.makeText(context, "Added " + product.getName() + " to cart.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v("LOGIN", "Failed to authenticate using email and password.");
                    }
                    realm.close();
                });
            });
        } else view.findViewById(R.id.btnAddToCart).setEnabled(false);
        return view;
    }
}
