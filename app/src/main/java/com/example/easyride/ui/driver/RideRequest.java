package com.example.easyride.ui.driver;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

/**
 * RideRequest class that ...
 * @author T22
 * @version 1.0
 * @see Location
 */
public class RideRequest {
  private String pickupPoint;
  private String targetPoint;
  private boolean rideAccepted;
  private boolean rideCompleted;
  private boolean rideConfirmAccepted;
  private boolean ridePaid;
  private String cost;
  // private int distance;
  private String riderUserName;
  private String driverUserName;
  private String key;
  private GeoPoint startPoint;
  private GeoPoint endPoint;

  public RideRequest() {
  }

  public boolean isRideConfirmAccepted() {
    return rideConfirmAccepted;
  }

  public boolean isRidePaid() {
    return ridePaid;
  }

  public void setRidePaid(boolean ridePaid) {
    this.ridePaid = ridePaid;
  }


  public void setKey(String key) {
    this.key = key;
  }

  public void setRideConfirmAccepted(boolean rideConfirmAccepted) {
    this.rideConfirmAccepted = rideConfirmAccepted;
  }

  public RideRequest(String key, String riderUserName, String pickupPoint, String targetPoint, String cost, boolean rideAccepted, boolean rideCompleted) {
    this.riderUserName = riderUserName;
    this.pickupPoint = pickupPoint;
    this.targetPoint = targetPoint;
    this.cost = cost;
    this.rideAccepted = rideAccepted;
    this.rideCompleted = rideCompleted;
    this.key = key;
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

  public String getKey() { return key; }

  //public int getDistance() {
  //  return distance;
  // }

  public String getDriverUserName() {
    return driverUserName;
  }

  public String getRiderUserName() {
    return riderUserName;
  }

  public GeoPoint getStartPoint() { return this.startPoint;}

  public GeoPoint getEndPoint() { return this.endPoint;}

  public boolean isRideAccepted() { return rideAccepted; }

  public boolean isRideCompleted() { return rideCompleted; }

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

  public void setStartPoint(GeoPoint startPoint) { this.startPoint = startPoint; }
  public void setEndPoint(GeoPoint endPoint) { this.endPoint = endPoint; }

}
