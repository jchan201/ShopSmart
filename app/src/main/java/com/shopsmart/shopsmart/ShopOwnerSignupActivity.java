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
    private Button dateButton;
    private ShopownerSignupActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerSignupActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //cancel go back sign up selection page
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopOwnerSignupActivity.this, SignupActivity.class));
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(ShopOwnerSignupActivity.this, ShopOwnerSignupActivity2.class));
                if(validation()){
                    Intent nextSignUpScreen = new Intent(ShopOwnerSignupActivity.this, ShopOwnerSignupActivity2.class);
                    nextSignUpScreen.putExtra("EXTRA_FNAME",binding.edtTextFName.getText().toString());
                    nextSignUpScreen.putExtra("EXTRA_MNAME", binding.edtTextInitial.getText().toString());
                    nextSignUpScreen.putExtra("EXTRA_LNAME", binding.edtTextLName.getText().toString());
                    nextSignUpScreen.putExtra("EXTRA_EMAIL", binding.editTextEmailAddress.getText().toString());
                    nextSignUpScreen.putExtra("EXTRA_PASSWORD", binding.editTextPassword.getText().toString());
                    nextSignUpScreen.putExtra("EXTRA_DOB", dateButton.getText().toString());
                    startActivity(nextSignUpScreen);
                }
            }
        });

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodayDate());
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

    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = makeDateString(day,month,year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
        if(month == 1){
            return "JAN";
        }
        if(month == 2){
            return "FEB";
        }
        if(month == 3){
            return "MAR";
        }
        if(month == 4){
            return "APR";
        }
        if(month == 5){
            return "MAY";
        }
        if(month == 6){
            return "JUN";
        }
        if(month == 7){
            return "JUL";
        }
        if(month == 8){
            return "AUG";
        }
        if(month == 9){
            return "SEP";
        }
        if(month == 10){
            return "OCT";
        }
        if(month == 11){
            return "NOV";
        }
        if(month == 12) {
            return "DEC";
        }

        return "JAN";

    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }
}