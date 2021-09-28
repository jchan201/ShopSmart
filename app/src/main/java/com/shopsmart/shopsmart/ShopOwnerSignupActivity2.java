package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerSignupActivity2Binding;
import com.shopsmart.shopsmart.databinding.ShopownerSignupActivity3Binding;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShopOwnerSignupActivity2 extends AppCompatActivity {
    private ShopownerSignupActivity2Binding binding;
    String userFName;
    String userMName;
    String userLName;
    String userEmail;
    String userPass;
    String userDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerSignupActivity2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = this.getIntent();
        if (currIntent != null) {
            this.userFName = currIntent.getStringExtra("EXTRA_FNAME");
            this.userMName = currIntent.getStringExtra("EXTRA_MNAME");
            this.userLName = currIntent.getStringExtra("EXTRA_LNAME");
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASSWORD");
            this.userDOB = currIntent.getStringExtra("EXTRA_DOB");
        }

        //cancel go back sign up selection page
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopOwnerSignupActivity2.this, ShopOwnerSignupActivity.class));
            }
        });

        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation()){
                    //Create Address Object
                    Address address = new Address(binding.edtTextAdd1.getText().toString(),
                            binding.edtTextAdd2.getText().toString(), "Canada",
                            binding.spinnerProvince.getSelectedItem().toString(),
                            binding.edtTextCity.getText().toString(),
                            binding.edtTextZip.getText().toString());

                    Intent nextSignUpScreen = new Intent(ShopOwnerSignupActivity2.this, ShopOwnerSignupActivity3.class);
                    nextSignUpScreen.putExtra("EXTRA_ADDRESS_OBJ", address);
                    nextSignUpScreen.putExtra("EXTRA_EMAIL", userEmail);
                    nextSignUpScreen.putExtra("EXTRA_PASSWORD", userPass);
                    nextSignUpScreen.putExtra("EXTRA_FNAME", userFName);
                    nextSignUpScreen.putExtra("EXTRA_MNAME", userMName);
                    nextSignUpScreen.putExtra("EXTRA_LNAME", userLName);
                    nextSignUpScreen.putExtra("EXTRA_DOB", userDOB);
                    nextSignUpScreen.putExtra("EXTRA_PHONE", binding.edtTextPhoneNum.getText().toString());
                    startActivity(nextSignUpScreen);
                }
            }
        });
    }

    private boolean validation(){
        boolean valid = true;

        if(this.binding.edtTextAdd1.getText().toString().isEmpty()){
            this.binding.edtTextAdd1.setError("Address Line 1 cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextCity.getText().toString().isEmpty()){
            this.binding.edtTextCity.setError("City cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextZip.getText().toString().isEmpty()){
            this.binding.edtTextZip.setError("Email cannot be empty");
            valid = false;
        }

        if(!this.binding.edtTextZip.getText().toString().matches("([A-Z]\\d[A-Z]\\s\\d[A-Z]\\d)")){
            this.binding.edtTextZip.setError("Postal code does not match schema: A1A 1A1");
            valid = false;
        }

        if(this.binding.edtTextAdd1.getText().toString().isEmpty()){
            this.binding.edtTextAdd1.setError("Email cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextPhoneNum.getText().toString().isEmpty()){
            this.binding.edtTextPhoneNum.setError("Password cannot be empty");
            valid = false;
        }

        return valid;
    }
}