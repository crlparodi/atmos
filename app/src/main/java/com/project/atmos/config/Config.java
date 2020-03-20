package com.project.atmos.config;

import android.content.Context;
import android.content.res.AssetManager;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.project.atmos.MainActivity;
import com.project.atmos.values.AppCycleStatus;
import com.project.atmos.values.Tags;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private Properties config;

    private AssetManager assetMgr;
    private String filePath = "app.config";
    private InputStream is = null;

    private boolean debugStatus = false;
    private AppCycleStatus cycleStatus = AppCycleStatus.DEVELOPMENT;

    public Config(){
        this.config = new Properties();
        this.assetMgr = MainActivity.context.getAssets();
    }

    public boolean isDebugStatus(){
        return this.debugStatus;
    }

    public AppCycleStatus isCycleStatus(){
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
            Toast.makeText(MainActivity.context, "Failed to retrieve configuration from app.config", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(Tags.getTag(Tags.MAIN), "Configuration info are retrieved successfully.");
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
                this.cycleStatus = AppCycleStatus.DEVELOPMENT;
                break;
            case "TESTING":
                this.cycleStatus = AppCycleStatus.TESTING;
                break;
            case "PRODUCTION":
                this.cycleStatus = AppCycleStatus.PRODUCTION;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
