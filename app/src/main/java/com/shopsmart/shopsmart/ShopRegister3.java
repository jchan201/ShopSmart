package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import com.shopsmart.shopsmart.databinding.ShopRegisterActivity3Binding;

public class ShopRegister3 extends AppCompatActivity {
    ShopRegisterActivity3Binding binding;
    Address address;
    String shopName;
    String shopDesc;
    String shopEmail;
    String shopPhone;
    String shopWebsite;
    CheckBox monStart, monEnd, tueStart, tueEnd, wedStart, wedEnd, thuStart, thuEnd, friStart,
            friEnd, satStart, satEnd, sunStart, sunEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopRegisterActivity3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = this.getIntent();
        if (currIntent != null) {
            this.address = (Address) currIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ");
            this.shopName = currIntent.getStringExtra("EXTRA_NAME");
            this.shopDesc = currIntent.getStringExtra("EXTRA_DESC");
            this.shopEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.shopPhone = currIntent.getStringExtra("EXTRA_PHONE");
            this.shopWebsite = currIntent.getStringExtra("EXTRA_WEBSITE");
        };


    }

    private boolean validation() {
        boolean valid = true;

        return valid;
    }
}