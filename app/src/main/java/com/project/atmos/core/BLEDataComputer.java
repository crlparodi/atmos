package com.project.atmos.core;

import android.bluetooth.BluetoothGattCharacteristic;

import com.project.atmos.hardware.Arduino;
import com.project.atmos.hardware.DS18B20Sensor;
import com.project.atmos.hardware.KTY81210Sensor;
import com.project.atmos.values.AtmosConstants;
import com.project.atmos.values.AtmosStrings;
import com.project.atmos.models.BLECharacteristicData;

public class BLEDataComputer {
    public static final String TAG = BLEDataComputer.class.getSimpleName();

    public BLEDataComputer() {
    }

    public BLECharacteristicData computeCharacteristic(BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        String final_type = AtmosStrings.MEASUREMENT;
        int hex_data = value[1];
        byte hex_spec = value[0];

        switch(hex_spec) {
            case AtmosConstants.BLUETOOTH_KTY_SENSOR:
                return new BLECharacteristicData(final_type, KTY81210Sensor.NAME, hex_data);
            case AtmosConstants.BLUETOOTH_DS18_SENSOR:
                return new BLECharacteristicData(final_type, DS18B20Sensor.NAME, hex_data);
            default:
                return null;
        }
    }

    public double computeData(BLECharacteristicData characteristicData) {
        double temperature = 0.0;
        double thermistance = 0.0;

        if (characteristicData.getCharacteristicType() == AtmosStrings.MEASUREMENT) {
            if (characteristicData.getCharacteristicSpec() == KTY81210Sensor.NAME) {
                /* The circuit is a simple voltage divider
                So the operation is the following: Rth = Rin(Vin/Vout - 1)
                The voltages are the picture of their conversions, so:
                    Rth = Rin(MaxSamples/ConversionSamples - 1)

                And finally, the temperature is the result of the equation related to the sensor characteristic
                trend line: Rth = cB*T + cA
                So: T = (Rth - cA) / cB
                 */

                thermistance = KTY81210Sensor.dividerResistance / (((Arduino.serialSamples - 1) / characteristicData.getCharacteristicValue()) - 1);
                temperature =  (thermistance - KTY81210Sensor.cA) / KTY81210Sensor.cB;
            } else if (characteristicData.getCharacteristicSpec() == DS18B20Sensor.NAME) {
                /* Nothing for now... */
            }
        }

        return temperature;
    }
}
