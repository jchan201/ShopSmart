package com.shopsmart.shopsmart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Product> {
    private final ArrayList<Product> products;
    private final Shop shop;

    public ListAdapter(Context context, ArrayList<Product> products, Shop shop) {
        super(context, 0, products);
        this.products = products;
        this.shop = shop;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item, parent, false);
        TextView productName = (TextView) view.findViewById(R.id.productName);
        productName.setText(products.get(i).getName());

        Button deleteBtn = (Button) view.findViewById(R.id.btnDelete);
        deleteBtn.setTag(i);
        deleteBtn.setOnClickListener(v -> {
            int pos = (Integer) v.getTag();
            ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                if (result.isSuccess()) {
                    ShopSmartApp.instantiateRealm();
                    ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                        Product deleteProduct = transactionRealm.where(Product.class)
                                .equalTo("_id", shop.getProducts().get(pos)).findFirst();
                        deleteProduct.deleteFromRealm();
                        shop.removeProduct(pos);
                    });
                    products.remove(pos);
                    this.notifyDataSetChanged();
                }
            });
        });

        Button viewBtn = (Button) view.findViewById(R.id.btnView);
        viewBtn.setTag(i);
        viewBtn.setOnClickListener(v -> {
            int pos = (Integer) v.getTag();
            Intent nextScreen = new Intent(getContext(), ProductViewActivity.class);
            nextScreen.putExtra("EXTRA_PRODUCT_ID", products.get(pos).getId());
            getContext().startActivity(nextScreen);
        });

        Button editBtn = (Button) view.findViewById(R.id.btnEdit);
        editBtn.setTag(i);
        editBtn.setOnClickListener(v -> {
            int pos = (Integer) v.getTag();
            Intent nextScreen = new Intent(getContext(), ProductUpdateActivity.class);
            nextScreen.putExtra("EXTRA_PRODUCT_ID", products.get(pos).getId());
            getContext().startActivity(nextScreen);
        });
        return view;
    }
}
