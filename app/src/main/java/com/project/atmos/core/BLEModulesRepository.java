package com.project.atmos.core;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.MainActivity;
import com.project.atmos.database.BLEModulesDAO;
import com.project.atmos.database.BLEModulesDataBase;
import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.models.BLEModuleObject;
import com.project.atmos.values.AppCycleStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BLEModulesRepository{
    private BLEModulesDAO modulesDao;
    private MutableLiveData<ArrayList<BLEModuleObject>> modulesList;
    GetAllModulesAsyncTask getAllTask;

    public static final AppCycleStatus APP_CYCLE_STATUS = MainActivity.config.isCycleStatus();

    public BLEModulesRepository(Application application) {
        this.modulesList = new MutableLiveData<>();
        BLEModulesDataBase modulesDatabase = BLEModulesDataBase.getInstance(application);
        modulesDao = modulesDatabase.dataAccessObject();
        getAllTask = new GetAllModulesAsyncTask(modulesDao);
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
        BLEModulesRepository parent;

        private BLEModulesDAO modulesDao;

        private BLEModuleObject module;

        private ArrayList<BLEModuleObject> modulesList;
        private List<BLEModuleEntity> moduleEntitiesList;

        private GetAllModulesAsyncTask(BLEModulesDAO modulesDao){
            this.modulesDao = modulesDao;
            modulesList = new ArrayList<>();
        }

        @Override
        protected ArrayList<BLEModuleObject> doInBackground(Void... voids) {
            moduleEntitiesList = modulesDao.getAll();

            for(BLEModuleEntity moduleEntity : moduleEntitiesList){
                module = new BLEModuleObject(moduleEntity);
                modulesList.add(module);
            }

            return this.modulesList;
        }
    }

    /* GET ALL MODULES FROM THE DATABASE - DB_REPOSITORY_MANAGER */

//    @Override
//    public void getAll() {
//        GetAllAsyncTask task = new GetAllAsyncTask(modulesDao);
//        task.execute();
//    }
//    public void getAllPostExec(ArrayList<BLEModuleObject> modulesList){
//        this.modulesList = modulesList;
//    }
//
//    private static class GetAllAsyncTask extends AsyncTask<Void, Void, ArrayList<BLEModuleObject>>{
//        BLEModulesRepository parent;
//
//        private BLEModulesDAO modulesDao;
//
//        private BLEModuleObject module;
//
//        private ArrayList<BLEModuleObject> modulesList;
//        private List<BLEModuleEntity> moduleEntitiesList;
//
//        private GetAllAsyncTask(BLEModulesDAO modulesDao){
//            this.modulesDao = modulesDao;
//        }
//
//        @Override
//        protected ArrayList<BLEModuleObject> doInBackground(Void... voids) {
//            this.moduleEntitiesList = modulesDao.getAll();
//
//            for(BLEModuleEntity moduleEntity : moduleEntitiesList){
//                this.module = new BLEModuleObject(moduleEntity);
//                this.modulesList.add(module);
//            }
//
//            return this.modulesList;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<BLEModuleObject> modulesList) {
//            parent.getAllPostExec(modulesList);
//        }
//    }
}
