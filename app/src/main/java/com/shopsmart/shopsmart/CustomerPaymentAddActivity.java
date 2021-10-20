package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerAddPaymentBinding;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class CustomerPaymentAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String PARTITION = "ShopSmart";
    CustomerAddPaymentBinding binding;
    private DatePickerDialog dpd;
    Intent currentIntent;
    App app;
    Address userAddress;
    Date userDOB;
    String userEmail;
    String userPass;

    private Realm realm;

    AppUser user;
    private PaymentMethod pMethod;
    private Address bAddress;

    String cCardPhone;

    boolean editPMethod = false;

    int updateIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.binding = CustomerAddPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());
        this.currentIntent = this.getIntent();

        Spinner provSpinner = findViewById(R.id.provPicker3);
        ArrayAdapter<CharSequence> provList = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
        provList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provSpinner.setAdapter(provList);
        provSpinner.setOnItemSelectedListener(this);

        if(this.currentIntent!= null){
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");
            this.updateIndex = currentIntent.getIntExtra("EXTRA_INDEX", updateIndex);
            boolean paymentEdit = currentIntent.getBooleanExtra("EXTRA_PMETHOD_EDIT", false);
            if (paymentEdit) {
                this.pMethod = (PaymentMethod) this.currentIntent.getSerializableExtra("EXTRA_PMETHOD");
                this.bAddress = (Address) this.currentIntent.getSerializableExtra("EXTRA_PMETHOD_ADDRESS");
                this.cCardPhone = this.currentIntent.getStringExtra("EXTRA_PMETHOD_PHONE");

                editPMethod = true;
                if(editPMethod){

                    binding.cCardName.setText(pMethod.getName());

                    binding.cCardNum.setText(pMethod.getCardNumber());
                    binding.expM.setText(pMethod.getExpiry().substring(0,2));
                    binding.expY.setText(pMethod.getExpiry().substring(3,5));
                    binding.cCardCCV.setText(pMethod.getSecurityCode());

                    binding.cCardAddress1.setText(bAddress.getAddress1());
                    binding.cCardAddress2.setText(bAddress.getAddress2());
                    binding.cCardCity.setText(bAddress.getCity());
                    //binding.provPicker3
                    binding.cCardPostalCode.setText(bAddress.getPostalCode());
                    binding.cCardCountry.setText(bAddress.getCountry());
                    binding.cCardPhoneNum.setText(cCardPhone);
                }
            }
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                // Open a Synced Realm for asynchronous transactions.
                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).allowWritesOnUiThread(true).build();
                realm = Realm.getInstance(config);

                // Retrieve all users in the Realm.
                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                // Find the AppUser
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(userEmail)) {
                        user = users.get(i);
                    }
                }
            }
            else{
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.buttonCancel.setOnClickListener(view -> {
            realm.close();
            Intent intentBack = new Intent(CustomerPaymentAddActivity.this, CustomerPaymentsActivity.class);
            intentBack.putExtra("EXTRA_EMAIL", userEmail);
            intentBack.putExtra("EXTRA_PASS", userPass);
            startActivity(intentBack);
        });

        binding.buttonAdd.setOnClickListener(view -> {
            if(validateData()){
                if(!editPMethod) {
                    pMethod = new PaymentMethod();
                    pMethod.setCardNumber(binding.cCardNum.getText().toString());
                    pMethod.setName(binding.cCardName.getText().toString());
                    pMethod.setSecurityCode(binding.cCardCCV.getText().toString());
                    pMethod.setExpiry(this.binding.expM.getText().toString() + "/" + this.binding.expY.getText().toString());

                    addCCardAddress(pMethod);

                    realm.executeTransaction(transactionRealm -> {
                        user.addPaymentMethod(pMethod);
                    });
                }

                else{
                    pMethod.setCardNumber(binding.cCardNum.getText().toString());
                    pMethod.setName(binding.cCardName.getText().toString());
                    pMethod.setSecurityCode(binding.cCardCCV.getText().toString());
                    pMethod.setExpiry(this.binding.expM.getText().toString() + "/" + this.binding.expY.getText().toString());

                    addCCardAddress(pMethod);

                    realm.executeTransaction(transactionRealm ->{
                        user.updatePaymentMethod(pMethod, updateIndex);
                    });
                }

                realm.close();


            }
            Intent intentBack = new Intent(CustomerPaymentAddActivity.this, CustomerPaymentsActivity.class);
            intentBack.putExtra("EXTRA_EMAIL", userEmail);
            intentBack.putExtra("EXTRA_PASS", userPass);
            startActivity(intentBack);
        });
    }

    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
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
        if(i != 0)
            Toast.makeText(getApplication(),choice + " selected", Toast.LENGTH_SHORT).show();
        else{
            Toast.makeText(getApplication(), "Please select a province/territory", Toast.LENGTH_LONG).show();
        }
    }

    private void addCCardAddress(PaymentMethod paymentMethod){
        if(!editPMethod) {
            bAddress = new Address();
            bAddress.setCity(binding.cCardCity.getText().toString());
            bAddress.setProvince(binding.provPicker3.getSelectedItem().toString());
            bAddress.setPostalCode(binding.cCardPostalCode.getText().toString());
            bAddress.setAddress1(binding.cCardAddress1.getText().toString());
            bAddress.setAddress2(binding.cCardAddress2.getText().toString());
            bAddress.setCountry("Canada");

            paymentMethod.setBillingAddress(bAddress);
        }
        else{
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

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    //@Override
    //public void onClick(View view) {
    //    if(view != null){
    //        switch (view.getId()){
    //            case R.id.cancelButton3:{
    //                Intent mainIntent = new Intent(this, StartupActivity.class);
    //                startActivity(mainIntent);
    //                break;
    //            }
    //            case R.id.finishButton:{
    //                if (this.validateData()) {
    //                    //this.createUser();
    //                    //Intent CRegister3 = new Intent(this, StartupActivity.class);
    //                    //startActivity(CRegister3);
    //                    break;
    //                }
    //            }
    //        }
    //    }
    //}

    private boolean validateData(){
        boolean valid = true;
        if (this.binding.cCardName.getText().toString().isEmpty()) {
            this.binding.cCardName.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.cCardPhoneNum.getText().toString().isEmpty()){
            this.binding.cCardPhoneNum.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.cCardCCV.getText().toString().isEmpty()){
            this.binding.cCardCCV.setError("Field cannot be empty");
            valid = false;
        }

        if(this.binding.cCardCCV.getText().toString().length() < 3){
            this.binding.cCardCCV.setError("Field must be 3 digits long");
            valid = false;
        }

        if(this.binding.cCardPhoneNum.getText().toString().length() < 9){
            this.binding.cCardPhoneNum.setError("Field must be 10 digits long");
            valid = false;
        }

        Calendar todaysDate = Calendar.getInstance();
        if(!this.binding.expY.getText().toString().isEmpty() && !this.binding.expM.getText().toString().isEmpty()) {
            if(Long.parseLong(this.binding.expY.getText().toString()) + 2000 + (Long.parseLong(this.binding.expM.getText().toString()) / 12) < todaysDate.get(Calendar.YEAR) + ((todaysDate.get(Calendar.MONTH) + 1) / 12)){
                this.binding.expY.setError("Please enter a valid date");
                valid = false;
            }
            //if (Long.parseLong(this.binding.expY.getText().toString()) + 2000 <= (todaysDate.get(Calendar.YEAR)) && Long.parseLong(this.binding.expM.getText().toString()) < todaysDate.get(Calendar.MONTH) + 1) {
            //    this.binding.expY.setError("Please enter a valid date");
            //    valid = false;
            //}
            else if(Long.parseLong(this.binding.expM.getText().toString()) > 12){
                this.binding.expM.setError("Please enter a valid date" + todaysDate.get(Calendar.YEAR));
                valid = false;
            }
        }

        if (this.binding.expY.getText().toString().isEmpty()) {
            this.binding.expY.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.expM.getText().toString().isEmpty()) {
            this.binding.expM.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.cCardPostalCode.getText().toString().isEmpty()) {
            this.binding.cCardPostalCode.setError("Field cannot be empty");
            valid = false;
        }
        if (!this.binding.cCardPostalCode.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")) {
            this.binding.cCardPostalCode.setError("Postal code must match schema A1A 1A1");
            valid = false;
        }
        if (this.binding.cCardCity.getText().toString().isEmpty()) {
            this.binding.cCardCity.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.cCardCountry.getText().toString().isEmpty()) {
            this.binding.cCardCountry.setError("Field cannot be empty");
            valid = false;
        }
        if (this.binding.cCardNum.getText().toString().length() < 16) {
            this.binding.cCardNum.setError("Must contain 16 digits");
            valid = false;
        }
            return valid;
        }
        //return true;

    private void createUser() {
        AppUser appUser = new AppUser();

        String password = this.currentIntent.getStringExtra("EXTRA_PASSWORD");


        if(currentIntent != null){
            this.userAddress = (Address)this.currentIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ");
        }

        //appUser.setEmail(this.currentIntent.getStringExtra("EXTRA_EMAIL"));
        //appUser.setFirstName(this.currentIntent.getStringExtra("EXTRA_FNAME"));
        //appUser.setMiddleInitial(this.currentIntent.getStringExtra("EXTRA_MNAME"));
        //appUser.setLastName(this.currentIntent.getStringExtra("EXTRA_LNAME"));
        //appUser.setPhone(this.currentIntent.getStringExtra("EXTRA_PHONE"));
        Log.v(PARTITION, userAddress.getAddress1());
        appUser.addOrUpdateAddress(this.userAddress);

        try {
            this.userDOB = new SimpleDateFormat("MMM dd yyyy").parse(currentIntent.getStringExtra("EXTRA_DATE"));
        } catch (Exception e) {
            Log.wtf(PARTITION, "Failed to register user: " + e.getMessage());
        }
        appUser.setBirthdate(userDOB);

        Calendar calendarUser = Calendar.getInstance();
        calendarUser.setTime(userDOB);
        Calendar calendar = Calendar.getInstance();

        appUser.setAge(calendar.get(Calendar.YEAR) - calendarUser.get(Calendar.YEAR));
        appUser.setUserType("Customer");

        appUser.addPaymentMethod(createPayment());

        // Create user in database
        app.getEmailPassword().registerUserAsync(appUser.getEmail(), password, it -> {
            Intent loginScreen = new Intent(CustomerPaymentAddActivity.this, StartupActivity.class);
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully registered user.");

                // Create AppUser with associated User
                Credentials credentials = Credentials.emailPassword(appUser.getEmail(), password);
                app.loginAsync(credentials, result -> {
                    Log.e("SSS", "In async: " + app.currentUser().getId());
                    SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart")
                            .allowWritesOnUiThread(true) // allow synchronous writes
                            .build();
                    Realm backgroundRealm = Realm.getInstance(config);
                    backgroundRealm.executeTransaction(transactionRealm -> {
                        transactionRealm.insert(appUser);
                    });
                    backgroundRealm.close();
                });
                loginScreen.putExtra("EXTRA_SIGNUP_SUCCESS", true);
            } else {
                Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
            }
            startActivity(loginScreen);
        });
    }

    private PaymentMethod createPayment(){
        PaymentMethod pMethod = new PaymentMethod();
        String cCardNum = this.binding.cCardNum.getText().toString();

        pMethod.setCardNumber(cCardNum);
        pMethod.setName(this.binding.cCardName.getText().toString());
        pMethod.setExpiry(this.binding.expM.getText().toString() + "/" + this.binding.expY.getText().toString());
        pMethod.setSecurityCode(this.binding.cCardCCV.getText().toString());

        Address cCardAddress = new Address();
        cCardAddress.setAddress1(this.binding.cCardAddress1.getText().toString());
        cCardAddress.setAddress2(this.binding.cCardAddress2.getText().toString());
        cCardAddress.setCity(this.binding.cCardCity.getText().toString());
        cCardAddress.setProvince(this.binding.provPicker3.getSelectedItem().toString());
        cCardAddress.setCountry("Canada");
        cCardAddress.setPostalCode(this.binding.cCardPostalCode.getText().toString());

        pMethod.setBillingAddress(cCardAddress);

        return pMethod;
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
}
