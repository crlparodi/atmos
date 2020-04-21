package com.project.atmos.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.project.atmos.models.BLEModuleEntity;

import java.util.List;

@Dao
public interface BLEModulesDAO {

    @Query("SELECT * FROM ble_modules")
    public List<BLEModuleEntity> getAll();

    @Query("SELECT * FROM ble_modules WHERE name = :name")
    public BLEModuleEntity getByName(String name);

    @Query("SELECT * FROM ble_modules WHERE address = :address")
    public BLEModuleEntity getByAddress(String address);

    @Insert
    public void insert(BLEModuleEntity module);

    @Update
    public void update(BLEModuleEntity module);

    @Query("DELETE from ble_modules WHERE address = :address")
    public void delete(String address);

    @Query("DELETE from ble_modules")
    public void clear();
}
