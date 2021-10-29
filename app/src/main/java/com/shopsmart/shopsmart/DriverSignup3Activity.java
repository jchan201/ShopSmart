package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivityDriverSignup3Binding;

public class DriverSignup3Activity extends AppCompatActivity {
    ActivityDriverSignup3Binding binding;

    AppUser appUser;
    String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityDriverSignup3Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get Intent
        Intent currIntent = this.getIntent();

        if (currIntent != null) {
            // Grab objects from intent
            this.userPassword = currIntent.getStringExtra("EXTRA_PASSWORD");
            //this.appUser = (AppUser)currIntent.getSerializableExtra("EXTRA_APPUSER_OBJ");
        }
    }
}