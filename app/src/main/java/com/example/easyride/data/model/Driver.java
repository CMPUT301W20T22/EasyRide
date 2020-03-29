package com.example.easyride.data.model;

import com.example.easyride.ui.driver.RideRequest;

import java.util.ArrayList;

/**
 * Driver class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */
public class Driver extends EasyRideUser {

  private EasyRideUser currentDriverInfo;
  private static Driver instance;


  private Driver(EasyRideUser user){
    super(user.getUserId());
    // UserDatabaseManager database = new UserDatabaseManager();
    // boolean exists = database.isDriver("hi");
    // if (!exists){ instance = null; }
    // else {
      // currentDriverInfo = database.getDriver("hi");

     // activeRequests = new ArrayList<>();
    // }
    currentDriverInfo = user;

    //TODO: add activeRequests
  }
  //return old instance or create a new one
  public static Driver getInstance(EasyRideUser user){
    if(instance == null){
      instance = new Driver(user);
    }
    return instance;
  }

  public EasyRideUser getCurrentDriverInfo(){ return currentDriverInfo; }

}
