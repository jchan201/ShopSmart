package com.shopsmart.shopsmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class SecondFragment extends Fragment {
    private static final String ARG_PARAM1 = "EXTRA_INDEX";
    private int index;

    public SecondFragment() {
    }

    public static SecondFragment newInstance(int index) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_second, container, false);
        index = getArguments().getInt(ARG_PARAM1);
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email)) {
                        user = u;
                    }
                }
                RealmResults<Shop> allShops = ShopSmartApp.realm.where(Shop.class).findAll();
                ArrayList<Shop> shops;
                if (user.getUserType().equals("Customer")) shops = new ArrayList<>(allShops);
                else {
                    List<ObjectId> shopIds = user.getShops();
                    shops = new ArrayList<>();
                    for (Shop s : allShops) {
                        for (ObjectId o : shopIds) {
                            if (s.getId().equals(o))
                                shops.add(s);
                        }
                    }
                }
                if (index >= 0) {
                    RealmResults<Product> allProducts = ShopSmartApp.realm.where(Product.class).findAll();
                    ArrayList<Product> products = new ArrayList<>();
                    for (Product p : allProducts) {
                        if (p.getShopId().equals(shops.get(index).getId())) {
                            products.add(p);
                        }
                    }
                    if (products.isEmpty()) {
                        TextView txtMsg = v.findViewById(R.id.txtMsg);
                        txtMsg.setText("No products found.");
                        txtMsg.setVisibility(View.VISIBLE);
                    } else {
                        ListView productsList = v.findViewById(R.id.lstProducts);
                        ProductViewAdapter adapter = new ProductViewAdapter(getContext(), products, user);
                        productsList.setAdapter(adapter);
                    }
                }
            }
        });
        return v;
    }
}