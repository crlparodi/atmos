package com.project.atmos.ui.synthesis;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.project.atmos.core.BluetoothDeviceRepository;
import com.project.atmos.models.BluetoothDeviceInfo;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SynthesisViewModel extends AndroidViewModel{

    public BluetoothDeviceRepository repository;

    private LiveData<ArrayList<BluetoothDeviceInfo>> mList;

    public static final int NB_OF_THREADS = 4;
    public ExecutorService executor = Executors.newFixedThreadPool(NB_OF_THREADS);

    public SynthesisViewModel(Application application) {
        super(application);
        this.repository = new BluetoothDeviceRepository(application);
        this.mList = repository.getList();
    }

    public LiveData<ArrayList<BluetoothDeviceInfo>> getList() {
        return repository.getList();
    }

    public void remove(String address){
        repository.remove(address);
    }

    public void remove(BluetoothDeviceInfo device){
        repository.remove(device);
    }
}
