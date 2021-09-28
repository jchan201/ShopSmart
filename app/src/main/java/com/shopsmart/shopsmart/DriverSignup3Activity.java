package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivityDriverSignup2Binding;
import com.shopsmart.shopsmart.databinding.ActivityDriverSignup3Binding;

public class DriverSignup3Activity extends AppCompatActivity implements View.OnClickListener {
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
            this.appUser = (AppUser)currIntent.getSerializableExtra("EXTRA_APPUSER_OBJ");
        }

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
                    // Validate data
                    if (this.validateData()) {
                        // Update AppUser Object
                        BankInformation bankInformation = new BankInformation();
                        bankInformation.setAccountNumber(this.binding.editAccountNumber.getText().toString());
                        bankInformation.setInstitutionNumber(this.binding.editInstitutionNumber.getText().toString());
                        bankInformation.setTransitNumber(this.binding.editTransitNumber.getText().toString());
                        this.createUser();

                        // Go to Dashboard
                        Intent dashboardIntent = new Intent(this, DriverDashboardActivity.class);
                        dashboardIntent.putExtra("EXTRA_APPUSER_OBJ", this.appUser);
                        startActivity(dashboardIntent);
                    }
                    break;
                }
            } // end of switch
        }
    }

    private void createUser() {
        // Create User and AppUser for database
    }

    private boolean validateData() {
        boolean validUser = true;

        if (this.binding.editAccountNumber.getText().toString().isEmpty()) {
            this.binding.editAccountNumber.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editInstitutionNumber.getText().toString().isEmpty()) {
            this.binding.editInstitutionNumber.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editTransitNumber.getText().toString().isEmpty()) {
            this.binding.editTransitNumber.setError("Cannot be empty");
            validUser = false;
        }

        return validUser;
    }
} // end of class