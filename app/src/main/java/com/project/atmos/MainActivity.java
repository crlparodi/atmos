package com.project.atmos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.atmos.config.Config;
import com.project.atmos.database.BLEModulesDAO;
import com.project.atmos.database.BLEModulesDataBase;
import com.project.atmos.libs.BLEHardwareManager;
import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.values.AtmosStrings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static Config config;

    public static Context context;

    private BLEHardwareManager mManager;

    private HashMap<String, BluetoothGatt> mGattMap;

    private Iterator mIterator;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AtmosStrings.MAIN_ACTIVITY)) {
                final boolean state = intent.getBooleanExtra(AtmosStrings.BLE_STATE_CHANGED, false);
                Log.d(TAG, "onReceive: Change STATE");
                Switch btSwitch = findViewById(R.id.atmos_mod_bt_enable);
                if (btSwitch != null) {
                    btSwitch.setChecked(state);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        config = new Config();
        config.load();

        mManager = new BLEHardwareManager(this);

        mGattMap = new HashMap<>();


        // UI Construction
        // Barre de navigation inférieure
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_synthesis, R.id.navigation_mod_manager, R.id.navigation_settings)
                .build();
        // Fragments configuration
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Custom Toolbar
        Toolbar toolbar = findViewById(R.id.atmos_toolbar);
        setSupportActionBar(toolbar);

        IntentFilter btIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mManager.getBroadcastReceiver(), btIntentFilter);

        IntentFilter intentFilter = new IntentFilter(AtmosStrings.MAIN_ACTIVITY);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        /**
         * We absolutely want to check the storage writing permission, this one is very essential
         * Without this one, the app will crash...
         */

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, android.os.Process.myPid(), android.os.Process.myUid()) < 0) {
            startActivity(new Intent(this, AppPermissionsActivity.class));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing()) {
            mIterator = mGattMap.entrySet().iterator();
            while (mIterator.hasNext()) {
                Map.Entry mMapEntry = (Map.Entry) mIterator.next();

                BluetoothGatt mGattForClosure = (BluetoothGatt) mMapEntry.getValue();
                mGattForClosure.disconnect();

                String mAddress = (String) mMapEntry.getKey();
                mGattMap.remove(mAddress);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGattMap.clear();

        if (broadcastReceiver.isOrderedBroadcast() || mManager.getBroadcastReceiver().isOrderedBroadcast()) {
            unregisterReceiver(broadcastReceiver);
            unregisterReceiver(mManager.getBroadcastReceiver());
        }

        int AtmosProcessID = android.os.Process.myPid();
        android.os.Process.killProcess(AtmosProcessID);
    }

    public BLEHardwareManager getmManager() {
        return mManager;
    }

    public BluetoothGatt getGatt(String mAdrress) {
        return this.mGattMap.get(mAdrress);
    }

    public void putGatt(String mAddress, BluetoothGatt mGatt) {
        this.mGattMap.put(mAddress, mGatt);
    }

    public void removeGatt(String mAddress) {
        this.mGattMap.remove(mAddress);
        Log.d(TAG, "removeGatt: removed");
    }

    public void showDebug(){
        mIterator = mGattMap.entrySet().iterator();
        while (mIterator.hasNext()) {
            Map.Entry mMapEntry = (Map.Entry) mIterator.next();
            String mAddress = (String) mMapEntry.getKey();
            BluetoothGatt mGattForDebug = (BluetoothGatt) mMapEntry.getValue();
            Log.d(TAG, "showDebug: ADDR: " + mAddress + ", GATT: " + mGattForDebug);
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
}
