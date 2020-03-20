package com.project.atmos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.project.atmos.config.Config;
import com.project.atmos.dev.BLEModulesGenerator;
import com.project.atmos.libs.BLEHardwareManager;
import com.project.atmos.models.BLEModuleObject;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    public static Config config;

    private BLEHardwareManager PHomeBluetoothLEHardwareManager;

    public static ArrayList<BLEModuleObject> BLEModulesList = new ArrayList<BLEModuleObject>();
    public static final Integer NB_OF_DEVICES_GENERATED = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        this.config = new Config();
        this.config.load();

        // Filesystem Permission Request
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Log.i(Tags.getTag(Tags.API_PERMISSION_INFO), "The following permission: WRITE_EXTERNAL_STORAGE, is not granted. Asking the permission now.");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionRequestCode.REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        else {
            Log.i(Tags.getTag(Tags.API_PERMISSION_INFO), "The following permission: WRITE_EXTERNAL_STORAGE, is successfully granted by the user");
        }

        // Bluetooth LE Permission Request
        PHomeBluetoothLEHardwareManager = new BLEHardwareManager(this);
        PHomeBluetoothLEHardwareManager.BluetoothLEHardwareSupport();
        PHomeBluetoothLEHardwareManager.BluetoothLEHardwareEnableRequest();

        // App Cycle Definition and Data Construction
        switch(config.isCycleStatus()){
            case DEVELOPMENT:
                BLEModulesGenerator bdg = new BLEModulesGenerator();
                BLEModulesList = bdg.generatePackOfDevices(NB_OF_DEVICES_GENERATED);
                break;
            case TESTING:
                break;
            case PRODUCTION:
                break;
            default:
                break;
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int PHomeProcessId = android.os.Process.myPid();
        android.os.Process.killProcess(PHomeProcessId);
    }
}
