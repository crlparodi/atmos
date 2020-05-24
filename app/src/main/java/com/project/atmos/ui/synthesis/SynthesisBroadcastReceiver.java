package com.project.atmos.ui.synthesis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.project.atmos.MainActivity;
import com.project.atmos.models.BluetoothDeviceInfo;
import com.project.atmos.values.AtmosStrings;

public class SynthesisBroadcastReceiver extends BroadcastReceiver {

    private MainActivity mActivity;
    private SynthesisListAdapter mListAdapter;

    public SynthesisBroadcastReceiver(MainActivity aActivity, SynthesisListAdapter aListAdapter) {
        mActivity = aActivity;
        mListAdapter = aListAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String mAction = intent.getAction();
        if (AtmosStrings.SYNTHESIS_FRAGMENT.equals(mAction)) {
            String mAddress = intent.getStringExtra(AtmosStrings.BLE_DEVICE_ADDRESS);
            BluetoothDeviceInfo mModule = mListAdapter.getItemByAddress(mAddress);
            if (mModule != null) {
                int position = mListAdapter.getPositionByAddress(mAddress);
                if (intent.hasExtra(AtmosStrings.BLE_STATE_CHANGED)) {
                    boolean extra = intent.getBooleanExtra(AtmosStrings.BLE_STATE_CHANGED, false);
                    mModule.setConnected(extra);
                    if (extra) {
                        Toast.makeText(context, AtmosStrings.ToastMessages.BLE_STATE_CONNECTED, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, AtmosStrings.ToastMessages.BLE_STATE_DISCONNECTED, Toast.LENGTH_SHORT).show();
                        if(mActivity.getGatt(mAddress) != null){
                            mActivity.removeGatt(mAddress);
                        }
                    }
                }
                if (intent.hasExtra(AtmosStrings.BLE_TIMEOUT_REACHED)) {
                    Toast.makeText(context, AtmosStrings.ToastMessages.BLE_CONNECTION_TIMED_OUT, Toast.LENGTH_SHORT).show();
                    mActivity.removeGatt(mAddress);
                }
                if (intent.hasExtra(AtmosStrings.BLE_CONNECTION_LOST)) {
                    Toast.makeText(context, AtmosStrings.ToastMessages.BLE_CONNECTION_LOST, Toast.LENGTH_SHORT).show();
                    mModule.setConnected(false);
                    mActivity.removeGatt(mAddress);
                }
                if (intent.hasExtra(AtmosStrings.BLE_DATA_UPDATED)) {
                    mModule.setData(intent.getDoubleExtra(AtmosStrings.BLE_DEVICE_DATA, 0.0));
                }
                mListAdapter.updateItem(position, mModule);
            }
        }
    }
}
