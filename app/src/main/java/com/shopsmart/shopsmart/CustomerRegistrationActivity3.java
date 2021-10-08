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

import com.shopsmart.shopsmart.databinding.CustomerRegister3Binding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class CustomerRegistrationActivity3 extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    CustomerRegister3Binding binding;
    private DatePickerDialog dpd;
    Intent currentIntent;
    App app;
    Address userAddress;
    Date userDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.binding = CustomerRegister3Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        this.currentIntent = this.getIntent();

        Spinner provSpinner = findViewById(R.id.provPicker);

        ArrayAdapter<CharSequence> provList = ArrayAdapter.createFromResource(this, R.array.provinces, android.R.layout.simple_spinner_item);
        provList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provSpinner.setAdapter(provList);
        provSpinner.setOnItemSelectedListener(this);

        this.binding.cancelButton3.setOnClickListener(this);
        this.binding.finishButton.setOnClickListener(this);
    }

    private String todaysDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900, 0, 1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }


    private String makeDateString(int day, int month, int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }


    private String getMonthFormat(int month){
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";
        return "JAN";
    }

    public void openDatePicker(View view){
        dpd.show();
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

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onClick(View view) {
        if(view != null){
            switch (view.getId()){
                case R.id.cancelButton3:{
                    Intent mainIntent = new Intent(this, StartupActivity.class);
                    startActivity(mainIntent);
                    break;
                }
                case R.id.finishButton:{
                    if (this.validateData()) {

                        //Address address = new Address();
                        //address.setCity(this.binding.city.getText().toString());
                        //address.setProvince(this.binding.provPicker.getSelectedItem().toString());
                        //address.setPostalCode(this.binding.zipCode.getText().toString());

                        this.createUser();

                        Intent CRegister3 = new Intent(this, StartupActivity.class);

                    }
                }
            }
        }
    }

    private boolean validateData(){
        //if(this.binding.nameFirst.getText().toString().isEmpty()
        //        || this.binding.nameLast.getText().toString().isEmpty()
        //        || this.binding.city.getText().toString().isEmpty()
        //        || this.binding.zipCode.getText().toString().isEmpty()
        //        || this.binding.address1.getText().toString().isEmpty()
        //        || this.binding.address2.getText().toString().isEmpty()
        //        || this.binding.phoneNum.getText().toString().isEmpty()){

            Toast.makeText(CustomerRegistrationActivity3.this, "Testing", Toast.LENGTH_SHORT).show();
            return true;
        }
        //return true;

    private void createUser() {
        AppUser appUser = new AppUser();
        PaymentMethod paymentMethod = new PaymentMethod();
        String password = this.currentIntent.getStringExtra("EXTRA_PASSWORD");

        String cCardNum = this.binding.cCardNum.getText().toString();
        String cCardNumLong = this.binding.cCardNum.getText().toString();

        //Example
        String expExample = "1/1";
        String secCodeExample = "1/1";

        paymentMethod.setCardNumber(cCardNum);
        paymentMethod.setExpiry(expExample);
        paymentMethod.setSecurityCode(secCodeExample);

        if(currentIntent != null){
            this.userAddress = (Address)this.currentIntent.getSerializableExtra("EXTRA_ADDRESS_OBJ");
        }

        appUser.setEmail(this.currentIntent.getStringExtra("EXTRA_EMAIL"));
        appUser.setFirstName(this.currentIntent.getStringExtra("EXTRA_FNAME"));
        appUser.setMiddleInitial(this.currentIntent.getStringExtra("EXTRA_MNAME"));
        appUser.setLastName(this.currentIntent.getStringExtra("EXTRA_LNAME"));
        appUser.setPhone(this.currentIntent.getStringExtra("EXTRA_PHONE"));
        appUser.addAddress(this.userAddress);
        try {
            this.userDOB = new SimpleDateFormat("MMM dd yyyy").parse(currentIntent.getStringExtra("EXTRA_DATE"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        appUser.setBirthdate(userDOB);

        Calendar calendarUser = Calendar.getInstance();
        calendarUser.setTime(userDOB);
        Calendar calendar = Calendar.getInstance();

        appUser.setAge(calendar.get(Calendar.YEAR) - calendarUser.get(Calendar.YEAR));
        appUser.setUserType("Customer");

        appUser.addPaymentMethod(createPayment());
        // TO-DO: NEED TO ADD PAYMENT INFORMATION

        // Create user in database
        app.getEmailPassword().registerUserAsync(appUser.getEmail(), password, it -> {
            if (it.isSuccess()) {
                Log.i("EXAMPLE", "Successfully registered user.");
            } else {
                Log.e("EXAMPLE", "Failed to register user: " + it.getError().getErrorMessage());
                appUser.deleteFromRealm();
                Intent mainIntent = new Intent(this, SignupActivity.class);
                mainIntent.putExtra("EXTRA_SIGNUP_SUCCESS", false);
                startActivity(mainIntent);
            }
        });

        // Create AppUser with associated User
        Credentials credentials = Credentials.emailPassword(appUser.getEmail(), password);
        app.loginAsync(credentials, result -> {
            Log.e("SSS", "In async: " + app.currentUser().getId());
            SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart").build();
            Realm backgroundRealm = Realm.getInstance(config);

            backgroundRealm.executeTransactionAsync(transactionRealm -> {
                // insert the user
                transactionRealm.insert(appUser);
            });
        });
        //AppUser appUser = new AppUser();
        //appUser.setUserType("Customer");
        //appUser.setEmail(this.binding.email.getText().toString());
    }

    private PaymentMethod createPayment(){
        PaymentMethod pMethod = new PaymentMethod();
        pMethod.setCardNumber(this.binding.cCardNum.getText().toString());
        pMethod.setSecurityCode(this.binding.cCardCCV.getText().toString());
        pMethod.setSecurityCode("123");

        Address cAddress = new Address();
        cAddress.setAddress1(this.binding.cCardAddress1.getText().toString());
        cAddress.setAddress2(this.binding.cCardAddress2.getText().toString());

        return pMethod;
    }
}
