package com.project.atmos.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "devices")
public class Device {

    @NonNull
    @ColumnInfo(name = "address")
    @PrimaryKey(autoGenerate = false)
    public String address;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "last_connection")
    public Date lastConnection;

    @NonNull
    public String getAddress() {
        return this.address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastConnection() {
        return this.lastConnection;
    }

    public void setLastConnection(Date lastConnection) {
        this.lastConnection = lastConnection;
    }

    public Device() {}

    @Ignore
    public Device(@NonNull String address, String name, Date lastConnection) {
        this.address = address;
        this.name = name;
        this.lastConnection = lastConnection;
    }

    @Ignore
    public Device(@NonNull String address, String name) {
        this.address = address;
        this.name = name;
        this.lastConnection = null;
    }
}
