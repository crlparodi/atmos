package com.project.atmos.core;

import android.bluetooth.BluetoothGattCharacteristic;

import com.project.atmos.hardware.DS18B20Sensor;
import com.project.atmos.hardware.KTY81210Sensor;
import com.project.atmos.models.BluetoothCharacteristicData;
import com.project.atmos.values.AtmosConstants;
import com.project.atmos.values.AtmosStrings;

public class BluetoothDataComputer {
    public static final String TAG = BluetoothDataComputer.class.getSimpleName();

    public BluetoothDataComputer() {
    }

    public BluetoothCharacteristicData computeCharacteristic(BluetoothGattCharacteristic characteristic) {

        byte[] value = characteristic.getValue();
        String final_type = AtmosStrings.MEASUREMENT;
        int hex_data = (value[1] & 0xFF) << 8 | (value[2] & 0xFF);
        byte hex_spec = value[0];

        switch (hex_spec) {
            case AtmosConstants.BLUETOOTH_KTY_SENSOR:
                return new BluetoothCharacteristicData(final_type, KTY81210Sensor.NAME, hex_data);
            case AtmosConstants.BLUETOOTH_DS18_SENSOR:
                return new BluetoothCharacteristicData(final_type, DS18B20Sensor.NAME, hex_data);
            default:
                return null;
        }
    }

    public double computeData(BluetoothCharacteristicData characteristicData) {
        double temperature = 0.0;

        if (characteristicData.getCharacteristicType() == AtmosStrings.MEASUREMENT) {
            if (characteristicData.getCharacteristicSpec() == KTY81210Sensor.NAME) {
                temperature = KTY81210Compute(characteristicData.getCharacteristicValue());
            } else if (characteristicData.getCharacteristicSpec() == DS18B20Sensor.NAME) {
                temperature = DS18B20Compute(characteristicData.getCharacteristicValue());
            }
        }

        return temperature;
    }

    public double KTY81210Compute(int data) {
        double[] codToTempTable = new double[256];
        int codBeginPol = 0; // segment beginning pole code
        int codEndPol;       // segment end pole code
        float koeff;         // coefficient for the interval between the poles
        double temperature;

        for (int p = 0; p < KTY81210Sensor.sensorTable.length - 1; p++) {
            // calculate the code for the next pole N= Rx * 1023 / (Re + Rx)
            codEndPol = (int) ((float) (KTY81210Sensor.sensorTable[p + 1][1] * 1023) / (float) (KTY81210Sensor.dividerResistance + KTY81210Sensor.sensorTable[p + 1][1]) + 0.5) - KTY81210Sensor.MIN_ADC;
            codToTempTable[codEndPol] = KTY81210Sensor.sensorTable[p + 1][0]; // temperature for the next pole

            // calculate the coefficient for the interval
            koeff = (float) (KTY81210Sensor.sensorTable[p + 1][0] - KTY81210Sensor.sensorTable[p][0]) / (float) (codEndPol - codBeginPol);

            // temperature interpolation
            for (int n = codBeginPol; n < codEndPol; n++) {
                codToTempTable[n] = KTY81210Sensor.sensorTable[p][0] + ((float) (n - codBeginPol) * koeff + 0.5);
            }
            codBeginPol = codEndPol;
        }

        // read the final value from the array
        temperature = codToTempTable[data - KTY81210Sensor.MIN_ADC];
        return temperature;
    }

    public double DS18B20Compute(int data) {
        return (double) data / 16;
    }
}
