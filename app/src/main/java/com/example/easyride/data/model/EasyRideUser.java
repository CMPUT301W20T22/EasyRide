package com.example.easyride.data.model;

import androidx.annotation.Nullable;

/**
 * EasyRideUser class that captures user information for logged in users retrieved from LoginRepository
 * Note that the user's password is not save in any field
 * @author T22
 * @version 1.0
 */
public abstract class EasyRideUser {

    private String userId;
    private String password;
    private String displayName;

    public EasyRideUser(String userId) {
        this.userId = userId;
       // this.displayName = displayName;
    }

    public String getPassword(){return password;}

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
