package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;

import com.shopsmart.shopsmart.databinding.ActivityDriverSignup2Binding;
import com.shopsmart.shopsmart.databinding.ActivityDriverSignupBinding;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DriverSignup2Activity extends AppCompatActivity implements View.OnClickListener {
    ActivityDriverSignup2Binding binding;
    AppUser appUser;
    String userPassword;

    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup ViewBinding
        this.binding = ActivityDriverSignup2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        appUser = new AppUser();

        // Grab object from incoming Intent
        Intent currIntent = this.getIntent();

        if (currIntent != null) {
            // Grab objects from intent
            this.userPassword = currIntent.getStringExtra("EXTRA_PASSWORD");
            this.appUser = (AppUser)currIntent.getSerializableExtra("EXTRA_APPUSER_OBJ");
        }

        // Initialize Date Picker
        initDatePicker();

        // Set Click Listeners
        this.binding.btnCancel.setOnClickListener(this);
        this.binding.btnNext.setOnClickListener(this);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(year, month, day);
                binding.btnDatePicker.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    private String makeDateString(int year, int month, int day) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        switch(month) {
            case 1:
                return "JAN";
            case 2:
                return "FEB";
            case 3:
                return "MAR";
            case 4:
                return "APR";
            case 5:
                return "MAY";
            case 6:
                return "JUN";
            case 7:
                return "JUL";
            case 8:
                return "AUG";
            case 9:
                return "SEP";
            case 10:
                return "OCT";
            case 11:
                return "NOV";
            case 12:
                return "DEC";
        }

        // Default
        return "JAN";
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
                    // Validate data
                    if (this.validateData()) {
                        // Update AppUser object and pass to next screen
                        this.appUser.setFirstName(this.binding.editFname.getText().toString());
                        this.appUser.setMiddleInitial(this.binding.editMiddleInitial.getText().toString());
                        this.appUser.setLastName(this.binding.editLname.getText().toString());
                        this.appUser.setPhone(this.binding.editPhone.getText().toString());

                        Address address = new Address();
                        address.setCity(this.binding.editCity.getText().toString());
                        address.setProvince(this.binding.spinnerProvince.getSelectedItem().toString());
                        address.setPostalCode(this.binding.editZipCode.getText().toString());
                        this.appUser.addAddress(address);

                        Intent nextSignUpScreen = new Intent(this, DriverSignup3Activity.class);
                        nextSignUpScreen.putExtra("EXTRA_APPUSER_OBJ", this.appUser);
                        nextSignUpScreen.putExtra("EXTRA_PASSWORD", this.userPassword);
                        startActivity(nextSignUpScreen);
                    }
                    break;
                }
            } // end of switch
        }
    }

    private boolean validateData() {
        boolean validUser = true;

        // Check for empty fields
        if (this.binding.editFname.getText().toString().isEmpty()) {
            this.binding.editFname.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editLname.getText().toString().isEmpty()) {
            this.binding.editLname.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editCity.getText().toString().isEmpty()) {
            this.binding.editCity.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editZipCode.getText().toString().isEmpty()) {
            this.binding.editZipCode.setError("Cannot be empty");
            validUser = false;
        }
        if (this.binding.editAddress1.getText().toString().isEmpty()) {
            this.binding.editZipCode.setError("Cannot be empty");
            validUser = false;
        }

        return validUser;
    }

} // end of class