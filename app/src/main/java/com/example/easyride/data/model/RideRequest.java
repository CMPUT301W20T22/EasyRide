package com.example.easyride.data.model;

import android.location.Address;
import android.location.Location;

/**
 * RideRequest class that ...
 * @author T22
 * @version 1.0
 * @see Location
 */
public class RideRequest {
 // private String key;
  private Address pickupPoint;
  private Address targetPoint;
  private boolean rideAccepted;
  private boolean rideCompleted;
  private int cost;
  private int distance;
  private String riderUserName;
  private String driverUserName;


  public RideRequest(String riderUserName, Address pickupPoint, Address targetPoint) {
    this.riderUserName = riderUserName;
    this.pickupPoint = pickupPoint;
    this.targetPoint = targetPoint;
  }
}
