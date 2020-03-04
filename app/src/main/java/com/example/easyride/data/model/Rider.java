package com.example.easyride.data.model;

import com.example.easyride.data.DataManager;

import java.util.ArrayList;

/**
 * Rider class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */
public class Rider {
  //private ArrayList<RideRequest> activeRequests;
  private EasyRideUser currentRiderInfo;
  private static Rider instance;

  private Rider(String userId){
    //super(userId);
    DataManager database = new DataManager();
    boolean exists = database.isRider("hi");
    if (!exists){ instance = null; }
    else {
      currentRiderInfo = database.getRider("hi");

      // activeRequests = new ArrayList<>();
    }
  }
  //return old instance or create a new one
  public static Rider getInstance(String userID){
    if(instance == null){
      instance = new Rider(userID);
    }
    return instance;
  }

}
