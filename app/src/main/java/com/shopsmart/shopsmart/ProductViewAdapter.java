package com.shopsmart.shopsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductViewAdapter extends BaseAdapter {
    final Context context;
    final ArrayList<Product> products;

    ProductViewAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
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
            view = LayoutInflater.from(context).inflate(R.layout.product_view_list_item, null);

        Product product = products.get(i);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        TextView txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        TextView txtStock = (TextView) view.findViewById(R.id.txtStock);
        txtName.setText(product.getName());
        txtDescription.setText(product.getDesc());
        txtPrice.setText(Double.toString(product.getPrice()));
        //if (stock > 0)
            txtStock.setText("?? / None in Stock");
        //else
            //txtStock.setText("None in Stock");
        view.findViewById(R.id.btnAddToCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Go to Add Cart activity
                // Pass in the product index
            }
        });
        return view;
    }
}
