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

public class ShoppingCartListAdapter extends ArrayAdapter<ProductItem> {
    private ArrayList<ProductItem> productItemArrayList;
    private Product product;
    private Shop shop;
    private AppUser appUser;
    private int currentQuantity = 1;

    public ShoppingCartListAdapter(Context context, ArrayList<ProductItem> productItemArrayList) {
        super(context, 0, productItemArrayList);
    }

    public ShoppingCartListAdapter(Context context, ArrayList<ProductItem> productItemArrayList, AppUser appUser) {
        super(context, 0, productItemArrayList);
        this.appUser = appUser;
        this.productItemArrayList = productItemArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_cart_list_item, parent, false);

        ProductItem productItem = productItemArrayList.get(position);
        TextView productItemName = convertView.findViewById(R.id.productItemName);
        TextView shopName = convertView.findViewById(R.id.shopSoldName);
        TextView price = convertView.findViewById(R.id.productItemPrice);

        Button deleteBtn = convertView.findViewById(R.id.buttonDelete);
        TextView quantity = convertView.findViewById(R.id.tvQuantity);
        Button AddBtn = convertView.findViewById(R.id.buttonQuantityAdd);
        Button SubBtn = convertView.findViewById(R.id.buttonQuantitySub);

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                product = ShopSmartApp.realm.where(Product.class).equalTo("_id", productItem.getProductId()).findFirst();
                shop = ShopSmartApp.realm.where(Shop.class).equalTo("_id", product.getShopId()).findFirst();

                productItemName.setText(product.getName());
                shopName.setText(shop.getName());
                price.setText(Double.toString(product.getPrice()));
            }
        });

        deleteBtn.setTag(position);
        AddBtn.setTag(position);
        SubBtn.setTag(position);
        currentQuantity = productItem.getQuantity();
        quantity.setText(Integer.toString(currentQuantity));

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        deleteItem(position);
                        productItemArrayList.remove(position);
                        ShoppingCartListAdapter.this.notifyDataSetChanged();

                        ((ShoppingCartActivity)getContext()).calculateAndSetSubTotal();
                    }
                });
            }
        });

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        //Quantity on UI
                        currentQuantity+=1;
                        quantity.setText(Integer.toString(currentQuantity));

                        //Quantity on Realm
                        ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                            appUser.getShoppingCart().get(position).setQuantity(currentQuantity);
                        });

                        ((ShoppingCartActivity)getContext()).calculateAndSetSubTotal();
                    }
                });
            }
        });

        SubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        //Quantity on UI
                        currentQuantity-=1;
                        quantity.setText(Integer.toString(currentQuantity));

                        //Quantity on Realm
                        ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                            appUser.getShoppingCart().get(position).setQuantity(currentQuantity);
                        });

                        ((ShoppingCartActivity)getContext()).calculateAndSetSubTotal();
                    }
                });
            }
        });

        return convertView;
    }

    private void deleteItem(int index) {
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                    appUser.removeShoppingItem(index);
                });
            }
        });
    }
}
