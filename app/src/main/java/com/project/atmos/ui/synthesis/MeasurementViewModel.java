package com.project.atmos.ui.synthesis;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.MainActivity;
import com.project.atmos.core.BLEModulesRepository;
import com.project.atmos.dev.BLEModulesGeneratorHandler;
import com.project.atmos.models.BLEModuleObject;
import com.project.atmos.values.BLEDataType;

import java.util.ArrayList;

public class MeasurementViewModel extends AndroidViewModel{

    BLEModulesGeneratorHandler devgen;
    private static final Integer NB_OF_DEVICES_GENERATED = 10;

    public BLEModulesRepository repository;

    public LiveData<ArrayList<BLEModuleObject>> modulesList;

    public MeasurementViewModel(Application application) {
        super(application);
        this.repository = new BLEModulesRepository(application);
        this.modulesList = new MutableLiveData<>();
        repository.getAllModules();
        this.modulesList = repository.getModulesList();
    }

    public LiveData<ArrayList<BLEModuleObject>> getBLEModuleList(){
        return repository.getModulesList();
    }

//    public void generate() {
//        ArrayList<BLEModuleObject> BLEModules = new ArrayList<BLEModuleObject>();
//        this.devgen = new BLEModulesGeneratorHandler();
//
//        for(int i = 0; i < this.NB_OF_DEVICES_GENERATED; i++){
//            BLEModuleObject BLEModule = this.devgen.generateDevice();
//            BLEModules.add(BLEModule);
//        }
//
//        modulesList.setValue(BLEModules);
//    }
//
//    public void refresh(ArrayList<BLEModuleObject> BLEModulesList) {
//        BLEModuleObject module = new BLEModuleObject(null);
//
//        for(int i = 0; i < BLEModulesList.size(); i++){
//            module.setName(BLEModulesList.get(i).getName());
//            module.setAddress(BLEModulesList.get(i).getAddress());
//            module.setLastAcuiredTemperature((Double) devgen.getRandomPermanentData().get(BLEDataType.TEMP_DATA));
//            BLEModulesList.set(i, module);
//        }
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        modulesList.setValue(BLEModulesList);
//    }
//
//    public void update(Context context){
//        this.devgen = new BLEModulesGeneratorHandler();
//        devgen.updatePermanently(MainActivity.BLEModulesList);
//
//        this.modulesList.setValue(devgen.getBLEList());
//    }
}
