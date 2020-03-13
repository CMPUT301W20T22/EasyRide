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
  private String rideAccepted;
  private String rideCompleted;
  private String cost;
  // private int distance;
  private String riderUserName;
  private String driverUserName;

  public RideRequest() {

  }


  public RideRequest(String riderUserName, String pickupPoint, String targetPoint, String cost) {
    this.riderUserName = riderUserName;
    this.pickupPoint = pickupPoint;
    this.targetPoint = targetPoint;
    this.cost = cost;

  }

  public String getPickupPoint() {
    return pickupPoint;
  }

  public String getTargetPoint() {
    return targetPoint;
  }

  public String getCost() {
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

  public String isRideAccepted() { return rideAccepted; }

  public String isRideCompleted() { return rideCompleted; }

  public void setCost(String cost) { this.cost = cost; }

//  public void setDistance(int distance) {
//    this.distance = distance;
//  }

  public void setDriverUserName(String driverUserName) {
    this.driverUserName = driverUserName;
  }

  public void setPickupPoint(String pickupPoint) {
    this.pickupPoint = pickupPoint;
  }

  public void setRideAccepted(String rideAccepted) {
    this.rideAccepted = rideAccepted;
  }

  public void setRideCompleted(String rideCompleted) {
    this.rideCompleted = rideCompleted;
  }

  public void setRiderUserName(String riderUserName) {
    this.riderUserName = riderUserName;
  }

  public void setTargetPoint(String targetPoint) {
    this.targetPoint = targetPoint;
  }



}
