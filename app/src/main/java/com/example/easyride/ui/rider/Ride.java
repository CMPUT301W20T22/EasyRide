package com.example.easyride.ui.rider;

// Ride class that stores important information on each Ride Request.
public class Ride {

    private String from;
    private String to;
    private String cost;
    private String user;
    private String distance;


    // CONSTRUCTOR
    public Ride(String from, String to, String cost, String user, String distance) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.user = user;
        this.distance = distance;

    }

    public Ride(){}
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
