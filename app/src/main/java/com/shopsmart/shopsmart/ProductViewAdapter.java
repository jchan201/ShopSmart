package com.shopsmart.shopsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductViewAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Product> products;
    private final AppUser appUser;

    ProductViewAdapter(Context context, ArrayList<Product> products, AppUser appUser) {
        this.context = context;
        this.products = products;
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
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.product_view_list_item, parent, false);

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
            Button btnAddToCart = view.findViewById(R.id.btnAddToCart);

            boolean existed = appUser.getShoppingCart().stream().anyMatch(n -> n.getProductId().equals(product.getId()));
            btnAddToCart.setEnabled(!existed);

            btnAddToCart.setOnClickListener(view1 -> {
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(realm ->
                                appUser.addShoppingItem(new ProductItem(product.getId(), 1)));
                    }
                });
                btnAddToCart.setEnabled(false);
                btnAddToCart.setText("Added to cart");
            });
        } else view.findViewById(R.id.btnAddToCart).setEnabled(false);
        return view;
    }
}
