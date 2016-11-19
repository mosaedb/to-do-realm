package com.example.mosaed.todorealm;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Mosaed on 10/11/16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }
}
