package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivity3Binding;

import java.util.ArrayList;

public class ShopRegister3 extends AppCompatActivity {
    ShopRegisterActivity3Binding binding;
    Address shopAddress;
    String shopName;
    String shopDesc;
    String shopEmail;
    String shopPhone;
    String shopWebsite;
    CheckBox monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    ArrayList<EditText> startTimes = new ArrayList<>();
    ArrayList<EditText> endTimes = new ArrayList<>();

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
        }

        monday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtMonday1.setVisibility(View.GONE);
                binding.edtTxtMonday2.setVisibility(View.GONE);
            } else {
                binding.edtTxtMonday1.setVisibility(View.VISIBLE);
                binding.edtTxtMonday2.setVisibility(View.VISIBLE);
            }
        });
        tuesday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtTuesday1.setVisibility(View.GONE);
                binding.edtTxtTuesday2.setVisibility(View.GONE);
            } else {
                binding.edtTxtTuesday1.setVisibility(View.VISIBLE);
                binding.edtTxtTuesday2.setVisibility(View.VISIBLE);
            }
        });
        wednesday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtWednesday1.setVisibility(View.GONE);
                binding.edtTxtWednesday2.setVisibility(View.GONE);
            } else {
                binding.edtTxtWednesday1.setVisibility(View.VISIBLE);
                binding.edtTxtWednesday2.setVisibility(View.VISIBLE);
            }
        });
        thursday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtThursday1.setVisibility(View.GONE);
                binding.edtTxtThursday2.setVisibility(View.GONE);
            } else {
                binding.edtTxtThursday1.setVisibility(View.VISIBLE);
                binding.edtTxtThursday2.setVisibility(View.VISIBLE);
            }
        });
        friday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtFriday1.setVisibility(View.GONE);
                binding.edtTxtFriday2.setVisibility(View.GONE);
            } else {
                binding.edtTxtFriday1.setVisibility(View.VISIBLE);
                binding.edtTxtFriday2.setVisibility(View.VISIBLE);
            }
        });
        saturday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtSaturday1.setVisibility(View.GONE);
                binding.edtTxtSaturday2.setVisibility(View.GONE);
            } else {
                binding.edtTxtSaturday1.setVisibility(View.VISIBLE);
                binding.edtTxtSaturday2.setVisibility(View.VISIBLE);
            }
        });
        sunday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                binding.edtTxtSunday1.setVisibility(View.GONE);
                binding.edtTxtSunday2.setVisibility(View.GONE);
            } else {
                binding.edtTxtSunday1.setVisibility(View.VISIBLE);
                binding.edtTxtSunday2.setVisibility(View.VISIBLE);
            }
        });

        startTimes.add(binding.edtTxtMonday1);
        endTimes.add(binding.edtTxtMonday2);
        startTimes.add(binding.edtTxtTuesday1);
        endTimes.add(binding.edtTxtTuesday2);
        startTimes.add(binding.edtTxtWednesday1);
        endTimes.add(binding.edtTxtWednesday2);
        startTimes.add(binding.edtTxtThursday1);
        endTimes.add(binding.edtTxtThursday2);
        startTimes.add(binding.edtTxtFriday1);
        endTimes.add(binding.edtTxtFriday2);
        startTimes.add(binding.edtTxtSaturday1);
        endTimes.add(binding.edtTxtSaturday2);
        startTimes.add(binding.edtTxtSunday1);
        endTimes.add(binding.edtTxtSunday2);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    Shop shop = new Shop(shopDesc, shopName, shopEmail, shopPhone, shopWebsite, shopAddress);
                    for (EditText time : startTimes) {
                        shop.addStartTime(time.getText().toString());
                    }
                    for (EditText time : endTimes) {
                        shop.addEndTime(time.getText().toString());
                    }
                }
            }
        });

        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopRegister3.this, ShopRegister.class)));
    }

    private boolean validation() {
        boolean valid = true;
        String regex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
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