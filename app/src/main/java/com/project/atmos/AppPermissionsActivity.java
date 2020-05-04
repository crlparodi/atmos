package com.project.atmos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.atmos.libs.BLEHardwareManager;
import com.project.atmos.libs.CustomAdaptableDialog;

public class AppPermissionsActivity extends AppCompatActivity {
    public static final String TAG = AppPermissionsActivity.class.getSimpleName();

    public static final int REQUESTS_APPLICATION_GLOBAL = 0;

    Integer onCheckPermissionsResults = 0;
    Integer onRequestPermissionsResults = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!BLEHardwareManager.checkBTSupport(this)){
            finish();
        }

        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        for (String permission :
                permissions) {
            onCheckPermissionsResults += this.checkSelfPermission(permission);
        }

        if (onCheckPermissionsResults != 0){
            setContentView(R.layout.activity_app_permissions);

            Button appConfigButton = (Button) findViewById(R.id.atmos_app_config_button);
            appConfigButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissions(permissions, REQUESTS_APPLICATION_GLOBAL);
                }
            });
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case REQUESTS_APPLICATION_GLOBAL: {
                for (int grantResult :
                        grantResults) {
                    if(grantResult != PackageManager.PERMISSION_GRANTED) onRequestPermissionsResults ++;
                }

                if(onRequestPermissionsResults != 0){
                    permissionDialogError();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            }
        }
    }

    public void permissionDialogError(){
        CustomAdaptableDialog dialog = new CustomAdaptableDialog(
                this,
                "Vous avez refusé une ou plusieurs autorisation(s) essentielle(s), l'application ne peut pas s'exécuter correctement. \nFermeture de l'application.",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
        );
        dialog.show(getSupportFragmentManager(), TAG);
    }
}