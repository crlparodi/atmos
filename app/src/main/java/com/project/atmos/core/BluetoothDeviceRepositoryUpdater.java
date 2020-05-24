package com.project.atmos.core;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import com.project.atmos.database.BluetoothDeviceDatabase;
import com.project.atmos.database.BluetoothDeviceDAO;

public class BluetoothDeviceRepositoryUpdater extends BluetoothDeviceRepository {
    private BluetoothDeviceDAO mDAO;

    public BluetoothDeviceRepositoryUpdater(Application application) {
        super(application);
        this.mDAO = BluetoothDeviceDatabase.getInstance(application).dataAccessObject();
    }

    public void updateModuleOnUI(BluetoothDevice mmDevice, int status){
//        super.updateModule(mmDevice, status);
    }
}
