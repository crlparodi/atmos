package com.project.atmos.ui.synthesis;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.R;
import com.project.atmos.models.BLEModuleEntity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class SynthesisListAdapter extends RecyclerView.Adapter<SynthesisListAdapter.BLEModuleListViewHolder>{
    public static final String TAG = SynthesisListAdapter.class.getSimpleName();

    ArrayList<BLEModuleEntity> modulesList = new ArrayList<>();

    private OnLongClickListener listener;

    @NonNull
    @Override
    public BLEModuleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.measurement_view, parent, false);
        BLEModuleListViewHolder viewHolder = new BLEModuleListViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BLEModuleListViewHolder holder, int position) {
        BLEModuleEntity currentModule = modulesList.get(position);

        if(currentModule.getName() != null)
            holder.mName.setText(currentModule.getName());
        else
            holder.mName.setText("-unnamed-");

        holder.mAddress.setText(currentModule.getAddress());

        String statusText = (currentModule.getStatus() == 1) ? "Active" : "Not active";
        holder.mStatus.setText(statusText);

        NumberFormat formater = new DecimalFormat("#0.0");
        String temperature = formater.format(currentModule.getLastTempEstimation()) + "°C";
        holder.mMeasurement.setText(temperature);
    }

    @Override
    public int getItemCount() {
        return this.modulesList.size();
    }

    public BLEModuleEntity getItem(int position) {
        return modulesList.get(position);
    }

    public void updateItem(int position, BLEModuleEntity mModule){
        modulesList.set(position, mModule);
        notifyItemChanged(position);
    }

    public void removeItem(int position){
        modulesList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, modulesList.size());
    }

    public ArrayList<BLEModuleEntity> getModulesList() {
        return modulesList;
    }

    public void setModulesList(ArrayList<BLEModuleEntity> modules) {
        this.modulesList = modules;
        notifyDataSetChanged();
    }

    public class BLEModuleListViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener {
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

            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            int position = getAdapterPosition();
            menu.setHeaderTitle(modulesList.get(position).getName());
            menu.add(0, R.id.atmos_oc_menu_connect, position, R.string.atmos_oc_connect);
            menu.add(0, R.id.atmos_oc_menu_disconnect, position, R.string.atmos_oc_disconnect);
            menu.add(0, R.id.atmos_oc_menu_update, position, R.string.atmos_oc_update);
            menu.add(0, R.id.atmos_oc_menu_delete, position, R.string.atmos_oc_delete);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            listener.onLongClick(v, position);
            return false;
        }
    }

    public interface OnLongClickListener{
        void onLongClick(View v, int position);
    }

    public void setOnLongClickListener(SynthesisListAdapter.OnLongClickListener listener) {
        this.listener = listener;
    }
}
