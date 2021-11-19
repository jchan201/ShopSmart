package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ProductViewActivityBinding;

import org.bson.types.ObjectId;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ProductViewActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    Intent currIntent;
    String userEmail;
    String userPass;
    ObjectId productId;
    Product product;

    private ProductViewActivityBinding binding;
    private App app;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProductViewActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access realm
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.productId = (ObjectId) currIntent.getSerializableExtra("EXTRA_PRODUCT_ID");
        }

        Credentials credentials = Credentials.emailPassword(userEmail, userPass);
        app.loginAsync(credentials, result -> {
            if (result.isSuccess()) {
                Log.v("LOGIN", "Successfully authenticated using email and password.");

                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).allowWritesOnUiThread(true).build();
                realm = Realm.getInstance(config);

                product = realm.where(Product.class).equalTo("_id", productId).findFirst();

                binding.productNameText.setText(product.getName());
                binding.edtTextDesc.setText(product.getDesc());
                binding.typeText.setText(product.getProductType());
                binding.priceText.setText(Double.toString(product.getPrice()));
                binding.stockCountText.setText(Integer.toString(product.getStock()));

            } else {
                Log.v("LOGIN", "Failed to authenticate using email and password.");
            }
        });

        binding.btnBack.setOnClickListener(view -> {
            realm.close();
            startActivity(new Intent(ProductViewActivity.this, ShopInventoryActivity.class)
                    .putExtra("EXTRA_EMAIL", userEmail)
                    .putExtra("EXTRA_PASS", userPass));
        });

        binding.btnEdit.setOnClickListener(view -> {
            realm.close();
            startActivity(new Intent(ProductViewActivity.this, ProductUpdateActivity.class)
                    .putExtra("EXTRA_EMAIL", userEmail)
                    .putExtra("EXTRA_PASS", userPass)
                    .putExtra("EXTRA_PRODUCT_ID", productId));
        });

        binding.addStockBtn.setOnClickListener(view -> {
            binding.addStockBtn.setVisibility(View.INVISIBLE);
            binding.stockCountText.setVisibility(View.INVISIBLE);

            binding.stockCountEditText.setVisibility(View.VISIBLE);
            binding.stockCountEditText.setText(Integer.toString(product.getStock()));

            binding.saveStockBtn.setVisibility(View.VISIBLE);
            binding.cancelBtn.setVisibility(View.VISIBLE);
        });

        binding.cancelBtn.setOnClickListener(view -> {
            binding.addStockBtn.setVisibility(View.VISIBLE);
            binding.stockCountText.setVisibility(View.VISIBLE);

            binding.stockCountEditText.setVisibility(View.INVISIBLE);
            binding.saveStockBtn.setVisibility(View.INVISIBLE);
            binding.cancelBtn.setVisibility(View.INVISIBLE);
        });

        binding.saveStockBtn.setOnClickListener(view -> {
            if(Integer.parseInt(binding.stockCountEditText.getText().toString()) >= 0){

                realm.executeTransaction(transactionRealm -> {
                    product.setStock(Integer.parseInt(binding.stockCountEditText.getText().toString()));
                });

                binding.addStockBtn.setVisibility(View.VISIBLE);
                binding.stockCountText.setVisibility(View.VISIBLE);
                binding.stockCountText.setText(Integer.toString(product.getStock()));

                binding.stockCountEditText.setVisibility(View.INVISIBLE);
                binding.saveStockBtn.setVisibility(View.INVISIBLE);
                binding.cancelBtn.setVisibility(View.INVISIBLE);
            }
            else{
                binding.stockCountEditText.setError("Stock count cannot be less than 0");
            }
        });
    }
}