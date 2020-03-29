package com.example.easyride.ui.rider;

// Ride class that stores important information on each Ride Request.
public class Ride {

    private String from;
    private String to;
    private String cost;
    private String user;
    private String distance;
    private String driverUserName;
    private boolean rideAccepted;
    private boolean rideConfirmAccepted;
    private boolean rideCompleted;
    private boolean ridePaid;

    // CONSTRUCTOR
    public Ride(String from, String to, String cost, String user, String distance) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.user = user;
        this.distance = distance;
        this.rideAccepted = false;
        this.rideCompleted = false;
        this.rideConfirmAccepted = false;
        this.ridePaid = false;
    }
    //TODO: Should we keep this constructor
    public Ride(){} 

    public boolean isRideConfirmAccepted() {
        return rideConfirmAccepted;
    }

    public void setRideConfirmAccepted(boolean rideConfirmAccepted) {
        this.rideConfirmAccepted = rideConfirmAccepted;
    }

    public boolean isRidePaid() {
        return ridePaid;
    }

    public void setRidePaid(boolean ridePaid) {
        this.ridePaid = ridePaid;
    }

    // GETTERS
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCost() {
        return cost;
    }

    public String getUser() {
        return user;
    }

    public String getDistance() { return distance; }

    public void setRideCompleted(boolean rideCompleted) {
        this.rideCompleted = rideCompleted;
    }

    private void setRideAccepted(boolean rideAccepted) {
        this.rideAccepted = rideAccepted;
    }

    public void setDriverUserName(String driverUserName) {
        this.driverUserName = driverUserName;
        setRideAccepted(true);
        if (driverUserName == null){
            setRideAccepted(false);
        }
    }

    public String getDriverUserName() {
        return driverUserName;
    }

    public boolean isRideAccepted() {
        return rideAccepted;
    }

    public boolean isRideCompleted() {
        return rideCompleted;
    }

    // SETTERS
    public void setUser(String user) {
        this.user = user;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setDistance(String distance) { this.distance = distance; }

}
