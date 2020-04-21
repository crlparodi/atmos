package com.project.atmos.core;

import androidx.annotation.NonNull;

import com.project.atmos.models.BLEModuleEntity;

import java.util.List;

public interface RandomRepositoryManager {

    void populateRandom();

    void insertRandom();
    void insertRandomAtPosition(Integer position);

    /* NOT MANDATORY FOR NOW */
    // BLEModuleObject getRandom(Integer position);
    // BLEModuleObject getRandomByName(String name);
    // BLEModuleObject getRandomByAddress(String Address);
    List<BLEModuleEntity> getRandomAll();

    void deleteRandom();
    void deleteRandomAtPosition(Integer position);
    void deleteRandombyName(String name);
    void deleteRandombyAddress(@NonNull String address);

    void clear();
}
