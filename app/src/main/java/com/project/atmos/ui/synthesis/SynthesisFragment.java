package com.project.atmos.ui.synthesis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
            if(AtmosStrings.SYNTHESIS_FRAGMENT.equals(mAction)){
                if(intent.hasExtra(AtmosStrings.BLE_STATUS_CHANGED)){
                    BLEModuleEntity mModule = listAdapter.getItem(mCurrentClickPosition);
                    Boolean extra = (Boolean) intent.getBooleanExtra(AtmosStrings.BLE_STATUS_CHANGED, false);
                    if(extra){
                        mModule.setStatus(1);
                    } else {
                        mModule.setStatus(0);
                    }
                    listAdapter.updateItem(mCurrentClickPosition, mModule);
                }
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.mHardwareConnection = new BLEHardwareConnection(getContext());

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

        this.listAdapter.setOnLongClickListener(new SynthesisListAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                mCurrentClickPosition = position;
            }
        });
    }

    @Override
    public void onLongClick(View v, int position) {
        mCurrentClickPosition = position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BLEModuleEntity mModuleEntity = this.listAdapter.getModulesList().get(this.mCurrentClickPosition);
        String mAddress = mModuleEntity.getAddress();
        switch (item.getItemId()) {
            case R.id.atmos_oc_menu_connect:
                BluetoothGatt mGattForConnect = ((MainActivity)getActivity()).getGatt(mAddress);
                if(mGattForConnect == null){
                    mGattForConnect = mHardwareConnection.connect(BluetoothAdapter.getDefaultAdapter(), mAddress);
                    ((MainActivity)getActivity()).putGatt(mAddress, mGattForConnect);
                    Toast.makeText(getActivity(), "Module connecté.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Vous êtes déjà connecté à ce module...", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.atmos_oc_menu_disconnect:
                BluetoothGatt mGattForDisconnect = ((MainActivity)getActivity()).getGatt(mAddress);
                if(mGattForDisconnect != null){
                    mGattForDisconnect.disconnect();
                    ((MainActivity)getActivity()).removeGatt(mAddress, mGattForDisconnect);
                    Toast.makeText(getActivity(), "Module déconnecté.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Vous n'êtes pas connecté à ce module...", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.atmos_oc_menu_update:
                return true;
            case R.id.atmos_oc_menu_delete:
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
}
