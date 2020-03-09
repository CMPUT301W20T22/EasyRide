package com.example.easyride.ui.rider;

public class Ride {

    private String from;
    private String to;
    private String price;
    private String user;


    // CONSTRUCTOR
    public Ride(String from, String to, String price, String user) {
        this.from = from;
        this.to = to;
        this.price = price;
        this.user = user;
    }


    // GETTERS
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getPrice() {
        return price;
    }

    public String getUser() {
        return user;
    }


    // SETTERS
    public void setUser(String user) {
        this.user = user;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

















}
