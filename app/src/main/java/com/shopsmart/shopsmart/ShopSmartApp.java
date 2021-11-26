package com.shopsmart.shopsmart;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class ShopSmartApp extends Application {
    public static Realm realm;
    public static App app;
    public static Credentials credentials;
    public static String email;
    public static String password;
    private static SyncConfiguration config;

    public static void login(String userEmail, String userPassword) {
        email = userEmail;
        password = userPassword;
        credentials = Credentials.emailPassword(email, password);
        config = new SyncConfiguration.Builder(app.currentUser(), "ShopSmart")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
    }

    public static void logout() {
        if (app.currentUser() != null)
            app.currentUser().logOut();
        credentials = null;
        email = null;
        password = null;
    }

    public static void instantiateRealm() {
        if (realm == null)
            realm = Realm.getInstance(config);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        app = new App(new AppConfiguration.Builder("shopsmart-acsmx").build());
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
}
