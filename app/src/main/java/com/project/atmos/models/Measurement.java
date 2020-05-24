package com.project.atmos.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "measurement")
@ForeignKey(entity = Device.class,
        parentColumns = "address",
        childColumns = "entityAddress",
        onDelete = ForeignKey.CASCADE)
public class Measurement {
    @NonNull
    @ColumnInfo(name = "entityAddress")
    public String entityAddress;

    @PrimaryKey(autoGenerate = true)
    public int measurementId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "temperature")
    public double temperature;

    public Measurement() {
    }

    @Ignore
    public Measurement(@NonNull String entityAddress, Date date, double temperature) {
        this.entityAddress = entityAddress;
        this.date = date;
        this.temperature = temperature;
    }

    @NonNull
    public String getEntityAddress() {
        return entityAddress;
    }

    public int getMeasurementId() {
        return measurementId;
    }

    public Date getDate() {
        return date;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setEntityAddress(@NonNull String entityAddress) {
        this.entityAddress = entityAddress;
    }

    public void setMeasurementId(int measurementId) {
        this.measurementId = measurementId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
