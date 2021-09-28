package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivityDriverDashboardBinding;

public class DriverDashboardActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityDriverDashboardBinding binding;
    AppUser appUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityDriverDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get Intent
        Intent currIntent = this.getIntent();

        if (currIntent != null) {
            // Grab objects from intent
            this.appUser = (AppUser)currIntent.getSerializableExtra("EXTRA_APPUSER_OBJ");
        }
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.btn_logout: {
                    // Go back to home screen
                    Intent startupIntent = new Intent(this, StartupActivity.class);
                    startActivity(startupIntent);
                    break;
                }
                case R.id.btn_profile: {
                    // Go to Driver profile
                    Intent profileIntent = new Intent(this, DriverProfileActivity.class);
                    profileIntent.putExtra("EXTRA_APPUSER_OBJ", this.appUser);
                    startActivity(profileIntent);
                    break;
                }
            }
        }
    }


} // end of class