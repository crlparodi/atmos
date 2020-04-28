package com.project.atmos.ui.synthesis;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.project.atmos.core.BLEModulesRepository;
import com.project.atmos.models.BLEModuleEntity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SynthesisViewModel extends AndroidViewModel{

    public BLEModulesRepository repository;

    public LiveData<ArrayList<BLEModuleEntity>> mList;

    public static final int NB_OF_THREADS = 4;
    public ExecutorService executor = Executors.newFixedThreadPool(NB_OF_THREADS);

    public SynthesisViewModel(Application application) {
        super(application);
        this.repository = new BLEModulesRepository(application);
        this.mList = repository.getmList();
    }

    public LiveData<ArrayList<BLEModuleEntity>> getBLEModuleList(){
        return this.mList;
    }

/*    public void updateBLEModulesList(){
        Runnable task = () -> {
            while(true){
                getBLEModuleList();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        executor.execute(task);
    }*/

    public void delete(String address){
        repository.delete(address);
    }
}
