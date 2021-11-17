package com.shopsmart.shopsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Product> {

    public ListAdapter(Context context, ArrayList<Product> productArrayList){
        super(context, R.layout.product_list_item, productArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list_item, parent, false);
        }

        TextView productName = convertView.findViewById(R.id.name);

        productName.setText(product.getName());

        return super.getView(position, convertView, parent);
    }
}
