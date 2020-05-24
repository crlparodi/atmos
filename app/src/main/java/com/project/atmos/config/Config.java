package com.project.atmos.config;

import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.project.atmos.MainActivity;
import com.project.atmos.values.AtmosAppCycle;
import com.project.atmos.values.AtmosStrings;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static final String TAG = Config.class.getSimpleName();

    private Properties config;

    private AssetManager assetMgr;
    private String filePath = "app.config";
    private InputStream is = null;

    private boolean debugStatus = false;
    private AtmosAppCycle cycleStatus = AtmosAppCycle.DEMO;

    public Config(){
        this.config = new Properties();
        this.assetMgr = MainActivity.context.getAssets();
    }

    public boolean isDebugStatus(){
        return this.debugStatus;
    }

    public AtmosAppCycle isCycleStatus(){
        return this.cycleStatus;
    }

    public void load(){
        try {
            this.is = new BufferedInputStream(this.assetMgr.open(filePath, AssetManager.ACCESS_BUFFER));
            this.config.load(this.is);

            this.getDebugStatus();
            this.getCycleStatus();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.context, AtmosStrings.ToastMessages.APP_CONFIG_NOT_FOUND, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Configuration info are retrieved successfully.");
    }

    private void getDebugStatus() throws IllegalArgumentException{
        String appDebugStatus = config.getProperty("DEBUG");

        if(!Boolean.valueOf(appDebugStatus)){
            throw new IllegalArgumentException();
        }

        this.debugStatus = Boolean.valueOf(appDebugStatus);
    }

    private void getCycleStatus() throws IllegalArgumentException{
        String appCycleStatus = config.getProperty("CYCLE_STATUS");
        switch(appCycleStatus){
            case "DEVELOPMENT":
                this.cycleStatus = AtmosAppCycle.DEMO;
                break;
            case "TESTING":
                this.cycleStatus = AtmosAppCycle.TESTING;
                break;
            case "PRODUCTION":
                this.cycleStatus = AtmosAppCycle.PRODUCTION;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
