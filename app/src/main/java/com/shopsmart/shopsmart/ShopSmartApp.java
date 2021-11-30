package com.shopsmart.shopsmart;

import android.app.Application;
import android.content.Context;

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

    public static void login(String userEmail, String userPassword) {
        email = userEmail;
        password = userPassword;
        credentials = Credentials.emailPassword(email, password);
    }

    public static void logout() {
        if (realm != null) {
            realm.close();
            realm = null;
        }
        if (app.currentUser().isLoggedIn())
            app.currentUser().logOutAsync(result -> {
            });
        credentials = null;
        email = null;
        password = null;
    }

    public static void instantiateRealm() {
        if (realm == null || realm.isClosed())
            realm = Realm.getInstance(new SyncConfiguration.Builder(app.currentUser(), "ShopSmart")
                    .allowQueriesOnUiThread(true)
                    .allowWritesOnUiThread(true)
                    .build());
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
