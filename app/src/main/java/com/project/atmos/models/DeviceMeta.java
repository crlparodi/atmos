package com.project.atmos.models;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class DeviceMeta {
    @Embedded public Device device;

    @Relation(
            parentColumn = "address",
            entityColumn = "entityAddress"
    )
    public List<Measurement> history;


    public DeviceMeta() {
        this.device = new Device();
        history = new ArrayList<>();
    }

    public DeviceMeta(String aName, String aAddress) {
        this.device = new Device(aName, aAddress);
        history = new ArrayList<>();
    }

    @Ignore
    public DeviceMeta(Device aDevice) {
        this.device = aDevice;
        history = new ArrayList<>();
    }

    @Ignore
    public DeviceMeta(Device aDevice, List<Measurement> aHistory) {
        this.device = aDevice;
        this.history = history;
    }

    public String getAddress() {
        return device.getAddress();
    }

    public Device getDevice() {
        return device;
    }

    public List<Measurement> getHistory() {
        return history;
    }

    public void setDevice(Device aDevice) {
        this.device = aDevice;
    }

    public void setHistory(List<Measurement> aHistory) {
        this.history = aHistory;
    }
}
