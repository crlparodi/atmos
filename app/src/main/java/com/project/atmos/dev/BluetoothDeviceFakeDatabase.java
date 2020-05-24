package com.project.atmos.dev;

import android.content.Context;
import android.util.Log;

import com.project.atmos.models.BluetoothDeviceInfo;
import com.project.atmos.models.Device;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceFakeDatabase {
    public static final String TAG = BluetoothDeviceFakeDatabase.class.getSimpleName();
    public static final BluetoothDeviceFakeDatabase INSTANCE = new BluetoothDeviceFakeDatabase();

    private List<BluetoothDeviceInfo> database;

    private BluetoothDeviceFakeDatabase() {
        this.database = new ArrayList<>();
    }

    public static BluetoothDeviceFakeDatabase getInstance() {
        return INSTANCE;
    }

    public List<BluetoothDeviceInfo> getDatabase() {
        return database;
    }

    /**
     * BUNCH OF CODE FOR LATER
     *
     *            List<Measurement> mList = new ArrayList<>();
     *            for (int j = 0; j < mNbOfDataPerEntities; j++) {
     *                Measurement measurement = new Measurement(
     *                        mEntity.getAddress(),
     *                        mGenerator.getRandomDataGenerator().getRandomDate().getGregorianChange(),
     *                        mGenerator.getRandomDataGenerator().getRandomData()
     *                );
     *                mList.add(measurement);
     *            }
     *            DeviceMeta mModule = new DeviceMeta(mEntity, mList);
     */


    public void populate(Context context, int mNbOfEntities, int mNbOfDataPerEntities) {
        BluetoothDeviceGenerator mGenerator = new BluetoothDeviceGenerator(context);
        for (int i = 0; i < mNbOfEntities; i++) {
            Device mEntity = mGenerator.generateDevice();
            BluetoothDeviceInfo bDevice = new BluetoothDeviceInfo(mEntity);
            bDevice.setConnected(false);
            bDevice.setData(mGenerator.getRandomDataGenerator().getRandomData());
            database.add(bDevice);
        }
    }

    public void addDevice(BluetoothDeviceInfo bDevice) {
        database.add(bDevice);
    }

    public void deleteByModule(BluetoothDeviceInfo bDevice) {
        if (database.contains(bDevice)) {
            database.remove(bDevice);
        } else {
            Log.d(TAG, "deleteByModule: Le module n'existe pas...");
        }
    }

    public void deleteByAddress(String aAddress) {
        for (BluetoothDeviceInfo module :
                database) {
            if (module.getDevice().getAddress().equals(aAddress)) {
                database.remove(module);
                break;
            }
        }
    }

    public void clearAll() {
        database.clear();
    }
}
