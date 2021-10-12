package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerDashboard1Binding;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;


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

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result ->{
            if(result.isSuccess()){
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                realm = Realm.getInstance(config);

                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();

                for(int x = 0; x < users.size(); x++){
                    if(users.get(x).getEmail().equals(userEmail)){
                        user = users.get(x);
                    }
                }
                binding.userName.setText(user.getEmail());
            }
        });
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

    @Override
    protected void onDestroy(){
        super.onDestroy();;
        binding = null;

        app.currentUser().logOutAsync(result -> {
            if(result.isSuccess()){
                Log.v("LOGOUT", "Successfully logged out.");
            }else{
                Log.e("LOGOUT", "Failed to log out, error: " + result.getError());
            }
        });
    }
}
