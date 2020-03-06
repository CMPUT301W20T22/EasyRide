package com.example.easyride.data.model;

/**
 * Driver class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */
public class Driver extends EasyRideUser {
  public Driver(String userId, String displayName) {
    super(userId, displayName);
  }
}
