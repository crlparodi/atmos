package com.project.atmos.models;

import androidx.annotation.NonNull;

public class BluetoothCharacteristicData {
    private final String characteristicType;
    private final String characteristicSpec;
    private final int characteristicValue;

    public BluetoothCharacteristicData(@NonNull String characteristicType, String characteristicSpec, int characteristicValue) {
        this.characteristicType = characteristicType;
        this.characteristicSpec = characteristicSpec;
        this.characteristicValue = characteristicValue;
    }

    public String getCharacteristicType() {
        return characteristicType;
    }

    public String getCharacteristicSpec() {
        return characteristicSpec;
    }

    public int getCharacteristicValue() {
        return characteristicValue;
    }
}
