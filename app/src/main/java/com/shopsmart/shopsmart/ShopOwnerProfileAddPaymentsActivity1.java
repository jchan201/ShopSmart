package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileAddPaymentActivity1Binding;

import java.util.Calendar;

public class ShopOwnerProfileAddPaymentsActivity1 extends AppCompatActivity {
    private ShopownerProfileAddPaymentActivity1Binding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileAddPaymentActivity1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        binding.edtTextExpiryDateYear.setText(Integer.toString(year));

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
        }

        binding.btnNext.setOnClickListener(view -> {
            if(validation()){
                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setCardNumber(binding.edtTextCardNum.getText().toString());
                paymentMethod.setName(binding.edtTextCardName.getText().toString());
                paymentMethod.setSecurityCode(binding.edtTextCCV.getText().toString());
                paymentMethod.setExpiry(this.binding.edtTextExpiryDateMonth.getSelectedItem().toString()+"/"+this.binding.edtTextExpiryDateYear.getText().toString());

                Intent intentToNext = new Intent(ShopOwnerProfileAddPaymentsActivity1.this, ShopOwnerProfileAddPaymentsActivity2.class);
                intentToNext.putExtra("EXTRA_PASS", userPass);
                intentToNext.putExtra("EXTRA_EMAIL", userEmail);
                intentToNext.putExtra("EXTRA_PAYMENT_METHOD_OBJ", paymentMethod);
                startActivity(intentToNext);
            }
        });

        binding.btnCancel.setOnClickListener(view -> {
            Intent intentToBack = new Intent(ShopOwnerProfileAddPaymentsActivity1.this, ShopOwnerProfilePaymentsActivity.class);
            intentToBack.putExtra("EXTRA_PASS", userPass);
            intentToBack.putExtra("EXTRA_EMAIL", userEmail);
            startActivity(intentToBack);
        });
    }

    private boolean validation() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        boolean valid = true;

        if(this.binding.edtTextCardName.getText().toString().isEmpty()){
            this.binding.edtTextCardName.setError("Name on card cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextCardNum.getText().toString().isEmpty()){
            this.binding.edtTextCardNum.setError("Card number cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextCCV.getText().toString().isEmpty()){
            this.binding.edtTextCCV.setError("CCV cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextExpiryDateYear.getText().toString().isEmpty()){
            this.binding.edtTextExpiryDateYear.setError("Expire year cannot be empty");
            valid = false;
        }

        if(Integer.parseInt(this.binding.edtTextExpiryDateYear.getText().toString()) < year){
            this.binding.edtTextExpiryDateYear.setError("Card expired");
            valid = false;
        }

        return valid;
    }
}