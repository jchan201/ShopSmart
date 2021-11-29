package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDetailUpdateProfileActivityBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.RealmResults;

public class ShopOwnerDetailUpdateProfileActivity extends AppCompatActivity {
    private ShopownerDetailUpdateProfileActivityBinding binding;
    private DatePickerDialog datePickerDialog;
    private AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDetailUpdateProfileActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email))
                        user = users.get(i);
                }
                binding.textFName.setText(user.getFirstName());
                binding.textMName.setText(user.getMiddleInitial());
                binding.textLName.setText(user.getLastName());
                binding.textPhone.setText(user.getPhone());

                Calendar cal = Calendar.getInstance();
                cal.setTime(user.getBirthdate());
                int calYear = cal.get(Calendar.YEAR);
                int calMonth = cal.get(Calendar.MONTH);
                int calDay = cal.get(Calendar.DAY_OF_MONTH);
                binding.datePickerButton.setText(makeDateString(calDay, calMonth, calYear));
                DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) ->
                        binding.datePickerButton.setText(makeDateString(day, month, year));
                datePickerDialog = new DatePickerDialog(this, dateSetListener, calYear, calMonth, calDay);
            }
        });
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerDetailUpdateProfileActivity.this, ShopOwnerProfileDetailActivity.class)));
        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(transactionRealm -> {
                            user.setFirstName(binding.textFName.getText().toString());
                            user.setMiddleInitial(binding.textMName.getText().toString());
                            user.setLastName(binding.textLName.getText().toString());
                            user.setPhone(binding.textPhone.getText().toString());
                            try {
                                user.setBirthdate(new SimpleDateFormat("MMM dd yyyy").parse(binding.datePickerButton.getText().toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        startActivity(new Intent(ShopOwnerDetailUpdateProfileActivity.this, ShopOwnerProfileDetailActivity.class));
                    }
                });
            }
        });
    }

    private boolean validation() {
        boolean valid = true;
        if (this.binding.textFName.getText().toString().isEmpty()) {
            this.binding.textFName.setError("First name cannot be empty");
            valid = false;
        }
        if (this.binding.textLName.getText().toString().isEmpty()) {
            this.binding.textLName.setError("Last name cannot be empty");
            valid = false;
        }
        if (this.binding.textPhone.getText().toString().isEmpty()) {
            this.binding.textPhone.setError("Phone number cannot be empty");
            valid = false;
        }
        return valid;
    }

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
        datePickerDialog.show();
    }
}