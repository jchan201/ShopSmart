package com.shopsmart.shopsmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

public class FirstFragment extends Fragment {
    private static final String ARG_PARAM1 = "EXTRA_INDEX";
    private int index;

    public FirstFragment() {
    }

    public static FirstFragment newInstance(int index) {
        FirstFragment fragment = new FirstFragment();
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
        View v = inflater.inflate(R.layout.fragment_first, container, false);
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
                    Shop shop = shops.get(index);
                    RealmList<String> shopDaysOpen = shop.getStartTimes();
                    RealmList<String> shopDaysClosed = shop.getEndTimes();
                    TextView sAddress1 = v.findViewById(R.id.queryShopAddress1);
                    TextView sAddress2 = v.findViewById(R.id.queryShopAddress2);
                    TextView pCodeView = v.findViewById(R.id.queryShopPCode);
                    TextView sCountry = v.findViewById(R.id.queryShopCountry);
                    sAddress1.setText(shop.getAddress().getAddress1());
                    sAddress2.setText(shop.getAddress().getAddress2());
                    pCodeView.setText(shop.getAddress().getPostalCode());
                    sCountry.setText("Canada");
                    TextView[] tvDaysOpen = new TextView[7];
                    tvDaysOpen[0] = v.findViewById(R.id.queryDay1Hours);
                    tvDaysOpen[1] = v.findViewById(R.id.queryDay2Hours);
                    tvDaysOpen[2] = v.findViewById(R.id.queryDay3Hours);
                    tvDaysOpen[3] = v.findViewById(R.id.queryDay4Hours);
                    tvDaysOpen[4] = v.findViewById(R.id.queryDay5Hours);
                    tvDaysOpen[5] = v.findViewById(R.id.queryDay6Hours);
                    tvDaysOpen[6] = v.findViewById(R.id.queryDay7Hours);
                    for (int x = 0; x < 7; x++) {
                        if (!shopDaysOpen.get(x).equals("c"))
                            tvDaysOpen[x].setText(shopDaysOpen.get(x) + " - " + shopDaysClosed.get(x));
                        else
                            tvDaysOpen[x].setText("Closed");
                    }
                }
            }
        });
        return v;
    }
}