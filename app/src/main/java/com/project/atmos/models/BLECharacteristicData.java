package com.project.atmos.models;

import androidx.annotation.NonNull;

public class BLECharacteristicData {
    private final String characteristicType;
    private final String characteristicSpec;
    private final double characteristicValue;

    public BLECharacteristicData(@NonNull String characteristicType, String characteristicSpec, double characteristicValue) {
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

    public double getCharacteristicValue() {
        return characteristicValue;
    }
}
