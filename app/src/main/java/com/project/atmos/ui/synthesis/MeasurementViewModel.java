package com.project.atmos.ui.synthesis;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.core.BLEModulesRepository;
import com.project.atmos.models.BLEModuleObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeasurementViewModel extends AndroidViewModel{

    public BLEModulesRepository repository;

    public LiveData<ArrayList<BLEModuleObject>> modulesList;

    public static final int NB_OF_THREADS = 4;
    public ExecutorService executor = Executors.newFixedThreadPool(NB_OF_THREADS);

    public MeasurementViewModel(Application application) {
        super(application);
        this.repository = new BLEModulesRepository(application);
        this.modulesList = new MutableLiveData<>();
        repository.getAllModules();
        this.modulesList = repository.handleAllModules();
        repository.updateAllModules(modulesList);
    }

    public void getFinalBLEModuleList(){
        this.modulesList = repository.getModulesList();
    }

    public LiveData<ArrayList<BLEModuleObject>> getBLEModuleList(){
        return repository.handleAllModules();
    }

    public void updateBLEModulesList(){
        Runnable task = () -> {
            while(true){
                getFinalBLEModuleList();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        executor.execute(task);
    }
}
