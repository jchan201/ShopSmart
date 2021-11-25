package com.shopsmart.shopsmart;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopListAdapter extends ArrayAdapter<Product> {
    String userEmail;
    String userPassword;
    private final String PARTITION = "ShopSmart";
    private App app;
    private Realm realm;
    Shop shop;
    ArrayList<Product> shopArrayList;

    public ShopListAdapter(Context context, ArrayList<Product> shopArrayList){
        super(context, 0, shopArrayList);
    }

    public ShopListAdapter(Context context, ArrayList<Product> shopArrayList, String userEmail, String userPassword, App app, Shop shop){
        super(context, 0, shopArrayList);
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.app = app;
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
//        Button deleteBtn = (Button) convertView.findViewById(R.id.btnDelete);
        Button viewBtn = (Button) convertView.findViewById(R.id.buttonView);
//        Button editBtn = (Button) convertView.findViewById(R.id.btnEdit);

        productName.setText(product.getName());
//        deleteBtn.setTag(position);
        viewBtn.setTag(position);
//        editBtn.setTag(position);

//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = (Integer) view.getTag();
//
//                Credentials credentials = Credentials.emailPassword(userEmail, userPassword);
//                app.loginAsync(credentials, result -> {
//                    if (result.isSuccess()) {
//                        Log.v("LOGIN", "Successfully authenticated using email and password.");
//
//                        // Open a Synced Realm for asynchronous transactions.
//                        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).allowWritesOnUiThread(true).build();
//                        realm = Realm.getInstance(config);
//
////                        deleteProduct(position);
//                        shopArrayList.remove(position);
//                        ShopListAdapter.this.notifyDataSetChanged();
//                        realm.close();
//
//                    } else {
//                        Log.v("LOGIN", "Failed to authenticate using email and password.");
//                    }
//                });
//            }
//        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();

                if (realm != null) {
                    if (!realm.isClosed()) {
                        realm.close();
                    }
                }

                Intent nextScreen = new Intent(getContext(), ProductViewActivity.class);
                nextScreen.putExtra("EXTRA_PASS", userPassword);
                nextScreen.putExtra("EXTRA_EMAIL", userEmail);
                nextScreen.putExtra("EXTRA_SHOP_ID", shopArrayList.get(position).getId());
                ((ShopInventoryActivity) getContext()).killActivity();
                getContext().startActivity(nextScreen);
            }
        });
        return convertView;
    }

//        editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = (Integer) view.getTag();
//
//                if(realm != null) {
//                    if (!realm.isClosed()) {
//                        realm.close();
//                    }
//                }
//
//                Intent nextScreen = new Intent(getContext(), ProductUpdateActivity.class);
//                nextScreen.putExtra("EXTRA_PASS", userPassword);
//                nextScreen.putExtra("EXTRA_EMAIL", userEmail);
//                nextScreen.putExtra("EXTRA_PRODUCT_ID", shopArrayList.get(position).getId());
//                ((ShopInventoryActivity)getContext()).killActivity();
//                getContext().startActivity(nextScreen);
//            }
//        });
//
//        return convertView;
//    }

    private void deleteProduct(int index) {
        realm.executeTransaction(transactionRealm -> {
            Product deleteProduct = transactionRealm.where(Product.class).equalTo("_id", shop.getProducts().get(index)).findFirst();
            deleteProduct.deleteFromRealm();

            shop.removeProduct(index);
        });
    }
}
