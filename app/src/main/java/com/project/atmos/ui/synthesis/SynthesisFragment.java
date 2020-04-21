package com.project.atmos.ui.synthesis;

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

import com.project.atmos.R;
import com.project.atmos.models.BLEModuleEntity;

import java.util.ArrayList;

public class SynthesisFragment extends Fragment implements BLEModuleListAdapter.OnLongClickListener {
    public static final String TAG = SynthesisFragment.class.getSimpleName();

    private int mCurrentClickPosition = 0;

    private MeasurementViewModel measurementViewModel;

    private RecyclerView recyclerView;
    private BLEModuleListAdapter listAdapter = new BLEModuleListAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.measurementViewModel = ViewModelProviders.of(this).get(MeasurementViewModel.class);
        this.measurementViewModel.getBLEModuleList().observe(getViewLifecycleOwner(), BLEModuleObjectObserver);

        return inflater.inflate(R.layout.fragment_synthesis, container, false);
    }

    public final Observer<ArrayList<BLEModuleEntity>> BLEModuleObjectObserver = new Observer<ArrayList<BLEModuleEntity>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<BLEModuleEntity> modulesList) {
//            android.os.Debug.waitForDebugger();
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

        this.listAdapter.setOnLongClickListener(new BLEModuleListAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                mCurrentClickPosition = position;
            }
        });

//        this.measurementViewModel.updateBLEModulesList();
    }

    @Override
    public void onLongClick(View v, int position) {
        mCurrentClickPosition = position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        BLEModuleEntity mModuleEntity = this.listAdapter.getModulesList().get(this.mCurrentClickPosition);
        switch (item.getItemId()) {
            case R.id.atmos_oc_menu_connect:
                return true;
            case R.id.atmos_oc_menu_update:
                return true;
            case R.id.atmos_oc_menu_delete:
                this.measurementViewModel.delete(mModuleEntity.getAddress());
                this.listAdapter.removeItem(mCurrentClickPosition);
                Toast.makeText(getContext(), "Module supprimé", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
