package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivity3Binding;

import java.util.ArrayList;

import io.realm.RealmResults;

public class ShopRegister3 extends AppCompatActivity {
    private static final String regex = "^(([0-1]?[0-9]|[2][0-3]):[0-5][0-9])|[c]$";
    private final ArrayList<EditText> startTimes = new ArrayList<>();
    private final ArrayList<EditText> endTimes = new ArrayList<>();
    private final CheckBox[] days = new CheckBox[7];
    private ShopRegisterActivity3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivity3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        days[0] = binding.chkMonday;
        days[1] = binding.chkTuesday;
        days[2] = binding.chkWednesday;
        days[3] = binding.chkThursday;
        days[4] = binding.chkFriday;
        days[5] = binding.chkSaturday;
        days[6] = binding.chkSunday;

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
        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(realm -> {
                            Intent currIntent = getIntent();
                            Shop shop = new Shop(
                                    currIntent.getStringExtra("EXTRA_DESC"),
                                    currIntent.getStringExtra("EXTRA_NAME"),
                                    currIntent.getStringExtra("EXTRA_EMAIL"),
                                    currIntent.getStringExtra("EXTRA_PHONE"),
                                    currIntent.getStringExtra("EXTRA_WEBSITE"),
                                    (Address) currIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ"));
                            for (EditText time : startTimes)
                                shop.addStartTime(time.getText().toString());
                            for (EditText time : endTimes)
                                shop.addEndTime(time.getText().toString());
                            AppUser user = null;
                            RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
                            for (AppUser u : users) {
                                if (u.getEmail().equals(ShopSmartApp.email)) user = u;
                            }
                            realm.insert(shop);
                            user.addShop(shop.getId());
                        });
                        startActivity(new Intent(ShopRegister3.this, ShopListActivity.class)
                                .putExtra("EXTRA_REGISTER_SHOP_SUCCESS", true));
                    }
                });
            }
        });

        binding.btnCancel.setOnClickListener(view -> {
            Intent nextSignUpScreen = new Intent(ShopRegister3.this, ShopRegister2.class);
            nextSignUpScreen.putExtra("EXTRA_NAME", getIntent().getStringExtra("EXTRA_NAME"));
            nextSignUpScreen.putExtra("EXTRA_DESC", getIntent().getStringExtra("EXTRA_DESC"));
            nextSignUpScreen.putExtra("EXTRA_EMAIL", getIntent().getStringExtra("EXTRA_EMAIL"));
            nextSignUpScreen.putExtra("EXTRA_PHONE", getIntent().getStringExtra("EXTRA_PHONE"));
            nextSignUpScreen.putExtra("EXTRA_WEBSITE", getIntent().getStringExtra("EXTRA_WEBSITE"));
            startActivity(nextSignUpScreen);
        });
    }

    private boolean validation() {
        boolean valid = true;
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
}