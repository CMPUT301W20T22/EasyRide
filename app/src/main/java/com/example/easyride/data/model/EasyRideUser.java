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

    /**
     * Class constructor
     * @param userId
     */
    public EasyRideUser(String userId) {
        this.userId = userId;
    }

    public String getPassword(){return password;}

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setPassword(String password){this.password = password;}

    public void setDisplayName(String name){this.displayName = name;}


}
