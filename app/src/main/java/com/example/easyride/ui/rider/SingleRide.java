package com.example.easyride.ui.rider;

import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;

import java.util.ArrayList;

public class SingleRide {

    private static SingleRide instance;
    private ArrayList<Ride> ride;


    private SingleRide(){

        // UserDatabaseManager database = new UserDatabaseManager();
        // boolean exists = database.isDriver("hi");
        // if (!exists){ instance = null; }
        // else {
        // currentDriverInfo = database.getDriver("hi");

        // activeRequests = new ArrayList<>();
        // }

        ride = new ArrayList<>();

        //TODO: add activeRequests
    }
    //return old instance or create a new one
    public static SingleRide getInstance(){
        if(instance == null){
            instance = new SingleRide();
        }
        return instance;
    }

    public void addRide(Ride rideInsert){
        this.ride.add(rideInsert);
    }

    public ArrayList<Ride> getRide() {
        return ride;
    }
}
