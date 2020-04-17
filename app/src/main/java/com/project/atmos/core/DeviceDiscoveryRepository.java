package com.project.atmos.core;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

public class DeviceDiscoveryRepository {
    private static final DeviceDiscoveryRepository INSTANCE = new DeviceDiscoveryRepository();

    private final MediatorLiveData<ArrayList<BluetoothDevice>> mDevicesList = new MediatorLiveData<>();

    private DeviceDiscoveryRepository(){}

    public static DeviceDiscoveryRepository instance(){
        return INSTANCE;
    }

    public LiveData<ArrayList<BluetoothDevice>> getMDevicesList(){
        return this.mDevicesList;
    }

    public void addDevicesList(LiveData<ArrayList<BluetoothDevice>> mDevicesList){
        this.mDevicesList.addSource(mDevicesList, this.mDevicesList::setValue);
    }

    public void removeDevicesList(LiveData<ArrayList<BluetoothDevice>> mDevicesList){
        this.mDevicesList.removeSource(mDevicesList);
    }
}
