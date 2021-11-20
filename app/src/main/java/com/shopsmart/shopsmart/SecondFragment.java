package com.shopsmart.shopsmart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class SecondFragment extends Fragment {
    private static final String ARG_PARAM1 = "EXTRA_USER";
    private static final String ARG_PARAM2 = "EXTRA_PASS";
    private static final String ARG_PARAM3 = "EXTRA_INDEX";
    private final String PARTITION = "ShopSmart";
    AppUser user;
    List<ObjectId> shopIds;
    ArrayList<Shop> shops;
    private String userEmail;
    private String userPass;
    private int index;
    private Shop shop;

    private App app;
    private Realm realm;

    public SecondFragment() {
    }

    public static SecondFragment newInstance(String email, String password, int index) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        args.putString(ARG_PARAM2, password);
        args.putInt(ARG_PARAM3, index);
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
        if (getArguments() != null) {
            app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());
            userEmail = getArguments().getString(ARG_PARAM1);
            userPass = getArguments().getString(ARG_PARAM2);
            index = getArguments().getInt(ARG_PARAM3);

            Credentials credentials = Credentials.emailPassword(userEmail, userPass);
            app.loginAsync(credentials, result -> {
                if (result.isSuccess()) {
                    Log.v("LOGIN", "Successfully authenticated using email and password.");

                    SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                    realm = Realm.getInstance(config);

                    RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
                    for (AppUser u : users) {
                        if (u.getEmail().equals(userEmail)) {
                            user = u;
                        }
                    }
                    RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
                    shopIds = user.getShops();
                    shops = new ArrayList<>();
                    for (Shop s : allShops) {
                        for (ObjectId o : shopIds) {
                            if (s.getId().equals(o))
                                shops.add(s);
                        }
                    }
                    if (index >= 0) {
                        shop = shops.get(index);
                        RealmResults<Product> allProducts = realm.where(Product.class).findAll();
                        ArrayList<Product> products = new ArrayList<>();
                        for (Product p : allProducts) {
                            if (p.getShopId().equals(shop.getId())) {
                                products.add(p);
                            }
                        }
                        if (products.isEmpty()) {
                            TextView txtMsg = v.findViewById(R.id.txtMsg);
                            txtMsg.setText("No products found.");
                            txtMsg.setVisibility(View.VISIBLE);
                        } else {
                            ListView productsList = v.findViewById(R.id.lstProducts);
                            ProductViewAdapter adapter = new ProductViewAdapter(this.getContext(), products);
                            productsList.setAdapter(adapter);
                        }
                    }
                } else {
                    Log.v("LOGIN", "Failed to authenticate using email and password.");
                }
            });
        }
        return v;
    }
}