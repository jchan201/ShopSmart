package com.shopsmart.shopsmart;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.shopsmart.shopsmart.databinding.CustomerProfileBinding;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerProfileActivity extends AppCompatActivity {

CustomerProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
            case R.id.Profile:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
