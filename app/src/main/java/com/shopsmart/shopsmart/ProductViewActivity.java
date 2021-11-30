package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ProductViewActivityBinding;

import org.bson.types.ObjectId;

public class ProductViewActivity extends AppCompatActivity {
    private ProductViewActivityBinding binding;
    private ObjectId productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProductViewActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = (ObjectId) getIntent().getSerializableExtra("EXTRA_PRODUCT_ID");
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                product = ShopSmartApp.realm.where(Product.class).equalTo("_id", productId).findFirst();
                binding.productNameText.setText(product.getName());
                binding.edtTextDesc.setText(product.getDesc());
                binding.typeText.setText(product.getProductType());
                binding.priceText.setText(Double.toString(product.getPrice()));
                binding.stockCountText.setText(Integer.toString(product.getStock()));
            }
        });
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ProductViewActivity.this, ShopInventoryActivity.class)));
        binding.btnEdit.setOnClickListener(view ->
                startActivity(new Intent(ProductViewActivity.this, ProductUpdateActivity.class)
                        .putExtra("EXTRA_PRODUCT_ID", productId)));
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
            if (Integer.parseInt(binding.stockCountEditText.getText().toString()) >= 0) {
                ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
                    if (result.isSuccess()) {
                        ShopSmartApp.instantiateRealm();
                        ShopSmartApp.realm.executeTransaction(transactionRealm ->
                                product.setStock(Integer.parseInt(binding.stockCountEditText.getText().toString())));
                    }
                });
                binding.addStockBtn.setVisibility(View.VISIBLE);
                binding.stockCountText.setVisibility(View.VISIBLE);
                binding.stockCountText.setText(Integer.toString(product.getStock()));
                binding.stockCountEditText.setVisibility(View.INVISIBLE);
                binding.saveStockBtn.setVisibility(View.INVISIBLE);
                binding.cancelBtn.setVisibility(View.INVISIBLE);
            } else
                binding.stockCountEditText.setError("Stock count cannot be less than 0");
        });
    }
}