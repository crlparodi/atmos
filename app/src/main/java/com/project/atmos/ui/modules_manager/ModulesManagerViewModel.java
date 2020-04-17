package com.project.atmos.ui.modules_manager;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.core.DeviceDiscoveryRepository;
import com.project.atmos.libs.BLEHardwareManager;

import java.util.ArrayList;

public class ModulesManagerViewModel extends AndroidViewModel {
    public static final String TAG = "ModulesManagerViewModel";

    public ModulesManagerViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<BluetoothDevice>> getmDeviceslist() {
        return DeviceDiscoveryRepository.instance().getMDevicesList();
    }
}