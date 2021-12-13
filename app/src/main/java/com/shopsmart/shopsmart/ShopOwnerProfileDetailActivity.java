package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerDetailActivityBinding;

import io.realm.RealmResults;

public class ShopOwnerProfileDetailActivity extends AppCompatActivity {
    private ShopownerDetailActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerDetailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getBooleanExtra("EXTRA_RESET_PASSWORD_SUCCESS", false))
            Toast.makeText(this, "Successfully reset password.", Toast.LENGTH_SHORT).show();

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email))
                        user = users.get(i);
                }
                binding.textEmail.setText(user.getEmail());
                if(user.getMiddleInitial().isEmpty()){
                    binding.textName.setText(user.getFirstName() + " " + user.getLastName());
                }
                else{
                    binding.textName.setText(user.getFirstName() + " " + user.getMiddleInitial() + ". " + user.getLastName());
                }
                binding.textDOB.setText(user.getBirthdateString());
                binding.textPhone.setText(user.getPhone());
            }
        });

        binding.btnProfile.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileDetailActivity.this, ShopOwnerDetailUpdateProfileActivity.class)));

        binding.btnResetPassword.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileDetailActivity.this, ShopOwnerDetailResetPasswordActivity.class)));

        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileDetailActivity.this, ShopOwnerProfileActivity.class)));
    }
}