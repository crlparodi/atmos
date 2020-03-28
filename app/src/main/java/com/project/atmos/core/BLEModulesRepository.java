package com.project.atmos.core;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.MainActivity;
import com.project.atmos.database.BLEModulesDAO;
import com.project.atmos.database.BLEModulesDataBase;
import com.project.atmos.dev.BLEModulesGenerator;
import com.project.atmos.dev.BLEValuesGenerator;
import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.models.BLEModuleObject;
import com.project.atmos.values.AppCycleStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BLEModulesRepository{
    private BLEModulesDAO modulesDao;

    private MutableLiveData<ArrayList<BLEModuleObject>> modulesList;

    GetAllModulesAsyncTask getAllTask;
    UpdateAllModulesAsyncTask updateAllTask;

    public static final Integer NB_OF_THREADS = 4;
    public static final ExecutorService executorService = Executors.newFixedThreadPool(NB_OF_THREADS);

    public static final AppCycleStatus APP_CYCLE_STATUS = MainActivity.config.isCycleStatus();

    public BLEModulesRepository(Application application) {
        this.modulesList = new MutableLiveData<>();
        BLEModulesDataBase modulesDatabase = BLEModulesDataBase.getInstance(application);
        modulesDao = modulesDatabase.dataAccessObject();
        getAllTask = new GetAllModulesAsyncTask(APP_CYCLE_STATUS, modulesDao);
        updateAllTask = new UpdateAllModulesAsyncTask(APP_CYCLE_STATUS);
    }

    private final Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            ArrayList<BLEModuleObject> tempModulesList = (ArrayList<BLEModuleObject>) msg.obj;
            modulesList.setValue(tempModulesList);
        }
    };
    public static final int MODULES_LIST_UPDATED = 1;

    public LiveData<ArrayList<BLEModuleObject>> getFinalModulesList(){
        return this.modulesList;
    }

    public LiveData<ArrayList<BLEModuleObject>> getModulesList() {
        try {
            this.modulesList.setValue(getAllTask.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.modulesList;
    }

    /* GET ALL MODULES FOR THE LIVE DATA */

    public void getAllModules() {
        getAllTask.execute();
    }

    private static class GetAllModulesAsyncTask extends AsyncTask<Void, Void, ArrayList<BLEModuleObject>>{
        AppCycleStatus appCycleStatus;

        private BLEModulesDAO modulesDao;
        private BLEModulesGenerator modulesGenerator;

        private BLEModuleObject module;

        private ArrayList<BLEModuleObject> modulesList;
        private List<BLEModuleEntity> moduleEntitiesList;

        private GetAllModulesAsyncTask(AppCycleStatus cycleStatus, BLEModulesDAO modulesDao){
            appCycleStatus = cycleStatus;
            this.modulesDao = modulesDao;
            this.modulesGenerator = new BLEModulesGenerator();
            modulesList = new ArrayList<>();
        }

        @Override
        protected ArrayList<BLEModuleObject> doInBackground(Void... voids) {
            switch(appCycleStatus){
                case DEVELOPMENT:
                    modulesList = modulesGenerator.generatePackOfDevices(20);
                    break;
                case PRODUCTION:
                    moduleEntitiesList = modulesDao.getAll();
                    for(BLEModuleEntity moduleEntity : moduleEntitiesList){
                        module = new BLEModuleObject(moduleEntity);
                        modulesList.add(module);
                    }
                    break;
            }

            return this.modulesList;
        }
    }

    public void updateAllModules(LiveData<ArrayList<BLEModuleObject>> liveModulesList) {
        ArrayList<BLEModuleObject> modulesList = liveModulesList.getValue();

        System.out.println("Am i launched ?");

        Callable<ArrayList<BLEModuleObject>> permanentTask = () -> {
            while(true){
                UpdateAllModulesAsyncTask asyncTask = new UpdateAllModulesAsyncTask(APP_CYCLE_STATUS);
                asyncTask.execute(modulesList);
                System.out.println("AsyncTask Launched");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<BLEModuleObject> tempModulesList = asyncTask.get();

                Message msg = updateHandler.obtainMessage(MODULES_LIST_UPDATED, modulesList);
                updateHandler.sendMessage(msg);
                System.out.println("Message sent.");
            }
        };

        executorService.submit(permanentTask);
    }

    private static class UpdateAllModulesAsyncTask extends AsyncTask<ArrayList<BLEModuleObject>, Void, ArrayList<BLEModuleObject>>{
        AppCycleStatus appCycleStatus;

        private Handler handler;

        private BLEValuesGenerator valuesGenerator;

        private BLEModuleObject module;

        private ArrayList<BLEModuleObject> modulesList;
        private ListIterator<BLEModuleObject> modulesListIterator;

        private UpdateAllModulesAsyncTask(AppCycleStatus cycleStatus){
            appCycleStatus = cycleStatus;
            valuesGenerator = new BLEValuesGenerator();
            modulesList = new ArrayList<>();
        }

        @Override
        protected ArrayList<BLEModuleObject> doInBackground(ArrayList<BLEModuleObject>... arrayLists) {
            switch(appCycleStatus){
                case DEVELOPMENT:
                    this.modulesList = arrayLists[0];
                    for(int i = 0; i < modulesList.size(); i++){
                        module = modulesList.get(i);
                        if(module.getStatus() == 1) {
                            module.setValue(valuesGenerator.generateRandomData());
                        }
                        modulesList.set(i, module);
                    }
                    break;
                case PRODUCTION:
                    break;
            }

            return this.modulesList;
        }
    }
}
