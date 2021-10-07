/*package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import com.shopsmart.shopsmart.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final String PARTITION = "ShopSmart";
    private ActivityMainBinding binding;
    private Realm realm;
    private App app;

    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonRegister = (Button) findViewById(R.id.btnRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerRegistration1();
            }
        });

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
                binding.txtError.setVisibility(View.INVISIBLE);
            }
        };
        binding.edtTxtEmail.addTextChangedListener(textWatcher); // Watch the email
        binding.edtTxtPassword.addTextChangedListener(textWatcher); // Watch the password

        // When the Login button is clicked.
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.edtTxtEmail.getText().toString();
                String password = binding.edtTxtPassword.getText().toString();
                // Get the user's credentials (email and password).
                Credentials credentials = Credentials.emailPassword(email, password);

                try {
                    // Attempt to log in.
                    User current = app.login(credentials);

                    // get user data

                    Log.v("LOGIN", "Successfully authenticated as ");
                } catch(Exception e) {
                    binding.txtError.setVisibility(View.VISIBLE);
                    Log.e("LOGIN", "Failed to log in. Error: " + e.getMessage());
                }

                // Open a Synced Realm for asynchronous transactions.
                SyncConfiguration config = new SyncConfiguration.Builder(app.currentUser(), PARTITION).build();
                realm = Realm.getInstance(config);

                // add a change listener to the AppUser collection
                RealmResults<AppUser> users = realm.where(AppUser.class).findAllAsync();
                users.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<AppUser>>() {
                    @Override
                    public void onChange(RealmResults<AppUser> appUsers, OrderedCollectionChangeSet changeSet) {
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
                    }
                });
            }
        });
    }

    public void customerRegistration1(){
        Intent intent = new Intent(this, CustomerRegistrationActivity1.class);
        startActivity(intent);
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
    public class TestingCode implements Runnable {
        User user;
        // constructor
        public TestingCode(User user) {
            this.user = user;
        }
        @Override
        public void run() {
            SyncConfiguration config = new SyncConfiguration.Builder(user, PARTITION).build();
            // Sync all Realm changes.
            Realm backgroundRealm = Realm.getInstance(config);

            // Create a user.
            AppUser userToAdd = new AppUser();
            backgroundRealm.executeTransaction(transactionRealm -> {
                // insert the user
                transactionRealm.insert(userToAdd);
            });

            // Retrieve all users in the Realm.
            RealmResults<AppUser> users = backgroundRealm.where(AppUser.class).findAll();

            // get a user from the realm
            AppUser someUser = users.get(0);
            // get the id of the user
            ObjectId someUserId = someUser.getId();

            // Modify a user.
            backgroundRealm.executeTransaction(transactionRealm -> {
                // find the specified user in the realm
                AppUser user = transactionRealm.where(AppUser.class).equalTo("_id", someUserId).findFirst();
                // modify it (add new address)
                user.addAddress(new Address("Canada", "Ontario", "Toronto", "A1B 2C3"));
            });

            // Delete a user.
            backgroundRealm.executeTransaction(transactionRealm -> {
                // find the specified user in the realm
                AppUser user = transactionRealm.where(AppUser.class).equalTo("_id", someUserId).findFirst();
                // delete it
                user.deleteFromRealm();
            });

            backgroundRealm.close();
        }
    }
}*/