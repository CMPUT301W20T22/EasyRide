package com.example.easyride.map;

import android.location.Address;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.Maps;

import java.util.Locale;

/**
 * This class handles start and end locations and shows them as markers on the map.
 */
public class MarkerHandler {
  private Marker startMarker = null;
  private Marker endMarker = null;
  private Address startAddress = null;
  private Address endAddress = null;
  private Maps map;

  public MarkerHandler() { // TODO: change this constructor
    mockAddresses();
  }

  public boolean showStartMarker(GoogleMap mMap){
//     TODO: if block here and implementation
    return false; // returns true response if it works well
  }
  public boolean showEndMarker(GoogleMap mMap){
//     TODO: if block here
    return false; // returns true response if it works well
  }

  public Address getStartAddress() {
    return startAddress;
  }
  public Marker getEndMarker() {
    return endMarker;
  }
  public boolean hasAddresses(){
    return startAddress!=null &  endAddress!=null;
  }
   private void mockAddresses(){
     startAddress = new Address(Locale.CANADA);
     startAddress.setLatitude(53.5273936);
     startAddress.setLongitude(-113.52911929999999);
     endAddress = new Address(Locale.CANADA);
     endAddress.setLatitude(53.5273536);
     endAddress.setLongitude(-113.52911929999999);
   }
}
