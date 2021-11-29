package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivityEditBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

public class ShopRegisterEdit extends AppCompatActivity {
    private static final String regex = "^[c]$";
    private final ArrayList<EditText> startTimes = new ArrayList<>();
    private final ArrayList<EditText> endTimes = new ArrayList<>();
    private final CheckBox[] days = new CheckBox[7];
    private ShopRegisterActivityEditBinding binding;
    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        days[0] = binding.chkMonday;
        days[1] = binding.chkTuesday;
        days[2] = binding.chkWednesday;
        days[3] = binding.chkThursday;
        days[4] = binding.chkFriday;
        days[5] = binding.chkSaturday;
        days[6] = binding.chkSunday;

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
                List<ObjectId> shopIds = user.getShops();
                ArrayList<Shop> shops = new ArrayList<>();
                for (Shop s : allShops) {
                    for (ObjectId o : shopIds) {
                        if (s.getId().equals(o))
                            shops.add(s);
                    }
                }
                int index = getIntent().getIntExtra("EXTRA_INDEX", 0);
                if (index >= 0) {
                    shop = shops.get(index);
                    displayShopInfo(shop);
                }
            }
        });

        days[0].setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtMonday1.setVisibility(View.VISIBLE);
                binding.edtTxtMonday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtMonday1.setVisibility(View.GONE);
                binding.edtTxtMonday2.setVisibility(View.GONE);
            }
        });
        days[1].setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtTuesday1.setVisibility(View.VISIBLE);
                binding.edtTxtTuesday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtTuesday1.setVisibility(View.GONE);
                binding.edtTxtTuesday2.setVisibility(View.GONE);
            }
        });
        days[2].setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtWednesday1.setVisibility(View.VISIBLE);
                binding.edtTxtWednesday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtWednesday1.setVisibility(View.GONE);
                binding.edtTxtWednesday2.setVisibility(View.GONE);
            }
        });
        days[3].setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtThursday1.setVisibility(View.VISIBLE);
                binding.edtTxtThursday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtThursday1.setVisibility(View.GONE);
                binding.edtTxtThursday2.setVisibility(View.GONE);
            }
        });
        days[4].setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtFriday1.setVisibility(View.VISIBLE);
                binding.edtTxtFriday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtFriday1.setVisibility(View.GONE);
                binding.edtTxtFriday2.setVisibility(View.GONE);
            }
        });
        days[5].setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtSaturday1.setVisibility(View.VISIBLE);
                binding.edtTxtSaturday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtSaturday1.setVisibility(View.GONE);
                binding.edtTxtSaturday2.setVisibility(View.GONE);
            }
        });
        days[6].setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtSunday1.setVisibility(View.VISIBLE);
                binding.edtTxtSunday2.setVisibility(View.VISIBLE);
            } else {
                binding.edtTxtSunday1.setVisibility(View.GONE);
                binding.edtTxtSunday2.setVisibility(View.GONE);
            }
        });
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopRegisterEdit.this, ShopListActivity.class)));

        binding.btnSave2.setOnClickListener(view ->
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(realm -> {
                            if (validation()) {
                                Address address = new Address();
                                address.setCity(binding.textCity.getText().toString());
                                address.setPostalCode(binding.textZipCode.getText().toString());
                                address.setAddress1(binding.textAddLine1.getText().toString());
                                address.setAddress2(binding.textAddLine2.getText().toString());
                                shop.setName(binding.edtTextShopName.getText().toString());
                                shop.setDesc(binding.edtTextDesc.getText().toString());
                                shop.setEmail(binding.edtTextEmail.getText().toString());
                                shop.setPhone(binding.edtTextPhoneNum.getText().toString());
                                shop.setWebsite(binding.edtTextWebsite.getText().toString());
                                shop.setAddress(address);
                                for (int i = 0; i < startTimes.size(); i++) {
                                    shop.setStartTime(i, startTimes.get(i).getText().toString());
                                    shop.setEndTime(i, endTimes.get(i).getText().toString());
                                }
                            }
                        });
                        startActivity(new Intent(ShopRegisterEdit.this, ShopListActivity.class));
                    }
                }));
    }

    private boolean validation() {
        boolean valid = true;
        String regex = "^(([0-1]?[0-9]|[2][0-3]):[0-5][0-9])|[c]$";
        if (!days[0].isChecked()) {
            binding.edtTxtMonday1.setText("c");
            binding.edtTxtMonday2.setText("c");
        }
        startTimes.add(binding.edtTxtMonday1);
        endTimes.add(binding.edtTxtMonday2);
        if (!days[1].isChecked()) {
            binding.edtTxtTuesday1.setText("c");
            binding.edtTxtTuesday2.setText("c");
        }
        startTimes.add(binding.edtTxtTuesday1);
        endTimes.add(binding.edtTxtTuesday2);
        if (!days[2].isChecked()) {
            binding.edtTxtWednesday1.setText("c");
            binding.edtTxtWednesday2.setText("c");
        }
        startTimes.add(binding.edtTxtWednesday1);
        endTimes.add(binding.edtTxtWednesday2);
        if (!days[3].isChecked()) {
            binding.edtTxtThursday1.setText("c");
            binding.edtTxtThursday2.setText("c");
        }
        startTimes.add(binding.edtTxtThursday1);
        endTimes.add(binding.edtTxtThursday2);
        if (!days[4].isChecked()) {
            binding.edtTxtFriday1.setText("c");
            binding.edtTxtFriday2.setText("c");
        }
        startTimes.add(binding.edtTxtFriday1);
        endTimes.add(binding.edtTxtFriday2);
        if (!days[5].isChecked()) {
            binding.edtTxtSaturday1.setText("c");
            binding.edtTxtSaturday2.setText("c");
        }
        startTimes.add(binding.edtTxtSaturday1);
        endTimes.add(binding.edtTxtSaturday2);
        if (!days[6].isChecked()) {
            binding.edtTxtSunday1.setText("c");
            binding.edtTxtSunday2.setText("c");
        }
        startTimes.add(binding.edtTxtSunday1);
        endTimes.add(binding.edtTxtSunday2);
        for (EditText time : startTimes) {
            if (!time.getText().toString().matches(regex)) {// || !time.getText().toString().matches(closed)) {
                time.setError("Field must be in HH:MM format");
                valid = false;
            }
        }
        for (EditText time : endTimes) {
            if (!time.getText().toString().matches(regex)) {// || !time.getText().toString().matches(closed)) {
                time.setError("Field must be in HH:MM format");
                valid = false;
            }
        }
        return valid;
    }

    private void displayShopInfo(Shop shop) {
        Address address = shop.getAddress();
        binding.textCity.setText(address.getCity());
        binding.textZipCode.setText(address.getPostalCode());
        binding.textAddLine1.setText(address.getAddress1());
        binding.textAddLine2.setText(address.getAddress2());
        binding.edtTextShopName.setText(shop.getName());
        binding.edtTextDesc.setText(shop.getDesc());
        binding.edtTextEmail.setText(shop.getEmail());
        binding.edtTextPhoneNum.setText(shop.getPhone());
        binding.edtTextWebsite.setText(shop.getWebsite());

        String[] daysOpen = new String[days.length];
        RealmList<String> sTimes = shop.getStartTimes();
        for (int x = 0; x < daysOpen.length; x++) {
            if (!sTimes.get(x).matches(regex)) daysOpen[x] = sTimes.get(x);
            else {
                daysOpen[x] = sTimes.get(x);
                daysOpen[x] = "c";
            }
        }
        String[] daysClosed = new String[days.length];
        RealmList<String> cTimes = shop.getEndTimes();
        for (int x = 0; x < daysClosed.length; x++) {
            if (!cTimes.get(x).matches(regex)) daysClosed[x] = cTimes.get(x);
            else {
                daysClosed[x] = cTimes.get(x);
                daysClosed[x] = "c";
            }
        }

        if (!daysOpen[0].matches(regex)) {
            binding.chkMonday.setChecked(true);
            binding.edtTxtMonday1.setText(daysOpen[0]);
            binding.edtTxtMonday2.setText(daysClosed[0]);
            binding.edtTxtMonday1.setVisibility(View.VISIBLE);
            binding.edtTxtMonday2.setVisibility(View.VISIBLE);
        }
        if (!daysOpen[1].matches(regex)) {
            binding.chkTuesday.setChecked(true);
            binding.edtTxtTuesday1.setText(daysOpen[1]);
            binding.edtTxtTuesday2.setText(daysClosed[1]);
            binding.edtTxtTuesday1.setVisibility(View.VISIBLE);
            binding.edtTxtTuesday2.setVisibility(View.VISIBLE);
        }
        if (!daysOpen[2].matches(regex)) {
            binding.chkWednesday.setChecked(true);
            binding.edtTxtWednesday1.setText(daysOpen[2]);
            binding.edtTxtWednesday2.setText(daysClosed[2]);
            binding.edtTxtWednesday1.setVisibility(View.VISIBLE);
            binding.edtTxtWednesday2.setVisibility(View.VISIBLE);
        }
        if (!daysOpen[3].matches(regex)) {
            binding.chkThursday.setChecked(true);
            binding.edtTxtThursday1.setText(daysOpen[3]);
            binding.edtTxtThursday2.setText(daysClosed[3]);
            binding.edtTxtThursday1.setVisibility(View.VISIBLE);
            binding.edtTxtThursday2.setVisibility(View.VISIBLE);
        }
        if (!daysOpen[4].matches(regex)) {
            binding.chkFriday.setChecked(true);
            binding.edtTxtFriday1.setText(daysOpen[4]);
            binding.edtTxtFriday2.setText(daysClosed[4]);
            binding.edtTxtFriday1.setVisibility(View.VISIBLE);
            binding.edtTxtFriday2.setVisibility(View.VISIBLE);
        }
        if (!daysOpen[5].matches(regex)) {
            binding.chkSaturday.setChecked(true);
            binding.edtTxtSaturday1.setText(daysOpen[5]);
            binding.edtTxtSaturday2.setText(daysClosed[5]);
            binding.edtTxtSaturday1.setVisibility(View.VISIBLE);
            binding.edtTxtSaturday2.setVisibility(View.VISIBLE);
        }
        if (!daysOpen[6].matches(regex)) {
            binding.chkSunday.setChecked(true);
            binding.edtTxtSunday1.setText(daysOpen[6]);
            binding.edtTxtSunday2.setText(daysClosed[6]);
            binding.edtTxtSunday1.setVisibility(View.VISIBLE);
            binding.edtTxtSunday2.setVisibility(View.VISIBLE);
        }
    }
}