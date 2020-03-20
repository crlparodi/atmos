package com.project.atmos.dev;

import java.util.GregorianCalendar;
import java.util.Random;

public class RandomDateGenerator extends Random {

    // Generate a random last connection date for a bluetooth module

    private int R_YEAR;
    private int R_MONTH;
    private int R_DATE;
    private int R_HOURS;
    private int R_MINUTES;
    private int R_SECONDS;
    private Random randomEngine;

    public RandomDateGenerator() {
        this.randomEngine = new Random();
    }

    public GregorianCalendar generateDate(){
        this.R_YEAR = randomEngine.nextInt(40) + 1980;
        this.R_MONTH = randomEngine.nextInt(11) + 1;

        if(this.R_MONTH == 2){
            this.R_DATE = randomEngine.nextInt(29) + 1;
        }
        else{
            this.R_DATE = randomEngine.nextInt(31) + 1;
        }

        this.R_HOURS = randomEngine.nextInt(23);
        this.R_MINUTES = randomEngine.nextInt(59);
        this.R_SECONDS = randomEngine.nextInt(59);

        return new GregorianCalendar(
                this.R_YEAR,
                this.R_MONTH,
                this.R_DATE,
                this.R_HOURS,
                this.R_MINUTES,
                this.R_SECONDS) {
        };
    }
}
