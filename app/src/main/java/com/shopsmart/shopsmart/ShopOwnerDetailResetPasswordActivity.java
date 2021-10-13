package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileDetailResetPasswordActivityBinding;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class ShopOwnerDetailResetPasswordActivity extends AppCompatActivity {
    private ShopownerProfileDetailResetPasswordActivityBinding binding;

    Intent currIntent;

    String userEmail;
    String userPass;

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileDetailResetPasswordActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

        binding.btnCancel.setOnClickListener(view -> {
                    Intent intentToProfile = new Intent(ShopOwnerDetailResetPasswordActivity.this, ShopOwnerProfileDetailActivity.class);
                    intentToProfile.putExtra("EXTRA_PASS", userPass);
                    intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                    startActivity(intentToProfile);
                }
        );

        binding.btnSave.setOnClickListener(view -> {
            if(validation()) {
                updatePassword();
            }
        });
    }

    private void updatePassword() {
        String[] args = {"Text1", "Text2", "Text3"};
        String newPassword = this.binding.textNewPassword.getText().toString();
        app.getEmailPassword().callResetPasswordFunctionAsync(userEmail, newPassword, args, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully reset the password for " + userPass);

                Intent intentToProfile = new Intent(ShopOwnerDetailResetPasswordActivity.this, ShopOwnerProfileDetailActivity.class);
                intentToProfile.putExtra("EXTRA_PASS", userPass);
                intentToProfile.putExtra("EXTRA_EMAIL", userEmail);
                intentToProfile.putExtra("EXTRA_RESET_PASSWORD_SUCCESS", true);
                startActivity(intentToProfile);
            } else {
                Log.e("EXAMPLE", "Failed to reset the password for " + userEmail + ": " + it.getError().getErrorMessage());
                Toast.makeText(ShopOwnerDetailResetPasswordActivity.this, "Failed to reset the password: " + it.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.textOldPassword.getText().toString().isEmpty()) {
            this.binding.textOldPassword.setError("Old password cannot be empty");
            valid = false;
        }

        if (!this.binding.textOldPassword.getText().toString().equals(userPass)) {
            this.binding.textOldPassword.setError("Old password incorrect");
            valid = false;
        }

        if (this.binding.textNewPassword.getText().toString().isEmpty()) {
            this.binding.textNewPassword.setError("New password cannot be empty");
            valid = false;
        }

        if (this.binding.textNewPassword.getText().toString().equals(userPass)) {
            this.binding.textNewPassword.setError("New password cannot be the same as old password");
            valid = false;
        }

        if(this.binding.textNewPassword.getText().toString().length() < 6){
            this.binding.textNewPassword.setError("Password must contain more than 6 or more characters");
            valid = false;
        }

        else if(!this.binding.textNewPassword.getText().toString().matches("(.*[A-Z].*)") || !this.binding.textNewPassword.getText().toString().matches("(.*[0-9].*)")){
            this.binding.textNewPassword.setError("Password must contain 1 Uppercase and 1 Number");
            valid = false;
        }

        if (this.binding.textConfirmNewPassword.getText().toString().isEmpty()) {
            this.binding.textConfirmNewPassword.setError("Confirm new password cannot be empty");
            valid = false;
        }

        if (!this.binding.textConfirmNewPassword.getText().toString().equals(this.binding.textNewPassword.getText().toString())) {
            this.binding.textConfirmNewPassword.setError("Confirm new password incorrect");
            valid = false;
        }

        return valid;
    }
}