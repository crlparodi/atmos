package com.project.atmos.libs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.R;
import com.project.atmos.values.AtmosConstants;
import com.project.atmos.values.AtmosStrings;

import java.util.ArrayList;
import java.util.List;

public class BLEHardwareManager {
    public static final String TAG = BLEHardwareManager.class.getSimpleName();

    private Context context;

    protected BluetoothAdapter btAdapter;
    protected BluetoothLeScanner mmScanner;

    private ArrayList<BluetoothDevice> mDevices;
    private MutableLiveData<ArrayList<BluetoothDevice>> mDevicesList;

    private Handler handler;

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
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: Sending STATE OFF");
                        uiUpdateIntent.putExtra(AtmosStrings.BLE_STATE_CHANGED, false);
                        context.sendBroadcast(uiUpdateIntent);
                        break;
                }
            }
        }
    };

    public BLEHardwareManager(Context context) {
        this.context = context;
        this.handler = new Handler();
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mDevices = new ArrayList<>();
        this.mDevicesList = new MutableLiveData<>();
    }

    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }

    public LiveData<ArrayList<BluetoothDevice>> getmDevicesList() {
        return mDevicesList;
    }

    public void setmDevicesList(ArrayList<BluetoothDevice> mDevicesList) {
        this.mDevicesList.setValue(mDevicesList);
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return broadcastReceiver;
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

    public void btLeScan(final boolean enable) {
        ScanCallback mmScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if(!mDevices.contains(result.getDevice())){
                    mDevices.add(result.getDevice());
                    setmDevicesList(mDevices);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(TAG, "onScanFailed: Failed to Scan any Bluetooth LE device");
            }
        };

        mDevices.clear();

        if (btAdapter.isEnabled()) {
            mmScanner = btAdapter.getBluetoothLeScanner();

            ScanSettings mmScanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .setReportDelay(0)
                    .build();

            ScanFilter mmScanFilter = new ScanFilter.Builder()
                    .build();
            List<ScanFilter> mmFilters = new ArrayList<>();
            mmFilters.add(mmScanFilter);

            if (enable) {
                handler.postDelayed(() -> {
                    if (mmScanner != null) {
                        mmScanner.stopScan(mmScanCallback);
                    }
                }, AtmosConstants.BLUETOOTH_LE_SCAN_PERIOD);

                mmScanner.startScan(mmFilters, mmScanSettings, mmScanCallback);
            } else {
                mmScanner.stopScan(mmScanCallback);
            }
        } else {
            Toast.makeText(this.context, AtmosStrings.ToastMessages.BLE_STATE_NOT_ACTIVE, Toast.LENGTH_SHORT).show();
        }
    }
}
