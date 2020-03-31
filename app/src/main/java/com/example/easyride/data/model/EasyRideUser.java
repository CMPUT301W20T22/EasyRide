package com.example.easyride.data.model;


/**
 * EasyRideUser class that captures user information for logged in users retrieved from LoginRepository
 * Note that the user's password is not save in any field
 * @author T22
 * @version 1.0
 */
public class EasyRideUser {

    private String userId;
    private String password;
    private String displayName;
    // private String Mode;

    /**
     * Class constructor
     * @param userId
     */
    public EasyRideUser(String userId) {
        this.userId = userId;
        //this.Mode = Mode;
       // this.displayName = displayName;
    }

    public String getPassword(){return password;}

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    // public String isDriver() { return Mode; }

    // public void setStatus(String isDriver) { this.Mode = isDriver; }

    public void setPassword(String password){this.password = password;}

    public void setDisplayName(String name){this.displayName = name;}


}
