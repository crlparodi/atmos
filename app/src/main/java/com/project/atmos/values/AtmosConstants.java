package com.project.atmos.values;

import android.os.ParcelUuid;

public class AtmosConstants {
    public static final int BLUETOOTH_LE_SCAN_PERIOD = 10000;
    public static final int BLUETOOTH_LE_CONNECT_PERIOD = 3000;

    public static class UUID{
        public static final ParcelUuid MY_UUID_SECURE =
                ParcelUuid.fromString("64c3e1f3-c265-42a0-bef9-808f60b9573b");
        public static final ParcelUuid MY_UUID_INSECURE =
                ParcelUuid.fromString("8591f7e0-1a84-473a-9b21-acc9dded3ca4");
    }

    public static java.util.UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new java.util.UUID(MSB | (value << 32), LSB);
    }
}
