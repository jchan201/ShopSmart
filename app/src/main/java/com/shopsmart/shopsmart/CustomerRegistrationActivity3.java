package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerRegister3Binding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomerRegistrationActivity3 extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private CustomerRegister3Binding binding;
    private Intent currentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerRegister3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentIntent = getIntent();

        Spinner provSpinner = findViewById(R.id.provPicker3);
        ArrayAdapter<CharSequence> provList = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
        provList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provSpinner.setAdapter(provList);
        provSpinner.setOnItemSelectedListener(this);

        binding.cancelButton3.setOnClickListener(this);
        binding.finishButton.setOnClickListener(this);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        if (i != 0)
            Toast.makeText(getApplication(), choice + " selected", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplication(), "Please select a province/territory", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            if (id == R.id.cancelButton3)
                startActivity(new Intent(this, StartupActivity.class));
            else if (id == R.id.finishButton && validateData())
                createUser();
        }
    }

    private boolean validateData() {
        boolean valid = true;
        if (binding.cCardName.getText().toString().isEmpty()) {
            binding.cCardName.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.cCardPhoneNum.getText().toString().isEmpty()) {
            binding.cCardPhoneNum.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.cCardCCV.getText().toString().isEmpty()) {
            binding.cCardCCV.setError("Field cannot be empty");
            valid = false;
        }

        if (binding.cCardCCV.getText().toString().length() < 3) {
            binding.cCardCCV.setError("Field must be 3 digits long");
            valid = false;
        }

        if (binding.cCardPhoneNum.getText().toString().length() < 9) {
            binding.cCardPhoneNum.setError("Field must be 10 digits long");
            valid = false;
        }

        Calendar todaysDate = Calendar.getInstance();
        if (!binding.expY.getText().toString().isEmpty() && !binding.expM.getText().toString().isEmpty()) {
            if (Long.parseLong(binding.expY.getText().toString()) + 2000 + (Long.parseLong(binding.expM.getText().toString()) / 12) < todaysDate.get(Calendar.YEAR) + ((todaysDate.get(Calendar.MONTH) + 1) / 12)) {
                binding.expY.setError("Please enter a valid date");
                valid = false;
            }
            else if (Long.parseLong(binding.expM.getText().toString()) > 12) {
                binding.expM.setError("Please enter a valid date" + todaysDate.get(Calendar.YEAR));
                valid = false;
            }
        }
        if (binding.expY.getText().toString().isEmpty()) {
            binding.expY.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.expM.getText().toString().isEmpty()) {
            binding.expM.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.cCardPostalCode.getText().toString().isEmpty()) {
            binding.cCardPostalCode.setError("Field cannot be empty");
            valid = false;
        }
        if (!binding.cCardPostalCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            binding.cCardPostalCode.setError("Postal code must match schema A1A 1A1");
            valid = false;
        }
        if (binding.cCardCity.getText().toString().isEmpty()) {
            binding.cCardCity.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.cCardCountry.getText().toString().isEmpty()) {
            binding.cCardCountry.setError("Field cannot be empty");
            valid = false;
        }
        if (binding.cCardNum.getText().toString().length() < 16) {
            binding.cCardNum.setError("Must contain 16 digits");
            valid = false;
        }
        return valid;
    }

    private void createUser() {
        String email = currentIntent.getStringExtra("EXTRA_EMAIL");
        String password = currentIntent.getStringExtra("EXTRA_PASSWORD");
        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setFirstName(currentIntent.getStringExtra("EXTRA_FNAME"));
        appUser.setMiddleInitial(currentIntent.getStringExtra("EXTRA_MNAME"));
        appUser.setLastName(currentIntent.getStringExtra("EXTRA_LNAME"));
        appUser.setPhone(currentIntent.getStringExtra("EXTRA_PHONE"));
        appUser.addOrUpdateAddress((Address) currentIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ"));
        try {
            Date dateOfBirth = new SimpleDateFormat("MMM dd yyyy").parse(currentIntent.getStringExtra("EXTRA_DATE"));
            Calendar calDateOfBirth = Calendar.getInstance();
            calDateOfBirth.setTime(dateOfBirth);
            Calendar calToday = Calendar.getInstance();
            appUser.setBirthdate(dateOfBirth);
            appUser.setAge(calToday.get(Calendar.YEAR) - calDateOfBirth.get(Calendar.YEAR));
        } catch (Exception e) {
            e.printStackTrace();
        }
        appUser.setUserType("Customer");
        appUser.addPaymentMethod(createPayment());

        // Create user in database
        ShopSmartApp.app.getEmailPassword().registerUserAsync(email, password, registerResult -> {
            Intent loginScreen = new Intent(CustomerRegistrationActivity3.this, StartupActivity.class);
            if (registerResult.isSuccess()) {
                // Create AppUser with associated User
                ShopSmartApp.login(email, password);
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, loginResult ->
                        ShopSmartApp.realm.executeTransaction(transactionRealm ->
                                transactionRealm.insert(appUser)));
                loginScreen.putExtra("EXTRA_SIGNUP_SUCCESS", true);
                Log.i("REGISTER", "Successfully registered user.");
            } else {
                Log.e("REGISTER", "Failed to register user: " + registerResult.getError().getErrorMessage());
            }
            startActivity(loginScreen);
        });
    }

    private PaymentMethod createPayment() {
        PaymentMethod pMethod = new PaymentMethod();
        pMethod.setCardNumber(binding.cCardNum.getText().toString());
        pMethod.setName(binding.cCardName.getText().toString());
        pMethod.setExpiry(binding.expM.getText().toString() + "/" + binding.expY.getText().toString());
        pMethod.setSecurityCode(binding.cCardCCV.getText().toString());

        Address cardAddress = new Address();
        cardAddress.setAddress1(binding.cCardAddress1.getText().toString());
        cardAddress.setAddress2(binding.cCardAddress2.getText().toString());
        cardAddress.setCity(binding.cCardCity.getText().toString());
        cardAddress.setProvince(binding.provPicker3.getSelectedItem().toString());
        cardAddress.setCountry("Canada");
        cardAddress.setPostalCode(binding.cCardPostalCode.getText().toString());
        pMethod.setBillingAddress(cardAddress);
        return pMethod;
    }
}
