package com.project.atmos.core;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import com.project.atmos.models.BLEModuleObject;

import java.util.ArrayList;

public interface DataBaseRepositoryManager {

//    void populate();
//
    void insertModule(BLEModuleObject mModule);
    void insertModule(BluetoothDevice mmModule);
//    void insertAtPosition(BLEModuleObject module, Integer position);

    /* NOT MANDATORY FOR NOW */
    // BLEModuleObject get(Integer position);
    // BLEModuleObject getByName(String name);
    // BLEModuleObject getByAddress(@NonNull String address);
//    void getAll();

//    void delete(Integer position);
//    void deletebyName(String name);
//    void deletebyAddress(@NonNull String address);
//
//    void clear();
}
