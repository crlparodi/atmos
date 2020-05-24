package com.project.atmos.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.project.atmos.models.Device;
import com.project.atmos.models.DeviceMeta;
import com.project.atmos.models.Measurement;

import java.util.Date;
import java.util.List;

@Dao
public abstract class BluetoothDeviceDAO {

    /**
     * DEVICE META
     */

    @Transaction
    @Query("SELECT * FROM devices")
    public abstract List<DeviceMeta> getAllMetas();

    @Transaction
    @Query("SELECT * FROM devices WHERE address=:aAddress")
    public abstract DeviceMeta getMetaByAddress(String aAddress);

    public void insertMeta(DeviceMeta aMeta) {
        insertDevice(aMeta.getDevice());
        for (Measurement measurement : aMeta.getHistory()
        ) {
            insertMeasurement(measurement);
        }
    }

    public void updateMeta(DeviceMeta aMeta) {
        updateDevice(aMeta.getDevice());
        for (Measurement measurement : aMeta.getHistory()
        ) {
            Measurement temp = getMeasurementForDeviceAddress(aMeta.getAddress(), measurement.getDate());
            if (temp == null) {
                insertMeasurement(measurement);
            }
        }
    }

    public void removeMeta(DeviceMeta aMeta) {
        removeDevice(aMeta.getAddress());
        clearMeasurementsForAddress(aMeta.getAddress());
    }

    public void clearMetas() {
        clearDevices();
        clearMeasurements();
    }

    /**
     * DEVICE
     */

    @Query("SELECT * FROM devices")
    public abstract List<Device> getAllDevices();

    @Query("SELECT * FROM devices WHERE name = :aName")
    public abstract Device getDeviceByName(String aName);

    @Query("SELECT * FROM devices WHERE address = :aAdress")
    public abstract Device getDeviceByAddress(String aAdress);

    @Insert
    public abstract void insertDevice(Device aDevice);

    @Update
    public abstract void updateDevice(Device aDevice);

    public void safeRemoveDevice(String aAddress) {
        removeDevice(aAddress);
        clearMeasurementsForAddress(aAddress);
    }

    @Query("DELETE FROM devices WHERE address = :aAddress")
    public abstract void removeDevice(String aAddress);

    @Query("DELETE FROM devices")
    public abstract void clearDevices();

    /**
     * MEASUREMENT
     */

    @Query("SELECT entityAddress, measurementId, date, temperature FROM measurement LEFT JOIN devices ON entityAddress=address WHERE address=:aAddress ORDER BY date")
    public abstract List<Measurement> getAllMeasurementsForDeviceAddress(String aAddress);

    @Query("SELECT entityAddress, measurementId, date, temperature FROM measurement LEFT JOIN devices ON entityAddress=address WHERE address=:aAddress AND date < :aFrom")
    public abstract List<Measurement> getMeasurementsFromDuration(String aAddress, Date aFrom);

    @Query("SELECT entityAddress, measurementId, date, temperature FROM measurement LEFT JOIN devices ON entityAddress=address WHERE address=:aAddress ORDER BY date DESC LIMIT 1")
    public abstract Measurement getLastMeasurementForDeviceAddress(String aAddress);

    @Query("SELECT entityAddress, measurementId, date, temperature FROM measurement LEFT JOIN devices ON entityAddress=address WHERE address=:aAddress AND date=:aDate")
    public abstract Measurement getMeasurementForDeviceAddress(String aAddress, Date aDate);

    @Query("INSERT INTO measurement(entityAddress, date, temperature) VALUES (:aAddress, :aDate, :aValue)")
    public abstract void insertMeasurement(String aAddress, Date aDate, Double aValue);

    @Insert
    public abstract void insertMeasurement(Measurement aMeasurement);

    @Query("DELETE FROM measurement WHERE entityAddress=:aAddress")
    public abstract void clearMeasurementsForAddress(String aAddress);

    @Query("DELETE FROM measurement")
    public abstract void clearMeasurements();
}
