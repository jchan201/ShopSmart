package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ProductAddActivityBinding;
import com.shopsmart.shopsmart.databinding.ShopRegisterActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ProductAddActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    AppUser user;
    List<ObjectId> shopIds;
    ArrayList<Shop> shops;
    Product product;
    Address address;
    String productType;

    Shop shop;
    int index = 0;
    int total = 0;
    private ProductAddActivityBinding binding;
    private App app;
    private Realm realm;
    SyncConfiguration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProductAddActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.index = currIntent.getIntExtra("EXTRA_INDEX", index);
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                realm = Realm.getInstance(config);

                RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
                for (AppUser u : users) {
                    if (u.getEmail().equals(userEmail)) {
                        user = u;
                    }
                }
                RealmResults<Shop> allShops = realm.where(Shop.class).findAll();
                shopIds = user.getShops();
                shops = new ArrayList<>();
                for (Shop s : allShops) {
                    for (ObjectId o : shopIds) {
                        if (s.getId().equals(o))
                            shops.add(s);
                    }
                }

                total = shops.size();

                if(index >= 0){
                    shop = shops.get(index);
                    address = shop.getAddress();
                }
            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnCancel.setOnClickListener(view -> startActivity(new Intent(ProductAddActivity.this, ShopListActivity.class)
                .putExtra("EXTRA_EMAIL", userEmail)
                .putExtra("EXTRA_PASS", userPass)));

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                product = new Product();

                product.setName(binding.edtTextProductName.getText().toString());
                product.setDesc(binding.edtTextDesc.getText().toString());
                product.setPrice(Double.parseDouble(binding.edtTextPrice.getText().toString()));

                //TODO set product type
                product.setProductType(binding.spinnerSub.getSelectedItem().toString());

                realm.executeTransaction(realm -> {
                    Log.d("Something", "Executing transaction...");

                    realm.insert(product);
                    shop.addProduct(product.getId());

                });

                Intent nextSignUpScreen = new Intent(ProductAddActivity.this, ShopRegister2.class);
                nextSignUpScreen.putExtra("EXTRA_EMAIL", userEmail);
                nextSignUpScreen.putExtra("EXTRA_PASS", userPass);
                startActivity(nextSignUpScreen);
            }
        });

        binding.spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position == 0){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Clothing, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 1){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Kitchen, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 2){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Food, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 3){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Electronics, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 4){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Household, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 5){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Pharmaceutical, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 6){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Pets, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 7){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.OfficeArtSchool, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 8){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Toys, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }

                if(position == 9){
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.SportsOutdoors, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean validation() {
        boolean valid = true;

        if (this.binding.edtTextProductName.getText().toString().isEmpty()) {
            this.binding.edtTextProductName.setError("Product name cannot be empty");
            valid = false;
        }

        if (this.binding.edtTextPrice.getText().toString().isEmpty()) {
            this.binding.edtTextPrice.setError("Product price cannot be empty");
            valid = false;
        }

        if (Double.parseDouble(this.binding.edtTextPrice.getText().toString()) < 0) {
            this.binding.edtTextPrice.setError("Product price cannot be negative value");
            valid = false;
        }

        return valid;
    }
}