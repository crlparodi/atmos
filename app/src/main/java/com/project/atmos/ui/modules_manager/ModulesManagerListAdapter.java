package com.project.atmos.ui.modules_manager;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.atmos.R;

import java.util.ArrayList;

public class ModulesManagerListAdapter extends RecyclerView.Adapter<ModulesManagerListAdapter.DeviceListViewHolder> {
    public static final String TAG = "DeviceListAdapter";

    ArrayList<BluetoothDevice> mDeviceslist = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public DeviceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_instance_view, parent, false);
        DeviceListViewHolder viewHolder = new DeviceListViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListViewHolder holder, int position) {
        BluetoothDevice device = mDeviceslist.get(position);

        if(device.getName() != null)
            holder.mName.setText(device.getName());
        else
            holder.mName.setText("-unnamed-");

        holder.mAddress.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return this.mDeviceslist.size();
    }

    public void setmDeviceslist(ArrayList<BluetoothDevice> mDeviceslist) {
        this.mDeviceslist = mDeviceslist;
        notifyDataSetChanged();
    }

    public class DeviceListViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mAddress;


        public DeviceListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mName = itemView.findViewById(R.id.atmos_bt_instance_name);
            this.mAddress = itemView.findViewById(R.id.atmos_bt_instance_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(mDeviceslist.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(BluetoothDevice device);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
