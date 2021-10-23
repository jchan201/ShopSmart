package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.bson.types.ObjectId;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.SyncConfiguration;

import com.shopsmart.shopsmart.databinding.ActivityStartupBinding;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class StartupActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ActivityStartupBinding binding;
    private Realm realm;
    private App app;
    final ArrayList<String> ATTEMPTS = new ArrayList<>();
    Intent currIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.currIntent = this.getIntent();
        if (this.currIntent != null) {
            boolean signup_success = currIntent.getBooleanExtra("EXTRA_SIGNUP_SUCCESS", false);
            if (signup_success)
                Toast.makeText(StartupActivity.this, "Successfully registered.", Toast.LENGTH_SHORT).show();

            boolean delete_success = currIntent.getBooleanExtra("EXTRA_DELETE_PAYMENT_SUCCESS", false);
            if (delete_success)
                Toast.makeText(StartupActivity.this, "Successfully delete account.", Toast.LENGTH_SHORT).show();
        }

        // Initialize the Realm library.
        Realm.init(this);

        // Access the Realm application.
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // Watches changes in certain EditTexts.
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Hide error message if the text is changed.
                binding.txtError.setVisibility(View.INVISIBLE);
            }
        };
        binding.edtTxtEmail.addTextChangedListener(textWatcher); // Watch the email
        binding.edtTxtPassword.addTextChangedListener(textWatcher); // Watch the password

        // When the Login button is clicked.
        binding.btnLogin.setOnClickListener(view -> {
            // Get the user's credentials (email and password).
            String email = binding.edtTxtEmail.getText().toString();
            String password = binding.edtTxtPassword.getText().toString();
            if (email.isEmpty()) {
                binding.txtError.setText("Please enter a email address.");
                binding.txtError.setVisibility(View.VISIBLE);
            } else if (password.isEmpty()) {
                binding.txtError.setText("Please enter a password.");
                binding.txtError.setVisibility(View.VISIBLE);
            } else {
                Credentials credentials = Credentials.emailPassword(email, password);
                app.loginAsync(credentials, result -> {
                    if (result.isSuccess()) {
                        Log.v("LOGIN", "Successfully authenticated using email and password.");

                        // Open a Synced Realm for asynchronous transactions.
                        SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                        realm = Realm.getInstance(config);

                        // Retrieve all users in the Realm.
                        RealmResults<AppUser> users = realm.where(AppUser.class).findAll();
                        AppUser user = null;

                        // Find the AppUser
                        for (int i = 0; i < users.size(); i++) {
                            if (users.get(i).getEmail().equals(email)) {
                                user = users.get(i);
                            }
                        }

                        if (user == null) Log.wtf(PARTITION, "No such AppUser exists...?");

                        // Go to the dashboard depending on the AppUser's type.
                        String type = user.getUserType();
                        switch (type) {
                            case "Customer":
                                // putExtra?
                                Intent customerDashboardActivity = new Intent(StartupActivity.this, CustomerDashboardActivity.class);
                                customerDashboardActivity.putExtra("EXTRA_PASS", password);
                                customerDashboardActivity.putExtra("EXTRA_EMAIL", email);

                                realm.close();
                                startActivity(customerDashboardActivity);
                                Log.v(PARTITION,"Successfully got to dashboard!");
                                break;
                            case "Owner":
                                // putExtra?
                                Intent intentToDashboard = new Intent(StartupActivity.this, ShopOwnerDashboardActivity.class);
                                intentToDashboard.putExtra("EXTRA_PASS", password);
                                intentToDashboard.putExtra("EXTRA_EMAIL", email);

                                realm.close();
                                startActivity(intentToDashboard);
                                Log.v(PARTITION,"Successfully got to dashboard!");
                                break;
                            default:
                                Log.wtf(PARTITION, "AppUser is neither a Customer nor ShopOwner.");
                        }
                    } else {
                        Log.e("LOGIN", "Failed to log in.");
                        // Show error message if login failed.
                        binding.txtError.setText("Invalid email/password.");
                        binding.txtError.setVisibility(View.VISIBLE);
                        ATTEMPTS.add(email);
                        if (ATTEMPTS.size() == 5) binding.btnLogin.setEnabled(false);
                    }
                });
            }
        });

        // When the Register button is clicked.
        binding.btnRegister.setOnClickListener(view -> {
            // Go to Signup Activity
            startActivity(new Intent(StartupActivity.this, SignupActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;

        // Log out.
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v("LOGOUT", "Successfully logged out.");
            } else {
                Log.e("LOGOUT", "Failed to log out, error: " + result.getError());
            }
        });
        realm.close(); // Close the realm.
    }

    // Objects of this class are intended to be executed by a thread, as denoted by "Runnable".
    public class SampleCode implements Runnable {
        User user;
        // constructor
        public SampleCode(User user) {
            this.user = user;
        }
        @Override
        public void run() {
            SyncConfiguration config = new SyncConfiguration.Builder(user, PARTITION).build();
            // Sync all Realm changes.
            Realm realm2 = Realm.getInstance(config);

            // Create a user.
            AppUser userToAdd = new AppUser();
            realm2.executeTransaction(transactionRealm -> {
                // insert the user
                transactionRealm.insert(userToAdd);
            });

            // Retrieve all users in the Realm.
            RealmResults<AppUser> users = realm2.where(AppUser.class).findAll();

            // get a user from the realm
            AppUser someUser = users.get(0);
            // get the id of the user
            ObjectId someUserId = someUser.getId();

            // Modify a user.
            realm2.executeTransaction(transactionRealm -> {
                // find the specified user in the realm
                AppUser user = transactionRealm.where(AppUser.class).equalTo("_id", someUserId).findFirst();
                // modify it (add new address)
                //user.addAddress(new Address("Address 1","Address 2", "Canada", "Ontario", "Toronto", "A1B 2C3"));
            });

            // Delete a user.
            realm2.executeTransaction(transactionRealm -> {
                // find the specified user in the realm
                AppUser user = transactionRealm.where(AppUser.class).equalTo("_id", someUserId).findFirst();
                // delete it
                user.deleteFromRealm();
            });

            // Add a change listener to the AppUser collection
            users.addChangeListener((appUsers, changeSet) -> {
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (OrderedCollectionChangeSet.Range range : deletions) {
                    Log.v("DELETION", "Deleted range: " + range.startIndex + " to "
                            + (range.startIndex + range.length - 1));
                }
                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    Log.v("INSERTION", "Inserted range: " + range.startIndex + " to "
                            + (range.startIndex + range.length - 1));
                }
                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    Log.v("UPDATES", "Updated range: " + range.startIndex + " to "
                            + (range.startIndex + range.length - 1));
                }
            });

            realm2.close();
        }
    }
}