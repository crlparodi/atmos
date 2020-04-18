package com.project.atmos.ui.modules_manager;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.R;
import com.project.atmos.core.DeviceDiscoveryRepository;
import com.project.atmos.libs.BLEHardwareManager;
import com.project.atmos.values.AtmosStrings;

import java.util.ArrayList;

public class ModulesManagerFragment extends Fragment {
    public static final String TAG = "ModulesManagerFragment";

    private Switch btSwitch;
    private BLEHardwareManager bleHardwareManager;

    RecyclerView recyclerView;
    DeviceListAdapter mDeviceListAdapter;
    private ModulesManagerViewModel modulesManagerViewModel;

    ArrayList<BluetoothDevice> bluetoothDevices;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        bleHardwareManager = new BLEHardwareManager(getActivity());

        modulesManagerViewModel =
                ViewModelProviders.of(this).get(ModulesManagerViewModel.class);
        modulesManagerViewModel.getmDeviceslist().observe(getViewLifecycleOwner(), getmDeviceslistObserver);

        View root = inflater.inflate(R.layout.fragment_mod_manager, container, false);

        this.btSwitch = (Switch) root.findViewById(R.id.atmos_mod_bt_enable);
        this.btSwitch.setChecked(bleHardwareManager.checkBTstatus());
        this.btSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bleHardwareManager.enableDisableBT(isChecked);
            }
        });

        this.recyclerView = root.findViewById(R.id.atmos_mod_recycler_view);
        this.mDeviceListAdapter = new DeviceListAdapter();
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(mDeviceListAdapter);

        Button button = root.findViewById(R.id.atmos_mod_manager_discover_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleHardwareManager.btLeScan(true);
            }
        });

        return root;
    }

    public final Observer<ArrayList<BluetoothDevice>> getmDeviceslistObserver = new Observer<ArrayList<BluetoothDevice>>() {
        @Override
        public void onChanged(ArrayList<BluetoothDevice> bluetoothDevices) {
            mDeviceListAdapter.setmDeviceslist(bluetoothDevices);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        DeviceDiscoveryRepository.instance().addDevicesList(bleHardwareManager.getmDevicesList());
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(AtmosStrings.MAIN_ACTIVITY);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        DeviceDiscoveryRepository.instance().removeDevicesList(bleHardwareManager.getmDevicesList());
        mDeviceListAdapter.setmDeviceslist(null);

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AtmosStrings.MAIN_ACTIVITY)) {
                final boolean state = intent.getBooleanExtra(AtmosStrings.BLE_STATE_CHANGED, false);
                Log.d(TAG, "onReceive: Change STATE");
                btSwitch.setChecked(state);
            }
        }
    };
}