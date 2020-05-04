package com.project.atmos.core;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.MainActivity;
import com.project.atmos.database.BLEModulesDAO;
import com.project.atmos.database.BLEModulesDataBase;
import com.project.atmos.dev.BLEModulesGenerator;
import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.values.AtmosAppCycle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BLEModulesRepository implements DataBaseRepositoryManager {
    public static final String TAG = BLEModulesRepository.class.getSimpleName();

    Context context;

    private BLEModulesDAO mDAO;
    private BLEModulesGenerator mModulesGen;

    /* The final data to send to the ViewModel */
    private MutableLiveData<ArrayList<BLEModuleEntity>> mList;

    /* App Cycle Status - Mandatory to get the right data... */
    public static final AtmosAppCycle APP_CYCLE_STATUS = MainActivity.config.isCycleStatus();

    /* Async Thread Routines */
//    UpdateAllModulesAsyncTask updateAllTask;

    /* Executor Thread Services */
    public static final int NB_OF_THREADS = 1;
    /* En mode developpement, augmenter ce nombre de threads créée des anomalies au niveau de la detection du nombre de mots
     * pour la génération d'un nombre aléatoire, en effet, une même instance du générateur est utilisé sur plusieurs threads.
     * Reste à 1 en attendant une solution à terme.
     */
    public static final ExecutorService mmExecutorService = Executors.newFixedThreadPool(NB_OF_THREADS);

    /* Handler */
    private final Handler repositoryHandler;

    /* Messages ID for the Handler */
    public static final int MODULES_LIST_GOTTEN_MSG_ID = 0;
    public static final int MODULES_LIST_UPDATED_MSG_ID = 1;
    public static final int MODULE_ADDED_MSG_ID = 2;
    public static final int MODULE_ALREADY_ADDED_MSG_ID = 3;

    /* REPOSITORY MODULES HANDLING */

    private static class RepositoryHandler extends Handler {

        BLEModulesRepository repository;
        Context context;

        public RepositoryHandler(BLEModulesRepository repository, Context context) {
            this.repository = repository;
            this.context = context;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MODULE_ADDED_MSG_ID:
                    Toast.makeText(context, "Module inserted into the database, go to Synthesis to connect with it", Toast.LENGTH_SHORT).show();
                    break;
                case MODULE_ALREADY_ADDED_MSG_ID:
                    Toast.makeText(context, "Ce module est déjà ajouté.", Toast.LENGTH_SHORT).show();
                    break;
                case MODULES_LIST_GOTTEN_MSG_ID:
                    repository.setmList((ArrayList<BLEModuleEntity>) msg.obj);
                    break;
                case MODULES_LIST_UPDATED_MSG_ID:
                    repository.setmList((ArrayList<BLEModuleEntity>) msg.obj);
                    break;
            }
        }
    }

    /* Constructor */
    public BLEModulesRepository(Application application) {
        this.context = application;
        BLEModulesDataBase modulesDatabase = BLEModulesDataBase.getInstance(application);
        mDAO = modulesDatabase.dataAccessObject();
        this.mModulesGen = new BLEModulesGenerator();
        repositoryHandler = new RepositoryHandler(this, this.context);
        this.mList = new MutableLiveData<>();
        getAllModules();
//        updateAllTask = new UpdateAllModulesAsyncTask(APP_CYCLE_STATUS);
    }

    /* GETTER / SETTERS */

    public LiveData<ArrayList<BLEModuleEntity>> getmList() {
        getAllModules();
        return this.mList;
    }

    public void setmList(ArrayList<BLEModuleEntity> mList) {
        this.mList.setValue(mList);
    }

    /* BLE MODULES GENERATION OR RETRIEVAL ROUTINES */

    public void getAllModules() {
        mmExecutorService.execute(() -> {
            List<BLEModuleEntity> mEntitiesList = new ArrayList<>();
            switch (APP_CYCLE_STATUS) {
                case DEVELOPMENT:
                    mEntitiesList = mModulesGen.generatePackOfDevices(20);
                    break;
                case PRODUCTION:
                    mEntitiesList = mDAO.getAll();

                    break;
            }

            Message msg = repositoryHandler.obtainMessage();
            msg.what = MODULES_LIST_GOTTEN_MSG_ID;
            msg.obj = mEntitiesList;
            repositoryHandler.sendMessage(msg);
        });
    }


    /*

    public void updateAllModules(LiveData<ArrayList<BLEModuleObject>> liveModulesList) {
        ArrayList<BLEModuleObject> modulesList = liveModulesList.getValue();

        Callable<ArrayList<BLEModuleObject>> permanentTask = () -> {
            while (true) {
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

    private static class UpdateAllModulesAsyncTask extends AsyncTask<ArrayList<BLEModuleObject>, Void, ArrayList<BLEModuleObject>> {
        AppCycleStatus appCycleStatus;

        private BLEValuesGenerator valuesGenerator;

        private BLEModuleObject module;

        private ArrayList<BLEModuleObject> modulesList;

        private UpdateAllModulesAsyncTask(AppCycleStatus cycleStatus) {
            appCycleStatus = cycleStatus;
            valuesGenerator = new BLEValuesGenerator();
            modulesList = new ArrayList<>();
        }

        @Override
        protected ArrayList<BLEModuleObject> doInBackground(ArrayList<BLEModuleObject>... arrayLists) {
            switch (appCycleStatus) {
                case DEVELOPMENT:
                    this.modulesList = arrayLists[0];
                    for (int i = 0; i < modulesList.size(); i++) {
                        module = modulesList.get(i);
                        if (module.getStatus() == 1) {
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
*/

    /* DATA BASE REPOSITORY MANAGER */

    @Override
    public void insertModule(BluetoothDevice mmModule) {
        BLEModulesDataBase.dataBaseWriteExecutor.execute(() -> {
            BLEModuleEntity module = mDAO.getByAddress(mmModule.getAddress());
            Message msg = repositoryHandler.obtainMessage();
            if (module == null) {
                mDAO.insert(new BLEModuleEntity(
                        mmModule.getAddress(),
                        mmModule.getName()
                ));
                msg.what = MODULE_ADDED_MSG_ID;
            } else {
                msg.what = MODULE_ALREADY_ADDED_MSG_ID;
            }
            repositoryHandler.sendMessage(msg);
        });
    }

    @Override
    public void delete(@NonNull String address) {
        BLEModulesDataBase.dataBaseWriteExecutor.execute(() -> {
            mDAO.delete(address);
        });
    }
}
