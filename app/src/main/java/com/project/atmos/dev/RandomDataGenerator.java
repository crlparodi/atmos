package com.project.atmos.dev;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.project.atmos.Atmos;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;
import java.util.Random;

public class RandomDataGenerator implements RandomDataGeneratorManager {

    // The custom random bluetooth address generator for the needs of the app dev.
    private RandomAddressGenerator RAGGLE_GEN = new RandomAddressGenerator();

    // The custom random bluetooth last connection date generator for the needs of the app dev.
    private RandomDateGenerator RADDLE_GEN = new RandomDateGenerator();

    // The generated and potentially updated data will be based only on bluetooth module
    // attributes.
    private String R_NAME;
    private int R_STATUS;
    private Double R_TEMP;

    // The elements for getting the date of the last pairing with a BLE module device
    public GregorianCalendar R_DATE;
    private String R_LAST_CONNECT;

    // The random engine with some type configurations
    private Random randomEngine;
    private int NUMBER_OF_WORDS_IN_DICTIONNARY = 0;

    private AssetManager assetMgr;
    private String filePath = "ble_names.txt";
    private InputStream fileBuffer;

    public RandomDataGenerator(){
        this.R_NAME = "";
        this.R_STATUS = 0;
        this.R_TEMP = 0.0;
//        this.R_LAST_CONNECT = new Date().getTime();
//        this.R_LAST_CONNECT.toString();

        this.randomEngine = new Random();

        this.assetMgr = Atmos.getAppContext().getAssets();

        // Dictionnary words file reading attempt
        try {
            this.fileBuffer = new BufferedInputStream(this.assetMgr.open(filePath, AssetManager.ACCESS_BUFFER));
            fileBuffer.mark(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    // RandomData
    public String getRandomName() {
        if (this.NUMBER_OF_WORDS_IN_DICTIONNARY > 0) this.NUMBER_OF_WORDS_IN_DICTIONNARY = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(fileBuffer));

        try {
            fileBuffer.reset();
            while(br.readLine() != null){
                this.NUMBER_OF_WORDS_IN_DICTIONNARY++;
            }

            int selector = this.randomEngine.nextInt(this.NUMBER_OF_WORDS_IN_DICTIONNARY);
            fileBuffer.reset();
            br = new BufferedReader(new InputStreamReader(fileBuffer));

            for(int i = 0; i != this.NUMBER_OF_WORDS_IN_DICTIONNARY; i++){
                if (i == selector){
                    this.R_NAME = br.readLine();
                    break;
                }
                else {
                    br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.R_NAME;
    }

    @Override
    // Random Data
    public String getRandomAddress() {
        return RAGGLE_GEN.generateAddress();
    }

    @Override
    // Random Data
    public Integer getRandomStatus() {
        this.R_STATUS = randomEngine.nextInt(2);
        return R_STATUS;
    }

    public String getRandomDate() {
        R_DATE = this.RADDLE_GEN.generateDate();
        R_LAST_CONNECT = R_DATE.toString();
        return this.R_LAST_CONNECT;
    }

    @Override
    // Random Data
    public Double getRandomTemperature() {
        this.R_TEMP = 15 + 15 * this.randomEngine.nextDouble();
        return this.R_TEMP;
    }
}
