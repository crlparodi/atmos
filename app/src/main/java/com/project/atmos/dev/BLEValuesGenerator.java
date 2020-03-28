package com.project.atmos.dev;

public class BLEValuesGenerator{

    private RandomDataGenerator dataGenerator;

    public BLEValuesGenerator(){
        this.dataGenerator = new RandomDataGenerator();
    }

    public Double generateRandomData(){
        return this.dataGenerator.getRandomTemperature();
    }
}
