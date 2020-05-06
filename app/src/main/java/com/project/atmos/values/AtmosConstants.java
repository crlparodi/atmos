package com.project.atmos.values;

public class AtmosConstants {
    /* Periods */
    public static final int BLUETOOTH_LE_SCAN_PERIOD = 10000;
    public static final int BLUETOOTH_LE_CONNECT_PERIOD = 3000;

    /* Bluetooth Codes */
    public static final int BLUETOOTH_KTY_SENSOR = 0x41;
    public static final int BLUETOOTH_DS18_SENSOR = 0x42;

    /* UUID converter */
    public static java.util.UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new java.util.UUID(MSB | (value << 32), LSB);
    }
}
