package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.shopsmart.shopsmart.databinding.CustomerProfileBinding;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerProfileActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private CustomerProfileBinding binding;
    Intent currentIntent;

    String userEmail;
    String userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.currentIntent = this.getIntent();

        if(this.currentIntent != null){
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_profile);

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
                Intent homeIntent = new Intent(CustomerProfileActivity.this, CustomerDashboardActivity.class);
                homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                homeIntent.putExtra("EXTRA_PASS", userPass);
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                startActivity(homeIntent);
                break;
        }

        return true;
    }
}
