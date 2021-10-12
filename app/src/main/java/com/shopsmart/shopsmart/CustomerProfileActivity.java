package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.shopsmart.shopsmart.databinding.CustomerProfileBinding;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import io.realm.mongodb.App;

public class CustomerProfileActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private CustomerProfileBinding binding;
    Intent currentIntent;

    private Realm realm;
    private App app;

    String userEmail;
    String userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        binding = CustomerProfileBinding.inflate(getLayoutInflater());
        setContentView(R.layout.customer_profile);

        this.currentIntent = this.getIntent();

        if(this.currentIntent != null){
            this.userEmail = currentIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currentIntent.getStringExtra("EXTRA_PASS");
        }

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
                realm.close();
                Intent homeIntent = new Intent(CustomerProfileActivity.this, CustomerDashboardActivity.class);
                homeIntent.putExtra("EXTRA_EMAIL", userEmail);
                homeIntent.putExtra("EXTRA_PASS", userPass);
                //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                startActivity(homeIntent);
                break;
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
}
