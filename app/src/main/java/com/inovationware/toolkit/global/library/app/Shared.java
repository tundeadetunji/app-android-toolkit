package com.inovationware.toolkit.global.library.app;

import android.app.Application;
import android.content.Context;

public class Shared extends Application {
    private static Shared instance;

    public static Shared getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
