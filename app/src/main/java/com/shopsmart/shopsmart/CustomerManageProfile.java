package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerRegister1Binding;

public class CustomerManageProfile extends AppCompatActivity implements View.OnClickListener {
    CustomerRegister1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = CustomerRegister1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.binding.cancelButton.setOnClickListener(this);
        this.binding.nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.cancelButton: {
                    Intent mainIntent = new Intent(this, StartupActivity.class);
                    startActivity(mainIntent);
                    break;
                }
                case R.id.nextButton: {
                    if (this.validateData()) {
                        this.writeDataUser();
                    } else {
                        Toast.makeText(getApplicationContext(), "Both Passwords must be the same", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private boolean validateData() {

        if (this.binding.email.getText().toString().isEmpty()) {
            Toast.makeText(CustomerManageProfile.this, "Must have an email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!this.binding.password1.getText().toString().equals(this.binding.password2.getText().toString())) {
            Toast.makeText(CustomerManageProfile.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (this.binding.password1.getText().toString().isEmpty() && this.binding.password2.getText().toString().isEmpty()) {
            Toast.makeText(CustomerManageProfile.this, "Must have a password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (this.binding.password1.getText().toString().length() < 8) {
            Toast.makeText(CustomerManageProfile.this, "Password must contain 8 or more characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!this.binding.password1.getText().toString().matches("(.*[A-Z].*)") || !this.binding.password1.getText().toString().matches("(.*[0-9].*)")) {
            Toast.makeText(CustomerManageProfile.this, "Password must contain 1 Uppercase and 1 Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void writeDataUser() {
        Intent CRegister2 = new Intent(this, CustomerRegistrationActivity2.class);
        CRegister2.putExtra("EXTRA_EMAIL", this.binding.email.getText().toString());
        CRegister2.putExtra("EXTRA_PASSWORD", this.binding.password1.getText().toString());
        startActivity(CRegister2);
    }
}
