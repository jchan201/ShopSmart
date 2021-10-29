package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivityDriverSignupBinding;

public class DriverSignupActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityDriverSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup Viewbinding
        this.binding = ActivityDriverSignupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Set on click listeners
        this.binding.btnCancel.setOnClickListener(this);
        this.binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.btn_cancel: {
                    // Go back to login page
                    Intent mainIntent = new Intent(this, StartupActivity.class);
                    startActivity(mainIntent);
                    break;
                }
                case R.id.btn_next: {
                    if (this.validateData()) {
                        this.makeDriverAppUser();
                    }
                    break;
                }
            } // end of switch
        }
    }

    private void makeDriverAppUser() {
        AppUser appUser = new AppUser();
        appUser.setUserType("Driver");
        appUser.setEmail(this.binding.editEmail.getText().toString());

        goToNextSignupPage(appUser);
    }

    private void goToNextSignupPage(AppUser appUser) {
        Intent nextSignUpIntent = new Intent(this, DriverSignup2Activity.class);
        // Put AppUser object, username, and password into the intent
        //nextSignUpIntent.putExtra("EXTRA_APPUSER_OBJ", appUser);
        nextSignUpIntent.putExtra("EXTRA_PASSWORD", this.binding.editPassword.getText().toString());

        startActivity(nextSignUpIntent);
    }

    private boolean validateData() {
        boolean validUser = true;

        // Check if password fields are the same
        if (!this.binding.editPassword.getText().toString().equals(this.binding.editConfirmPassword.getText().toString())) {
            this.binding.editPassword.setError("Both passwords must be the same");
            this.binding.editConfirmPassword.setError("Both passwords must be the same");
            validUser = false;
        }

        return validUser;
    }
} // end of class