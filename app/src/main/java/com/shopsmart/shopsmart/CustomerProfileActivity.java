package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerProfileBinding;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class CustomerProfileActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currentIntent;
    String userEmail;
    String userPass;
    AppUser user;
    boolean nameEdit = false, phoneEdit = false, cityEdit = false, addressEdit = false, provinceEdit = false, countryEdit = false, dobEdit = false, pCodeEdit = false;
    private DatePickerDialog dpd;
    private CustomerProfileBinding binding;
    private Realm realm;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        binding = CustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        this.currentIntent = this.getIntent();

        if (this.currentIntent != null) {
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");


        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart")
                        .allowWritesOnUiThread(true)
                        .build();
                realm = Realm.getInstance(config);

                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                for (int x = 0; x < users.size(); x++) {
                    if (users.get(x).getEmail().equals(userEmail)) {
                        user = users.get(x);
                    }
                }

                binding.fullName.setText(user.getFirstName() + " " + user.getMiddleInitial() + " " + user.getLastName());
                binding.firstName.setText(user.getFirstName());
                binding.middleName.setText(user.getMiddleInitial());
                binding.lastName.setText(user.getLastName());

                binding.queryCity.setText(user.getAddress().getCity());
                binding.city1.setText(user.getAddress().getCity());

                binding.queryAddress1.setText(user.getAddress().getAddress1());
                binding.addressLine1.setText(user.getAddress().getAddress1());
                binding.queryAddress2.setText(user.getAddress().getAddress2());
                binding.addressLine2.setText(user.getAddress().getAddress2());

                binding.queryCountry.setText(user.getAddress().getCountry());
                binding.userCountry.setText("Canada");

                binding.queryPhoneNum.setText(user.getPhone());
                binding.inputPhoneNumber.setText(user.getPhone());

                binding.queryDob.setText(user.getBirthdateString());
                binding.queryPostalCode.setText(user.getAddress().getPostalCode());

                //Calendar cal = Calendar.getInstance();
                //cal.setTime(user.getBirthdate());
                //int calY = cal.get(Calendar.YEAR);
                //int calM = cal.get(Calendar.MONTH);
                //int calD = cal.get(Calendar.DAY_OF_MONTH);
//
                //binding.queryDob.setText(makeDateString(calD, calM, calY));
                //DatePickerDialog.OnDateSetListener dsl =
                //        ((datePicker, year, month, day) -> binding.)

            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.cancelButton.setOnClickListener(view -> {
            realm.close();
            Intent cancelIntent = new Intent(CustomerProfileActivity.this, CustomerProfileActivity.class);
            cancelIntent.putExtra("EXTRA_EMAIL", userEmail);
            cancelIntent.putExtra("EXTRA_PASS", userPass);
            startActivity(cancelIntent);
        });

        binding.editAddress.setOnClickListener(view -> {
            binding.queryAddress1.setVisibility(View.INVISIBLE);
            binding.queryAddress2.setVisibility(View.INVISIBLE);
            binding.addressLine1.setVisibility(View.VISIBLE);
            binding.addressLine2.setVisibility(View.VISIBLE);
            addressEdit = true;
        });

        binding.editCity.setOnClickListener(view -> {
            binding.city1.setVisibility(View.VISIBLE);
            binding.queryCity.setVisibility(View.INVISIBLE);
            cityEdit = true;
        });

        binding.editCountry.setOnClickListener(view -> {
            binding.queryCity.setVisibility(View.INVISIBLE);
            binding.city1.setVisibility(View.VISIBLE);
            countryEdit = true;
        });

        binding.editDob.setOnClickListener(view -> {
            binding.dobProfile.setVisibility(View.VISIBLE);
            binding.queryDob.setVisibility(View.INVISIBLE);
            dobEdit = true;
        });

        binding.editFullName.setOnClickListener(view -> {
            binding.fullName.setVisibility(View.INVISIBLE);
            binding.firstName.setVisibility(View.VISIBLE);
            binding.middleName.setVisibility(View.VISIBLE);
            binding.lastName.setVisibility(View.VISIBLE);
            nameEdit = true;
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
                realm.executeTransaction(transactionRealm -> {
                    if (addressEdit) {
                        user.getAddress().setAddress1(binding.addressLine1.getText().toString());
                        user.getAddress().setAddress2(binding.addressLine2.getText().toString());
                        //Address City dob country name pCode prov phone)
                    }
                    if (cityEdit)
                        user.getAddress().setCity(binding.city1.getText().toString());

                    if (dobEdit) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(user.getBirthdate());
                    }

                    if (countryEdit)
                        user.getAddress().setCountry("Canada");

                    if (nameEdit) {
                        user.setFirstName(binding.firstName.getText().toString());
                        user.setMiddleInitial(binding.middleName.getText().toString());
                        user.setLastName(binding.lastName.getText().toString());
                    }

                    if (pCodeEdit) {
                        user.getAddress().setPostalCode(binding.userPostalCode.getText().toString());

                    }
                    if (phoneEdit) {
                        user.setPhone(binding.inputPhoneNumber.getText().toString());
                    }
                });
                realm.close();
                Intent goBack = new Intent(CustomerProfileActivity.this, CustomerProfileActivity.class);
                goBack.putExtra("EXTRA_EMAIL", userEmail);
                goBack.putExtra("EXTRA_PASS", userPass);
                startActivity(goBack);
            }
        });

        binding.cancelButton.setOnClickListener(view -> {
            realm.close();
            Intent goBack = new Intent(CustomerProfileActivity.this, CustomerManageProfileActivity.class);
            goBack.putExtra("EXTRA_PASS", userPass);
            goBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(goBack);
        });

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    private boolean validation() {
        boolean valid = true;



        return valid;
    }

    private void updateUser() {
        realm.executeTransaction(transactionRealm -> {
            user.setFirstName(binding.firstName.getText().toString());
            user.setMiddleInitial(binding.middleName.getText().toString());
        });
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
                //realm.close();
                Intent homeIntent = new Intent(CustomerProfileActivity.this, CustomerDashboardActivity.class);
                homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                homeIntent.putExtra("EXTRA_PASS", userPass);
                //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                startActivity(homeIntent);
                //break;
            case R.id.menuPrev:
                realm.close();
                Intent goBack = new Intent(CustomerProfileActivity.this, CustomerDashboardActivity.class);
                goBack.putExtra("EXTRA_EMAIL", userEmail);
                goBack.putExtra("EXTRA_PASS", userPass);
                startActivity(goBack);
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        // Log out.
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("LOGOUT", "Successfully logged out.");
            } else {
                Log.e("LOGOUT", "Failed to log out, error: " + result.getError());
            }
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
}
