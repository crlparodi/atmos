package com.project.atmos.dev;

import android.content.Context;

import com.project.atmos.models.BLEModuleObject;
import com.project.atmos.values.BLEDataType;

import java.util.ArrayList;
import java.util.EnumMap;

public class BLEModulesGenerator {

    protected BLEModuleObject module;
    ArrayList<BLEModuleObject> modulesList = new ArrayList<>();

    private RandomDataGenerator dataGenerator;

    public BLEModulesGenerator(){
        this.dataGenerator = new RandomDataGenerator();
    }

    public EnumMap<BLEDataType, Object> getRandomPermanentData(){
        EnumMap<BLEDataType, Object> permanentDataMap = new EnumMap<BLEDataType, Object>(BLEDataType.class);
        if(!permanentDataMap.isEmpty()) permanentDataMap.clear();

        permanentDataMap.put(BLEDataType.TEMP_DATA, dataGenerator.getRandomTemperature());

        return permanentDataMap;
    }

    public EnumMap<BLEDataType, Object> getRandomUniqueData(){
        EnumMap<BLEDataType, Object> uniqueDataMap = new EnumMap<BLEDataType, Object>(BLEDataType.class);
        if(!uniqueDataMap.isEmpty()) uniqueDataMap.clear();

        uniqueDataMap.put(BLEDataType.NAME, dataGenerator.getRandomName());
        uniqueDataMap.put(BLEDataType.ADDRESS, dataGenerator.getRandomAddress());
        uniqueDataMap.put(BLEDataType.LAST_CONNECTION_DATE, dataGenerator.getRandomDate());
        uniqueDataMap.put(BLEDataType.STATUS, dataGenerator.getRandomStatus());

        return uniqueDataMap;
    }

    public BLEModuleObject generateDevice(){
        EnumMap<BLEDataType, Object> uniqueDataMap = this.getRandomUniqueData();

        this.module = new BLEModuleObject(null);
        this.module.setName((String) uniqueDataMap.get(BLEDataType.NAME));
        this.module.setAddress((String) uniqueDataMap.get(BLEDataType.ADDRESS));

        return this.module;
    }

    public ArrayList<BLEModuleObject> generatePackOfDevices(Integer NB_OF_DEVICES){
        for(Integer i = 0; i < NB_OF_DEVICES; i++){
            this.modulesList.add(this.generateDevice());
        }
        return modulesList;
    }
}
