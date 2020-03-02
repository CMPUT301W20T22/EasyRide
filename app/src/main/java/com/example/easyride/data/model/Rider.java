package com.example.easyride.data.model;

/**
 * Rider class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */
public class Rider extends EasyRideUser {
  public Rider(String userId, String displayName) {
    super(userId, displayName);
  }
}
