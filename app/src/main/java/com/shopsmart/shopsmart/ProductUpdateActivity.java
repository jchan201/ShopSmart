package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ProductUpdateActivityBinding;

import org.bson.types.ObjectId;

public class ProductUpdateActivity extends AppCompatActivity {
    private ProductUpdateActivityBinding binding;
    private ObjectId productId;
    private Product product;
    private int subIndex = 0;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProductUpdateActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        if (currIntent != null) {
            index = currIntent.getIntExtra("EXTRA_SHOP_INDEX", 0);
            productId = (ObjectId) currIntent.getSerializableExtra("EXTRA_PRODUCT_ID");
        }

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                product = ShopSmartApp.realm.where(Product.class).equalTo("_id", productId).findFirst();
                binding.edtTextProductName.setText(product.getName());
                binding.edtTextDesc.setText(product.getDesc());
                binding.edtTextPrice.setText(Double.toString(product.getPrice()));
                productTypeSetting();
            }
        });
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ProductUpdateActivity.this, ShopInventoryActivity.class).putExtra("EXTRA_INDEX", index)));
        binding.btnSave.setOnClickListener(view -> {
            if (validation()) {
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(realm -> {
                            product.setName(binding.edtTextProductName.getText().toString());
                            product.setDesc(binding.edtTextDesc.getText().toString());
                            product.setProductType(binding.spinnerSub.getSelectedItem().toString());
                            product.setPrice(Double.parseDouble(binding.edtTextPrice.getText().toString()));
                        });
                        Intent nextSignUpScreen = new Intent(ProductUpdateActivity.this, ShopInventoryActivity.class);
                        nextSignUpScreen.putExtra("EXTRA_UPDATE_PRODUCT_SUCCESS", true);
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
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Clothing, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 1) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Kitchen, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 2) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Food, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 3) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Electronics, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 4) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Household, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 5) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Pharmaceutical, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 6) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Pets, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 7) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.OfficeArtSchool, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 8) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Toys, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                if (position == 9) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.SportsOutdoors, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerSub.setAdapter(adapter);
                }
                binding.spinnerSub.setSelection(subIndex, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductUpdateActivity.this, R.array.Clothing, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerSub.setAdapter(adapter);
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

    private void productTypeSetting() {
        int main = 0;
        int sub = 0;

        String[] mainType = getResources().getStringArray(R.array.MainType);
        String[] clothing = getResources().getStringArray(R.array.Clothing);
        String[] kitchen = getResources().getStringArray(R.array.Kitchen);
        String[] food = getResources().getStringArray(R.array.Food);
        String[] electronics = getResources().getStringArray(R.array.Electronics);
        String[] household = getResources().getStringArray(R.array.Household);
        String[] pharmaceutical = getResources().getStringArray(R.array.Pharmaceutical);
        String[] pets = getResources().getStringArray(R.array.Pets);
        String[] officeArtSchool = getResources().getStringArray(R.array.OfficeArtSchool);
        String[] toys = getResources().getStringArray(R.array.Toys);
        String[] sportsOutdoors = getResources().getStringArray(R.array.SportsOutdoors);

        for (int i = 0; i < mainType.length; i++) {
            if (i == 0) {
                for (int y = 0; y < clothing.length; y++) {
                    if (clothing[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 1) {
                for (int y = 0; y < kitchen.length; y++) {
                    if (kitchen[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 2) {
                for (int y = 0; y < food.length; y++) {
                    if (food[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 3) {
                for (int y = 0; y < electronics.length; y++) {
                    if (electronics[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 4) {
                for (int y = 0; y < household.length; y++) {
                    if (household[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 5) {
                for (int y = 0; y < pharmaceutical.length; y++) {
                    if (pharmaceutical[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 6) {
                for (int y = 0; y < pets.length; y++) {
                    if (pets[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 7) {
                for (int y = 0; y < officeArtSchool.length; y++) {
                    if (officeArtSchool[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 8) {
                for (int y = 0; y < toys.length; y++) {
                    if (toys[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
            if (i == 9) {
                for (int y = 0; y < sportsOutdoors.length; y++) {
                    if (sportsOutdoors[y].equals(product.getProductType())) {
                        sub = y;
                        main = i;
                        break;
                    }
                }
            }
        }
        subIndex = sub;
        binding.spinnerMain.setSelection(main, false);
    }
}