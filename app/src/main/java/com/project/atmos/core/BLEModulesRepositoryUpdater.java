package com.project.atmos.core;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import com.project.atmos.database.BLEModulesDAO;
import com.project.atmos.database.BLEModulesDataBase;

public class BLEModulesRepositoryUpdater extends BLEModulesRepository {
    private BLEModulesDAO mDAO;

    public BLEModulesRepositoryUpdater(Application application) {
        super(application);
        this.mDAO = BLEModulesDataBase.getInstance(application).dataAccessObject();
    }

    public void updateModuleOnUI(BluetoothDevice mmDevice, int status){
        super.updateModule(mmDevice, status);
    }
}
