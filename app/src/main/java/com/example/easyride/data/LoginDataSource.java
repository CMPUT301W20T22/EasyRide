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
            EasyRideUser fakeUser =
                    new Rider(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Error(new Exception("Wrong password!"));
//            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
