package com.project.atmos.libs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.project.atmos.core.BLEModulesRepository;
import com.project.atmos.database.BLEModulesDAO;
import com.project.atmos.database.BLEModulesDataBase;
import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.ui.synthesis.SynthesisFragment;
import com.project.atmos.values.AtmosConstants;
import com.project.atmos.values.AtmosStrings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BLEHardwareConnection extends BluetoothGattCallback {
    public static final String TAG = BLEHardwareConnection.class.getSimpleName();

    private List<BluetoothGattService> mArrayList;

    private BLEModulesDAO mDAO;

    public static final UUID CUSTOM_SERVICE_UUID = AtmosConstants.convertFromInteger(0xFFE0);
    public static final UUID CUSTOM_CHARACTERISTIC_UUID = AtmosConstants.convertFromInteger(0xFFE1);
    public static final UUID CUSTOM_CHARACTERISTIC_USER_DESC_UUID = AtmosConstants.convertFromInteger(0x2901);
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
        BLEModuleEntity mModule = mDAO.getByAddress(gatt.getDevice().getAddress());

        if (BluetoothProfile.STATE_CONNECTED == newState) {
            boolean bool = gatt.discoverServices();
            Log.d(TAG, "onConnectionStateChange: Connection to " + gatt.getDevice().getName());

            mModule.setStatus(1);
            mStatusUpdateIntent.putExtra(AtmosStrings.BLE_STATUS_CHANGED, true);
        }
        if (BluetoothProfile.STATE_DISCONNECTED == newState){
            gatt.close();

            mModule.setStatus(0);
            mStatusUpdateIntent.putExtra(AtmosStrings.BLE_STATUS_CHANGED, false);
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

        BluetoothGattCharacteristic mCharacteristic = gatt.getService(CUSTOM_SERVICE_UUID).getCharacteristic(CUSTOM_CHARACTERISTIC_UUID);
        gatt.setCharacteristicNotification(mCharacteristic, true);

        BluetoothGattDescriptor mDescriptor = mCharacteristic.getDescriptor(CUSTOM_CHARACTERISTIC_CONFIG_UUID);
        mDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(mDescriptor);
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

    public BluetoothGatt connect(BluetoothAdapter mAdapter, String mAddress) {
        BluetoothDevice mDevice = mAdapter.getRemoteDevice(mAddress);
        BluetoothGatt mServer = mDevice.connectGatt(
                this.mContext,
                false, // AUTO-CONNECT
                this);
        return mServer;
    }

    public void disconnect(BluetoothGatt gatt) {
        if(gatt == null){
            Log.d(TAG, "disconnect: The device is not even connected.");
            return;
        }
        gatt.disconnect();
    }
}
