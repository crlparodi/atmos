package com.project.atmos.ui.modules_manager;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.project.atmos.core.BluetoothDeviceRepository;
import com.project.atmos.core.DeviceDiscoveryRepository;
import com.project.atmos.ui.synthesis.SynthesisListAdapter;

import java.util.ArrayList;

public class ModulesManagerViewModel extends AndroidViewModel {
    public static final String TAG = "ModulesManagerViewModel";

    BluetoothDeviceRepository mRepository;

    public ModulesManagerViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BluetoothDeviceRepository(application);
    }

    public LiveData<ArrayList<BluetoothDevice>> getmDeviceslist() {
        return DeviceDiscoveryRepository.instance().getMDevicesList();
    }

    public void insertModule(BluetoothDevice mmDevice){
        mRepository.insertModule(mmDevice);
    }
}