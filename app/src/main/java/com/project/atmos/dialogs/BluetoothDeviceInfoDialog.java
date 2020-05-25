package com.project.atmos.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.project.atmos.R;
import com.project.atmos.models.BluetoothDeviceInfo;

import java.util.Date;

public class BluetoothDeviceInfoDialog extends AppCompatDialogFragment {
    private BluetoothDeviceInfo bDevice;

    public BluetoothDeviceInfoDialog(BluetoothDeviceInfo aDevice) {
        this.bDevice = aDevice;
    }

    public BluetoothDeviceInfoDialog() {
        this.bDevice = null;
    }

    public void setDevice(BluetoothDeviceInfo bDevice) {
        this.bDevice = bDevice;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder adaptableBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater mInflater = getActivity().getLayoutInflater();
        View mView = mInflater.inflate(R.layout.synthesis_oc_info, null);
        TextView bName = mView.findViewById(R.id.atmos_oc_info_name);
        bName.setText(bDevice.getDevice().getName());
        TextView bLastConnect = mView.findViewById(R.id.atmos_oc_info_last_connect);

        Date lastConnection = bDevice.getDevice().getLastConnection();
        if (lastConnection != null) {
            bLastConnect.setText(lastConnection.toString());
        } else {
            bLastConnect.setText("Jamais");
        }
        adaptableBuilder.setView(mView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return adaptableBuilder.create();
    }
}
