package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerResetPasswordBinding;

import io.realm.RealmResults;

public class CustomerPasswordActivity extends AppCompatActivity {
    private CustomerResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email)) {
                        user = users.get(i);
                    }
                }
                if (user != null) binding.queryUser.setText(user.getEmail());
            }
        });
        binding.cancelButton.setOnClickListener(view ->
                startActivity(new Intent(CustomerPasswordActivity.this, CustomerManageProfileActivity.class))
        );
        binding.confirmButton.setOnClickListener(view -> {
            if (validation()) {
                String email = ShopSmartApp.email;
                String password = binding.password1.getText().toString();
                ShopSmartApp.app.getEmailPassword().callResetPasswordFunctionAsync(
                        email,
                        password, new String[]{}, it -> {
                            if (it.isSuccess()) {
                                Log.i("PASS_RESET", "Successfully reset the password for " + ShopSmartApp.email);
                                ShopSmartApp.password = password;
                                ShopSmartApp.logout();
                                ShopSmartApp.login(email, password);
                                startActivity(new Intent(CustomerPasswordActivity.this, CustomerManageProfileActivity.class)
                                        .putExtra("EXTRA_RESET_PASSWORD_SUCCESS", true));
                            } else
                                Log.e("PASS_RESET", "Failed to reset the password for " + ShopSmartApp.email + ": " + it.getError().getErrorMessage());
                        });
            }
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