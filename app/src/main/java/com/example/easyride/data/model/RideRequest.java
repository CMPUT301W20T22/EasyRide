package com.example.easyride.data.model;

import android.location.Location;

/**
 * RideRequest class that ...
 * @author T22
 * @version 1.0
 * @see Location
 */
public class RideRequest {
  private String key;
  private Location pickupPoint;
  private Location targetPoint;
  private boolean rideAccepted;
  private boolean rideCompleted;
  private int cost;
  private int distnace;
  private String riderUserName;
  private String driverUserName;

  public RideRequest(String riderUserName, Location pickupPoint, Location targetPoint) {
    this.riderUserName = riderUserName;
    this.pickupPoint = pickupPoint;
    this.targetPoint = targetPoint;
  }
}
