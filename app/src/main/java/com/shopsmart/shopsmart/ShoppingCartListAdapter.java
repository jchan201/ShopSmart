package com.shopsmart.shopsmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ShoppingCartListAdapter extends ArrayAdapter<ProductItem> {
    private static final int QUANTITY_LIMIT = 99;
    private final ArrayList<ProductItem> shoppingCart;
    private final AppUser appUser;
    private int[] quantities;
    private double subtotal;
    private int[] stocks;

    public ShoppingCartListAdapter(Context context, ArrayList<ProductItem> shoppingCart, AppUser appUser, double subtotal) {
        super(context, 0, shoppingCart);
        this.appUser = appUser;
        this.shoppingCart = shoppingCart;
        quantities = new int[shoppingCart.size()];
        this.subtotal = subtotal;
        stocks = new int[shoppingCart.size()];
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
                Product product = ShopSmartApp.realm.where(Product.class)
                        .equalTo("_id", productItem.getProductId()).findFirst();
                stocks[position] = product.getStock();
                Shop shop = ShopSmartApp.realm.where(Shop.class)
                        .equalTo("_id", product.getShopId()).findFirst();
                productItemName.setText(product.getName());
                shopName.setText(shop.getName());
                price.setText(String.format("%.2f",product.getPrice()));
            }
        });
        AddBtn.setTag(position);
        AddBtn.setOnClickListener(view -> {
            int pos = (Integer) view.getTag();
            if (quantities[pos] < QUANTITY_LIMIT) {
                if (quantities[pos] < stocks[pos]) {
                    toggleAllAddButton(false, parent);
                    toggleAllSubButton(false, parent);
                    toggleAllDeleteButton(false, parent);
                    ((CustomerShoppingCartActivity) getContext()).toggleCheckout(false);

                    ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                        if (result.isSuccess()) {
                            ShopSmartApp.instantiateRealm();
                            ShopSmartApp.realm.executeTransaction(realm ->
                                    productItem.setQuantity(++quantities[pos]));
                            quantity.setText(Integer.toString(quantities[pos]));
                            Product product = ShopSmartApp.realm.where(Product.class)
                                    .equalTo("_id", productItem.getProductId()).findFirst();
                            subtotal += product.getPrice();
                            ((CustomerShoppingCartActivity) getContext()).updateSubtotal(subtotal);
                        }
                        toggleAllAddButton(true, parent);
                        toggleAllSubButton(true, parent);
                        toggleAllDeleteButton(true, parent);
                        ((CustomerShoppingCartActivity) getContext()).toggleCheckout(true);
                    });
                } else
                    Toast.makeText(getContext(), "Cannot exceed stock of " + stocks[pos], Toast.LENGTH_SHORT).show();
            }
        });
        SubBtn.setTag(position);
        SubBtn.setOnClickListener(view -> {
            int pos = (Integer) view.getTag();
            if (quantities[pos] > 1) {
                toggleAllAddButton(false, parent);
                toggleAllSubButton(false, parent);
                toggleAllDeleteButton(false, parent);
                ((CustomerShoppingCartActivity) getContext()).toggleCheckout(false);
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(realm ->
                                productItem.setQuantity(--quantities[pos]));
                        quantity.setText(Integer.toString(quantities[pos]));
                        Product product = ShopSmartApp.realm.where(Product.class)
                                .equalTo("_id", productItem.getProductId()).findFirst();
                        subtotal -= product.getPrice();
                        ((CustomerShoppingCartActivity) getContext()).updateSubtotal(subtotal);
                    }
                    toggleAllAddButton(true, parent);
                    toggleAllSubButton(true, parent);
                    toggleAllDeleteButton(true, parent);
                    ((CustomerShoppingCartActivity) getContext()).toggleCheckout(true);
                });
            }
        });
        deleteBtn.setTag(position);
        deleteBtn.setOnClickListener(view -> {
            int pos = (Integer) view.getTag();
            ((CustomerShoppingCartActivity) getContext()).toggleCheckout(false);
            toggleAllAddButton(false, parent);
            toggleAllSubButton(false, parent);
            toggleAllDeleteButton(false, parent);
            ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                if (result.isSuccess()) {
                    ShopSmartApp.instantiateRealm();
                    Product product = ShopSmartApp.realm.where(Product.class)
                            .equalTo("_id", productItem.getProductId()).findFirst();
                    subtotal -= product.getPrice() * quantities[pos];
                    ShopSmartApp.realm.executeTransaction(realm ->
                        appUser.removeShoppingItem(pos));
                    shoppingCart.remove(pos);
                    int[] temp = new int[quantities.length - 1];
                    for (int i = 0, j = 0; i < temp.length; i++) {
                        if (i == pos) j++;
                        temp[i] = quantities[i + j];
                    }
                    quantities = temp;
                    temp = new int[stocks.length - 1];
                    for (int i = 0, j = 0; i < temp.length; i++) {
                        if (i == pos) j++;
                        temp[i] = stocks[i + j];
                    }
                    stocks = temp;
                    if (shoppingCart.isEmpty()) subtotal = 0;
                    ShoppingCartListAdapter.this.notifyDataSetChanged();
                    ((CustomerShoppingCartActivity) getContext()).removeShopFromList(product.getShopId());
                    ((CustomerShoppingCartActivity) getContext()).updateSubtotal(subtotal);
                }
                ((CustomerShoppingCartActivity) getContext()).toggleCheckout(true);
                toggleAllAddButton(true, parent);
                toggleAllSubButton(true, parent);
                toggleAllDeleteButton(true, parent);
            });
        });
        return convertView;
    }

    public void toggleAllDeleteButton(boolean toggle, ViewGroup parent){
        for(int i = 0; i < parent.getChildCount(); i++){
            parent.getChildAt(i).findViewById(R.id.buttonDelete).setEnabled(toggle);
        }
    }

    public void toggleAllAddButton(boolean toggle, ViewGroup parent){
        for(int i = 0; i < parent.getChildCount(); i++){
            parent.getChildAt(i).findViewById(R.id.buttonQuantityAdd).setEnabled(toggle);
        }
    }

    public void toggleAllSubButton(boolean toggle, ViewGroup parent){
        for(int i = 0; i < parent.getChildCount(); i++){
            parent.getChildAt(i).findViewById(R.id.buttonQuantitySub).setEnabled(toggle);
        }
    }
}
