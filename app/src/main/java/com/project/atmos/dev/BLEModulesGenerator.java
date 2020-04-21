package com.project.atmos.dev;

import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.values.BLEDataType;

import java.util.ArrayList;
import java.util.EnumMap;

public class BLEModulesGenerator {

    protected BLEModuleEntity mModule;
    ArrayList<BLEModuleEntity> mList = new ArrayList<>();

    private RandomDataGenerator mDataGen;

    public BLEModulesGenerator(){
        this.mDataGen = new RandomDataGenerator();
    }

    public EnumMap<BLEDataType, Object> getRandomUniqueData(){
        EnumMap<BLEDataType, Object> uniqueDataMap = new EnumMap<BLEDataType, Object>(BLEDataType.class);
        if(!uniqueDataMap.isEmpty()) uniqueDataMap.clear();

        uniqueDataMap.put(BLEDataType.NAME, mDataGen.getRandomName());
        uniqueDataMap.put(BLEDataType.ADDRESS, mDataGen.getRandomAddress());
        uniqueDataMap.put(BLEDataType.LAST_CONNECTION_DATE, mDataGen.getRandomDate());
        uniqueDataMap.put(BLEDataType.STATUS, mDataGen.getRandomStatus());

        return uniqueDataMap;
    }

    public BLEModuleEntity generateDevice(){
        EnumMap<BLEDataType, Object> uniqueDataMap = this.getRandomUniqueData();

        this.mModule = new BLEModuleEntity();
        this.mModule.setName((String) uniqueDataMap.get(BLEDataType.NAME));
        this.mModule.setAddress((String) uniqueDataMap.get(BLEDataType.ADDRESS));
//        this.module.setStatus((Integer) uniqueDataMap.get(BLEDataType.STATUS));

        return this.mModule;
    }

    public ArrayList<BLEModuleEntity> generatePackOfDevices(Integer NB_OF_DEVICES){
        for(Integer i = 0; i < NB_OF_DEVICES; i++){
            this.mList.add(this.generateDevice());
        }
        return mList;
    }
}
