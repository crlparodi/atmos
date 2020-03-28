package com.project.atmos.ui.synthesis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.R;
import com.project.atmos.models.BLEModuleObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class BLEModuleListAdapter extends RecyclerView.Adapter<BLEModuleListAdapter.BLEModuleListViewHolder> {

    ArrayList<BLEModuleObject> modulesList = new ArrayList<>();

    @NonNull
    @Override
    public BLEModuleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.measurement_view, parent, false);
        BLEModuleListViewHolder viewHolder = new BLEModuleListViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BLEModuleListViewHolder holder, int position) {
        BLEModuleObject currentModule = modulesList.get(position);
        holder.mName.setText(currentModule.getName());
        holder.mAddress.setText(currentModule.getAddress());

        String statusText = (currentModule.getStatus() == 1) ? "Active" : "Not active";
        holder.mStatus.setText(statusText);

        NumberFormat formater = new DecimalFormat("#0.0");
        String temperature = formater.format(currentModule.getValue()) + "°C";
        holder.mMeasurement.setText(temperature);
    }

    @Override
    public int getItemCount() {
        return this.modulesList.size();
    }

    public void setModulesList(ArrayList<BLEModuleObject> modules){
        this.modulesList = modules;
        notifyDataSetChanged();
    }

    public class BLEModuleListViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mAddress;
        private TextView mStatus;
        private TextView mMeasurement;

        public BLEModuleListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mName = itemView.findViewById(R.id.atmos_device_name);
            this.mAddress = itemView.findViewById(R.id.atmos_device_address);
            this.mStatus = itemView.findViewById(R.id.atmos_device_status);
            this.mMeasurement = itemView.findViewById(R.id.atmos_device_measurement);
        }
    }
}
