package com.project.atmos.ui.synthesis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.R;
import com.project.atmos.models.BLEModuleObject;

import java.util.ArrayList;

public class SynthesisFragment extends Fragment {

    private MeasurementViewModel measurementViewModel;

    private RecyclerView recyclerView;
    private BLEModuleListAdapter listAdapter = new BLEModuleListAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        this.measurementViewModel = ViewModelProviders.of(this).get(MeasurementViewModel.class);
        this.measurementViewModel.getBLEModuleList().observe(getViewLifecycleOwner(), BLEModuleObjectObserver);

        return inflater.inflate(R.layout.fragment_synthesis, container, false);
    }

    public final Observer<ArrayList<BLEModuleObject>> BLEModuleObjectObserver = new Observer<ArrayList<BLEModuleObject>>() {
        @Override
        public void onChanged(@Nullable final ArrayList<BLEModuleObject> modulesList) {
            listAdapter.setModulesList(modulesList);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.atmos_synthesis_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(listAdapter);

        this.measurementViewModel.updateBLEModulesList();
    }
}
