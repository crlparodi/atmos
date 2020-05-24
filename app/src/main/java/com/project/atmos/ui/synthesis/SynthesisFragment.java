package com.project.atmos.ui.synthesis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.MainActivity;
import com.project.atmos.R;
import com.project.atmos.libs.BLEHardwareConnection;
import com.project.atmos.models.BluetoothDeviceInfo;
import com.project.atmos.values.AtmosStrings;

import java.util.ArrayList;

public class SynthesisFragment extends Fragment implements SynthesisListAdapter.OnLongClickListener {
    public static final String TAG = SynthesisFragment.class.getSimpleName();

    private int mCurrentClickPosition = 0;
    private BLEHardwareConnection mHardwareConnection;

    private SynthesisViewModel mViewModel;

    private RecyclerView recyclerView;
    private SynthesisListAdapter listAdapter;

    private BroadcastReceiver mBroadcastReceiver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.mHardwareConnection = new BLEHardwareConnection(((MainActivity) getActivity()).getApplication());

        this.mViewModel = ViewModelProviders.of(this).get(SynthesisViewModel.class);
        this.mViewModel.getList().observe(getViewLifecycleOwner(), mListObserver);

        listAdapter = new SynthesisListAdapter();
        mBroadcastReceiver = new SynthesisBroadcastReceiver((MainActivity) getActivity(), listAdapter);

        return inflater.inflate(R.layout.fragment_synthesis, container, false);
    }

    public final Observer<ArrayList<BluetoothDeviceInfo>> mListObserver = new Observer<ArrayList<BluetoothDeviceInfo>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<BluetoothDeviceInfo> mList) {
            for(BluetoothDeviceInfo bDevice : mList) {
                if(((MainActivity) getActivity()).getGatt(bDevice.getDevice().getAddress()) != null){
                    bDevice.setConnected(true);
                }
            }
            listAdapter.setModulesList(mList);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.atmos_synthesis_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setHasFixedSize(true);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        this.recyclerView.setAdapter(listAdapter);
        registerForContextMenu(this.recyclerView);

        this.listAdapter.setOnLongClickListener(this);
    }

    @Override
    public void onLongClick(View v, int position) {
        mCurrentClickPosition = position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BluetoothDeviceInfo bDevice = this.listAdapter.getModulesList().get(this.mCurrentClickPosition);
        String mAddress = bDevice.getDevice().getAddress();
        BluetoothAdapter mAdapter = ((MainActivity) getActivity()).getmManager().getBtAdapter();
        switch (item.getItemId()) {
            case R.id.atmos_oc_menu_connect:
                if (mAdapter.isEnabled()) {
                    menuOnConnect(mAdapter, mAddress);
                } else {
                    Toast.makeText(getActivity(), AtmosStrings.ToastMessages.BLUETOOTH_NEEDED, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.atmos_oc_menu_disconnect:
                menuOnDisconnect(mAddress);
                return true;
            case R.id.atmos_oc_menu_update:
                return true;
            case R.id.atmos_oc_menu_delete:
                menuOnDelete(mAddress, bDevice);
                return true;
            case R.id.atmos_oc_menu_details:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter mBTSTatusChangedIntentFilter = new IntentFilter(AtmosStrings.SYNTHESIS_FRAGMENT);
        this.getContext().registerReceiver(this.mBroadcastReceiver, mBTSTatusChangedIntentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.mBroadcastReceiver.isOrderedBroadcast()) {
            this.getContext().unregisterReceiver(this.mBroadcastReceiver);
        }
    }

    public void menuOnConnect(BluetoothAdapter mAdapter, String mAddress) {
        BluetoothDevice mDevice = mAdapter.getRemoteDevice(mAddress);
        if (mDevice != null) {
            BluetoothGatt mGattForConnect = null;
            mGattForConnect = ((MainActivity) getActivity()).getGatt(mAddress);
            ((MainActivity) getActivity()).showDebug();

            Log.d(TAG, "onContextItemSelected: mGattForConnect: " + mGattForConnect);
            if (mGattForConnect == null) {
                Toast.makeText(getActivity(), AtmosStrings.ToastMessages.BLE_STATE_CONNECTING, Toast.LENGTH_SHORT).show();
                mGattForConnect = mHardwareConnection.connect(BluetoothAdapter.getDefaultAdapter(), mAddress);
                ((MainActivity) getActivity()).putGatt(mAddress, mGattForConnect);
            } else {
                Toast.makeText(getActivity(), AtmosStrings.ToastMessages.BLE_STATE_ALREADY_CONNECTED, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), AtmosStrings.ToastMessages.BLE_DEVICE_NOT_ACCESSIBLE, Toast.LENGTH_SHORT).show();
        }
    }

    public void menuOnDisconnect(String address) {
        BluetoothGatt mGattForDisconnect = ((MainActivity) getActivity()).getGatt(address);
        if (mGattForDisconnect != null) {
            mGattForDisconnect.disconnect();
//            ((MainActivity) getActivity()).removeGatt(address);
        } else {
            Toast.makeText(getActivity(), AtmosStrings.ToastMessages.BLE_STATE_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
        }
    }

    public void menuOnDelete(String address, BluetoothDeviceInfo bDevice) {
        BluetoothGatt mGattForRemove = ((MainActivity) getActivity()).getGatt(address);
        if (mGattForRemove != null) {
            mGattForRemove.disconnect();
//            ((MainActivity) getActivity()).removeGatt(address);
        }
        this.mViewModel.remove(bDevice);
        this.listAdapter.removeItem(mCurrentClickPosition);
        Toast.makeText(getContext(), AtmosStrings.ToastMessages.BLE_DEVICE_REMOVED, Toast.LENGTH_SHORT).show();
    }
}
