package com.project.atmos.libs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.R;
import com.project.atmos.core.DeviceDiscoveryRepository;
import com.project.atmos.values.AtmosStrings;

import java.util.ArrayList;

public class BLEHardwareManager {
    public static final String TAG = "BLEHardwareManager";

    private Context context;

    protected BluetoothAdapter btAdapter;

    private ArrayList<BluetoothDevice> mDevices;
    private MutableLiveData<ArrayList<BluetoothDevice>> mDevicesList;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: " + device.getName() + ", " + device.getAddress());
                mDevices.add(device);
                setmDevicesList(mDevices);
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                Intent uiUpdateIntent = new Intent();
                uiUpdateIntent.setAction(AtmosStrings.MAIN_ACTIVITY);
                switch (state) {
                    /* Get different BLE States */
                    /* For example to update the Switch on a Fragment */
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: Sending STATE ON");
                        uiUpdateIntent.putExtra(AtmosStrings.BLE_STATE_CHANGED, true);
                        context.sendBroadcast(uiUpdateIntent);
                        btDiscovery();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: Sending STATE OFF");
                        uiUpdateIntent.putExtra(AtmosStrings.BLE_STATE_CHANGED, false);
                        context.sendBroadcast(uiUpdateIntent);
                        btDiscoveryStop();
                        break;
                }
            }
        }
    };

    public BLEHardwareManager(Context context) {
        this.context = context;
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mDevices = new ArrayList<>();
        this.mDevicesList = new MutableLiveData<>();
    }

    public LiveData<ArrayList<BluetoothDevice>> getmDevicesList() {
        return mDevicesList;
    }

    public void setmDevicesList(ArrayList<BluetoothDevice> mDevicesList) {
        this.mDevicesList.setValue(mDevicesList);
    }

    public static boolean checkBTSupport(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, R.string.atmos_ble_hardware_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean checkBTstatus() {
        return this.btAdapter.isEnabled();
    }

    public void enableDisableBT(boolean bool) {
        if (bool) {
            if (!btAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.context.startActivity(enableBTIntent);

                IntentFilter btIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                this.context.registerReceiver(this.broadcastReceiver, btIntentFilter);
            }
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.disable();

                IntentFilter btIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                this.context.registerReceiver(this.broadcastReceiver, btIntentFilter);
            }
        }
    }

    public void btDiscovery() {
        IntentFilter discoveryDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
            mDevices.clear();

            this.context.registerReceiver(broadcastReceiver, discoveryDevicesIntent);
            btAdapter.startDiscovery();
        } else {
            mDevices.clear();

            this.context.registerReceiver(broadcastReceiver, discoveryDevicesIntent);
            btAdapter.startDiscovery();
        }
    }

    public void btDiscoveryStop() {
        btAdapter.cancelDiscovery();
    }
}
