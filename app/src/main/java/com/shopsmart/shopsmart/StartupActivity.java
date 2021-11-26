package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ActivityStartupBinding;

import java.util.ArrayList;

import javax.annotation.Nullable;

import io.realm.RealmResults;

public class StartupActivity extends AppCompatActivity {
    private final ArrayList<String> ATTEMPTS = new ArrayList<>();
    private ActivityStartupBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //quick login
        binding.edtTxtEmail.setText("testestest");
        binding.edtTxtPassword.setText("abcABC123");

        Intent currIntent = getIntent();
        if (currIntent != null) {
            boolean signup_success = currIntent.getBooleanExtra("EXTRA_SIGNUP_SUCCESS", false);
            if (signup_success)
                Toast.makeText(StartupActivity.this, "Successfully registered.", Toast.LENGTH_SHORT).show();

            boolean delete_success = currIntent.getBooleanExtra("EXTRA_DELETE_PAYMENT_SUCCESS", false);
            if (delete_success)
                Toast.makeText(StartupActivity.this, "Successfully delete account.", Toast.LENGTH_SHORT).show();
        }

        // Watches changes in certain EditTexts.
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Hide error message if the text is changed.
                binding.txtError.setVisibility(View.INVISIBLE);
            }
        };
        binding.edtTxtEmail.addTextChangedListener(textWatcher); // Watch the email
        binding.edtTxtPassword.addTextChangedListener(textWatcher); // Watch the password

        // When the Login button is clicked.
        binding.btnLogin.setOnClickListener(view -> {
            // Get the user's credentials (email and password).
            String email = binding.edtTxtEmail.getText().toString();
            String password = binding.edtTxtPassword.getText().toString();
            if (email.isEmpty()) {
                binding.txtError.setText("Please enter an email address.");
                binding.txtError.setVisibility(View.VISIBLE);
            } else if (password.isEmpty()) {
                binding.txtError.setText("Please enter a password.");
                binding.txtError.setVisibility(View.VISIBLE);
            } else {
                ShopSmartApp.login(email, password);
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        // Retrieve all users in the Realm.
                        ShopSmartApp.instantiateRealm();
                        RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                        AppUser user = null;

                        // Find the AppUser
                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i).getEmail().equals(email)) {
                                user = users.get(i);
                            }
                        }

                        // Go to the dashboard depending on the AppUser's type.
                        String type = user.getUserType();
                        switch (type) {
                            case "Customer":
                                Intent customerDashboardActivity = new Intent(StartupActivity.this, CustomerDashboardActivity.class);
                                startActivity(customerDashboardActivity);
                                break;
                            case "Owner":
                                Intent intentToDashboard = new Intent(StartupActivity.this, ShopOwnerDashboardActivity.class);
                                startActivity(intentToDashboard);
                        }
                    } else {
                        // Show error message if login failed.
                        binding.txtError.setText("Invalid email/password.");
                        binding.txtError.setVisibility(View.VISIBLE);
                        ATTEMPTS.add(email);
                        if (ATTEMPTS.size() == 5) binding.btnLogin.setEnabled(false);
                    }
                });
            }
        });

        // When the Register button is clicked.
        binding.btnRegister.setOnClickListener(view -> {
            // Go to Signup Activity
            startActivity(new Intent(StartupActivity.this, SignupActivity.class));
        });
    }
}