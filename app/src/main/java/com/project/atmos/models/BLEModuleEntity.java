package com.project.atmos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ble_modules")
public class BLEModuleEntity {

    @NonNull
    @ColumnInfo(name = "address")
    @PrimaryKey(autoGenerate = false)
    public String address;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "last_connection")
    public String lastConnection;

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

    public String getLastConnection() {
        return this.lastConnection;
    }

    public void setLastConnection(String lastConnection) {
        this.lastConnection = lastConnection;
    }

    public BLEModuleEntity() {}

    @Ignore
    public BLEModuleEntity(@NonNull String address, String name, String lastConnection) {
        this.address = address;
        this.name = name;
        this.lastConnection = lastConnection;
    }
}
