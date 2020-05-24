package com.project.atmos.dev;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;

public class RandomDataGenerator {
    public static final String TAG = RandomDataGenerator.class.getSimpleName();

    private int NB_OF_ADDR_INTEGERS = 6;

    // The random engine with some type configurations
    private Random randomEngine;
    private int NUMBER_OF_WORDS_IN_DICTIONNARY = 0;

    private AssetManager assetMgr;
    private String filePath = "ble_names.txt";
    private InputStream fileBuffer;

    public RandomDataGenerator(Context context) {
        this.randomEngine = new Random();

        this.assetMgr = context.getAssets();

        try {
            this.fileBuffer = new BufferedInputStream(this.assetMgr.open(filePath, AssetManager.ACCESS_BUFFER));
            fileBuffer.mark(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRandomName() {
        String R_NAME = "";
        if (this.NUMBER_OF_WORDS_IN_DICTIONNARY > 0) this.NUMBER_OF_WORDS_IN_DICTIONNARY = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(fileBuffer));

        try {
            fileBuffer.reset();
            while (br.readLine() != null) {
                this.NUMBER_OF_WORDS_IN_DICTIONNARY++;
            }

            int selector = this.randomEngine.nextInt(this.NUMBER_OF_WORDS_IN_DICTIONNARY);
            fileBuffer.reset();
            br = new BufferedReader(new InputStreamReader(fileBuffer));

            for (int i = 0; i != this.NUMBER_OF_WORDS_IN_DICTIONNARY; i++) {
                if (i == selector) {
                    R_NAME = br.readLine();
                    break;
                } else {
                    br.readLine();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "getRandomName: Failed to get a random name", e);
            return null;
        }

        return R_NAME;
    }

    public String getRandomAddress() {
        String addressCompositor = "";
        ArrayList<Integer> addressIntegerSet = new ArrayList<Integer>();
        for (int i = 0; i < this.NB_OF_ADDR_INTEGERS; i++) {
            addressIntegerSet.add(this.randomEngine.nextInt(255));
            addressCompositor += Integer.toHexString(addressIntegerSet.get(i));
            if (i != this.NB_OF_ADDR_INTEGERS - 1) {
                addressCompositor += ":";
            }
        }
        return addressCompositor.toUpperCase();
    }

    public int getRandomStatus() {
        return randomEngine.nextInt(2);
    }

    public GregorianCalendar getRandomDate() {
        int R_YEAR = randomEngine.nextInt(40) + 1980;
        int R_MONTH = randomEngine.nextInt(11) + 1;
        int R_DATE;

        if (R_MONTH == 2) {
            if (R_YEAR % 4 == 0) {
                R_DATE = randomEngine.nextInt(29) + 1;
            } else {
                if ((R_YEAR % 100 == 0) && (R_YEAR % 400 != 0)) {
                    R_DATE = randomEngine.nextInt(29) + 1;
                } else {
                    R_DATE = randomEngine.nextInt(28) + 1;
                }
            }
        } else if (R_MONTH % 2 != 0) {
            R_DATE = randomEngine.nextInt(31) + 1;
        } else {
            R_DATE = randomEngine.nextInt(30) + 1;
        }

        int R_HOURS = randomEngine.nextInt(23);
        int R_MINUTES = randomEngine.nextInt(59);
        int R_SECONDS = randomEngine.nextInt(59);

        return new GregorianCalendar(R_YEAR, R_MONTH, R_DATE, R_HOURS, R_MINUTES, R_SECONDS);
    }

    public double getRandomData() {
        return (15 + 15 * this.randomEngine.nextDouble());
    }
}
