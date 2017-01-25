package com.taptm.shurikus.realmtutorial;

import android.app.Application;

import io.realm.Realm;

public class ApplicationApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
