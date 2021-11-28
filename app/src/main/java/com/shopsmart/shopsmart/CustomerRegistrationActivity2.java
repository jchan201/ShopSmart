package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerRegister2Binding;

public class CustomerRegistrationActivity2 extends AppCompatActivity {
    private CustomerRegister2Binding binding;
    private DatePickerDialog dpd;
    private Intent currentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerRegister2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentIntent = getIntent();

        // Date picker
        binding.dob.setText(makeDateString(1, 0, 2000));
        DatePickerDialog.OnDateSetListener dateSetListener =
                (datePicker, year, month, day) -> binding.dob.setText(makeDateString(day, month, year));
        dpd = new DatePickerDialog(this, dateSetListener, 2000, 0, 1);

        // Province spinner
        Spinner provSpinner = findViewById(R.id.provPicker);
        ArrayAdapter<CharSequence> provList = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
        provList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provSpinner.setAdapter(provList);

        binding.cancelButton2.setOnClickListener(view ->
                startActivity(new Intent(CustomerRegistrationActivity2.this, SignupActivity.class)));
        binding.nextButton2.setOnClickListener(view -> {
            if (validateData()) createUser();
        });
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

    public void openDatePicker(View view) {
        dpd.show();
    }

    private void createUser() {
        Address address = new Address();
        address.setCity(binding.city.getText().toString());
        address.setProvince(binding.provPicker.getSelectedItem().toString());
        address.setPostalCode(binding.zipCode.getText().toString());
        address.setAddress1(binding.address1.getText().toString());
        address.setAddress2(binding.address2.getText().toString());
        address.setCountry("Canada");

        Intent CRegister3 = new Intent(this, CustomerRegistrationActivity3.class);
        CRegister3.putExtra("EXTRA_ADDRESS_OBJ", address);
        CRegister3.putExtra("EXTRA_EMAIL", currentIntent.getStringExtra("EXTRA_EMAIL"));
        CRegister3.putExtra("EXTRA_PASSWORD", currentIntent.getStringExtra("EXTRA_PASSWORD"));
        CRegister3.putExtra("EXTRA_FNAME", binding.nameFirst.getText().toString());
        CRegister3.putExtra("EXTRA_MNAME", binding.nameMiddle.getText().toString());
        CRegister3.putExtra("EXTRA_LNAME", binding.nameLast.getText().toString());
        CRegister3.putExtra("EXTRA_PHONE", binding.phoneNum.getText().toString());
        CRegister3.putExtra("EXTRA_DATE", binding.dob.getText().toString());
        startActivity(CRegister3);
    }

    private boolean validateData() {
        boolean valid = true;
        if (binding.nameFirst.getText().toString().isEmpty()) {
            binding.nameFirst.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.nameLast.getText().toString().isEmpty()) {
            binding.nameLast.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.city.getText().toString().isEmpty()) {
            binding.city.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.zipCode.getText().toString().isEmpty()) {
            binding.zipCode.setError("Field cannot be empty");
            valid = false;
        }
        else if (!binding.zipCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            binding.zipCode.setError("Postal code must match schema A1A 1A1");
            valid = false;
        }
        if (binding.address1.getText().toString().isEmpty()) {
            binding.address1.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.phoneNum.getText().toString().isEmpty()) {
            binding.phoneNum.setError("Phone number cannot be empty");
            valid = false;
        }
        else if (!binding.phoneNum.getText().toString().matches("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d")) {
            binding.phoneNum.setError("Must contain 10 digits");
            valid = false;
        }
        return valid;
    }
}
