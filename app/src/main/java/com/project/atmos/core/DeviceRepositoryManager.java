package com.project.atmos.core;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import com.project.atmos.models.BluetoothDeviceInfo;

public interface DeviceRepositoryManager {
    BluetoothDeviceInfo getByAddress(@NonNull String address);
    void insertModule(BluetoothDevice mmModule);
    void updateModule(BluetoothDeviceInfo aModule, boolean aState);
    void updateModule(BluetoothDeviceInfo aModule, double aValue);
    void remove(@NonNull String address);
    void remove(@NonNull BluetoothDeviceInfo device);
}
