package com.project.atmos.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.project.atmos.R;
import com.project.atmos.models.BluetoothDeviceInfo;

import java.util.Date;

public class BluetoothDeviceInfoDialog extends DialogFragment {
    private Context mContext;
    private BluetoothDeviceInfo bDevice;
    private View mView;
    private DialogInterface.OnClickListener mListener;

    public BluetoothDeviceInfoDialog(
            Context aContext,
            BluetoothDeviceInfo aDevice,
            View aView,
            DialogInterface.OnClickListener aListener
    ){
        this.mContext = aContext;
        this.bDevice = aDevice;
        this.mView = aView;
        this.mListener = aListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder adaptableBuilder = new AlertDialog.Builder(this.mContext);
        TextView bName = this.mView.findViewById(R.id.atmos_oc_info_name);
        bName.setText(bDevice.getDevice().getName());
        TextView bLastConnect = this.mView.findViewById(R.id.atmos_oc_info_last_connect);
        bLastConnect.setText(bDevice.getDevice().getLastConnection().toString());
        adaptableBuilder.setView(this.mView)
                .setPositiveButton("OK", this.mListener);
        return adaptableBuilder.create();
    }
}
