package com.example.easyride.data;

import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class will handle the communication with database
 */
public class DataManager {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference driverRef = database.collection("driver");
    CollectionReference riderRef = database.collection("rider");


    public boolean isDriver(String userID){
        return false;
    }

    public EasyRideUser getDriver(String userID){
        //make the user class
        driverRef.
        return null;
    }

    public void insertDriver(Driver driver){
        EasyRideUser driverInfo = driver.getCurrentDriverInfo();
        driverRef.document(driverInfo.getUserId()).set(driverInfo);
    }

    public boolean isRider(String userID){
        return false;
    }

    public EasyRideUser getRider(String userID){
        //make the user class
        return null;
    }

    public void insertRider(Rider driver){
        EasyRideUser riderInfo = driver.getCurrentRiderInfo();
        driverRef.document(riderInfo.getUserId()).set(riderInfo);
    }




}
