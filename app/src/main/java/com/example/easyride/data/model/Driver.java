package com.example.easyride.data.model;

import com.example.easyride.data.UserDatabaseManager;

import java.util.ArrayList;

/**
 * Driver class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 *
 * get rid of extend
 */
public class Driver extends EasyRideUser {

  private ArrayList<RideRequest> activeRequests;
  private EasyRideUser currentDriverInfo;
  private static Driver instance;

  private Driver(String userId){
    super(userId);
    UserDatabaseManager database = new UserDatabaseManager();
    boolean exists = database.isDriver("hi");
    if (!exists){ instance = null; }
    else {
      currentDriverInfo = database.getDriver("hi");

     // activeRequests = new ArrayList<>();
    }
  }
  //return old instance or create a new one
  public static Driver getInstance(String userID){
    if(instance == null){
      instance = new Driver(userID);
    }
    return instance;
  }

  public EasyRideUser getCurrentDriverInfo(){return currentDriverInfo;}

}
