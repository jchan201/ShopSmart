package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.CustomerCheckoutBinding;

import java.io.Serializable;

import io.realm.RealmResults;

public class CustomerCheckoutActivity extends AppCompatActivity implements Serializable {
    private CustomerCheckoutBinding binding;
    private AppUser user;
    private PaymentMethod[] paymentMethods;
    private int index = 0;
    private int total = 0;
    private int numItems = 0;
    private double itemTotal = 0;
    private int numUniqueShops = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustomerCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        if (currIntent != null) {
            numItems = currIntent.getIntExtra("EXTRA_NUMITEMS", numItems);
            itemTotal = currIntent.getDoubleExtra("EXTRA_TOTAL", itemTotal);
            numUniqueShops = currIntent.getIntExtra("EXTRA_UNIQUESHOPPES", numUniqueShops);

            if (numItems != 0) {
                binding.buttonCheckout.setEnabled(true);
                binding.tvItems.setText("(" + numItems + ") Items:");
                binding.tvItemsDollars.setText("$" + String.format("%.2f", itemTotal));
                binding.tvTaxDollar.setText("$" + String.format("%.2f", itemTotal * 0.13));
                binding.tvDFeeDollar.setText("$3.00 x " + numUniqueShops);
                binding.tvTotalDollars.setText("$" + String.format("%.2f", (itemTotal*1.13 + numUniqueShops * 3)));
            }
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
                binding.queryCardIndex.setText(Integer.toString(total == 0 ? index : index + 1));
                if (total <= 0) {
                    binding.customerPaymentView.setVisibility(View.GONE);
                    binding.queryCardNum.setVisibility(View.GONE);
                    binding.queryCardName.setVisibility(View.GONE);
                    binding.queryCardNum3.setVisibility(View.GONE);
                    binding.queryExpDate.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                    binding.buttonCheckout.setText("Add Payment");
                    binding.buttonCheckout.setEnabled(true);
                } else {
                    if (total <= 1) {
                        binding.buttonPrev.setEnabled(false);
                        binding.buttonNext.setEnabled(false);
                    } else {
                        binding.buttonNext.setEnabled(true);
                    }
                    displayCardInfo(paymentMethods[index]);
                }
            }
        });
        binding.buttonPrev.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                binding.buttonNext.setEnabled(true);
                binding.queryCardIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);
                if (index == 0)
                    binding.buttonPrev.setEnabled(false);
            }
        });
        binding.buttonNext.setOnClickListener(view -> {
            if (index < total) {
                index += 1;
                binding.buttonPrev.setEnabled(true);
                binding.queryCardIndex.setText(Integer.toString(index + 1));
                displayCardInfo(paymentMethods[index]);

                if (index + 1 == total)
                    binding.buttonNext.setEnabled(false);
            }
        });
        binding.buttonCheckout.setOnClickListener(view -> {
            if (binding.buttonCheckout.getText().toString().equals("CHECKOUT")) {
                Intent intentToProfile = new Intent(CustomerCheckoutActivity.this, CustomerDashboardActivity.class);
                intentToProfile.putExtra("EXTRA_PMETHOD_EDIT", false);
                startActivity(intentToProfile);
                finish();
            } else {
                Intent intentToProfile = new Intent(CustomerCheckoutActivity.this, CustomerPaymentAddActivity.class);
                intentToProfile.putExtra("EXTRA_PMETHOD_EDIT", false);
                startActivity(intentToProfile);
                finish();
            }
        });
        binding.btnBack2.setOnClickListener(view -> {
            Intent intentToProfile = new Intent(CustomerCheckoutActivity.this, CustomerShoppingCartActivity.class);
            intentToProfile.putExtra("EXTRA_BOOLCRAP", true);
            startActivity(intentToProfile);
            finish();
        });
    }

    private void displayCardInfo(PaymentMethod paymentMethod) {
        binding.queryCardNum.setText("***" + paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length() - 4));
        binding.queryCardName.setText(paymentMethod.getName().toUpperCase());
        binding.queryExpDate.setText(paymentMethod.getExpiry());
    }
}