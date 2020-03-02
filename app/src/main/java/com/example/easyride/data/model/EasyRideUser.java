package com.example.easyride.data.model;

/**
 * EasyRideUser class that captures user information for logged in users retrieved from LoginRepository
 * Note that the user's password is not save in any field
 * @author T22
 * @version 1.0
 */
public abstract class EasyRideUser {

    private String userId;
    private String displayName;

    public EasyRideUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
