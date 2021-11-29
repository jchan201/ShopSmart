package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileAddPaymentActivity1Binding;

import java.util.Calendar;

public class ShopOwnerProfileAddPaymentsActivity1 extends AppCompatActivity {
    private ShopownerProfileAddPaymentActivity1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileAddPaymentActivity1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.edtTextExpiryDateYear.setText(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
        binding.btnNext.setOnClickListener(view -> {
            if (validation()) {
                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setCardNumber(binding.edtTextCardNum.getText().toString());
                paymentMethod.setName(binding.edtTextCardName.getText().toString());
                paymentMethod.setSecurityCode(binding.edtTextCCV.getText().toString());
                paymentMethod.setExpiry(this.binding.spinnerTextExpiryDateMonth.getSelectedItem().toString() + "/" + this.binding.edtTextExpiryDateYear.getText().toString());
                startActivity(new Intent(ShopOwnerProfileAddPaymentsActivity1.this, ShopOwnerProfileAddPaymentsActivity2.class)
                        .putExtra("EXTRA_PAYMENT_METHOD_OBJ", paymentMethod));
            }
        });
        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileAddPaymentsActivity1.this, ShopOwnerProfilePaymentsActivity.class)));
    }

    private boolean validation() {
        boolean valid = true;
        if (this.binding.edtTextCardName.getText().toString().isEmpty()) {
            this.binding.edtTextCardName.setError("Name on card cannot be empty");
            valid = false;
        }
        if (this.binding.edtTextCardNum.getText().toString().isEmpty()) {
            this.binding.edtTextCardNum.setError("Card number cannot be empty");
            valid = false;
        }
        if (this.binding.edtTextCCV.getText().toString().isEmpty()) {
            this.binding.edtTextCCV.setError("CCV cannot be empty");
            valid = false;
        }

        if(this.binding.edtTextCCV.getText().toString().length() < 3){
            this.binding.edtTextCCV.setError("CCV length invalid");
            valid = false;
        }

        if (this.binding.edtTextExpiryDateYear.getText().toString().isEmpty()) {
            this.binding.edtTextExpiryDateYear.setError("Expire year cannot be empty");
            valid = false;
        }
        if (Integer.parseInt(this.binding.edtTextExpiryDateYear.getText().toString()) < Calendar.getInstance().get(Calendar.YEAR)) {
            this.binding.edtTextExpiryDateYear.setError("Card expired");
            valid = false;
        }
        return valid;
    }
}