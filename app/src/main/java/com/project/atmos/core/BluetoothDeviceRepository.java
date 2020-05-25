package com.project.atmos.core;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.project.atmos.MainActivity;
import com.project.atmos.database.BluetoothDeviceDAO;
import com.project.atmos.database.BluetoothDeviceDatabase;
import com.project.atmos.dev.BluetoothDeviceFakeDatabase;
import com.project.atmos.dev.BluetoothDeviceGenerator;
import com.project.atmos.models.BluetoothDeviceInfo;
import com.project.atmos.models.Device;
import com.project.atmos.models.DeviceMeta;
import com.project.atmos.models.Measurement;
import com.project.atmos.values.AtmosAppCycle;
import com.project.atmos.values.AtmosStrings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothDeviceRepository implements DeviceRepositoryManager {
    public static final String TAG = BluetoothDeviceRepository.class.getSimpleName();

    Context mContext;

    private BluetoothDeviceDAO mDAO;

    private BluetoothDeviceGenerator mModulesGen;
    private BluetoothDeviceFakeDatabase mFakeInstance;

    /* The final data to send to the ViewModel */
    private MutableLiveData<ArrayList<BluetoothDeviceInfo>> mList;

    /* App Cycle Status - Mandatory to get the right data... */
    public static final AtmosAppCycle APP_CYCLE_STATUS = MainActivity.config.isCycleStatus();

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
    public static final int MODULE_UPDATED_MSG_ID = 4;
    public static final int MODULE_UNKNOWN_MSG_ID = 5;

    /* REPOSITORY MODULES HANDLING */

    private static class RepositoryHandler extends Handler {

        BluetoothDeviceRepository repository;
        Context context;

        public RepositoryHandler(BluetoothDeviceRepository repository, Context context) {
            this.repository = repository;
            this.context = context;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MODULE_ADDED_MSG_ID:
                    repository.getAllModules();
                    Toast.makeText(context, AtmosStrings.ToastMessages.BLE_DEVICE_ADDED, Toast.LENGTH_SHORT).show();
                    break;
                case MODULE_ALREADY_ADDED_MSG_ID:
                    Toast.makeText(context, AtmosStrings.ToastMessages.BLE_DEVICE_ALREADY_ADDED, Toast.LENGTH_SHORT).show();
                    break;
                case MODULE_UNKNOWN_MSG_ID:
                    Toast.makeText(context, AtmosStrings.ToastMessages.BLE_DEVICE_UNKNOWN, Toast.LENGTH_SHORT).show();
                    break;
                case MODULE_UPDATED_MSG_ID:
                    repository.setList((ArrayList<BluetoothDeviceInfo>) msg.obj);
                    break;
                case MODULES_LIST_GOTTEN_MSG_ID:
                    repository.setList((ArrayList<BluetoothDeviceInfo>) msg.obj);
                    break;
                case MODULES_LIST_UPDATED_MSG_ID:
                    repository.setList((ArrayList<BluetoothDeviceInfo>) msg.obj);
                    break;
            }
        }
    }

    /* Constructor */
    public BluetoothDeviceRepository(Application application) {
        this.mContext = application;

        if (APP_CYCLE_STATUS == AtmosAppCycle.PRODUCTION) {
            BluetoothDeviceDatabase modulesDatabase = BluetoothDeviceDatabase.getInstance(application);
            this.mDAO = modulesDatabase.dataAccessObject();
        }
        if (APP_CYCLE_STATUS == AtmosAppCycle.DEMO) {
            this.mModulesGen = new BluetoothDeviceGenerator(application);
            this.mFakeInstance = BluetoothDeviceFakeDatabase.getInstance();
            if (this.mFakeInstance.getDatabase().isEmpty()) {
                this.mFakeInstance.populate(application, 10, 30);
            }
        }
        this.repositoryHandler = new RepositoryHandler(this, this.mContext);
        this.mList = new MutableLiveData<>();
        getAllModules();
    }

    /* GETTER / SETTERS */

    public MutableLiveData<ArrayList<BluetoothDeviceInfo>> getList() {
        return this.mList;
    }

    public void setList(ArrayList<BluetoothDeviceInfo> mList) {
        this.mList.setValue(mList);
    }

    /* BLE MODULES GENERATION OR RETRIEVAL ROUTINES */

    public void getAllModules() {
        mmExecutorService.execute(() -> {
            List<BluetoothDeviceInfo> bList = new ArrayList<>();
            switch (APP_CYCLE_STATUS) {
                case DEMO:
                    bList = mFakeInstance.getDatabase();
                    break;
                case PRODUCTION:
                    List<Device> mList = mDAO.getAllDevices();
                    for (Device mDevice : mList) {
                        BluetoothDeviceInfo bDevice = new BluetoothDeviceInfo(mDevice);
                        Measurement measurement = mDAO.getLastMeasurementForDeviceAddress(mDevice.getAddress());
                        if(measurement != null){
                            bDevice.setData(measurement.getTemperature());
                        } else {
                            bDevice.setData(0.0);
                        }
                        bList.add(bDevice);
                    }
                    break;
            }

            Message msg = repositoryHandler.obtainMessage();
            msg.what = MODULES_LIST_GOTTEN_MSG_ID;
            msg.obj = bList;
            repositoryHandler.sendMessage(msg);
        });
    }

    /**
     * MODULES REPOSITORY MANAGER
     */

    @Override
    public BluetoothDeviceInfo getByAddress(@NonNull String address) {
        BluetoothDeviceInfo bDevice = new BluetoothDeviceInfo(mDAO.getDeviceByAddress(address));
        return bDevice;
    }

    @Override
    public void insertModule(BluetoothDevice mmModule) {
        BluetoothDeviceDatabase.dataBaseWriteExecutor.execute(() -> {
            Device module = mDAO.getDeviceByAddress(mmModule.getAddress());
            Message msg = repositoryHandler.obtainMessage();
            if (module == null) {
                mDAO.insertMeta(new DeviceMeta(
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
    public void updateModule(BluetoothDeviceInfo aModule, boolean aState) {
        BluetoothDeviceDatabase.dataBaseWriteExecutor.execute(() -> {
            Message msg = repositoryHandler.obtainMessage();
            Device module = mDAO.getDeviceByAddress(aModule.getDevice().getAddress());
            if (module == null) {
                msg.what = MODULE_UNKNOWN_MSG_ID;
                msg.obj = null;
            } else {
                if(aState){
                    module.setLastConnection(new Date(System.currentTimeMillis()));
                    mDAO.updateDevice(module);
                }

                List<BluetoothDeviceInfo> bList = mList.getValue();
                for (BluetoothDeviceInfo bDevice : bList) {
                    if (bDevice.getDevice().getAddress().equals(module.getAddress())) {
                        bDevice.setConnected(aModule.isConnected());
                    }
                }
                msg.what = MODULE_UPDATED_MSG_ID;
                msg.obj = bList;
            }
            repositoryHandler.sendMessage(msg);
        });
    }

    @Override
    public void updateModule(BluetoothDeviceInfo aModule, double aValue) {
        BluetoothDeviceDatabase.dataBaseWriteExecutor.execute(() -> {
            Message msg = repositoryHandler.obtainMessage();
            Device module = mDAO.getDeviceByAddress(aModule.getDevice().getAddress());
            if (module == null) {
                msg.what = MODULE_UNKNOWN_MSG_ID;
                msg.obj = null;
            } else {
                /*
                Update the database by inserting a new measurement for the current address.
                 */
                mDAO.insertMeasurement(
                        module.getAddress(),
                        new Date(System.currentTimeMillis()),
                        aValue
                );

                /*
                Update the BluetoothDeviceInfo Wrapper by updating the ModelView List
                 */
                List<BluetoothDeviceInfo> bList = mList.getValue();
                for (BluetoothDeviceInfo bDevice : bList) {
                    if (bDevice.getDevice().getAddress().equals(module.getAddress())) {
                        bDevice.setData(aValue);
                    }
                }
                msg.what = MODULE_UPDATED_MSG_ID;
                msg.obj = bList;
            }
            repositoryHandler.sendMessage(msg);
        });
    }

    @Override
    public void remove(@NonNull String address) {
        if (APP_CYCLE_STATUS == AtmosAppCycle.PRODUCTION) {
            BluetoothDeviceDatabase.dataBaseWriteExecutor.execute(() -> {
                mDAO.safeRemoveDevice(address);
            });
        }
        if (APP_CYCLE_STATUS == AtmosAppCycle.DEMO) {
            mmExecutorService.execute(() -> {
                mFakeInstance.deleteByAddress(address);
            });
        }
    }

    @Override
    public void remove(@NonNull BluetoothDeviceInfo device) {
        if (APP_CYCLE_STATUS == AtmosAppCycle.PRODUCTION) {
            BluetoothDeviceDatabase.dataBaseWriteExecutor.execute(() -> {
                mDAO.safeRemoveDevice(device.getDevice().getAddress());
            });
        }
        if (APP_CYCLE_STATUS == AtmosAppCycle.DEMO) {
            mmExecutorService.execute(() -> {
                mFakeInstance.deleteByAddress(device.getDevice().getAddress());
            });
        }
    }
}