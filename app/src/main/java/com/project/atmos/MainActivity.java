package com.project.atmos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.project.atmos.config.Config;
import com.project.atmos.libs.BLEHardwareManager;
import com.project.atmos.values.AtmosStrings;
import com.project.atmos.values.PermissionRequestCode;
import com.project.atmos.values.Tags;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    public static Config config;

    public static Context context;

    private BLEHardwareManager bleHardwareManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        config = new Config();
        config.load();

        // Bluetooth LE Permission Request
        bleHardwareManager = new BLEHardwareManager(this);
        bleHardwareManager.enableDisableBT(true);

        // UI Construction
        // Barre de navigation inférieure
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        // Fragments configuration
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Custom Toolbar
        Toolbar toolbar = findViewById(R.id.atmos_toolbar);
        setSupportActionBar(toolbar);
    }

    public BLEHardwareManager getBleHardwareManager(){
        return this.bleHardwareManager;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * We absolutely want to check the storage writing permission, this one is very essential
         * Without this one, the app will crash...
         */

        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, android.os.Process.myPid(), android.os.Process.myUid()) < 0){
            startActivity(new Intent(this, AppPermissionsActivity.class));
            finish();
        }
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int AtmosProcessID = android.os.Process.myPid();
        android.os.Process.killProcess(AtmosProcessID);
    }
}
