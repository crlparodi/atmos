package com.project.atmos.hardware;

public class KTY81210Sensor {
    public static final String NAME = "KTY81-210";

    public static final double cA = 1650.19891008174;
    public static final double cB = 14.9703542234332;

    public static final double dividerResistance = 2670.0;

    public static final int MIN_ADC = 266;
    public static final int MAX_ADC = 579;

    public static final int[][] sensorTable = new int[][]{
        {-30, 1247},
        {-20, 1367},
        {-10, 1467},
        {0, 1630},
        {10, 1772},
        {20, 1922},
        {25, 2000},
        {30, 2080},
        {40, 2245},
        {50, 2417},
        {60, 2597}
    };
}
