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
    private static final int QUANTITY_LIMIT = 99;
    private final ArrayList<ProductItem> shoppingCart;
    private final AppUser appUser;
    private int[] quantities;
    private double subtotal;

    public ShoppingCartListAdapter(Context context, ArrayList<ProductItem> shoppingCart, AppUser appUser, double subtotal) {
        super(context, 0, shoppingCart);
        this.appUser = appUser;
        this.shoppingCart = shoppingCart;
        quantities = new int[shoppingCart.size()];
        this.subtotal = subtotal;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_cart_list_item, parent, false);

        TextView productItemName = convertView.findViewById(R.id.productItemName);
        TextView shopName = convertView.findViewById(R.id.shopSoldName);
        TextView price = convertView.findViewById(R.id.productItemPrice);
        Button deleteBtn = convertView.findViewById(R.id.buttonDelete);
        TextView quantity = convertView.findViewById(R.id.tvQuantity);
        Button AddBtn = convertView.findViewById(R.id.buttonQuantityAdd);
        Button SubBtn = convertView.findViewById(R.id.buttonQuantitySub);

        ProductItem productItem = shoppingCart.get(position);
        quantities[position] = productItem.getQuantity();
        quantity.setText(Integer.toString(quantities[position]));
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                Product product = ShopSmartApp.realm.where(Product.class).equalTo("_id", productItem.getProductId()).findFirst();
                Shop shop = ShopSmartApp.realm.where(Shop.class).equalTo("_id", product.getShopId()).findFirst();

                productItemName.setText(product.getName());
                shopName.setText(shop.getName());
                price.setText(Double.toString(product.getPrice()));
            }
        });

        AddBtn.setTag(quantities[position]);
        AddBtn.setOnClickListener(view -> {
            int pos = (Integer) view.getTag();
            if (quantities[pos] < QUANTITY_LIMIT) {
                AddBtn.setEnabled(false);
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        productItem.setQuantity(++quantities[pos]);
                        quantity.setText(Integer.toString(quantities[pos]));
                        Product product = ShopSmartApp.realm.where(Product.class).equalTo("_id", productItem.getProductId()).findFirst();
                        subtotal += product.getPrice();
                        ((CustomerShoppingCartActivity) getContext()).updateSubtotal(subtotal);
                    }
                    AddBtn.setEnabled(true);
                });
            }
        });
        SubBtn.setTag(quantities[position]);
        SubBtn.setOnClickListener(view -> {
            int pos = (Integer) view.getTag();
            if (quantities[pos] < QUANTITY_LIMIT) {
                SubBtn.setEnabled(false);
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        productItem.setQuantity(--quantities[pos]);
                        quantity.setText(Integer.toString(quantities[pos]));
                        Product product = ShopSmartApp.realm.where(Product.class).equalTo("_id", productItem.getProductId()).findFirst();
                        subtotal -= product.getPrice();
                        ((CustomerShoppingCartActivity) getContext()).updateSubtotal(subtotal);
                    }
                    SubBtn.setEnabled(true);
                });
            }
        });
        deleteBtn.setTag(position);
        deleteBtn.setOnClickListener(view -> {
            int pos = (Integer) view.getTag();
            ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                if (result.isSuccess()) {
                    ShopSmartApp.instantiateRealm();
                    Product product = ShopSmartApp.realm.where(Product.class).equalTo("_id", productItem.getProductId()).findFirst();
                    subtotal -= product.getPrice() * quantities[pos];
                    appUser.removeShoppingItem(pos);
                    shoppingCart.remove(pos);
                    int[] temp = new int[shoppingCart.size()];
                    for (int i = 0; i < quantities.length; i++) {
                        if (i != pos) temp[i] = quantities[i];
                    }
                    quantities = temp;
                    ShoppingCartListAdapter.this.notifyDataSetChanged();
                    ((CustomerShoppingCartActivity) getContext()).updateSubtotal(subtotal);
                }
            });
        });
        return convertView;
    }
}
