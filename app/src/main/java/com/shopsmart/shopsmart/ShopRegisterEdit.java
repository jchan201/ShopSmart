package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivityBinding;
import com.shopsmart.shopsmart.databinding.ShopRegisterActivityEditBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopRegisterEdit extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    String userEmail;
    String userPass;
    int index = 0;
    int total = 0;
    AppUser user;
    List<ObjectId> shopIds;
    ArrayList<Shop> shops;
    private ShopRegisterActivityEditBinding binding;
    private App app;
    private Realm realm;
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private final ArrayList<EditText> startTimes = new ArrayList<>();
    private final ArrayList<EditText> endTimes = new ArrayList<>();
    SyncConfiguration config;
    Shop shop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivityEditBinding.inflate(getLayoutInflater());

        monday = binding.chkMonday;
        tuesday = binding.chkTuesday;
        wednesday = binding.chkWednesday;
        thursday = binding.chkThursday;
        friday = binding.chkFriday;
        saturday = binding.chkSaturday;
        sunday = binding.chkSunday;

        setContentView(binding.getRoot());
        Realm.init(this);

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        monday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtMonday1.setVisibility(View.VISIBLE);
                binding.edtTxtMonday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtMonday1.setVisibility(View.GONE);
                binding.edtTxtMonday2.setVisibility(View.GONE);
            }
        });
        tuesday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtTuesday1.setVisibility(View.VISIBLE);
                binding.edtTxtTuesday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtTuesday1.setVisibility(View.GONE);
                binding.edtTxtTuesday2.setVisibility(View.GONE);
            }
        });
        wednesday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtWednesday1.setVisibility(View.VISIBLE);
                binding.edtTxtWednesday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtWednesday1.setVisibility(View.GONE);
                binding.edtTxtWednesday2.setVisibility(View.GONE);
            }
        });
        thursday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtThursday1.setVisibility(View.VISIBLE);
                binding.edtTxtThursday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtThursday1.setVisibility(View.GONE);
                binding.edtTxtThursday2.setVisibility(View.GONE);
            }
        });
        friday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtFriday1.setVisibility(View.VISIBLE);
                binding.edtTxtFriday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtFriday1.setVisibility(View.GONE);
                binding.edtTxtFriday2.setVisibility(View.GONE);
            }
        });
        saturday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtSaturday1.setVisibility(View.VISIBLE);
                binding.edtTxtSaturday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtSaturday1.setVisibility(View.GONE);
                binding.edtTxtSaturday2.setVisibility(View.GONE);
            }
        });
        sunday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtSunday1.setVisibility(View.VISIBLE);
                binding.edtTxtSunday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtSunday1.setVisibility(View.GONE);
                binding.edtTxtSunday2.setVisibility(View.GONE);
            }
        });

        Intent currIntent = this.getIntent();
        if (currIntent != null) {
            userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            userPass = currIntent.getStringExtra("EXTRA_PASS");
            index = currIntent.getIntExtra("EXTRA_INDEX", index);
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION)
                        .allowWritesOnUiThread(true)
                        .allowQueriesOnUiThread(true)
                        .build();
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

                total = shops.size();

                if (index >= 0) {
                    shop = shops.get(index);
                    displayShopInfo(shop);
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnCancel.setOnClickListener(view -> {
            realm.close();
            Intent intentBack = new Intent(ShopRegisterEdit.this, ShopListActivity.class);
            intentBack.putExtra("EXTRA_EMAIL", userEmail);
            intentBack.putExtra("EXTRA_PASS", userPass);
            startActivity(intentBack);
        });

        binding.btnSave2.setOnClickListener(view -> {
//            Credentials credentials2 = Credentials.emailPassword(userEmail, userPass);
//            app.loginAsync(credentials2, result -> {
//                        if (result.isSuccess()) {
//                            Log.v("LOGIN", "Successfully authenticated using email and password.");
//
//                            config = new SyncConfiguration.Builder(app.currentUser(), PARTITION)
//                                    .allowWritesOnUiThread(true)
//                                    .allowQueriesOnUiThread(true)
//                                    .build();
//                            realm = Realm.getInstance(config);
//
//                            RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
//                            for (AppUser u : users) {
//                                if (u.getEmail().equals(userEmail)) {
//                                    user = u;
//                                }
//                            }
//
//                            RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
//                            shopIds = user.getShops();
//                            shops = new ArrayList<>();
//                            for (Shop s : allShops) {
//                                for (ObjectId o : shopIds) {
//                                    if (s.getId().equals(o))
//                                        shops.add(s);
//                                }
//                            }
//
//                            total = shops.size();
//
//                            if (index >= 0) {
//                                shop = shops.get(index);
////                        displayShopInfo(shop);
//                            }
//                        } else {
//                            Log.v("LOGIN", "Failed to authenticate using email and password.");
//                        }

//                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION)
//                        .allowWritesOnUiThread(true)
//                        .allowQueriesOnUiThread(true)
//                        .build();
//                RealmConfiguration config2 = new RealmConfiguration.Builder().allowWritesOnUiThread(true).allowQueriesOnUiThread(true).build();
//                realm = Realm.getInstance(config);
                realm.executeTransaction(realm -> {
                    Log.d("Something", "Executing transaction...");

//                Shop shop = shops.get(index);
                    int x = 0;
                    if (validation()) {
                        shop.setName(binding.edtTextShopName.getText().toString());
                        shop.setDesc(binding.edtTextDesc.getText().toString());
                        shop.setEmail(binding.edtTextEmail.getText().toString());
                        shop.setPhone(binding.edtTextPhoneNum.getText().toString());
                        shop.setWebsite(binding.edtTextWebsite.getText().toString());

                        Address address = new Address();

                        address.setCity(binding.textCity.getText().toString());
                        address.setPostalCode(binding.textZipCode.getText().toString());
                        address.setAddress1(binding.textAddLine1.getText().toString());
                        address.setAddress2(binding.textAddLine2.getText().toString());
                        //address.setProvince();
                        shop.setAddress(address);

                        while (x < 7) {
                            for (EditText time : startTimes) {
                                shop.setStartTime(x++, time.getText().toString());
                            }
                        }

                        x = 0;
                        for (EditText time : endTimes) {
                            shop.setEndTime(x++, time.getText().toString());
                        }
                    }
//                });
            });
//            realm.close();
            realm.close();
            Intent intentDone = new Intent(ShopRegisterEdit.this, ShopListActivity.class);
            intentDone.putExtra("EXTRA_EMAIL", userEmail);
            intentDone.putExtra("EXTRA_PASS", userPass);
            startActivity(intentDone);
        });

    }

    private boolean validation(){
        boolean valid = true;
        String regex = "^(([0-1]?[0-9]|[2][0-3]):[0-5][0-9])|[c]$";
//        String closed = "^[c]$";
        if (monday.isChecked()) {
            startTimes.add(binding.edtTxtMonday1);
            endTimes.add(binding.edtTxtMonday2);
        }
        else{
            binding.edtTxtMonday1.setText("c");
            binding.edtTxtMonday2.setText("c");
            startTimes.add(binding.edtTxtMonday1);
            endTimes.add(binding.edtTxtMonday2);
        }
        if (tuesday.isChecked()) {
            startTimes.add(binding.edtTxtTuesday1);
            endTimes.add(binding.edtTxtTuesday2);
        }
        else{
            binding.edtTxtTuesday1.setText("c");
            binding.edtTxtTuesday2.setText("c");
            startTimes.add(binding.edtTxtTuesday1);
            endTimes.add(binding.edtTxtTuesday2);
        }
        if (wednesday.isChecked()) {
            startTimes.add(binding.edtTxtWednesday1);
            endTimes.add(binding.edtTxtWednesday2);
        }
        else{
            binding.edtTxtWednesday1.setText("c");
            binding.edtTxtWednesday2.setText("c");
            startTimes.add(binding.edtTxtWednesday1);
            endTimes.add(binding.edtTxtWednesday2);
        }
        if (thursday.isChecked()) {
            startTimes.add(binding.edtTxtThursday1);
            endTimes.add(binding.edtTxtThursday2);
        }
        else{
            binding.edtTxtThursday1.setText("c");
            binding.edtTxtThursday2.setText("c");
            startTimes.add(binding.edtTxtThursday1);
            endTimes.add(binding.edtTxtThursday2);
        }
        if (friday.isChecked()) {
            startTimes.add(binding.edtTxtFriday1);
            endTimes.add(binding.edtTxtFriday2);
        }
        else{
            binding.edtTxtFriday1.setText("c");
            binding.edtTxtFriday2.setText("c");
            startTimes.add(binding.edtTxtFriday1);
            endTimes.add(binding.edtTxtFriday2);
        }
        if (saturday.isChecked()) {
            startTimes.add(binding.edtTxtSaturday1);
            endTimes.add(binding.edtTxtSaturday2);
        }
        else{
            binding.edtTxtSaturday1.setText("c");
            binding.edtTxtSaturday2.setText("c");
            startTimes.add(binding.edtTxtSaturday1);
            endTimes.add(binding.edtTxtSaturday2);
        }
        if (sunday.isChecked()) {
            startTimes.add(binding.edtTxtSunday1);
            endTimes.add(binding.edtTxtSunday2);
        }
        else{
            binding.edtTxtSunday1.setText("c");
            binding.edtTxtSunday2.setText("c");
            startTimes.add(binding.edtTxtSunday1);
            endTimes.add(binding.edtTxtSunday2);
        }
        for (EditText time : startTimes) {
            if (!time.getText().toString().matches(regex)){// || !time.getText().toString().matches(closed)) {
                time.setError("Field must be in HH:MM format");
                valid = false;
            }
        }
        for (EditText time : endTimes) {
            if (!time.getText().toString().matches(regex)){// || !time.getText().toString().matches(closed)) {
                time.setError("Field must be in HH:MM format");
                valid = false;
            }
        }
        return valid;
    }

    private void displayShopInfo(Shop shop){

        binding.edtTextShopName.setText(shop.getName());
        binding.edtTextDesc.setText(shop.getDesc());
        binding.edtTextEmail.setText(shop.getEmail());
        binding.edtTextPhoneNum.setText(shop.getPhone());
        binding.edtTextWebsite.setText(shop.getWebsite());

//        binding.queryShopName.setText(shop.getName());
//        binding.queryShopDescription.setText(shop.getDesc());
//        binding.queryShopPhone.setText(shop.getPhone());
//        binding.queryShopWebsite.setText(shop.getWebsite());
//        binding.queryShopEmail.setText(shop.getEmail());

        Address address = shop.getAddress();
        String address1 = address.getAddress1();
        String address2 = address.getAddress2();
        String pCode = address.getPostalCode();

        binding.textCity.setText(address.getCity());

        binding.textZipCode.setText(address.getPostalCode());
        binding.textAddLine1.setText(address.getAddress1());
        binding.textAddLine2.setText(address.getAddress2());

        RealmList<String> sTimes = shop.getStartTimes();
        RealmList<String> cTimes = shop.getEndTimes();
        RealmList<String> sRegexTimes = shop.getStartTimes();
        RealmList<String> cRegexTimes = shop.getEndTimes();


        String regex = "^[c]$";

        String[] daysOpen = new String[7];
        String[] daysClosed = new String[7];
//
        for(int x = 0; x < 7; x++){
            if(!sRegexTimes.get(x).matches(regex)){
                daysOpen[x] = sTimes.get(x);
            }
            else{
                daysOpen[x] = sTimes.get(x);
                daysOpen[x] = "c";
            }
        }

        for(int x = 0; x < 7; x++){
            if(!cRegexTimes.get(x).matches(regex)){
                daysClosed[x] = cTimes.get(x);
            }
            else{
                daysClosed[x] = cTimes.get(x);
                daysClosed[x] = "c";
            }
        }

        if(!daysOpen[0].matches(regex)){
            binding.chkMonday.setChecked(true);
            binding.edtTxtMonday1.setText(daysOpen[0]);
            binding.edtTxtMonday2.setText(daysClosed[0]);
            binding.edtTxtMonday1.setVisibility(View.VISIBLE);
            binding.edtTxtMonday2.setVisibility(View.VISIBLE);
        }

        if(!daysOpen[1].matches(regex)){
            binding.chkTuesday.setChecked(true);
            binding.edtTxtTuesday1.setText(daysOpen[1]);
            binding.edtTxtTuesday2.setText(daysClosed[1]);;
            binding.edtTxtTuesday1.setVisibility(View.VISIBLE);
            binding.edtTxtTuesday2.setVisibility(View.VISIBLE);
        }

        if(!daysOpen[2].matches(regex)){
            binding.chkWednesday.setChecked(true);
            binding.edtTxtWednesday1.setText(daysOpen[2]);
            binding.edtTxtWednesday2.setText(daysClosed[2]);;
            binding.edtTxtWednesday1.setVisibility(View.VISIBLE);
            binding.edtTxtWednesday2.setVisibility(View.VISIBLE);
        }

        if(!daysOpen[3].matches(regex)){
            binding.chkThursday.setChecked(true);
            binding.edtTxtThursday1.setText(daysOpen[3]);
            binding.edtTxtThursday2.setText(daysClosed[3]);;
            binding.edtTxtThursday1.setVisibility(View.VISIBLE);
            binding.edtTxtThursday2.setVisibility(View.VISIBLE);
        }

        if(!daysOpen[4].matches(regex)){
            binding.chkFriday.setChecked(true);
            binding.edtTxtFriday1.setText(daysOpen[4]);
            binding.edtTxtFriday2.setText(daysClosed[4]);;
            binding.edtTxtFriday1.setVisibility(View.VISIBLE);
            binding.edtTxtFriday2.setVisibility(View.VISIBLE);
        }

        if(!daysOpen[5].matches(regex)){
            binding.chkSaturday.setChecked(true);
            binding.edtTxtSaturday1.setText(daysOpen[5]);
            binding.edtTxtSaturday2.setText(daysClosed[5]);;
            binding.edtTxtSaturday1.setVisibility(View.VISIBLE);
            binding.edtTxtSaturday2.setVisibility(View.VISIBLE);
        }

        if(!daysOpen[6].matches(regex)){
            binding.chkSunday.setChecked(true);
            binding.edtTxtSunday1.setText(daysOpen[6]);
            binding.edtTxtSunday2.setText(daysClosed[6]);;
            binding.edtTxtSunday1.setVisibility(View.VISIBLE);
            binding.edtTxtSunday2.setVisibility(View.VISIBLE);
        }




//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
////        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
//        Bundle bundle = new Bundle();
//        bundle.putString("SHOP_ADDRESS1", address1);
//        bundle.putString("SHOP_ADDRESS2", address2);
//        bundle.putString("PCODE", pCode);
//
//        bundle.putStringArray("DAYSOPEN", daysOpen);
//        bundle.putStringArray("DAYSCLOSED", daysClosed);
//
//        bundle.putString("EXTRA_USER", userEmail);
//        bundle.putString("EXTRA_PASS", userPass);
//
//
//        FirstFragment fragment = new FirstFragment();
//        fragment.setArguments(bundle);
//
//        ft.replace(R.id.flSecondFragment, fragment).commit();

//        String MondayOpen = sTimes.get(0);
//        String TuesdayOpen = sTimes.get(1);
//        String WednesdayOpen = sTimes.get(2);
//        String ThursdayOpen = sTimes.get(3);
//        String FridayOpen = sTimes.get(4);
//        String SaturdayOpen = sTimes.get(5);
//        String SundayOpen = sTimes.get(6);
//
//        RealmList<String> cTimes = shop.getEndTimes();
//        String MondayC = cTimes.get(0);
//        String TuesdayC = cTimes.get(1);
//        String WednesdayC = cTimes.get(2);
//        String ThursdayC = cTimes.get(3);
//        String FridayC = cTimes.get(4);
//        String SaturdayC = cTimes.get(5);
//        String SundayC = cTimes.get(6);

//        for(int x = 0; x < 7; x++){
//            bundle.putString("DAYSOPEN" + x, daysOpen[x]);
//            bundle.putString("DAYSCLOSED" + x, daysClosed[x]);
//        }

//        bundle.putString("MONDAYOPEN", MondayOpen);
//        bundle.putString("TUESDAYOPEN", TuesdayOpen);
//        bundle.putString("WEDNESDAYOPEN", WednesdayOpen);
//        bundle.putString("THURSDAYOPEN", ThursdayOpen);
//        bundle.putString("FRIDAYOPEN", FridayOpen);
//        bundle.putString("SATURDAYOPEN", SaturdayOpen);
//        bundle.putString("SUNDAYOPEN", SundayOpen);
//
//        bundle.putString("MONDAYC", MondayC);
//        bundle.putString("TUESDAYC", TuesdayC);
//        bundle.putString("WEDNESDAYC", WednesdayC);
//        bundle.putString("THURSDAYC", ThursdayC);
//        bundle.putString("FRIDAYOC", FridayC);
//        bundle.putString("SATURDAYC", SaturdayC);
//        bundle.putString("SUNDAYC", SundayC);

//        Fragment f = SecondFragment.newInstance(address1, address2, pCode);
//
//        FragmentTransaction ft = getFragmentManager().beginTransaction();//.replace(R.id.tab_layout,new SecondFragment());
//        ft.commit();

//        address1.setText(address.getAddress1());
        //View tab1 = View.inflate(getBaseContext(), R.layout.)
        //TextView address1 = (TextView) vi

    }
}/*
        binding.btnCancel.setOnClickListener(view -> startActivity(new Intent(ShopRegisterEdit.this, ShopListActivity.class)
                .putExtra("EXTRA_EMAIL", userPass)
                .putExtra("EXTRA_PASS", userPass)));

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                Intent nextSignUpScreen = new Intent(ShopRegisterEdit.this, ShopRegister2.class);
                nextSignUpScreen.putExtra("EXTRA_NAME", binding.edtTextShopName.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_DESC", binding.edtTextDesc.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_EMAIL", binding.edtTextEmail.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_PHONE", binding.edtTextPhoneNum.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_WEBSITE", binding.edtTextWebsite.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_PASS", userPass);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", userEmail);
                startActivity(nextSignUpScreen);
            }
        });
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.edtTextShopName.getText().toString().isEmpty()) {
            this.binding.edtTextShopName.setError("Shop name cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextDesc.getText().toString().isEmpty()) {
            this.binding.edtTextDesc.setError("Shop Description cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextEmail.getText().toString().isEmpty()) {
            this.binding.edtTextEmail.setError("Shop Email cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextPhoneNum.getText().toString().isEmpty()) {
            this.binding.edtTextPhoneNum.setError("Shop phone number cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextWebsite.getText().toString().isEmpty()) {
            this.binding.edtTextWebsite.setError("Shop website link cannot be empty");
            valid = false;
        }

        return valid;
    }
}*/