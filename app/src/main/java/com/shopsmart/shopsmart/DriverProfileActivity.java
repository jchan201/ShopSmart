package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivityDriverProfileBinding;

public class DriverProfileActivity extends AppCompatActivity {
    ActivityDriverProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityDriverProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}