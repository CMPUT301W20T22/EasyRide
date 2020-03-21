package com.example.easyride.ui.driver;

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
  private String pickupPoint;
  private String targetPoint;
  private boolean rideAccepted;
  private boolean rideCompleted;
  private Integer cost;
  // private int distance;
  private String riderUserName;
  private String driverUserName;

  public RideRequest() {

  }


  public RideRequest(String riderUserName, String pickupPoint, String targetPoint, Integer cost, boolean rideAccepted, boolean rideCompleted) {
    this.riderUserName = riderUserName;
    this.pickupPoint = pickupPoint;
    this.targetPoint = targetPoint;
    this.cost = cost;
    this.rideAccepted = rideAccepted;
    this.rideCompleted = rideCompleted;
  }

  public String getPickupPoint() {
    return pickupPoint;
  }

  public String getTargetPoint() {
    return targetPoint;
  }

  public Integer getCost() {
    return cost;
  }

  //public int getDistance() {
  //  return distance;
  // }

  public String getDriverUserName() {
    return driverUserName;
  }

  public String getRiderUserName() {
    return riderUserName;
  }

  public boolean isRideAccepted() { return rideAccepted; }

  public boolean isRideCompleted() { return rideCompleted; }

  public void setCost(int cost) { this.cost = cost; }

//  public void setDistance(int distance) {
//    this.distance = distance;
//  }

  public void setDriverUserName(String driverUserName) {
    this.driverUserName = driverUserName;
  }

  public void setPickupPoint(String pickupPoint) {
    this.pickupPoint = pickupPoint;
  }

  public void setRideAccepted(boolean rideAccepted) {
    this.rideAccepted = rideAccepted;
  }

  public void setRideCompleted(boolean rideCompleted) {
    this.rideCompleted = rideCompleted;
  }

  public void setRiderUserName(String riderUserName) {
    this.riderUserName = riderUserName;
  }

  public void setTargetPoint(String targetPoint) {
    this.targetPoint = targetPoint;
  }



}
