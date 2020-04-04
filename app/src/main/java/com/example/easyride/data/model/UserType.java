package com.example.easyride.data.model;

/**
 * Class the return the status of the user (Driver/Rider)
 * @author T22
 * @version 1.0
 */
public enum UserType
{
  RIDER("rider"),
  DRIVER("driver");

  private String type;

  UserType(String type) {
    this.type =  type;
  }

  public String getType() {
    return type;
  }
}
