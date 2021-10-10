package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.shopsmart.shopsmart.databinding.ShopownerSignupActivityBinding;

import java.util.Calendar;

public class ShopOwnerSignupActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private ShopownerSignupActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerSignupActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Date picker
        binding.datePickerButton.setText(makeDateString(1, 0, 2000));
        DatePickerDialog.OnDateSetListener dateSetListener =
                (datePicker, year, month, day) -> binding.datePickerButton.setText(makeDateString(day, month, year));
        datePickerDialog = new DatePickerDialog(this, dateSetListener, 2000, 0, 1);

        //cancel go back sign up selection page
        binding.buttonCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerSignupActivity.this, SignupActivity.class)));

        //next
        binding.buttonNext.setOnClickListener(view -> {
            if(validation()){
                Intent nextSignUpScreen = new Intent(ShopOwnerSignupActivity.this, ShopOwnerSignupActivity2.class);
                nextSignUpScreen.putExtra("EXTRA_FNAME",binding.edtTextFName.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_MNAME", binding.edtTextInitial.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_LNAME", binding.edtTextLName.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_EMAIL", binding.editTextEmailAddress.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_PASSWORD", binding.editTextPassword.getText().toString());
                nextSignUpScreen.putExtra("EXTRA_DOB", binding.datePickerButton.getText().toString());
                startActivity(nextSignUpScreen);
            }
        });
    }

    private boolean validation(){
        boolean valid = true;

        if(this.binding.edtTextFName.getText().toString().isEmpty()){
            this.binding.edtTextFName.setError("First name cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextLName.getText().toString().isEmpty()){
            this.binding.edtTextLName.setError("Last name cannot be empty");
            valid = false;
        }

        if(this.binding.editTextEmailAddress.getText().toString().isEmpty()){
            this.binding.editTextEmailAddress.setError("Email cannot be empty");
            valid = false;
        }

        if(this.binding.editTextEmailAddress.getText().toString().isEmpty()){
            this.binding.editTextEmailAddress.setError("Email cannot be empty");
            valid = false;
        }

        if(this.binding.editTextPassword.getText().toString().isEmpty()){
            this.binding.editTextPassword.setError("Password cannot be empty");
            valid = false;
        }

        if(this.binding.editTextPassword.getText().toString().length() < 6){
            this.binding.editTextPassword.setError("Password must contain more than 6 or more characters");
            valid = false;
        }

        else if(!this.binding.editTextPassword.getText().toString().matches("(.*[A-Z].*)") || !this.binding.editTextPassword.getText().toString().matches("(.*[0-9].*)")){
            this.binding.editTextPassword.setError("Password must contain 1 Uppercase and 1 Number");
            valid = false;
        }

        if(this.binding.editTextConfirmPassword.getText().toString().isEmpty()){
            this.binding.editTextConfirmPassword.setError("Confirm password cannot be empty");
            valid = false;
        }

        if(!this.binding.editTextPassword.getText().toString().equals(this.binding.editTextConfirmPassword.getText().toString())){
            this.binding.editTextConfirmPassword.setError("Confirm password does not match password");
            valid = false;
        }

        return valid;
    }

    // Date Functions
    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }
    private String getMonthFormat(int month) {
        switch (month) {
            case 0:
                return "JAN";
            case 1:
                return "FEB";
            case 2:
                return "MAR";
            case 3:
                return "APR";
            case 4:
                return "MAY";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AUG";
            case 8:
                return "SEP";
            case 9:
                return "OCT";
            case 10:
                return "NOV";
            case 11:
                return "DEC";
            default:
                return "";
        }
    }
    public void openDatePicker(View view){
        datePickerDialog.show();
    }
}