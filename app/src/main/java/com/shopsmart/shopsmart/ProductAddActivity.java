package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ProductAddActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ProductAddActivity extends AppCompatActivity {
    private ProductAddActivityBinding binding;
    private Shop shop;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProductAddActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email)) {
                        user = u;
                    }
                }
                RealmResults<Shop> allShops = ShopSmartApp.realm.where(Shop.class).findAll();
                List<ObjectId> shopIds = user.getShops();
                ArrayList<Shop> shops = new ArrayList<>();
                for (Shop s : allShops) {
                    for (ObjectId o : shopIds) {
                        if (s.getId().equals(o))
                            shops.add(s);
                    }
                }
                index = getIntent().getIntExtra("EXTRA_INDEX", 0);
                if (index >= 0) shop = shops.get(index);
            }
        });

        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ProductAddActivity.this, ShopInventoryActivity.class)
                        .putExtra("EXTRA_INDEX", index)));

        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(realm -> {
                            Product product = new Product(
                                    shop.getId(),
                                    binding.spinnerSub.getSelectedItem().toString(),
                                    binding.edtTextProductName.getText().toString(),
                                    binding.edtTextDesc.getText().toString(),
                                    Double.parseDouble(binding.edtTextPrice.getText().toString()),
                                    Integer.parseInt(binding.edtTextStock.getText().toString()));
                            realm.insert(product);
                            shop.addProduct(product.getId());
                        });
                        Intent nextSignUpScreen = new Intent(ProductAddActivity.this, ShopInventoryActivity.class);
                        nextSignUpScreen.putExtra("EXTRA_ADD_PRODUCT_SUCCESS", true);
                        nextSignUpScreen.putExtra("EXTRA_INDEX", index);
                        startActivity(nextSignUpScreen);
                    }
                });
            }
        });

        binding.spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Clothing, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 1) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Kitchen, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 2) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Food, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 3) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Electronics, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 4) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Household, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 5) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Pharmaceutical, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 6) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Pets, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 7) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.OfficeArtSchool, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 8) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductAddActivity.this, R.array.Toys, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 9) {
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
        if (binding.edtTextProductName.getText().toString().isEmpty()) {
            binding.edtTextProductName.setError("Product name cannot be empty");
            valid = false;
        }
        if (binding.edtTextPrice.getText().toString().isEmpty()) {
            binding.edtTextPrice.setError("Product price cannot be empty");
            valid = false;
        }
        if (Double.parseDouble(binding.edtTextPrice.getText().toString()) < 0) {
            binding.edtTextPrice.setError("Product price cannot be negative value");
            valid = false;
        }
        if (binding.edtTextStock.getText().toString().isEmpty()) {
            binding.edtTextStock.setError("Stock cannot be empty");
            valid = false;
        }
        if (Integer.parseInt(binding.edtTextStock.getText().toString()) < 0) {
            binding.edtTextStock.setError("Stock cannot be negative value");
            valid = false;
        }
        return valid;
    }
}