package com.project.atmos.values;

public enum Tags{
    MAIN,
    BLUETOOTHLE,
    API_PERMISSION_INFO;

    public static String getTag(Tags tag){
        return tag.toString();
    }
}
