package com.project.atmos.ui.synthesis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
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
import com.project.atmos.libs.BLEHardwareManager;
import com.project.atmos.models.BLEModuleEntity;
import com.project.atmos.values.AtmosStrings;

import java.util.ArrayList;

public class SynthesisFragment extends Fragment implements SynthesisListAdapter.OnLongClickListener {
    public static final String TAG = SynthesisFragment.class.getSimpleName();

    private int mCurrentClickPosition = 0;
    private BLEHardwareConnection mHardwareConnection;

    private SynthesisViewModel mViewModel;

    private RecyclerView recyclerView;
    private SynthesisListAdapter listAdapter = new SynthesisListAdapter();

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            if (AtmosStrings.SYNTHESIS_FRAGMENT.equals(mAction)) {
                String mAddress = intent.getStringExtra(AtmosStrings.BLE_MODULE_ADDRESS);
                BLEModuleEntity mModule = listAdapter.getItemByAddress(mAddress);
                int position = listAdapter.getPositionByAddress(mAddress);
                if (intent.hasExtra(AtmosStrings.BLE_STATUS_CHANGED)) {
                    Boolean extra = (Boolean) intent.getBooleanExtra(AtmosStrings.BLE_STATUS_CHANGED, false);
                    if (extra) {
                        mModule.setStatus(1);
                        Toast.makeText(context, "Module connecté avec succès !", Toast.LENGTH_SHORT).show();
                    } else {
                        mModule.setStatus(0);
                        Toast.makeText(context, "Module déconnecté.", Toast.LENGTH_SHORT).show();
                    }
                }
                if(intent.hasExtra(AtmosStrings.BLE_TIMEOUT_REACHED)) {
                    Toast.makeText(context, "Echec de la connextion...\nDélai d'attente dépassé.", Toast.LENGTH_SHORT).show();
                    String extra = intent.getStringExtra(AtmosStrings.BLE_TIMEOUT_REACHED);
                    ((MainActivity) getActivity()).removeGatt(extra);
                    ((MainActivity) getActivity()).showDebug();
                }
                if(intent.hasExtra(AtmosStrings.BLE_CONNECTION_LOST)) {
                    Toast.makeText(context, "Perte de la connexion avec le module.", Toast.LENGTH_SHORT).show();
                    mModule.setStatus(0);
                    String extra = intent.getStringExtra(AtmosStrings.BLE_CONNECTION_LOST);
                    ((MainActivity) getActivity()).removeGatt(extra);
                    ((MainActivity) getActivity()).showDebug();
                }
                if(intent.hasExtra(AtmosStrings.BLE_DATA_UPDATED)) {
                    mModule.setLastTempEstimation(intent.getDoubleExtra(
                            AtmosStrings.BLE_DATA_UPDATED,
                            mModule.getLastTempEstimation()
                    ));
                }
                listAdapter.updateItem(position, mModule);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.mHardwareConnection = new BLEHardwareConnection(getActivity());

        this.mViewModel = ViewModelProviders.of(this).get(SynthesisViewModel.class);
        this.mViewModel.getBLEModuleList().observe(getViewLifecycleOwner(), BLEModuleObjectObserver);

        return inflater.inflate(R.layout.fragment_synthesis, container, false);
    }

    public final Observer<ArrayList<BLEModuleEntity>> BLEModuleObjectObserver = new Observer<ArrayList<BLEModuleEntity>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<BLEModuleEntity> modulesList) {
            listAdapter.setModulesList(modulesList);
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
        BLEModuleEntity mModuleEntity = this.listAdapter.getModulesList().get(this.mCurrentClickPosition);
        String mAddress = mModuleEntity.getAddress();
        BluetoothAdapter mAdapter = ((MainActivity) getActivity()).getmManager().getBtAdapter();
        switch (item.getItemId()) {
            case R.id.atmos_oc_menu_connect:
                if (mAdapter.isEnabled()) {
                    BluetoothDevice mDevice = mAdapter.getRemoteDevice(mAddress);
                    if (mDevice != null) {
                        BluetoothGatt mGattForConnect = null;
                        mGattForConnect = ((MainActivity) getActivity()).getGatt(mAddress);
                        ((MainActivity) getActivity()).showDebug();

                        Log.d(TAG, "onContextItemSelected: mGattForConnect: " + mGattForConnect);
                        if (mGattForConnect == null) {
                            Toast.makeText(getActivity(), "Tentative de connexion au module...", Toast.LENGTH_SHORT).show();
                            mGattForConnect = mHardwareConnection.connect(BluetoothAdapter.getDefaultAdapter(), mAddress);
                            ((MainActivity) getActivity()).putGatt(mAddress, mGattForConnect);
                        } else {
                            Toast.makeText(getActivity(), "Vous êtes déjà connecté à ce module...", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Ce module n'est pas accessible", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Veuillez activer le bluetooth avant de vous connecter au module.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.atmos_oc_menu_disconnect:
                BluetoothGatt mGattForDisconnect = ((MainActivity) getActivity()).getGatt(mAddress);
                if (mGattForDisconnect != null) {
                    mGattForDisconnect.disconnect();
                    ((MainActivity) getActivity()).removeGatt(mAddress);
                } else {
                    Toast.makeText(getActivity(), "Vous n'êtes pas connecté à ce module...", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.atmos_oc_menu_update:
                return true;
            case R.id.atmos_oc_menu_delete:
                BluetoothGatt mGattForRemove = ((MainActivity) getActivity()).getGatt(mAddress);
                if (mGattForRemove != null) {
                    mGattForRemove.disconnect();
                    ((MainActivity) getActivity()).removeGatt(mAddress);
                }
                this.mViewModel.delete(mModuleEntity.getAddress());
                this.listAdapter.removeItem(mCurrentClickPosition);
                Toast.makeText(getContext(), "Module supprimé", Toast.LENGTH_SHORT).show();
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
}
