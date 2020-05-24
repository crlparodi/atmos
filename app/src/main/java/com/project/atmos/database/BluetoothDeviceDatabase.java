package com.project.atmos.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.project.atmos.models.Device;
import com.project.atmos.models.Measurement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Device.class, Measurement.class}, exportSchema = false, version = 2)
@TypeConverters(DateConverter.class)
public abstract class BluetoothDeviceDatabase extends RoomDatabase {

    public abstract BluetoothDeviceDAO dataAccessObject();
    private static final String DB_PATH = "/storage/emulated/0/atmos/ble_modules_db.db";
    private static volatile BluetoothDeviceDatabase DB_INSTANCE;
    private static final Integer NUMBER_OF_THREADS = 4;
    public static final ExecutorService dataBaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BluetoothDeviceDatabase getInstance(final Context context){
        if(DB_INSTANCE == null){
            DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    BluetoothDeviceDatabase.class,
                    DB_PATH)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return DB_INSTANCE;
    }

    public String getDbPath() {
        return DB_PATH;
    }

}
