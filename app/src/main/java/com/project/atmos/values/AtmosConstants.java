package com.project.atmos.values;

import android.os.ParcelUuid;

public class AtmosConstants {
    public static final int BLUETOOTH_LE_SCAN_PERIOD = 10000;

    public static class UUID{
        public static final ParcelUuid MY_UUID_SECURE =
                ParcelUuid.fromString("64c3e1f3-c265-42a0-bef9-808f60b9573b");
        public static final ParcelUuid MY_UUID_INSECURE =
                ParcelUuid.fromString("8591f7e0-1a84-473a-9b21-acc9dded3ca4");
    }
}
