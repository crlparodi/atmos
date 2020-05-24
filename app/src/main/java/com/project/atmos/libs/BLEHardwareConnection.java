package com.project.atmos.libs;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.project.atmos.core.BluetoothDataComputer;
import com.project.atmos.core.BluetoothDeviceRepository;
import com.project.atmos.models.BluetoothCharacteristicData;
import com.project.atmos.models.BluetoothDeviceInfo;
import com.project.atmos.values.AtmosConstants;
import com.project.atmos.values.AtmosStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BLEHardwareConnection extends BluetoothGattCallback {
    public static final String TAG = BLEHardwareConnection.class.getSimpleName();

    private List<BluetoothGattService> mServicesList;

    private BluetoothDeviceRepository mRepository;

    private Handler mHandler = new Handler();

    private BluetoothDataComputer mComputer;

    public static final UUID CUSTOM_SERVICE_UUID = AtmosConstants.convertFromInteger(0xFFE0);
    public static final UUID CUSTOM_CHARACTERISTIC_UUID = AtmosConstants.convertFromInteger(0xFFE1);
    public static final UUID CUSTOM_CHARACTERISTIC_CONFIG_UUID = AtmosConstants.convertFromInteger(0x2902);

    public static final int GATT_INTERNAL_ERROR = 0x129;

    private Context mContext;

    public BLEHardwareConnection(Application application) {
        mServicesList = new ArrayList<>();
        this.mRepository = new BluetoothDeviceRepository(application);
        this.mComputer = new BluetoothDataComputer();
        this.mContext = application;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        Intent mStatusUpdateIntent = new Intent(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.setAction(AtmosStrings.SYNTHESIS_FRAGMENT);
        String mAddress = gatt.getDevice().getAddress();
        BluetoothDeviceInfo mModule = mRepository.getByAddress(mAddress);
        boolean isConnected = false;

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                boolean bool = gatt.discoverServices();
                mStatusUpdateIntent.putExtra(AtmosStrings.BLE_STATE_CHANGED, true);
                isConnected = true;
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();
                mStatusUpdateIntent.putExtra(AtmosStrings.BLE_STATE_CHANGED, false);
                isConnected = false;
            }
        } else {
            Log.d(TAG, "onConnectionStateChange: Echec de la connextion - Timeout reached.");
            mStatusUpdateIntent.putExtra(AtmosStrings.BLE_CONNECTION_LOST, true);
            gatt.close();
        }

        mRepository.updateModule(mModule, isConnected);
        mStatusUpdateIntent.putExtra(AtmosStrings.BLE_DEVICE_ADDRESS, gatt.getDevice().getAddress());
        mContext.sendBroadcast(mStatusUpdateIntent);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        if (status == GATT_INTERNAL_ERROR) {
            Log.e(TAG, "onServicesDiscovered: " + AtmosStrings.DebugMessages.SERVICE_DISCOVERY_FAILED);
            gatt.disconnect();
            return;
        }

        mServicesList = gatt.getServices();
        for (BluetoothGattService service : mServicesList) {
            Log.d(TAG, "onServicesDiscovered: Service: " + service);
        }

        BluetoothGattService mService = gatt.getService(CUSTOM_SERVICE_UUID);

        if (mService != null) {
            BluetoothGattCharacteristic mCharacteristic = mService.getCharacteristic(CUSTOM_CHARACTERISTIC_UUID);
            gatt.setCharacteristicNotification(mCharacteristic, true);

            BluetoothGattDescriptor mDescriptor = mCharacteristic.getDescriptor(CUSTOM_CHARACTERISTIC_CONFIG_UUID);
            mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(mDescriptor);
        } else {
            Log.d(TAG, "onServicesDiscovered: Ce module n'est pas fait pour être utilisé sous atmos, déconnexion...");
            gatt.disconnect();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        BluetoothCharacteristicData characteristicData = mComputer.computeCharacteristic(characteristic);

        if(characteristicData == null){
            Log.e(TAG, "onCharacteristicChanged: " + AtmosStrings.DebugMessages.SENSOR_DISCOVERY_FAILED);
            gatt.disconnect();
            return;
        }

        double temperature = mComputer.computeData(characteristicData);

        BluetoothDevice mmDevice = gatt.getDevice();
        BluetoothDeviceInfo mDevice = mRepository.getByAddress(mmDevice.getAddress());
        mRepository.updateModule(mDevice, temperature);

        Intent mStatusUpdateIntent = new Intent(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.setAction(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.putExtra(AtmosStrings.BLE_DEVICE_ADDRESS, mmDevice.getAddress());
        mStatusUpdateIntent.putExtra(AtmosStrings.BLE_DATA_UPDATED, true);
        mStatusUpdateIntent.putExtra(AtmosStrings.BLE_DEVICE_DATA, temperature);
        mContext.sendBroadcast(mStatusUpdateIntent);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        if (CUSTOM_CHARACTERISTIC_CONFIG_UUID.equals(descriptor.getUuid())) {
            BluetoothGattCharacteristic characteristic = gatt
                    .getService(CUSTOM_SERVICE_UUID)
                    .getCharacteristic(CUSTOM_CHARACTERISTIC_UUID);
            gatt.readCharacteristic(characteristic);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    public BluetoothGatt connect(BluetoothAdapter mAdapter, String mAddress) {
        BluetoothDevice mDevice = mAdapter.getRemoteDevice(mAddress);
        BluetoothManager mManager = (BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);

        BluetoothGatt mGatt = mDevice.connectGatt(
                this.mContext,
                false, // AUTO-CONNECT
                this,
                BluetoothDevice.TRANSPORT_LE);

        mHandler.postDelayed(() -> {
            if (mGatt != null && mManager.getConnectionState(mDevice, BluetoothProfile.GATT) == BluetoothProfile.STATE_DISCONNECTED) {
                mGatt.disconnect();
                mGatt.close();
                onConnectionFailed(mDevice, mAddress);
            }
        }, AtmosConstants.BLUETOOTH_LE_CONNECT_PERIOD);

        return mGatt;
    }

    public void onConnectionFailed(BluetoothDevice mDevice, String mAddress) {
        ExecutorService mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(() -> {
            BluetoothDeviceInfo mModule = mRepository.getByAddress(mAddress);
//            mModule.setStatus(0);
//            mRepository.updateModule(mModule);
        });

        Intent mStatusUpdateIntent = new Intent(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.setAction(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.putExtra(AtmosStrings.BLE_DEVICE_ADDRESS, mDevice.getAddress());
        mStatusUpdateIntent.putExtra(AtmosStrings.BLE_TIMEOUT_REACHED, true);
        mContext.sendBroadcast(mStatusUpdateIntent);
    }
}
