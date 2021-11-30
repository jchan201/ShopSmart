package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerProfileBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmResults;

public class CustomerProfileActivity extends AppCompatActivity {
    private CustomerProfileBinding binding;
    private DatePickerDialog dpd;
    private boolean nameEdit = false;
    private boolean phoneEdit = false;
    private boolean cityEdit = false;
    private boolean addressEdit = false;
    private boolean dobEdit = false;
    private boolean pCodeEdit = false;
    private boolean provinceEdit = false;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (int x = 0; x < users.size(); x++) {
                    if (users.get(x).getEmail().equals(ShopSmartApp.email)) {
                        user = users.get(x);
                    }
                }
                if (user != null) {
                    binding.fullName.setText(user.getFirstName() + " " + user.getMiddleInitial() + " " + user.getLastName());
                    binding.firstName.setText(user.getFirstName());
                    binding.middleName.setText(user.getMiddleInitial());
                    binding.lastName.setText(user.getLastName());
                    binding.queryDob.setText(user.getBirthdateString());
                    binding.queryCity.setText(user.getAddress().getCity());
                    binding.city1.setText(user.getAddress().getCity());
                    binding.queryAddress1.setText(user.getAddress().getAddress1());
                    binding.addressLine1.setText(user.getAddress().getAddress1());
                    binding.queryAddress2.setText(user.getAddress().getAddress2());
                    binding.addressLine2.setText(user.getAddress().getAddress2());
                    binding.queryCountry.setText(user.getAddress().getCountry());
                    binding.userCountry.setText("Canada");
                    binding.queryProvince.setText(user.getAddress().getProvince());
                    binding.queryPostalCode.setText(user.getAddress().getPostalCode());
                    binding.userPostalCode.setText(user.getAddress().getPostalCode());
                    binding.queryPhoneNum.setText(user.getPhone());
                    binding.inputPhoneNumber.setText(user.getPhone());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(user.getBirthdate());
                    binding.dobProfile.setText(makeDateString(
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.YEAR)));
                    dpd = new DatePickerDialog(
                            this,
                            (datePicker, year, month, day) ->
                                    binding.dobProfile.setText(makeDateString(day, month, year)),
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));

                    Spinner provSpinner = findViewById(R.id.provPickerProfile);
                    ArrayAdapter<CharSequence> provList = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
                    provList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    provSpinner.setAdapter(provList);
                }
            }
        });
        binding.editFullName.setOnClickListener(view -> {
            binding.fullName.setVisibility(View.INVISIBLE);
            binding.firstName.setVisibility(View.VISIBLE);
            binding.middleName.setVisibility(View.VISIBLE);
            binding.lastName.setVisibility(View.VISIBLE);
            nameEdit = true;
        });
        binding.editDob.setOnClickListener(view -> {
            binding.dobProfile.setVisibility(View.VISIBLE);
            binding.queryDob.setVisibility(View.INVISIBLE);
            dobEdit = true;
        });
        binding.editCity.setOnClickListener(view -> {
            binding.city1.setVisibility(View.VISIBLE);
            binding.queryCity.setVisibility(View.INVISIBLE);
            cityEdit = true;
        });
        binding.editAddress.setOnClickListener(view -> {
            binding.queryAddress1.setVisibility(View.INVISIBLE);
            binding.queryAddress2.setVisibility(View.INVISIBLE);
            binding.addressLine1.setVisibility(View.VISIBLE);
            binding.addressLine2.setVisibility(View.VISIBLE);
            addressEdit = true;
        });
        binding.editCountry.setOnClickListener(view -> {
            Toast.makeText(this, "Sorry, can't edit country currently (this app is exclusive to Canada).", Toast.LENGTH_SHORT).show();
        });
        binding.editPCode.setOnClickListener(view -> {
            binding.queryPostalCode.setVisibility(View.INVISIBLE);
            binding.userPostalCode.setVisibility(View.VISIBLE);
            pCodeEdit = true;
        });
        binding.editProvince.setOnClickListener(view -> {
            binding.queryProvince.setVisibility(View.INVISIBLE);
            binding.provPickerProfile.setVisibility(View.VISIBLE);
            provinceEdit = true;
        });
        binding.editPhoneNum.setOnClickListener(view -> {
            binding.queryPhoneNum.setVisibility(View.INVISIBLE);
            binding.inputPhoneNumber.setVisibility(View.VISIBLE);
            phoneEdit = true;
        });
        binding.applyButton.setOnClickListener(view -> {
            if (validation()) {
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                            if (nameEdit) {
                                user.setFirstName(binding.firstName.getText().toString());
                                user.setMiddleInitial(binding.middleName.getText().toString());
                                user.setLastName(binding.lastName.getText().toString());
                                binding.fullName.setText(user.getFirstName() + " " + user.getMiddleInitial() + " " + user.getLastName());
                                binding.fullName.setVisibility(View.VISIBLE);
                                binding.firstName.setVisibility(View.INVISIBLE);
                                binding.middleName.setVisibility(View.INVISIBLE);
                                binding.lastName.setVisibility(View.INVISIBLE);
                                nameEdit = false;
                            }
                            if (dobEdit) {
                                Date dateOfBirth = null;
                                try {
                                    dateOfBirth = new SimpleDateFormat("MMM dd yyyy").parse(binding.dobProfile.getText().toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                user.setBirthdate(dateOfBirth);
                                binding.queryDob.setText(user.getBirthdateString());
                                binding.queryDob.setVisibility(View.VISIBLE);
                                binding.dobProfile.setVisibility(View.INVISIBLE);
                            }
                            if (cityEdit) {
                                user.getAddress().setCity(binding.city1.getText().toString());
                                binding.queryCity.setText(user.getAddress().getCity());
                                binding.queryCity.setVisibility(View.VISIBLE);
                                binding.city1.setVisibility(View.INVISIBLE);
                                binding.city1.setText(user.getAddress().getCity());
                                cityEdit = false;
                            }
                            if (addressEdit) {
                                user.getAddress().setAddress1(binding.addressLine1.getText().toString());
                                user.getAddress().setAddress2(binding.addressLine2.getText().toString());
                                binding.queryAddress1.setText(user.getAddress().getAddress1());
                                binding.queryAddress2.setText(user.getAddress().getAddress2());
                                binding.queryAddress1.setVisibility(View.VISIBLE);
                                binding.queryAddress2.setVisibility(View.VISIBLE);
                                binding.addressLine1.setVisibility(View.INVISIBLE);
                                binding.addressLine2.setVisibility(View.INVISIBLE);
                                addressEdit = false;
                            }
                            if (provinceEdit) {
                                user.getAddress().setProvince(binding.provPickerProfile.getSelectedItem().toString());
                                binding.queryProvince.setText(user.getAddress().getProvince());
                                binding.queryProvince.setVisibility(View.VISIBLE);
                                binding.provPickerProfile.setVisibility(View.INVISIBLE);
                                provinceEdit = false;
                            }
                            if (pCodeEdit) {
                                user.getAddress().setPostalCode(binding.userPostalCode.getText().toString());
                                binding.queryPostalCode.setText(user.getAddress().getPostalCode());
                                binding.queryPostalCode.setVisibility(View.VISIBLE);
                                binding.userPostalCode.setVisibility(View.INVISIBLE);
                                pCodeEdit = false;
                            }
                            if (phoneEdit) {
                                user.setPhone(binding.inputPhoneNumber.getText().toString());
                                binding.queryPhoneNum.setText(user.getPhone());
                                binding.queryPhoneNum.setVisibility(View.VISIBLE);
                                binding.inputPhoneNumber.setVisibility(View.INVISIBLE);
                                phoneEdit = false;
                            }
                        });
                    }
                });
            }
        });
        binding.cancelButton.setOnClickListener(view ->
                startActivity(new Intent(CustomerProfileActivity.this, CustomerManageProfileActivity.class)));
    }

    private boolean validation() {
        boolean valid = true;
        if (nameEdit) {
            if (binding.firstName.getText().toString().isEmpty()) {
                binding.firstName.setError("Field cannot be empty");
                valid = false;
            }
            if (binding.lastName.getText().toString().isEmpty()) {
                binding.lastName.setError("Field cannot be empty");
                valid = false;
            }
        }
        if (cityEdit && binding.city1.getText().toString().isEmpty()) {
            binding.city1.setError("Field cannot be empty");
            valid = false;
        }
        if (pCodeEdit) {
            if (binding.userPostalCode.getText().toString().isEmpty()) {
                binding.userPostalCode.setError("Field cannot be empty");
                valid = false;
            } else if (!binding.userPostalCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
                binding.userPostalCode.setError("Postal code must match schema A1A 1A1");
                valid = false;
            }
        }
        if (addressEdit && binding.addressLine1.getText().toString().isEmpty()) {
            binding.addressLine1.setError("Field cannot be empty");
            valid = false;
        }
        if (phoneEdit) {
            if (binding.inputPhoneNumber.getText().toString().isEmpty()) {
                binding.inputPhoneNumber.setError("Phone number cannot be empty");
                valid = false;
            } else if (!binding.inputPhoneNumber.getText().toString().matches("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d")) {
                binding.inputPhoneNumber.setError("Must contain 10 digits");
                valid = false;
            }
        }

        if (dobEdit){
            Date dateOfBirth = null;
            try {
                dateOfBirth = new SimpleDateFormat("MMM dd yyyy").parse(binding.dobProfile.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date todayDate = Calendar.getInstance().getTime();

            if(dateOfBirth.after(todayDate)){
                binding.dobProfile.setError("Date of birth cannot be after today");
                valid = false;
            }
        }

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHome:
                startActivity(new Intent(CustomerProfileActivity.this, CustomerDashboardActivity.class));
            case R.id.menuPrev:
                startActivity(new Intent(CustomerProfileActivity.this, CustomerManageProfileActivity.class));
        }
        return true;
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
        if (dpd != null)
            dpd.show();
    }
}
