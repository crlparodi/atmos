package com.project.atmos.values;

public class AtmosStrings {
    /* Activities */
    public static final String MAIN_ACTIVITY = "com.project.atmos.MainActivity";

    /* Fragments */
    public static final String SYNTHESIS_FRAGMENT = "com.project.atmos.ui.synthesis.SynthesisFragment";

    /* BLE-States-Extras */
    public static final String BLE_STATE_CHANGED = "BLE_STATE_CHANGED";
    public static final String BLE_TIMEOUT_REACHED = "BLE_TIMEOUT_REACHED";
    public static final String BLE_CONNECTION_LOST = "BLE_CONNECTION_LOST";
    public static final String BLE_DATA_UPDATED = "BLE_DATA_UPDATED";

    /* BLE-Datas-Extras */
    public static final String BLE_DEVICE_ADDRESS = "BLE_DEVICE_ADDRESS";
    public static final String BLE_DEVICE_DATA = "BLE_DEVICE_DATA";

    /* BLE-Data-Type */
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String STATUS = "status";
    public static final String MEASUREMENT = "measurement";
    public static final String UNIT = "unit";
    public static final String LAST_CONNECTION_DATE = "last_connection_date";

    /* Toasts */
    public static class ToastMessages{
        // Français
        public static final String APP_CONFIG_NOT_FOUND = "Echec de la récupération de app.config.";

        public static final String BLUETOOTH_NEEDED = "Veuillez activer le bluetooth.";
        public static final String BLUETOOTH_ABORTED = "Le bluetooth a été désactivé.\nArrêt de la recherche.";

        public static final String BLE_STATE_CONNECTED = "Module connecté avec succès !";
        public static final String BLE_STATE_ALREADY_CONNECTED = "Vous êtes déjà connecté à ce module...";
        public static final String BLE_STATE_DISCONNECTED = "Module déconnecté.";
        public static final String BLE_STATE_NOT_CONNECTED = "Vous n'êtes pas connecté à ce module...";
        public static final String BLE_STATE_CONNECTING = "Tentative de connexion au module...";

        public static final String BLE_CONNECTION_TIMED_OUT = "Echec de la connextion...\nDélai d'attente dépassé.";
        public static final String BLE_CONNECTION_LOST = "Perte de la connexion avec le module.";

        public static final String BLE_DEVICE_ADDED = "Module inséré dans la base de données.";
        public static final String BLE_DEVICE_ALREADY_ADDED = "Ce module est déjà ajouté.";
        public static final String BLE_DEVICE_REMOVED = "Module supprimé.";
        public static final String BLE_DEVICE_UNKNOWN = "Ce module n'existe pas ...";
        public static final String BLE_DEVICE_NOT_ACCESSIBLE = "Ce module n'est pas accessible.";
    }

    /* Debug */
    public static class DebugMessages{
        //Français
        public static final String SERVICE_DISCOVERY_FAILED = "Echec de la découverte des services...";
        public static final String SENSOR_DISCOVERY_FAILED = "Echec de la reconnaissance du capteur, est-il compatible ?";
    }
}
