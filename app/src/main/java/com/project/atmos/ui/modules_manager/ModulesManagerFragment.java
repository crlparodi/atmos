package com.project.atmos.ui.modules_manager;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.R;
import com.project.atmos.core.DeviceDiscoveryRepository;
import com.project.atmos.libs.BLEHardwareManager;

import java.util.ArrayList;

public class ModulesManagerFragment extends Fragment {
    public static final String TAG = "ModulesManagerFragment";

    private Switch btSwitch;
    private BLEHardwareManager bleHardwareManager;

    RecyclerView recyclerView;
    ModulesManagerListAdapter mListAdapter;
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
        this.mListAdapter = new ModulesManagerListAdapter();
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setAdapter(mListAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        mListAdapter.setOnItemClickListener(new ModulesManagerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BluetoothDevice device) {
                modulesManagerViewModel.insertModule(device);
            }
        });

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
            mListAdapter.setmDeviceslist(bluetoothDevices);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        DeviceDiscoveryRepository.instance().addDevicesList(bleHardwareManager.getmDevicesList());
    }

    @Override
    public void onStop() {
        super.onStop();
        DeviceDiscoveryRepository.instance().removeDevicesList(bleHardwareManager.getmDevicesList());
        mListAdapter.setmDeviceslist(null);

    }

}