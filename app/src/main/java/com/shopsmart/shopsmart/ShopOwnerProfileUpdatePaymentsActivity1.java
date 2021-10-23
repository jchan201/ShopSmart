package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfileAddPaymentActivity1Binding;
import com.shopsmart.shopsmart.databinding.ShopownerProfileUpdatePaymentActivity1Binding;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopOwnerProfileUpdatePaymentsActivity1 extends AppCompatActivity {
    private ShopownerProfileUpdatePaymentActivity1Binding binding;
    Intent currIntent;

    String userEmail;
    String userPass;

    PaymentMethod paymentMethod;
    Address billingAddress;
    int updateIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfileUpdatePaymentActivity1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get Intent
        this.currIntent = this.getIntent();

        if (this.currIntent != null) {
            this.userEmail = currIntent.getStringExtra("EXTRA_EMAIL");
            this.userPass = currIntent.getStringExtra("EXTRA_PASS");
            this.paymentMethod = (PaymentMethod) this.currIntent.getSerializableExtra("EXTRA_PAYMENT_METHOD_OBJ");
            this.billingAddress = (Address) this.currIntent.getSerializableExtra("EXTRA_BILLING_ADDRESS_OBJ");
            this.updateIndex = currIntent.getIntExtra("EXTRA_UPDATE_INDEX", updateIndex);
        }

        int month = Integer.parseInt(paymentMethod.getExpiry().substring(0, paymentMethod.getExpiry().indexOf("/")));

        int index = 0;
        String[] months = getResources().getStringArray(R.array.months);
        for(int i = 0; i < months.length; i++){
            if(months[i].equals(month)){
                index = i;
                break;
            }
        }

        this.binding.edtTextCardName.setText(paymentMethod.getName());
        this.binding.edtTextCardNum.setText(paymentMethod.getCardNumber());
        this.binding.edtTextCCV.setText(paymentMethod.getSecurityCode());
        this.binding.spinnerTextExpiryDateMonth.setSelection(index);
        this.binding.edtTextExpiryDateYear.setText(paymentMethod.getExpiry().substring(paymentMethod.getExpiry().indexOf("/")+1).trim());

        binding.btnNext.setOnClickListener(view -> {
            if(validation()){
                paymentMethod.setCardNumber(binding.edtTextCardNum.getText().toString());
                paymentMethod.setName(binding.edtTextCardName.getText().toString());
                paymentMethod.setSecurityCode(binding.edtTextCCV.getText().toString());
                paymentMethod.setExpiry(this.binding.spinnerTextExpiryDateMonth.getSelectedItem().toString()+"/"+this.binding.edtTextExpiryDateYear.getText().toString());

                Intent intentToNext = new Intent(ShopOwnerProfileUpdatePaymentsActivity1.this, ShopOwnerProfileUpdatePaymentsActivity2.class);
                intentToNext.putExtra("EXTRA_PASS", userPass);
                intentToNext.putExtra("EXTRA_EMAIL", userEmail);
                intentToNext.putExtra("EXTRA_PAYMENT_METHOD_OBJ", paymentMethod);
                intentToNext.putExtra("EXTRA_BILLING_ADDRESS_OBJ", billingAddress);
                intentToNext.putExtra("EXTRA_UPDATE_INDEX", updateIndex);
                startActivity(intentToNext);
            }
        });

        binding.btnCancel.setOnClickListener(view -> {
            Intent intentToBack = new Intent(ShopOwnerProfileUpdatePaymentsActivity1.this, ShopOwnerProfilePaymentsActivity.class);
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