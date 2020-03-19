package com.example.easyride.ui.rider;

// Ride class that stores important information on each Ride Request.
public class Ride {

    private String from;
    private String to;
    private String cost;
    private String user;


    // CONSTRUCTOR
    public Ride(String from, String to, String cost, String user) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.user = user;
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






}
