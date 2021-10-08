package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerRegister2Binding;

import java.util.Calendar;

public class CustomerRegistrationActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private CustomerRegister2Binding binding;
    private DatePickerDialog dpd;
    private Button dateButton;
    String currEmail;
    String currPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = CustomerRegister2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent currentIntent = this.getIntent();

        if (currentIntent != null) {
            this.currEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.currPassword = currentIntent.getStringExtra("EXTRA_PASSWORD");
        }

        initDatePicker();
        dateButton = findViewById(R.id.dob);
        dateButton.setText(todaysDate());
        Spinner provSpinner = findViewById(R.id.provPicker);

        ArrayAdapter<CharSequence> provList = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
        provList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provSpinner.setAdapter(provList);
        provSpinner.setOnItemSelectedListener(this);

        //this.binding.cancelButton2.setOnClickListener(this);
        //this.binding.nextButton2.setOnClickListener(this);

        binding.cancelButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(validateData()){
                createUser();
                startActivity(new Intent(CustomerRegistrationActivity2.this, SignupActivity.class));
                //}
            }
        });

        binding.nextButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    createUser();
                }
            }
        });
    }

    private String todaysDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900, 0, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };

        Calendar calendar = Calendar.getInstance();
        calendar.set(1900, 0, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = R.style.Theme_MaterialComponents_Dialog_Alert;

        dpd = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
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
            default:
                return "";
        }
    }

    public void openDatePicker(View view) {
        dpd.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        if (i != 0)
            Toast.makeText(getApplication(), choice + " selected", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(getApplication(), "Please select a province/territory", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void createUser() {
        Address address = new Address();
        address.setCity(this.binding.city.getText().toString());
        address.setProvince(this.binding.provPicker.getSelectedItem().toString());
        address.setPostalCode(this.binding.zipCode.getText().toString());
        address.setAddress1(this.binding.address1.getText().toString());
        address.setAddress2(this.binding.address2.getText().toString());
        address.setCountry("Canada");


        Intent CRegister3 = new Intent(this, CustomerRegistrationActivity3.class);
        CRegister3.putExtra("EXTRA_ADDRESS_OBJ", address);
        CRegister3.putExtra("EXTRA_EMAIL", this.currEmail);
        CRegister3.putExtra("EXTRA_PASSWORD", this.currPassword);
        CRegister3.putExtra("EXTRA_FNAME", this.binding.nameFirst.getText().toString());
        CRegister3.putExtra("EXTRA_MNAME", this.binding.nameMiddle.getText().toString());
        CRegister3.putExtra("EXTRA_LNAME", this.binding.nameLast.getText().toString());
        CRegister3.putExtra("EXTRA_PHONE", this.binding.phoneNum.getText().toString());
        CRegister3.putExtra("EXTRA_DATE", this.binding.dob.getText().toString());
        startActivity(CRegister3);
    }

    private boolean validateData() {
        boolean valid = true;
        if (this.binding.nameFirst.getText().toString().isEmpty()) {
            this.binding.nameFirst.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.nameLast.getText().toString().isEmpty()) {
            this.binding.nameLast.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.city.getText().toString().isEmpty()) {
            this.binding.city.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.zipCode.getText().toString().isEmpty()) {
            this.binding.zipCode.setError("Field cannot be empty");
            valid = false;
        }
        if (!this.binding.zipCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            this.binding.zipCode.setError("Postal code must match schema A1A 1A1");
            valid = false;
        }
        if (this.binding.address1.getText().toString().isEmpty()) {
            this.binding.address1.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.phoneNum.getText().toString().isEmpty()) {
            this.binding.phoneNum.setError("Phone number cannot be empty");
            valid = false;
        }
        if (!this.binding.phoneNum.getText().toString().matches("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d")) {
            this.binding.phoneNum.setError("Must contain 10 digits");
            valid = false;
        }
        //|| this.binding.address2.getText().toString().isEmpty()
        //|| this.binding.phoneNum.getText().toString().isEmpty()){

        //Toast.makeText(CustomerRegistrationActivity2.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        //}
        return valid;
    }
}
