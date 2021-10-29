package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivity3Binding;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopRegister3 extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ShopRegisterActivity3Binding binding;
    private App app;
    private Realm realm;
    private String userEmail;
    private String userPass;
    private Address shopAddress;
    private String shopName;
    private String shopDesc;
    private String shopEmail;
    private String shopPhone;
    private String shopWebsite;
    private CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private ArrayList<EditText> startTimes = new ArrayList<>();
    private ArrayList<EditText> endTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivity3Binding.inflate(getLayoutInflater());
        monday = binding.chkMonday;
        tuesday = binding.chkTuesday;
        wednesday = binding.chkWednesday;
        thursday = binding.chkThursday;
        friday = binding.chkFriday;
        saturday = binding.chkSaturday;
        sunday = binding.chkSunday;
        setContentView(binding.getRoot());

        Intent currIntent = this.getIntent();
        if (currIntent != null) {
            this.shopAddress = (Address) currIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ");
            this.shopName = currIntent.getStringExtra("EXTRA_NAME");
            this.shopDesc = currIntent.getStringExtra("EXTRA_DESC");
            this.shopEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.shopPhone = currIntent.getStringExtra("EXTRA_PHONE");
            this.shopWebsite = currIntent.getStringExtra("EXTRA_WEBSITE");
            userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            userPass = currIntent.getStringExtra("EXTRA_PASS");
        }
        Realm.init(this);
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

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                Credentials credentials = Credentials.emailPassword(userEmail, userPass);
                app.loginAsync(credentials, result -> {
                    Log.v("ShopCreation", "Logging in...");
                    if (result.isSuccess()) {
                        Log.v("ShopCreation", "Log in success!");
                        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION)
                                .allowWritesOnUiThread(true)
                                .allowQueriesOnUiThread(true)
                                .build();
                        realm = Realm.getInstance(config);
                        realm.executeTransaction(realm -> {
                            Shop shop = new Shop(shopDesc, shopName, shopEmail, shopPhone, shopWebsite, shopAddress);
                            for (EditText time : startTimes) {
                                shop.addStartTime(time.getText().toString());
                            }
                            for (EditText time : endTimes) {
                                shop.addEndTime(time.getText().toString());
                            }
                            AppUser user = null;
                            RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
                            for (AppUser u : users) {
                                if (u.getEmail().equals(userEmail)) {
                                    user = u;
                                }
                            }
                            user.addShop(shop.getId());
                        });
                        realm.close();
                    }
                });
                startActivity(new Intent(ShopRegister3.this, ShopListActivity.class)
                        .putExtra("EXTRA_REGISTER_SHOP_SUCCESS", true)
                        .putExtra("EXTRA_PASS", userPass)
                        .putExtra("EXTRA_EMAIL", userEmail));
            } else {
                realm.close();
                startActivity(new Intent(ShopRegister3.this, ShopListActivity.class)
                        .putExtra("EXTRA_PASS", userPass)
                        .putExtra("EXTRA_EMAIL", userEmail));
            }
        });

        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopRegister3.this, ShopRegister.class)));
    }

    private boolean validation() {
        boolean valid = true;
        String regex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
        if (monday.isChecked()) {
            startTimes.add(binding.edtTxtMonday1);
            endTimes.add(binding.edtTxtMonday2);
        }
        if (tuesday.isChecked()) {
            startTimes.add(binding.edtTxtTuesday1);
            endTimes.add(binding.edtTxtTuesday2);
        }
        if (wednesday.isChecked()) {
            startTimes.add(binding.edtTxtWednesday1);
            endTimes.add(binding.edtTxtWednesday2);
        }
        if (thursday.isChecked()) {
            startTimes.add(binding.edtTxtThursday1);
            endTimes.add(binding.edtTxtThursday2);
        }
        if (friday.isChecked()) {
            startTimes.add(binding.edtTxtFriday1);
            endTimes.add(binding.edtTxtFriday2);
        }
        if (saturday.isChecked()) {
            startTimes.add(binding.edtTxtSaturday1);
            endTimes.add(binding.edtTxtSaturday2);
        }
        if (sunday.isChecked()) {
            startTimes.add(binding.edtTxtSunday1);
            endTimes.add(binding.edtTxtSunday2);
        }
        for (EditText time : startTimes) {
            if (!time.getText().toString().matches(regex)) {
                time.setError("Field must be in HH:MM format");
                valid = false;
            }
        }
        for (EditText time : endTimes) {
            if (!time.getText().toString().matches(regex)) {
                time.setError("Field must be in HH:MM format");
                valid = false;
            }
        }
        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        // Log out.
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("LOGOUT", "Successfully logged out.");
            } else {
                Log.e("LOGOUT", "Failed to log out, error: " + result.getError());
            }
        });
    }
}