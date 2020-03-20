package com.project.atmos.dev;

import java.util.ArrayList;
import java.util.Random;

public class RandomAddressGenerator extends Random {

    // An Bluetooth Address contains in general 6 Integers separated by a coma.
    // -unnamed- -> C8:FD:98:AA:30:K7

    // The aim of this class is to get randomly a BT address for development and test purpose.

    private static int NB_OF_INTEGERS = 6;
    private Random randomEngine;

    public RandomAddressGenerator() {
        this.randomEngine = new Random();
    }

    public String generateAddress(){
        String addressCompositor = "";
        ArrayList<Integer> addressIntegerSet = new ArrayList<Integer>();
        for(int i = 0; i < this.NB_OF_INTEGERS; i++){
            addressIntegerSet.add(this.randomEngine.nextInt(255));
            addressCompositor += Integer.toHexString(addressIntegerSet.get(i));
            if(i != this.NB_OF_INTEGERS - 1) {
                addressCompositor += ":";
            }
        }
        return addressCompositor.toUpperCase();
    }
}
