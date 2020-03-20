package com.project.atmos.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.project.atmos.models.BLEModuleEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = BLEModuleEntity.class, exportSchema = false, version = 1)
public abstract class BLEModulesDataBase extends RoomDatabase {

    public abstract BLEModulesDAO dataAccessObject();
    private static final String DB_PATH = "/storage/emulated/0/folder/ble_modules_db.db";
    private static volatile BLEModulesDataBase DB_INSTANCE;
    private static final Integer NUMBER_OF_THREADS = 4;
    public static final ExecutorService dataBaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BLEModulesDataBase getInstance(final Context context){
        if(DB_INSTANCE == null){
            DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    BLEModulesDataBase.class,
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
