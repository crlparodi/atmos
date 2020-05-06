package com.project.atmos.core;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import com.project.atmos.models.BLEModuleEntity;

public interface DataBaseRepositoryManager {

//    void populate();
//
//    void insertModule(BLEModuleObject mModule);
    void insertModule(BluetoothDevice mmModule);
    void updateModule(BluetoothDevice mmModule, int status);
    void updateModule(BLEModuleEntity mmModule);

    /* NOT MANDATORY FOR NOW */
    // BLEModuleObject get(Integer position);
//     BLEModuleObject getByName(String name);
    // BLEModuleObject getByAddress(@NonNull String address);
//    void getAll();

    void delete(@NonNull String address);
//
//    void clear();
}
