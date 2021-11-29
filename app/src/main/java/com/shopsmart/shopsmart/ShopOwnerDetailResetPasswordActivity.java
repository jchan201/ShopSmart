package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileDetailResetPasswordActivityBinding;

public class ShopOwnerDetailResetPasswordActivity extends AppCompatActivity {
    private ShopownerProfileDetailResetPasswordActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileDetailResetPasswordActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerDetailResetPasswordActivity.this, ShopOwnerProfileDetailActivity.class))
        );

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                String email = ShopSmartApp.email;
                String password = binding.textNewPassword.getText().toString();
                ShopSmartApp.app.getEmailPassword().callResetPasswordFunctionAsync(
                        email,
                        password, new String[]{}, it -> {
                            if (it.isSuccess()) {
                                Log.i("PASS_RESET", "Successfully reset the password for " + ShopSmartApp.email);
                                ShopSmartApp.password = password;
                                ShopSmartApp.logout();
                                ShopSmartApp.login(email, password);
                                startActivity(new Intent(ShopOwnerDetailResetPasswordActivity.this, ShopOwnerProfileDetailActivity.class)
                                        .putExtra("EXTRA_RESET_PASSWORD_SUCCESS", true));
                            } else
                                Log.e("PASS_RESET", "Failed to reset the password for " + ShopSmartApp.email + ": " + it.getError().getErrorMessage());
                        });
            }
        });
    }

    private boolean validation() {
        boolean valid = true;
        if (this.binding.textOldPassword.getText().toString().isEmpty()) {
            this.binding.textOldPassword.setError("Old password cannot be empty");
            valid = false;
        }
        if (!this.binding.textOldPassword.getText().toString().equals(ShopSmartApp.password)) {
            this.binding.textOldPassword.setError("Old password incorrect");
            valid = false;
        }
        if (this.binding.textNewPassword.getText().toString().isEmpty()) {
            this.binding.textNewPassword.setError("New password cannot be empty");
            valid = false;
        }
        if (this.binding.textNewPassword.getText().toString().equals(ShopSmartApp.password)) {
            this.binding.textNewPassword.setError("New password cannot be the same as old password");
            valid = false;
        }
        if (this.binding.textNewPassword.getText().toString().length() < 6) {
            this.binding.textNewPassword.setError("Password must contain more than 6 or more characters");
            valid = false;
        } else if (!this.binding.textNewPassword.getText().toString().matches("(.*[A-Z].*)")
                || !this.binding.textNewPassword.getText().toString().matches("(.*[0-9].*)")) {
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