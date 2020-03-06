package com.example.easyride.data;

import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<EasyRideUser> login(String username, String password, boolean isRider) {

        try {
            // TODO: handle loggedInUser authentication
            EasyRideUser user;
            DataManager dataManager = new DataManager();
            boolean userExists;
            if (isRider){
                userExists = dataManager.isRider(username);
                if (userExists){
                    user = dataManager.getRider(username);
                }else {
                    return new Result.Error(new Exception("Wrong Username!"));
                }
            }else{
                userExists = dataManager.isDriver(username);
                if (userExists){
                     user = dataManager.getRider(username);
                }else {
                    return new Result.Error(new Exception("Wrong Username!"));
                }
            }

            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
