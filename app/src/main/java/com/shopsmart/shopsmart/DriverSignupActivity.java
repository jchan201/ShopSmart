package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shopsmart.shopsmart.databinding.ActivityDriverSignupBinding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.auth.EmailPasswordAuth;
import io.realm.mongodb.sync.SyncConfiguration;

public class DriverSignupActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityDriverSignupBinding binding;
    private App app;

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
                        this.goToNextSignupPage();
                    }
                    break;
                }
            } // end of switch
        }
    }

    private void goToNextSignupPage() {
        Intent nextSignUpIntent = new Intent(this, DriverSignup2Activity.class);
        nextSignUpIntent.putExtra("EXTRA_EMAIL", this.binding.editEmail.getText().toString());
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