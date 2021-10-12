package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerDashboard1Binding;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;


public class CustomerDashboardActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private CustomerDashboard1Binding binding;
    Intent currentIntent;

    String userEmail;
    String userPass;

    private App app;

    private Realm realm;

    AppUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = CustomerDashboard1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        this.currentIntent = this.getIntent();

        if(this.currentIntent != null){
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");
        }
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
            case R.id.Profile:
                Intent settingsIntent = new Intent(this, CustomerProfileActivity.class);
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                startActivity(settingsIntent);
                break;
        }

        return true;
    }
}
