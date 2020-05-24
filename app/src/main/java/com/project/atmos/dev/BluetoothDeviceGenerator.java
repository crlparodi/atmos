package com.project.atmos.dev;

import android.content.Context;

import com.project.atmos.models.Device;

import java.util.ArrayList;

public class BluetoothDeviceGenerator {

    protected Device mModule;
    ArrayList<Device> mList = new ArrayList<>();

    private RandomDataGenerator mDataGen;

    public BluetoothDeviceGenerator(Context context){
        this.mDataGen = new RandomDataGenerator(context);
    }

    public RandomDataGenerator getRandomDataGenerator() {
        return this.mDataGen;
    }

    public Device generateDevice(){
        this.mModule = new Device();
        this.mModule.setName(mDataGen.getRandomName());
        this.mModule.setAddress(mDataGen.getRandomAddress());
        this.mModule.setLastConnection(mDataGen.getRandomDate().getGregorianChange());

        return this.mModule;
    }

    public ArrayList<Device> generatePackOfDevices(int NB_OF_DEVICES){
        for(int i = 0; i < NB_OF_DEVICES; i++){
            this.mList.add(this.generateDevice());
        }
        return mList;
    }
}
