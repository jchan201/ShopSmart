package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerPaymentsBinding;

import java.io.Serializable;

import io.realm.RealmResults;

public class CustomerPaymentsActivity extends AppCompatActivity implements Serializable {
    private CustomerPaymentsBinding binding;
    private AppUser user;
    private PaymentMethod[] paymentMethods;
    private int index = 0;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerPaymentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        if (currIntent != null) {
            boolean paymentSuccess = currIntent.getBooleanExtra("EXTRA_UPDATE_PAYMENT_SUCCESS", false);
            if (paymentSuccess)
                Toast.makeText(CustomerPaymentsActivity.this, "Successfully update payment method.", Toast.LENGTH_SHORT).show();
            boolean addSuccess = currIntent.getBooleanExtra("EXTRA_ADD_PAYMENT_SUCCESS", false);
            if (addSuccess)
                Toast.makeText(CustomerPaymentsActivity.this, "Successfully add new payment method.", Toast.LENGTH_SHORT).show();
        }

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email)) {
                        user = users.get(i);
                    }
                }
                binding.queryFullName.setText(user.getFirstName() + " " + user.getMiddleInitial() + " " + user.getLastName());
                paymentMethods = user.getPaymentMethods().toArray(new PaymentMethod[0]);
                total = paymentMethods.length;
                binding.queryTotalIndex.setText(Integer.toString(total));
                binding.queryCardIndex.setText(Integer.toString(index + 1));
                if (index == 0 && total == 0) {
                    binding.customerPaymentView.setVisibility(View.GONE);
                    binding.queryCardNum.setVisibility(View.GONE);
                    binding.queryCardName.setVisibility(View.GONE);
                    binding.queryCardNum3.setVisibility(View.GONE);
                    binding.queryExpDate.setVisibility(View.GONE);
                    binding.buttonEdit.setVisibility(View.GONE);
                    binding.buttonRemove.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                } else {
                    if (index + 1 == total) {
                        binding.buttonPrev.setVisibility(View.INVISIBLE);
                        binding.buttonNext.setVisibility(View.INVISIBLE);
                    } else if (index + 1 < total)
                        binding.buttonPrev.setVisibility(View.INVISIBLE);
                    displayCardInfo(paymentMethods[index]);
                }
            }
        });
        binding.buttonPrev.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                binding.buttonNext.setVisibility(View.VISIBLE);
                binding.queryCardIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);
                if (index == 0)
                    binding.buttonPrev.setVisibility(View.INVISIBLE);
            }
        });
        binding.buttonNext.setOnClickListener(view -> {
            if (index < total) {
                index += 1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.queryCardIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);

                if (index + 1 == total) {
                    binding.buttonNext.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.buttonAdd.setOnClickListener(view -> {
            Intent intentToProfile = new Intent(CustomerPaymentsActivity.this, CustomerPaymentAddActivity.class);
            intentToProfile.putExtra("EXTRA_PMETHOD_EDIT", false);
            startActivity(intentToProfile);
        });
        binding.buttonEdit.setOnClickListener(view -> {
            PaymentMethod pMethod = new PaymentMethod();
            pMethod.setName(paymentMethods[index].getName());
            pMethod.setCardNumber(paymentMethods[index].getCardNumber());
            pMethod.setExpiry(paymentMethods[index].getExpiry());
            pMethod.setSecurityCode(paymentMethods[index].getSecurityCode());

            Address bAddress = new Address();
            bAddress.setCountry(paymentMethods[index].getBillingAddress().getCountry());
            bAddress.setCity(paymentMethods[index].getBillingAddress().getCity());
            bAddress.setProvince(paymentMethods[index].getBillingAddress().getProvince());
            bAddress.setPostalCode(paymentMethods[index].getBillingAddress().getPostalCode());
            bAddress.setAddress1(paymentMethods[index].getBillingAddress().getAddress1());
            bAddress.setAddress2(paymentMethods[index].getBillingAddress().getAddress2());

            Intent intentAdd = new Intent(CustomerPaymentsActivity.this, CustomerPaymentAddActivity.class);
            intentAdd.putExtra("EXTRA_UPDATE_INDEX", index);
            intentAdd.putExtra("EXTRA_PMETHOD", pMethod);
            intentAdd.putExtra("EXTRA_PMETHOD_ADDRESS", bAddress);
            intentAdd.putExtra("EXTRA_PMETHOD_PHONE", user.getPhone());
            intentAdd.putExtra("EXTRA_PMETHOD_EDIT", true);
            intentAdd.putExtra("EXTRA_INDEX", index);
            startActivity(intentAdd);
        });
        binding.btnBack2.setOnClickListener(view ->
                startActivity(new Intent(CustomerPaymentsActivity.this, CustomerManageProfileActivity.class)));
    }

    private void displayCardInfo(PaymentMethod paymentMethod) {
        binding.queryCardNum.setText("***" + paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length() - 4));
        binding.queryCardName.setText(paymentMethod.getName().toUpperCase());
        binding.queryExpDate.setText(paymentMethod.getExpiry());
    }
}