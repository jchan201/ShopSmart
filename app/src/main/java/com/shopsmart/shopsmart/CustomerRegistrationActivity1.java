package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerRegister1Binding;

public class CustomerRegistrationActivity1 extends AppCompatActivity implements View.OnClickListener {
    private CustomerRegister1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerRegister1Binding.inflate(getLayoutInflater());
        binding.cancelButton.setOnClickListener(this);
        binding.nextButton.setOnClickListener(this);
        setContentView(binding.getRoot());
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            if (id == R.id.cancelButton)
                startActivity(new Intent(this, StartupActivity.class));
            else if (id == R.id.nextButton && validateData()) {
                Intent intent = new Intent(this, CustomerRegistrationActivity2.class);
                intent.putExtra("EXTRA_EMAIL", binding.email.getText().toString());
                intent.putExtra("EXTRA_PASSWORD", binding.password1.getText().toString());
                startActivity(intent);
            }
            else
                Toast.makeText(getApplicationContext(), "Both Passwords must be the same", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateData() {
        if (binding.email.getText().toString().isEmpty()) {
            Toast.makeText(CustomerRegistrationActivity1.this, "Must have an email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!binding.password1.getText().toString().equals(binding.password2.getText().toString())) {
            Toast.makeText(CustomerRegistrationActivity1.this, "Passwords must match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.password1.getText().toString().isEmpty() && binding.password2.getText().toString().isEmpty()) {
            Toast.makeText(CustomerRegistrationActivity1.this, "Must have a password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.password1.getText().toString().length() < 8) {
            Toast.makeText(CustomerRegistrationActivity1.this, "Password must contain 8 or more characters", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!binding.password1.getText().toString().matches("(.*[A-Z].*)") || !binding.password1.getText().toString().matches("(.*[0-9].*)")) {
            Toast.makeText(CustomerRegistrationActivity1.this, "Password must contain 1 Uppercase and 1 Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
