package com.project.atmos.libs;
import com.project.atmos.values.Tags;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class BLEHardwareManager {

    private static final int REQUEST_ENABLE_BT = 1;
    protected BluetoothAdapter globalBLEAdapter;
    protected BluetoothManager globalBLEManager;
    protected Intent BLEEnablerIntent;
    protected AppCompatActivity BLEDedicatedActivity;

    public BLEHardwareManager(AppCompatActivity BLETargetedActivity){
        this.BLEDedicatedActivity = BLETargetedActivity;
    }

    public void BluetoothLEHardwareSupport(){
        globalBLEManager = (BluetoothManager) BLEDedicatedActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        globalBLEAdapter = globalBLEManager.getAdapter();

        if(globalBLEAdapter == null){
            Log.e(Tags.getTag(Tags.BLUETOOTHLE),
                    "The BluetoothLE is not supported on this device.\n" +
                            "Closing the application.");
        }
    }

    public void BluetoothLEHardwareEnableRequest(){
        if(!globalBLEAdapter.isEnabled()) {
            BLEEnablerIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            BLEDedicatedActivity.startActivityForResult(BLEEnablerIntent, REQUEST_ENABLE_BT);
        }
    }
}
