package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerSignupActivity3Binding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShopOwnerSignupActivity3 extends AppCompatActivity {
    private ShopownerSignupActivity3Binding binding;
    private Intent currIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerSignupActivity3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currIntent = getIntent();

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        binding.edtTextExpiryDateYear.setText(Integer.toString(year));
        binding.spinnerExpiryDateMonth.setSelection(cal.get(Calendar.MONTH));
        binding.buttonCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerSignupActivity3.this, ShopOwnerSignupActivity2.class)));
        binding.buttonNext.setOnClickListener(view -> {
            if (validation()) createUser();
        });
    }

    private boolean validation() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        boolean valid = true;
        if (binding.edtTextCardName.getText().toString().isEmpty()) {
            binding.edtTextCardName.setError("Name on card cannot be empty");
            valid = false;
        }
        if (binding.edtTextCardNum.getText().toString().isEmpty()) {
            binding.edtTextCardNum.setError("Card number cannot be empty");
            valid = false;
        }
        if (binding.edtTextCCV.getText().toString().isEmpty()) {
            binding.edtTextCCV.setError("CCV cannot be empty");
            valid = false;
        }
        if (binding.edtTextExpiryDateYear.getText().toString().isEmpty()) {
            binding.edtTextExpiryDateYear.setError("Expire year cannot be empty");
            valid = false;
        }
        if (Integer.parseInt(binding.edtTextExpiryDateYear.getText().toString()) < year) {
            binding.edtTextExpiryDateYear.setError("Card expired");
            valid = false;
        }
        if (binding.edtTextBillCity.getText().toString().isEmpty()) {
            binding.edtTextBillCity.setError("City cannot be empty");
            valid = false;
        }
        if (binding.edtTextBillPostalCode.getText().toString().isEmpty()) {
            binding.edtTextBillPostalCode.setError("Postal Code cannot be empty");
            valid = false;
        }
        if (!binding.edtTextBillPostalCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            binding.edtTextBillPostalCode.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }
        if (binding.edtTextBillAdd1.getText().toString().isEmpty()) {
            binding.edtTextBillAdd1.setError("Address cannot be empty");
            valid = false;
        }
        return valid;
    }

    private PaymentMethod createPaymentMethod() {
        Address billAddress = new Address();
        billAddress.setAddress1(binding.edtTextBillAdd1.getText().toString());
        billAddress.setAddress2(binding.edtTextBillAdd2.getText().toString());
        billAddress.setCity(binding.edtTextBillCity.getText().toString());
        billAddress.setProvince(binding.spinnerProvince.getSelectedItem().toString());
        billAddress.setPostalCode(binding.edtTextBillPostalCode.getText().toString());
        billAddress.setCountry("Canada");

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(binding.edtTextCardName.getText().toString());
        paymentMethod.setCardNumber(binding.edtTextCardNum.getText().toString());
        paymentMethod.setExpiry(binding.spinnerExpiryDateMonth.getSelectedItem().toString() + "/" + binding.edtTextExpiryDateYear.getText().toString());
        paymentMethod.setSecurityCode(binding.edtTextCCV.getText().toString());
        paymentMethod.setBillingAddress(billAddress);
        return paymentMethod;
    }

    private void createUser() {
        String email = currIntent.getStringExtra("EXTRA_EMAIL");
        String password = currIntent.getStringExtra("EXTRA_PASSWORD");
        Calendar calToday = Calendar.getInstance();
        Calendar calDateOfBirth = Calendar.getInstance();
        Date dateOfBirth;
        try {
            dateOfBirth = new SimpleDateFormat("MMM dd yyyy").parse(currIntent.getStringExtra("EXTRA_DOB"));
            calDateOfBirth.setTime(dateOfBirth);
        } catch (ParseException e) {
            dateOfBirth = null;
            e.printStackTrace();
        }
        AppUser appUser = new AppUser("Owner",
                currIntent.getStringExtra("EXTRA_FNAME"),
                currIntent.getStringExtra("EXTRA_MNAME"),
                currIntent.getStringExtra("EXTRA_LNAME"),
                calToday.get(Calendar.YEAR) - calDateOfBirth.get(Calendar.YEAR),
                email,
                currIntent.getStringExtra("EXTRA_PHONE"),
                dateOfBirth);
        appUser.addPaymentMethod(createPaymentMethod());
        appUser.addOrUpdateAddress((Address) currIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ"));

        ShopSmartApp.app.getEmailPassword().registerUserAsync(email, password, registerResult -> {
            Intent loginScreen = new Intent(ShopOwnerSignupActivity3.this, StartupActivity.class);
            if (registerResult.isSuccess()) {
                ShopSmartApp.login(email, password);
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, loginResult -> {
                    ShopSmartApp.instantiateRealm();
                    ShopSmartApp.realm.executeTransaction(transactionRealm ->
                            transactionRealm.insert(appUser));
                    ShopSmartApp.logout();
                });
                loginScreen.putExtra("EXTRA_SIGNUP_SUCCESS", true);
                Log.i("REGISTER", "Successfully registered user.");
            } else {
                Toast.makeText(this, "Email already taken.", Toast.LENGTH_SHORT).show();
                Log.e("REGISTER", "Failed to register user: " + registerResult.getError().getErrorMessage());
            }
            startActivity(loginScreen);
            finish();
        });
    }
}