package com.shopsmart.shopsmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.shopsmart.shopsmart.databinding.ShopListActivityBinding;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ShopListActivity extends AppCompatActivity {
    private ShopListActivityBinding binding;
    private ArrayList<Shop> shops;
    private int index;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ShopListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent currIntent = getIntent();
        if (currIntent != null) {
            boolean addSuccess = currIntent.getBooleanExtra("EXTRA_REGISTER_SHOP_SUCCESS", false);
            if (addSuccess)
                Toast.makeText(ShopListActivity.this, "Successfully registered new shop.", Toast.LENGTH_SHORT).show();
            boolean deleteSuccess = currIntent.getBooleanExtra("EXTRA_DELETE_SHOP_SUCCESS", false);
            if (deleteSuccess)
                Toast.makeText(ShopListActivity.this, "Successfully closed shop.", Toast.LENGTH_SHORT).show();
        }
        ShopSmartApp.app.loginAsync(ShopSmartApp.credentials, result -> {
            if (result.isSuccess()) {
                ShopSmartApp.instantiateRealm();
                RealmResults<AppUser> users = ShopSmartApp.realm.where(AppUser.class).findAll();
                AppUser user = null;
                for (AppUser u : users) {
                    if (u.getEmail().equals(ShopSmartApp.email)) {
                        user = u;
                    }
                }
                RealmResults<Shop> allShops = ShopSmartApp.realm.where(Shop.class).findAll();
                List<ObjectId> shopIds;
                if (user != null) shopIds = user.getShops();
                else shopIds = null;
                shops = new ArrayList<>();
                for (Shop s : allShops) {
                    for (ObjectId o : shopIds) {
                        if (s.getId().equals(o)) shops.add(s);
                    }
                }
                total = shops.size();
                binding.textShopTotal.setText(Integer.toString(total));
                binding.textShopIndex.setText(Integer.toString(total == 0 ? index : index + 1));
                if (index == 0 && total == 0) {
                    binding.singleShopView.setVisibility(View.GONE);
                    binding.textShopName.setVisibility(View.GONE);
                    binding.btnView.setVisibility(View.GONE);
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                    binding.buttonPrev.setVisibility(View.GONE);
                    binding.buttonNext.setVisibility(View.GONE);
                    binding.textSlash.setVisibility(View.GONE);
                    binding.textShopIndex.setVisibility(View.GONE);
                    binding.textShopTotal.setVisibility(View.GONE);
                    binding.btnInventory.setVisibility(View.GONE);
                } else {
                    binding.singleShopView.setVisibility(View.VISIBLE);
                    binding.textShopName.setVisibility(View.VISIBLE);
                    binding.btnView.setVisibility(View.VISIBLE);
                    binding.btnEdit.setVisibility(View.VISIBLE);
                    binding.btnDelete.setVisibility(View.VISIBLE);
                    binding.buttonPrev.setVisibility(View.VISIBLE);
                    binding.buttonNext.setVisibility(View.VISIBLE);
                    binding.textSlash.setVisibility(View.VISIBLE);
                    binding.textShopIndex.setVisibility(View.VISIBLE);
                    binding.textShopTotal.setVisibility(View.VISIBLE);
                    binding.btnInventory.setVisibility(View.VISIBLE);
                    if (index + 1 == total) binding.buttonNext.setVisibility(View.GONE);
                    if (index == 0) binding.buttonPrev.setVisibility(View.GONE);
                    displayCardInfo(shops.get(index));
                }
            }
        });
        binding.buttonPrev.setOnClickListener(view -> {
            if (index > 0) {
                index -= 1;
                binding.buttonNext.setVisibility(View.VISIBLE);
                binding.textShopIndex.setText(Integer.toString(index + 1));
                displayCardInfo(shops.get(index));
                if (index == 0) binding.buttonPrev.setVisibility(View.INVISIBLE);
            }
        });
        binding.buttonNext.setOnClickListener(view -> {
            if (index < total) {
                index += 1;
                binding.buttonPrev.setVisibility(View.VISIBLE);
                binding.textShopIndex.setText(Integer.toString(index + 1));
                displayCardInfo(shops.get(index));
                if (index + 1 == total) binding.buttonNext.setVisibility(View.INVISIBLE);
            }
        });
        binding.btnView.setOnClickListener(view -> {
            Intent intent = new Intent(ShopListActivity.this, ShopViewActivity.class);
            intent.putExtra("EXTRA_INDEX", index);
            intent.putExtra("CUSTOMER", false);
            startActivity(intent);
        });
        binding.btnEdit.setOnClickListener(view -> {
            Intent intent = new Intent(ShopListActivity.this, ShopRegisterEdit.class);
            intent.putExtra("EXTRA_INDEX", index);
            startActivity(intent);
        });
        binding.btnDelete.setOnClickListener(view -> {
            Intent intent = new Intent(ShopListActivity.this, ShopDeleteConfirmActivity.class);
            intent.putExtra("EXTRA_REMOVE_INDEX", index);
            startActivity(intent);
        });
        binding.btnInventory.setOnClickListener(view -> {
            Intent intent = new Intent(ShopListActivity.this, ShopInventoryActivity.class);
            intent.putExtra("EXTRA_INDEX", index);
            startActivity(intent);
        });
        binding.btnAdd.setOnClickListener(view ->
                startActivity(new Intent(ShopListActivity.this, ShopRegister.class)));
        binding.btnBack.setOnClickListener(view ->
                startActivity(new Intent(ShopListActivity.this, ShopOwnerDashboardActivity.class)));
    }

    private void displayCardInfo(Shop shop) {
        binding.textShopName.setText(shop.getName());
    }
}