package com.project.atmos.libs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomAdaptableDialog extends DialogFragment {

    private Context context;
    private String adaptableString;
    private DialogInterface.OnClickListener listener;

    public CustomAdaptableDialog(Context context, String string, DialogInterface.OnClickListener listener){
        this.context = context;
        this.adaptableString = string;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder adaptableBuilder = new AlertDialog.Builder(this.context);
        adaptableBuilder.setMessage(adaptableString)
                .setPositiveButton("OK", listener);
        return adaptableBuilder.create();
    }
}
