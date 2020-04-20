package com.project.atmos.core;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
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

public class BLEModulesRepository implements DataBaseRepositoryManager{
    /* To move later.... */
    private BLEModulesDAO modulesDao;

    /* The final data to send to the ViewModel */
    private MutableLiveData<ArrayList<BLEModuleObject>> modulesList;

    /* App Cycle Status - Mandatory to get the right data... */
    public static final AppCycleStatus APP_CYCLE_STATUS = MainActivity.config.isCycleStatus();

    /* Async Thread Routines */
    GetAllModulesAsyncTask getAllTask;
    UpdateAllModulesAsyncTask updateAllTask;

    /* Executor Thread Services */
    public static final int NB_OF_THREADS = 4;
    public static final ExecutorService executorService = Executors.newFixedThreadPool(NB_OF_THREADS);

    /* Messages ID for the Handler */
    public static final int MODULES_LIST_GOTTEN_MSG_ID = 0;
    public static final int MODULES_LIST_UPDATED_MSG_ID = 1;


    /* Constructor */
    public BLEModulesRepository(Application application) {
        this.modulesList = new MutableLiveData<>();
        BLEModulesDataBase modulesDatabase = BLEModulesDataBase.getInstance(application);
        modulesDao = modulesDatabase.dataAccessObject();
        getAllTask = new GetAllModulesAsyncTask(APP_CYCLE_STATUS, modulesDao);
        updateAllTask = new UpdateAllModulesAsyncTask(APP_CYCLE_STATUS);
    }

    /*******************************/
    /* REPOSITORY MODULES HANDLING */
    /*******************************/

    private static class RepositoryHandler extends Handler {

        BLEModulesRepository repository;

        public RepositoryHandler(BLEModulesRepository repository) {
            this.repository = repository;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch(msg.what) {
                case MODULES_LIST_GOTTEN_MSG_ID:
                    break;
                case MODULES_LIST_UPDATED_MSG_ID:
                    ArrayList<BLEModuleObject> tempModulesList = (ArrayList<BLEModuleObject>) msg.obj;
                    repository.setModulesList(tempModulesList);
                    break;
            }
        }
    }

    private final Handler repositoryHandler = new RepositoryHandler(this);

    /********************/
    /* GETTER / SETTERS */
    /********************/

    public LiveData<ArrayList<BLEModuleObject>> getModulesList(){
        return this.modulesList;
    }

    public void setModulesList(ArrayList<BLEModuleObject> modulesList){
        this.modulesList.setValue(modulesList);
    }

    /************************************************/
    /* BLE MODULES GENERATION OR RETRIEVAL ROUTINES */
    /************************************************/


    public LiveData<ArrayList<BLEModuleObject>> handleAllModules() {
        try {
            this.modulesList.setValue(getAllTask.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.modulesList;
    }

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

    /*******************************/
    /* BLE MODULES UPDATE ROUTINES */
    /*******************************/

    public void updateAllModules(LiveData<ArrayList<BLEModuleObject>> liveModulesList) {
        ArrayList<BLEModuleObject> modulesList = liveModulesList.getValue();

        Callable<ArrayList<BLEModuleObject>> permanentTask = () -> {
            while(true){
                UpdateAllModulesAsyncTask asyncTask = new UpdateAllModulesAsyncTask(APP_CYCLE_STATUS);
                asyncTask.execute(modulesList);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<BLEModuleObject> tempModulesList = asyncTask.get();

                Message msg = this.repositoryHandler.obtainMessage(MODULES_LIST_UPDATED_MSG_ID, modulesList);
                this.repositoryHandler.sendMessage(msg);
            }
        };

        executorService.submit(permanentTask);
    }

    private static class UpdateAllModulesAsyncTask extends AsyncTask<ArrayList<BLEModuleObject>, Void, ArrayList<BLEModuleObject>>{
        AppCycleStatus appCycleStatus;

        private BLEValuesGenerator valuesGenerator;

        private BLEModuleObject module;

        private ArrayList<BLEModuleObject> modulesList;

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

    /*******************************/
    /* DATA BASE REPO. MANAGER     */
    /*******************************/

    public void insertModule(BLEModuleObject mModule){
        BLEModulesDataBase.dataBaseWriteExecutor.execute(() -> {
            modulesDao.insert(new BLEModuleEntity(
                    mModule.getAddress(),
                    mModule.getName()
            ));
        });
    }

    public void insertModule(BluetoothDevice mmModule){
        BLEModulesDataBase.dataBaseWriteExecutor.execute(() -> {
            modulesDao.insert(new BLEModuleEntity(
                    mmModule.getAddress(),
                    mmModule.getName()
            ));
        });
    }
}
