package com.shopsmart.shopsmart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ShopListAdapter extends ArrayAdapter<Product> {
    Shop shop;
    ArrayList<Product> shopArrayList;

    public ShopListAdapter(Context context, ArrayList<Product> shopArrayList) {
        super(context, 0, shopArrayList);
    }

    public ShopListAdapter(Context context, ArrayList<Product> shopArrayList, Shop shop) {
        super(context, 0, shopArrayList);
        this.shop = shop;
        this.shopArrayList = shopArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Product product = shopArrayList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shop_list_item, parent, false);
        }
        TextView productName = (TextView) convertView.findViewById(R.id.productName);
        Button viewBtn = (Button) convertView.findViewById(R.id.buttonView);
        productName.setText(product.getName());
        viewBtn.setTag(position);

        viewBtn.setOnClickListener(view -> {
            int position1 = (Integer) view.getTag();
            Intent nextScreen = new Intent(getContext(), ProductViewActivity.class);
            nextScreen.putExtra("EXTRA_SHOP_ID", shopArrayList.get(position1).getId());
            getContext().startActivity(nextScreen);
        });
        return convertView;
    }
}
