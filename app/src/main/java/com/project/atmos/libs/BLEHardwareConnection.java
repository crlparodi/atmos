package com.project.atmos.libs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.project.atmos.database.BLEModulesDAO;
import com.project.atmos.database.BLEModulesDataBase;
import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.values.AtmosConstants;
import com.project.atmos.values.AtmosStrings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BLEHardwareConnection extends BluetoothGattCallback {
    public static final String TAG = BLEHardwareConnection.class.getSimpleName();

    private List<BluetoothGattService> mArrayList;

    private BLEModulesDAO mDAO;

    private Handler handler = new Handler();

    public static final UUID CUSTOM_SERVICE_UUID = AtmosConstants.convertFromInteger(0xFFE0);
    public static final UUID CUSTOM_CHARACTERISTIC_UUID = AtmosConstants.convertFromInteger(0xFFE1);
//    public static final UUID CUSTOM_CHARACTERISTIC_USER_DESC_UUID = AtmosConstants.convertFromInteger(0x2901);
    public static final UUID CUSTOM_CHARACTERISTIC_CONFIG_UUID = AtmosConstants.convertFromInteger(0x2902);

    private Context mContext;

    public BLEHardwareConnection(Context context) {
        mArrayList = new ArrayList<>();
        this.mDAO = BLEModulesDataBase.getInstance(context).dataAccessObject();
        this.mContext = context;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        Intent mStatusUpdateIntent = new Intent(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.setAction(AtmosStrings.SYNTHESIS_FRAGMENT);
        String mAddress = gatt.getDevice().getAddress();
        BLEModuleEntity mModule = mDAO.getByAddress(mAddress);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mModule.setStatus(1);
                mStatusUpdateIntent.putExtra(AtmosStrings.BLE_STATUS_CHANGED, true);
                boolean bool = gatt.discoverServices();
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();
                mModule.setStatus(0);
                mStatusUpdateIntent.putExtra(AtmosStrings.BLE_STATUS_CHANGED, false);
            }
        } else {
            Log.d(TAG, "onConnectionStateChange: Echec de la connextion - Timeout reached.");
            mStatusUpdateIntent.putExtra(AtmosStrings.BLE_CONNECTION_LOST, mAddress);
            gatt.close();
        }

        mDAO.update(mModule);
        mContext.sendBroadcast(mStatusUpdateIntent);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        mArrayList = gatt.getServices();
        for (BluetoothGattService service : mArrayList) {
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
        byte[] value = characteristic.getValue();
        String value2 = new BigInteger(1, value).toString(16);
        Log.d(TAG, "onCharacteristicRead: Value: " + value2);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        if (CUSTOM_CHARACTERISTIC_CONFIG_UUID.equals(descriptor.getUuid())) {
            Log.d(TAG, "onDescriptorWrite: " + descriptor.getUuid());
            BluetoothGattCharacteristic characteristic = gatt
                    .getService(CUSTOM_SERVICE_UUID)
                    .getCharacteristic(CUSTOM_CHARACTERISTIC_UUID);
            Log.d(TAG, "onDescriptorWrite: Reading characteristic");
            gatt.readCharacteristic(characteristic);
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
    }

    public BluetoothGatt connect(BluetoothAdapter mAdapter, String mAddress){
        BluetoothDevice mDevice = mAdapter.getRemoteDevice(mAddress);
        BluetoothManager mManager = (BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);

        BluetoothGatt mGatt = mDevice.connectGatt(
                this.mContext,
                false, // AUTO-CONNECT
                this);

        handler.postDelayed(() -> {
            if(mGatt != null && mManager.getConnectionState(mDevice, BluetoothProfile.GATT) == BluetoothProfile.STATE_DISCONNECTED){
                mGatt.disconnect();
                mGatt.close();
                onConnectionFailed(mDevice, mAddress);
            }
        }, AtmosConstants.BLUETOOTH_LE_CONNECT_PERIOD);

        return mGatt;
    }

    public void onConnectionFailed(BluetoothDevice mDevice, String mAddress){
        ExecutorService mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(() -> {
            BLEModuleEntity mModule = mDAO.getByAddress(mAddress);
            mModule.setStatus(0);
            mDAO.update(mModule);
        });

        Intent mStatusUpdateIntent = new Intent(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.setAction(AtmosStrings.SYNTHESIS_FRAGMENT);
        mStatusUpdateIntent.putExtra(AtmosStrings.BLE_TIMEOUT_REACHED, mAddress);
        mContext.sendBroadcast(mStatusUpdateIntent);
    }
}
