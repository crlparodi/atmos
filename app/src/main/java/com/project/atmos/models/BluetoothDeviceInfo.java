package com.project.atmos.models;

public class BluetoothDeviceInfo {
    private Device bDevice;
    private boolean bConnected;
    private Double bData;

    public BluetoothDeviceInfo(Device aDevice) {
        this.bDevice = aDevice;
        this.bConnected = false;
        this.bData = 0.0;
    }

    public Device getDevice() {
        return bDevice;
    }

    public boolean isConnected() {
        return this.bConnected;
    }

    public Double getData() {
        return bData;
    }

    public void setDevice(Device bDevice) {
        this.bDevice = bDevice;
    }

    public void setConnected(boolean aConnected) {
        this.bConnected = aConnected;
    }

    public void setData(Double bData) {
        this.bData = bData;
    }
}
