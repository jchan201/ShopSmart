package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerResetPasswordBinding;
import com.shopsmart.shopsmart.databinding.ShopownerProfileDetailResetPasswordActivityBinding;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.SyncConfiguration;

public class CustomerPasswordActivity extends AppCompatActivity {
    private CustomerResetPasswordBinding binding;

    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

        binding.cancelButton.setOnClickListener(view -> {
                    Intent intentToProfile = new Intent(CustomerPasswordActivity.this, CustomerDashboardActivity.class);
                    intentToProfile.putExtra("EXTRA_PASS", userPass);
                    intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(intentToProfile);
                }
        );

        binding.confirmButton.setOnClickListener(view -> {
            if(validation()) {
                updatePassword();
            }
        });
    }

    private void updatePassword() {
        String[] args = {};
        String newPassword = this.binding.password1.getText().toString();
        app.getEmailPassword().callResetPasswordFunctionAsync(userEmail, newPassword, args, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully reset the password for " + userPass);

                Intent intentToProfile = new Intent(CustomerPasswordActivity.this, CustomerManageProfileActivity.class);
                intentToProfile.putExtra("EXTRA_PASS", newPassword);
                intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                //intentToProfile.putExtra("EXTRA_RESET_PASSWORD_SUCCESS", true);
                startActivity(intentToProfile);
            } else {
                Log.e("EXAMPLE", "Failed to reset the password for " + userEmail + ": " + it.getError().getErrorMessage());
                Toast.makeText(CustomerPasswordActivity.this, "Failed to reset the password: " + it.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.inputOldPassword.getText().toString().isEmpty()) {
            this.binding.inputOldPassword.setError("Old password cannot be empty");
            valid = false;
        }

        if (!this.binding.inputOldPassword.getText().toString().equals(userPass)) {
            this.binding.inputOldPassword.setError("Old password incorrect");
            valid = false;
        }

        if (this.binding.password1.getText().toString().isEmpty()) {
            this.binding.password1.setError("New password cannot be empty");
            valid = false;
        }

        if (this.binding.password1.getText().toString().equals(userPass)) {
            this.binding.password1.setError("New password cannot be the same as old password");
            valid = false;
        }

        if(this.binding.password1.getText().toString().length() < 6){
            this.binding.password1.setError("Password must contain more than 6 or more characters");
            valid = false;
        }

        else if(!this.binding.password1.getText().toString().matches("(.*[A-Z].*)") || !this.binding.password1.getText().toString().matches("(.*[0-9].*)")){
            this.binding.password1.setError("Password must contain 1 Uppercase and 1 Number");
            valid = false;
        }

        if (this.binding.password2.getText().toString().isEmpty()) {
            this.binding.password2.setError("Confirm new password cannot be empty");
            valid = false;
        }

        if (!this.binding.password2.getText().toString().equals(this.binding.password1.getText().toString())) {
            this.binding.password2.setError("Confirm new password incorrect");
            valid = false;
        }

        return valid;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.Profile:
                Intent settingsIntent = new Intent(CustomerPasswordActivity.this, CustomerManageProfileActivity.class);
                settingsIntent.putExtra("EXTRA_EMAIL", userEmail);
                settingsIntent.putExtra("EXTRA_PASS", userPass);
                Toast.makeText(CustomerPasswordActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                startActivity(settingsIntent);
                //finish();
                break;
            case R.id.menuHome:
                Intent homeIntent = new Intent(CustomerPasswordActivity.this, CustomerDashboardActivity.class);
                homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                homeIntent.putExtra("EXTRA_PASS", userPass);
                startActivity(homeIntent);
        }
        return true;
    }
}