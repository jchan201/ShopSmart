package com.shopsmart.shopsmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.shopsmart.shopsmart.databinding.CustomerProfileBinding;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class CustomerProfileActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private DatePickerDialog dpd;
    private CustomerProfileBinding binding;
    Intent currentIntent;

    private Realm realm;
    private App app;

    String userEmail;
    String userPass;

    AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        binding = CustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(R.layout.customer_profile);

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        this.currentIntent = this.getIntent();

        if(this.currentIntent != null){
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result ->{
            if(result.isSuccess()){
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart")
                        .allowWritesOnUiThread(true)
                        .build();
                realm = Realm.getInstance(config);

                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                for(int x = 0; x < users.size(); x++){
                    if(users.get(x).getEmail().equals(userEmail)){
                        user = users.get(x);
                    }
                }
                binding.fullName.setText(user.getFirstName() + " " + user.getMiddleInitial() + " " + user.getLastName());
                binding.queryCity.setText(user.getAddress().getCity());



            }
        });

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menuHome:
                //realm.close();
                Intent homeIntent = new Intent(CustomerProfileActivity.this, CustomerDashboardActivity.class);
                homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                homeIntent.putExtra("EXTRA_PASS", userPass);
                //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                startActivity(homeIntent);
                //break;
        }

        return true;
    }

    @Override
    protected void onDestroy(){
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
