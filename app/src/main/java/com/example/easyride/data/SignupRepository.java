package com.example.easyride.data;

import com.example.easyride.data.model.EasyRideUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class SignupRepository {

  private static volatile SignupRepository instance;

  private SignupDataSource dataSource;

  // If user credentials will be cached in local storage, it is recommended it be encrypted
  // @see https://developer.android.com/training/articles/keystore
  private EasyRideUser user = null;

  // private constructor : singleton access
  private SignupRepository(SignupDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public static SignupRepository getInstance(SignupDataSource dataSource) {
    if(instance == null){
      instance = new SignupRepository(dataSource);
    }
    return instance;
  }

  public boolean isLoggedIn() {
    return user != null;
  }


  public Result<EasyRideUser> signup(String username, String password,String name, boolean isRider) {
    // handle login
    Result<EasyRideUser> result = dataSource.signup(username, password, name, isRider);
//    if (result instanceof Result.Success) {
//    }
    return result;
  }
}
