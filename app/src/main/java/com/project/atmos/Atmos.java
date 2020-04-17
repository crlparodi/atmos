package com.project.atmos;

import android.app.Application;
import android.content.Context;

public class Atmos extends Application {
    public static final String applicationName = "Atmos";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Atmos.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return Atmos.context;
    }
}
