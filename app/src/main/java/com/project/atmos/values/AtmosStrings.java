package com.project.atmos.values;

public class AtmosStrings {
    /* Activities */
    public static final String MAIN_ACTIVITY = "com.project.atmos.MainActivity";

    /* Fragments */
    public static final String SYNTHESIS_FRAGMENT = "com.project.atmos.ui.synthesis.SynthesisFragment";

    /* States-Extras */
    public static final String BLE_STATE_CHANGED = "BLE_STATE_CHANGED";
    public static final String BLE_STATUS_CHANGED = "BLE_STATUS_CHANGED";

    /* Other-Extras */
    public static final String BLE_TIMEOUT_REACHED = "BLE_TIMEOUT_REACHED";
    public static final String BLE_CONNECTION_LOST = "BLE_CONNECTION_LOST";

    /* Data-Type */
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String STATUS = "status";
    public static final String MEASUREMENT = "measurement";
    public static final String UNIT = "unit";
    public static final String LAST_CONNECTION_DATE = "last_connection_date";

    /* Toasts */
    public static class ToastMessages{
        /* Messages */
        // Français
        public static final String BLE_STATE_NOT_ACTIVE = "Veuillez activer le bluetooth avant de procéder à une recherche...";
    }
}
