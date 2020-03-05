package com.example.easyride.data;

import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class SignupDataSource {

  public Result<EasyRideUser> signup(String username, String password,String name, boolean isRider) {

    try {
      // TODO: handle loggedInUser authentication
      EasyRideUser fakeUser =
          new Rider(
              "Jane Doe");
      return new Result.Error(new Exception("Wrong password!"));
//            return new Result.Success<>(fakeUser);
    } catch (Exception e) {
      return new Result.Error(new IOException("Error logging in", e));
    }
  }
}
