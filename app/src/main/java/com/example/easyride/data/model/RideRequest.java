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


  public RideRequest(String riderUserName, Address pickupPoint, Address targetPoint, int distance) {
    this.riderUserName = riderUserName;
    this.pickupPoint = pickupPoint;
    this.targetPoint = targetPoint;
    this.distance = distance;
    int cost = distance*2;
    this.cost = cost;
  }

  public Address getPickupPoint() {
    return pickupPoint;
  }

  public Address getTargetPoint() {
    return targetPoint;
  }

  public int getCost() {
    return cost;
  }

  public int getDistance() {
    return distance;
  }

  public String getDriverUserName() {
    return driverUserName;
  }

  public String getRiderUserName() {
    return riderUserName;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public void setDriverUserName(String driverUserName) {
    this.driverUserName = driverUserName;
  }

  public void setPickupPoint(Address pickupPoint) {
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

  public void setTargetPoint(Address targetPoint) {
    this.targetPoint = targetPoint;
  }



}
