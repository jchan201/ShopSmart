package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerResetPasswordBinding;

public class CustomerPasswordActivity extends AppCompatActivity {
    private CustomerResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cancelButton.setOnClickListener(view ->
                startActivity(new Intent(CustomerPasswordActivity.this, CustomerDashboardActivity.class))
        );
        binding.confirmButton.setOnClickListener(view -> {
            if (validation())
                updatePassword();
        });
    }

    private void updatePassword() {
        ShopSmartApp.app.getEmailPassword().callResetPasswordFunctionAsync(
                ShopSmartApp.email,
                binding.password1.getText().toString(), new String[] {}, it -> {
            if (it.isSuccess()) {
                Log.i("PASS_RESET", "Successfully reset the password for " + ShopSmartApp.email);
                startActivity(new Intent(CustomerPasswordActivity.this, CustomerManageProfileActivity.class));
            } else
                Log.e("PASS_RESET", "Failed to reset the password for " + ShopSmartApp.email + ": " + it.getError().getErrorMessage());
        });
    }

    private boolean validation() {
        boolean valid = true;
        if (binding.inputOldPassword.getText().toString().isEmpty()) {
            binding.inputOldPassword.setError("Old password cannot be empty");
            valid = false;
        }
        if (!binding.inputOldPassword.getText().toString().equals(ShopSmartApp.password)) {
            binding.inputOldPassword.setError("Old password incorrect");
            valid = false;
        }
        if (binding.password1.getText().toString().isEmpty()) {
            binding.password1.setError("New password cannot be empty");
            valid = false;
        }
        if (binding.password1.getText().toString().equals(ShopSmartApp.password)) {
            binding.password1.setError("New password cannot be the same as old password");
            valid = false;
        }
        if (binding.password1.getText().toString().length() < 6) {
            binding.password1.setError("Password must contain more than 6 or more characters");
            valid = false;
        } else if (!binding.password1.getText().toString().matches("(.*[A-Z].*)") || !binding.password1.getText().toString().matches("(.*[0-9].*)")) {
            binding.password1.setError("Password must contain 1 Uppercase and 1 Number");
            valid = false;
        }
        if (binding.password2.getText().toString().isEmpty()) {
            binding.password2.setError("Confirm new password cannot be empty");
            valid = false;
        }
        if (!binding.password2.getText().toString().equals(binding.password1.getText().toString())) {
            binding.password2.setError("Confirm new password incorrect");
            valid = false;
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Profile:
                startActivity(new Intent(CustomerPasswordActivity.this, CustomerManageProfileActivity.class));
                break;
            case R.id.menuHome:
                startActivity(new Intent(CustomerPasswordActivity.this, CustomerDashboardActivity.class));
        }
        return true;
    }
}