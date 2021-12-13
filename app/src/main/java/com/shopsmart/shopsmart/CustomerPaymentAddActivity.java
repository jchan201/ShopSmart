package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerAddPaymentBinding;

import java.util.Calendar;

import io.realm.RealmResults;

public class CustomerPaymentAddActivity extends AppCompatActivity {
    private CustomerAddPaymentBinding binding;
    private int updateIndex;
    private PaymentMethod pMethod;
    private Address bAddress;
    private String cCardPhone;
    private boolean editPMethod;
    private AppUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerAddPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        Spinner provSpinner = findViewById(R.id.provPicker3);
        ArrayAdapter<CharSequence> provList = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
        provList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provSpinner.setAdapter(provList);

        if (currIntent != null) {
            updateIndex = currIntent.getIntExtra("EXTRA_INDEX", 0);
            editPMethod = currIntent.getBooleanExtra("EXTRA_PMETHOD_EDIT", false);
            if (editPMethod) {
                pMethod = (PaymentMethod) currIntent.getSerializableExtra("EXTRA_PMETHOD");
                bAddress = (Address) currIntent.getSerializableExtra("EXTRA_PMETHOD_ADDRESS");
                cCardPhone = currIntent.getStringExtra("EXTRA_PMETHOD_PHONE");
                binding.cCardName.setText(pMethod.getName());
                binding.cCardNum.setText(pMethod.getCardNumber());
                binding.expM.setText(pMethod.getExpiry().substring(0, 2));
                binding.expY.setText(pMethod.getExpiry().substring(3, 5));
                binding.cCardCCV.setText(pMethod.getSecurityCode());
                binding.cCardAddress1.setText(bAddress.getAddress1());
                binding.cCardAddress2.setText(bAddress.getAddress2());
                binding.cCardCity.setText(bAddress.getCity());
                binding.cCardPostalCode.setText(bAddress.getPostalCode());
                binding.cCardCountry.setText(bAddress.getCountry());
                binding.cCardPhoneNum.setText(cCardPhone);
                binding.buttonAdd.setText("Update");
            }
        }

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email)) {
                        user = users.get(i);
                    }
                }
            }
        });

        binding.buttonCancel.setOnClickListener(view ->
                startActivity(new Intent(CustomerPaymentAddActivity.this, CustomerPaymentsActivity.class)));

        binding.buttonAdd.setOnClickListener(view -> {
            if (validateData()) {
                binding.buttonAdd.setEnabled(false);
                if (!editPMethod) {
                    pMethod = new PaymentMethod();
                    pMethod.setCardNumber(binding.cCardNum.getText().toString());
                    pMethod.setName(binding.cCardName.getText().toString());
                    pMethod.setSecurityCode(binding.cCardCCV.getText().toString());
                    pMethod.setExpiry(binding.expM.getText().toString() + "/" + binding.expY.getText().toString());
                    addCCardAddress(pMethod);
                    ShopSmartApp.realm.executeTransaction(transactionRealm ->
                            user.addPaymentMethod(pMethod));
                } else {
                    pMethod.setCardNumber(binding.cCardNum.getText().toString());
                    pMethod.setName(binding.cCardName.getText().toString());
                    pMethod.setSecurityCode(binding.cCardCCV.getText().toString());
                    pMethod.setExpiry(binding.expM.getText().toString() + "/" + binding.expY.getText().toString());
                    addCCardAddress(pMethod);
                    ShopSmartApp.realm.executeTransaction(transactionRealm ->
                            user.updatePaymentMethod(pMethod, updateIndex));
                }
                startActivity(new Intent(CustomerPaymentAddActivity.this, CustomerPaymentsActivity.class));
            }
        });
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

    private void addCCardAddress(PaymentMethod paymentMethod) {
        if (!editPMethod) {
            bAddress = new Address();
            bAddress.setCity(binding.cCardCity.getText().toString());
            bAddress.setProvince(binding.provPicker3.getSelectedItem().toString());
            bAddress.setPostalCode(binding.cCardPostalCode.getText().toString());
            bAddress.setAddress1(binding.cCardAddress1.getText().toString());
            bAddress.setAddress2(binding.cCardAddress2.getText().toString());
            bAddress.setCountry("Canada");
            paymentMethod.setBillingAddress(bAddress);
        } else {
            bAddress = new Address();
            bAddress.setCity(binding.cCardCity.getText().toString());
            bAddress.setProvince(binding.provPicker3.getSelectedItem().toString());
            bAddress.setPostalCode(binding.cCardPostalCode.getText().toString());
            bAddress.setAddress1(binding.cCardAddress1.getText().toString());
            bAddress.setAddress2(binding.cCardAddress2.getText().toString());
            bAddress.setCountry("Canada");
            paymentMethod.setBillingAddress(bAddress);
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
            } else if (Long.parseLong(binding.expM.getText().toString()) > 12) {
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
}
