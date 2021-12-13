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
    int shop_index = 0;

    public ListAdapter(Context context, ArrayList<Product> products, Shop shop, int shop_index) {
        super(context, 0, products);
        this.products = products;
        this.shop = shop;
        this.shop_index = shop_index;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item, parent, false);
        TextView productName = view.findViewById(R.id.productName);
        productName.setText(products.get(i).getName());

        Button deleteBtn = view.findViewById(R.id.btnDelete);
        Button viewBtn = view.findViewById(R.id.btnView);
        Button editBtn = view.findViewById(R.id.btnEdit);

        deleteBtn.setTag(i);
        deleteBtn.setOnClickListener(v -> {
            int pos = (Integer) v.getTag();

            toggleAllDeleteButton(false, parent);
            toggleAllViewButton(false, parent);
            toggleAllEditButton(false, parent);

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
                    ((ShopInventoryActivity) getContext()).EmptyOrNot(products);
                }

                toggleAllDeleteButton(true, parent);
                toggleAllViewButton(true, parent);
                toggleAllEditButton(true, parent);

            });
        });

        viewBtn.setTag(i);
        viewBtn.setOnClickListener(v -> {
            int pos = (Integer) v.getTag();
            Intent nextScreen = new Intent(getContext(), ProductViewActivity.class);
            nextScreen.putExtra("EXTRA_PRODUCT_ID", products.get(pos).getId());
            nextScreen.putExtra("EXTRA_SHOP_INDEX", shop_index);
            getContext().startActivity(nextScreen);
        });

        editBtn.setTag(i);
        editBtn.setOnClickListener(v -> {
            int pos = (Integer) v.getTag();
            Intent nextScreen = new Intent(getContext(), ProductUpdateActivity.class);
            nextScreen.putExtra("EXTRA_PRODUCT_ID", products.get(pos).getId());
            nextScreen.putExtra("EXTRA_SHOP_INDEX", shop_index);
            getContext().startActivity(nextScreen);
        });
        return view;
    }

    public void toggleAllDeleteButton(boolean toggle, ViewGroup parent){
        for(int i = 0; i < parent.getChildCount(); i++){
            parent.getChildAt(i).findViewById(R.id.btnDelete).setEnabled(toggle);
        }
    }

    public void toggleAllViewButton(boolean toggle, ViewGroup parent){
        for(int i = 0; i < parent.getChildCount(); i++){
            parent.getChildAt(i).findViewById(R.id.btnView).setEnabled(toggle);
        }
    }

    public void toggleAllEditButton(boolean toggle, ViewGroup parent){
        for(int i = 0; i < parent.getChildCount(); i++){
            parent.getChildAt(i).findViewById(R.id.btnEdit).setEnabled(toggle);
        }
    }
}
