package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopownerProfilePaymentsActivityBinding;

import io.realm.RealmResults;

public class ShopOwnerProfilePaymentsActivity extends AppCompatActivity {
    private ShopownerProfilePaymentsActivityBinding binding;
    private PaymentMethod[] paymentMethods;
    private int index = 0;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopownerProfilePaymentsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        boolean paymentSuccess = currIntent.getBooleanExtra("EXTRA_UPDATE_PAYMENT_SUCCESS", false);
        if (paymentSuccess)
            Toast.makeText(ShopOwnerProfilePaymentsActivity.this, "Successfully update payment method.", Toast.LENGTH_SHORT).show();
        boolean addSuccess = currIntent.getBooleanExtra("EXTRA_ADD_PAYMENT_SUCCESS", false);
        if (addSuccess)
            Toast.makeText(ShopOwnerProfilePaymentsActivity.this, "Successfully add new payment method.", Toast.LENGTH_SHORT).show();
        boolean deleteSuccess = currIntent.getBooleanExtra("EXTRA_DELETE_PAYMENT_SUCCESS", false);
        if (deleteSuccess)
            Toast.makeText(ShopOwnerProfilePaymentsActivity.this, "Successfully remove payment method.", Toast.LENGTH_SHORT).show();

        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getEmail().equals(ShopSmartApp.email))
                        user = users.get(i);
                }
                paymentMethods = user.getPaymentMethods().toArray(new PaymentMethod[0]);
                total = paymentMethods.length;
                binding.textPaymentTotal.setText(Integer.toString(total));
                binding.textPaymentIndex.setText(Integer.toString(total == 0 ? index : index + 1));
                if (index == 0 && total == 0) {
                    binding.singlePaymentView.setVisibility(View.GONE);
                    binding.textCardNum.setVisibility(View.GONE);
                    binding.textCardName.setVisibility(View.GONE);
                    binding.textCardExpireTitle.setVisibility(View.GONE);
                    binding.textCardExpire.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnRemove.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                } else {
                    if (index + 1 == total) {
                        binding.buttonPrev.setVisibility(View.INVISIBLE);
                        binding.buttonNext.setVisibility(View.INVISIBLE);
                    }
                    if (index + 1 < total) binding.buttonPrev.setVisibility(View.INVISIBLE);
                    displayCardInfo(paymentMethods[index]);
                }
            }
        });
        binding.buttonPrev.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                binding.buttonNext.setVisibility(View.VISIBLE);
                binding.textPaymentIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);
                if (index == 0) binding.buttonPrev.setVisibility(View.INVISIBLE);
            }
        });
        binding.buttonNext.setOnClickListener(view -> {
            if (index < total) {
                index += 1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.textPaymentIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);
                if (index + 1 == total) binding.buttonNext.setVisibility(View.INVISIBLE);
            }
        });
        binding.btnRemove.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileDeletePaymentsConfirmActivity.class)
                        .putExtra("EXTRA_REMOVE_INDEX", index)));
        binding.btnEdit.setOnClickListener(view -> {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setName(paymentMethods[index].getName());
            paymentMethod.setCardNumber(paymentMethods[index].getCardNumber());
            paymentMethod.setExpiry(paymentMethods[index].getExpiry());
            paymentMethod.setSecurityCode(paymentMethods[index].getSecurityCode());

            Address billingAddress = new Address();
            billingAddress.setCountry(paymentMethods[index].getBillingAddress().getCountry());
            billingAddress.setCity(paymentMethods[index].getBillingAddress().getCity());
            billingAddress.setProvince(paymentMethods[index].getBillingAddress().getProvince());
            billingAddress.setPostalCode(paymentMethods[index].getBillingAddress().getPostalCode());
            billingAddress.setAddress1(paymentMethods[index].getBillingAddress().getAddress1());
            billingAddress.setAddress2(paymentMethods[index].getBillingAddress().getAddress2());

            startActivity(new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileUpdatePaymentsActivity1.class)
                    .putExtra("EXTRA_UPDATE_INDEX", index)
                    .putExtra("EXTRA_PAYMENT_METHOD_OBJ", paymentMethod)
                    .putExtra("EXTRA_BILLING_ADDRESS_OBJ", billingAddress));
        });
        binding.btnAdd.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileAddPaymentsActivity1.class)));
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopOwnerProfilePaymentsActivity.this, ShopOwnerProfileActivity.class)));
    }

    private void displayCardInfo(PaymentMethod paymentMethod) {
        binding.textCardNum.setText("***" + paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length() - 4));
        binding.textCardName.setText(paymentMethod.getName().toUpperCase());
        binding.textCardExpire.setText(paymentMethod.getExpiry());
    }
}