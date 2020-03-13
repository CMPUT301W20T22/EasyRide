package com.example.easyride.data;

import com.example.easyride.data.model.EasyRideUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<EasyRideUser> login(String username, String password, boolean isRider) {

        try {
            // TODO: handle loggedInUser authentication
            EasyRideUser user;
            UserDatabaseManager userDatabaseManager = new UserDatabaseManager();
            boolean userExists;
            if (isRider){
                userExists = userDatabaseManager.isRider(username);
                if (userExists){
                    user = userDatabaseManager.getRider(username);
                }else {
                    return new Result.Error(new Exception("Wrong Username!"));
                }
            }else{
                userExists = userDatabaseManager.isDriver(username);
                if (userExists){
                     user = userDatabaseManager.getRider(username);
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
