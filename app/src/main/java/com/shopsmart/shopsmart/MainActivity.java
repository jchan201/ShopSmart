package com.shopsmart.shopsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MainActivity extends AppCompatActivity {
    private App app;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());

        // (EXAMPLE) Log in:
        Credentials credentials = Credentials.emailPassword("something", "something");
        app.loginAsync(credentials/*Credentials.anonymous()*/, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    Log.v("User", "Logged In Successfully");
                    User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("ShopSmartDB");
                    MongoCollection mongoCollection = mongoDatabase.getCollection("someCollection");
                }
                else {
                    Log.v("User", "Failed to Login");
                }
            }
        });
        // (EXAMPLE) Register:
        app.getEmailPassword().registerUserAsync("something", "something", it -> {
            if (it.isSuccess()) {
                Log.v("User", "Registered with email successfully");
            }
            else {
                Log.v("User", "Registration failed");
            }
        });
    }
}