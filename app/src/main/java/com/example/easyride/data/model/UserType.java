package com.example.easyride.data.model;

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
