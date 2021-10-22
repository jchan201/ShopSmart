package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;

public class ShopRegister2 extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_register_activty2);
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }
}