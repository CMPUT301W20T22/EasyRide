package com.example.easyride.ui.rider;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Singleton class for Ride.
 * @author T22
 * @version 1.0
 */
public class SingleRide {

    private static SingleRide instance;
    private ArrayList<Ride> ride;
    private FirebaseFirestore db;

    private SingleRide(){

        // UserDatabaseManager database = new UserDatabaseManager();
        // boolean exists = database.isDriver("hi");
        // if (!exists){ instance = null; }
        // else {
        // currentDriverInfo = database.getDriver("hi");

        // activeRequests = new ArrayList<>();
        // }
        db = FirebaseFirestore.getInstance();

        ride = new ArrayList<>();

        //TODO: add activeRequests
    }

    /**
     * Return old instance or create a new one.
     * @return SingleRide
     */
    public static SingleRide getInstance(){
        if(instance == null){
            instance = new SingleRide();
        }
        return instance;
    }

    /**
     * Add the ride to the database
     * @param rideInsert
     */
    public void addRide(Ride rideInsert){
        DocumentReference newCityRef = db.collection("RideRequest").document();
        newCityRef.set(rideInsert);
        this.ride.add(rideInsert);
    }

    /**
     * get the Ride
     * @return ArrayList<Ride>
     */
    public ArrayList<Ride> getRide() {
        return ride;
    }

    /**
     * Remove the Ride
     * @param position
     */
    public void removeAt(int position){
        ride.remove(position);
    }
}
