package com.project.atmos.dev;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.project.atmos.models.BLEModuleObject;
import com.project.atmos.values.BLEDataType;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BLEModulesGeneratorHandler extends BLEModulesGenerator {

    private static final int DATA_UPDATE_NONE = 0;
    private static final int DATA_UPDATE_ON_PROCESS = 10;
    private static final int DATA_UPDATE_OK = 20;
    private static final int DATA_UPDATE_FAILED = 30;

    private static class UpdateHandler extends Handler {

        private ArrayList<BLEModuleObject> BLEModulesList;

        public UpdateHandler(){}

        public ArrayList<BLEModuleObject> getBLEList(){
            return BLEModulesList;
        }

        public void setBLEList(ArrayList<BLEModuleObject> BLEModulesList){
            this.BLEModulesList = BLEModulesList;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case DATA_UPDATE_OK:
                    this.setBLEList((ArrayList<BLEModuleObject>) msg.obj);
                    break;
            }
        }
    }

    private final UpdateHandler dataUpdateHandler = new UpdateHandler();

    private static final Integer NUMBER_OF_THREADS = 4;

    public static final ExecutorService engineExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public BLEModulesGeneratorHandler(){
        super();
    }

    public ArrayList<BLEModuleObject> getBLEList() {
        return dataUpdateHandler.getBLEList();
    }

    public void updatePermanently(ArrayList<BLEModuleObject> BLEModulesList){
        this.engineExecutor.execute(() -> {
            while(true){
                Message msg = new Message();
                dataUpdateHandler.sendEmptyMessage(DATA_UPDATE_NONE);
                super.module = new BLEModuleObject(null);

                try {
                    dataUpdateHandler.sendEmptyMessage(DATA_UPDATE_ON_PROCESS);
                    for(Integer i = 0; i < BLEModulesList.size(); i++){
                        super.module.setName(BLEModulesList.get(i).getName());
                        super.module.setAddress(BLEModulesList.get(i).getAddress());
                        super.module.setLastAcuiredTemperature((Double) super.getRandomPermanentData().get(BLEDataType.TEMP_DATA));
                        BLEModulesList.set(i, module);
                    }

                    msg.what = DATA_UPDATE_OK;
                    msg.obj = BLEModulesList;
                    dataUpdateHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                    dataUpdateHandler.sendEmptyMessage(DATA_UPDATE_FAILED);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
