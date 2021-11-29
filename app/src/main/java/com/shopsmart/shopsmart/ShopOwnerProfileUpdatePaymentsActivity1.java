package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileUpdatePaymentActivity1Binding;

import java.util.Calendar;

public class ShopOwnerProfileUpdatePaymentsActivity1 extends AppCompatActivity {
    private ShopownerProfileUpdatePaymentActivity1Binding binding;
    private PaymentMethod paymentMethod;
    private Address billingAddress;
    private int updateIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileUpdatePaymentActivity1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        paymentMethod = (PaymentMethod) currIntent.getSerializableExtra("EXTRA_PAYMENT_METHOD_OBJ");
        billingAddress = (Address) currIntent.getSerializableExtra("EXTRA_BILLING_ADDRESS_OBJ");
        updateIndex = currIntent.getIntExtra("EXTRA_UPDATE_INDEX", 0);

        int index = 0;
        String[] months = getResources().getStringArray(R.array.months);
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(paymentMethod.getExpiry().substring(0, paymentMethod.getExpiry().indexOf("/")))) {
                index = i;
                break;
            }
        }
        binding.edtTextCardName.setText(paymentMethod.getName());
        binding.edtTextCardNum.setText(paymentMethod.getCardNumber());
        binding.edtTextCCV.setText(paymentMethod.getSecurityCode());
        binding.spinnerTextExpiryDateMonth.setSelection(index);
        binding.edtTextExpiryDateYear.setText(paymentMethod.getExpiry().substring(paymentMethod.getExpiry().indexOf("/") + 1).trim());

        binding.btnNext.setOnClickListener(view -> {
            if (validation()) {
                paymentMethod.setCardNumber(binding.edtTextCardNum.getText().toString());
                paymentMethod.setName(binding.edtTextCardName.getText().toString());
                paymentMethod.setSecurityCode(binding.edtTextCCV.getText().toString());
                paymentMethod.setExpiry(binding.spinnerTextExpiryDateMonth.getSelectedItem().toString() + "/" + binding.edtTextExpiryDateYear.getText().toString());

                Intent intentToNext = new Intent(ShopOwnerProfileUpdatePaymentsActivity1.this, ShopOwnerProfileUpdatePaymentsActivity2.class);
                intentToNext.putExtra("EXTRA_PAYMENT_METHOD_OBJ", paymentMethod);
                intentToNext.putExtra("EXTRA_BILLING_ADDRESS_OBJ", billingAddress);
                intentToNext.putExtra("EXTRA_UPDATE_INDEX", updateIndex);
                startActivity(intentToNext);
            }
        });

        binding.btnCancel.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfileUpdatePaymentsActivity1.this, ShopOwnerProfilePaymentsActivity.class)));
    }

    private boolean validation() {
        boolean valid = true;
        if (binding.edtTextCardName.getText().toString().isEmpty()) {
            binding.edtTextCardName.setError("Name on card cannot be empty");
            valid = false;
        }
        if (binding.edtTextCardNum.getText().toString().isEmpty()) {
            binding.edtTextCardNum.setError("Card number cannot be empty");
            valid = false;
        }
        if (binding.edtTextCCV.getText().toString().isEmpty()) {
            binding.edtTextCCV.setError("CCV cannot be empty");
            valid = false;
        }

        if(binding.edtTextCCV.getText().toString().length() < 3){
            binding.edtTextCCV.setError("CCV length invalid");
            valid = false;
        }

        if (binding.edtTextExpiryDateYear.getText().toString().isEmpty()) {
            binding.edtTextExpiryDateYear.setError("Expire year cannot be empty");
            valid = false;
        }
        if (Integer.parseInt(binding.edtTextExpiryDateYear.getText().toString()) < Calendar.getInstance().get(Calendar.YEAR)) {
            binding.edtTextExpiryDateYear.setError("Card expired");
            valid = false;
        }
        return valid;
    }
}