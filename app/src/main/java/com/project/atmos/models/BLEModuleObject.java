package com.project.atmos.models;

import java.util.Date;

import com.project.atmos.values.TemperatureUnit;
import static com.project.atmos.values.TemperatureUnit.*;

public class BLEModuleObject {
    // Info n°1: Nom du Module
    // Info n°2: Adresse du module
    // Info n°3: Actif ou Non (Booleen)
    // Info n°4: Dernière Température Enregistrée (Double)
    // Info n°5: Unité (enum)
    // Info n°6: Dernière Connexion (Date)

    // Certainly not necessary...
    // private BluetoothDevice BLEBufferedInstance;

    private String name;
    private String address;
    private Integer lastStatus = 0;
    private Double value = 0.0;
    private TemperatureUnit unit = CELSIUS;
    private Date lastConnectionDate = null;

//    public BLEModuleModel(BluetoothDevice BLEInstance){
//        this.BLEModuleName = BLEInstance.getName();
//        this.BLEModuleAddress = BLEInstance.getAddress();
//        this.BLEModuleStatusActive = BLEInstance.getBondState();
//    }

    public BLEModuleObject(BLEModuleEntity BLEDBInstance){
        if(BLEDBInstance != null){
            this.name = BLEDBInstance.getName();
            this.address = BLEDBInstance.getAddress();
        }
    }

    public void setUnit(TemperatureUnit unit){
        this.unit = unit;
    }

    public String getUnitString(){
        switch (unit){
            case CELSIUS:
                return "°C";
            case FARENHEIT:
                return "°F";
            default:
                return "";
        }
    }

    public Date getLastConnectionDate() {
        return lastConnectionDate;
    }

    public void refreshLastConnectionDate(Date currentConnectionDate){
        this.lastConnectionDate = currentConnectionDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String _address) {
        this.address = _address;
    }

    public Integer getStatus() {
        return lastStatus;
    }

    public void setStatus(Integer _status) { this.lastStatus = _status; }

    public Double getValue() { return this.value; }

    public void setValue(Double _value){ this.value = _value; }

    // Probablement dans une autre classe chargée du traitement des données reçues en bluetooth...

//    public Double getTemperature() {
//        return BLEModuleTemperature;
//    }
//
//    public void setTemperature(Double BLEModuleTemperature) {
//        this.BLEModuleTemperature = BLEModuleTemperature;
//    }
}
