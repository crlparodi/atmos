package com.project.atmos.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.project.atmos.models.BLEModuleEntity;

import java.util.List;

public interface BLEModulesRepositoryManager {
    // But: Permettre les interactions avec la Base de Données sous
    // certaines conditions:
    //      - L'appli dispose des autorisations en écriture
    //      - La base de données est bel est bien présente
    //      - # autre condition à rajouter si nécessaire #

    boolean checkPermissionsOrRequirements(Context context);

    void generateToastForFailedPermission(Context context, String failedPermission);

    void insertModuleIntoDB(Context context, BLEModuleEntity module);

    BLEModuleEntity getModuleByName(Context context, String name);

    BLEModuleEntity getModuleByAddress(Context context, @NonNull String address);

    List<BLEModuleEntity> getModulesList(Context context);

    void eraseDB(Context context);
}
