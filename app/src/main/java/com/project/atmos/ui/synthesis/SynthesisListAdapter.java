package com.project.atmos.ui.synthesis;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.MainActivity;
import com.project.atmos.R;
import com.project.atmos.models.BluetoothDeviceInfo;
import com.project.atmos.models.DeviceMeta;
import com.project.atmos.values.AtmosAppCycle;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class SynthesisListAdapter extends RecyclerView.Adapter<SynthesisListAdapter.BLEModuleListViewHolder>{
    public static final String TAG = SynthesisListAdapter.class.getSimpleName();

    public static final AtmosAppCycle APP_CYCLE_STATUS = MainActivity.config.isCycleStatus();

    ArrayList<BluetoothDeviceInfo> mList = new ArrayList<>();

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
        BluetoothDeviceInfo mDevice = mList.get(position);

        if(mDevice.getDevice().getName() != null)
            holder.mName.setText(mDevice.getDevice().getName());
        else
            holder.mName.setText("-unnamed-");

        holder.mAddress.setText(mDevice.getDevice().getAddress());

        String statusText = mDevice.isConnected() ? "Connected" : "Not connected";
        holder.mStatus.setText(statusText);

        NumberFormat formater = new DecimalFormat("#0.0");
        String temperature = formater.format(mDevice.getData()) + "°C";
        holder.mMeasurement.setText(temperature);
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public BluetoothDeviceInfo getItem(int position) {
        return mList.get(position);
    }

    public int getPositionByAddress(String mAddress){
        int position = 0;

        for (BluetoothDeviceInfo mDevice : mList){
            if(mAddress.equals(mDevice.getDevice().getAddress())){
                position = mList.indexOf(mDevice);
            }
        }
        return position;
    }

    public BluetoothDeviceInfo getItemByAddress(String mAddress){
        BluetoothDeviceInfo mModule = null;
        for(BluetoothDeviceInfo tModule : mList){
            if(mAddress.equals(tModule.getDevice().getAddress())){
                mModule = tModule;
                break;
            }
        }
        return mModule;
    }

    public void updateItem(int position, BluetoothDeviceInfo mModule){
        mList.set(position, mModule);
        notifyItemChanged(position);
    }

    public void removeItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }

    public ArrayList<BluetoothDeviceInfo> getModulesList() {
        return mList;
    }

    public void setModulesList(ArrayList<BluetoothDeviceInfo> modules) {
        this.mList = modules;
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
            menu.setHeaderTitle(mList.get(position).getDevice().getName());
            if(APP_CYCLE_STATUS == AtmosAppCycle.PRODUCTION){
                menu.add(0, R.id.atmos_oc_menu_connect, position, R.string.atmos_oc_connect);
                menu.add(0, R.id.atmos_oc_menu_disconnect, position, R.string.atmos_oc_disconnect);
            }
            menu.add(0, R.id.atmos_oc_menu_update, position, R.string.atmos_oc_update);
            menu.add(0, R.id.atmos_oc_menu_delete, position, R.string.atmos_oc_delete);
            menu.add(0, R.id.atmos_oc_menu_details, position, R.string.atmos_oc_details);
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
