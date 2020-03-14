package com.example.easyride.map;

import android.graphics.Color;
import android.location.Address;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Maps;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
//import com.journaldev.maproutebetweenmarkers.DirectionsJSONParser;

/**
 * This class handles start and end locations and shows them as markers on the map.
 */
public class MarkerHandler {
  private Marker startMarker = null;
  private Marker endMarker = null;
  private LatLng startLatLang = null;
  private LatLng endLatLang = null;
  public GoogleMap mMap;
  private Route route;

  public MarkerHandler() { // TODO: change this constructor
    mockLatLangs();
  }

  public MarkerHandler(GoogleMap mMap) {
    this.mMap = mMap;
    route = new Route(mMap);
//    route = new Route(){
//      final gmap = mMap;
//      @Override
//      public void setMap() {
//        gMap = MarkerHandler.this.mMap;
//      }
//    };
  }

  private boolean showStartMarker() {
    if (startLatLang == null)
      return false;
    if (startMarker != null) {
      startMarker.remove();
    }
//    LatLng latLng = new LatLng(startLatLang.getLatitude(), startLatLang.getLongitude());
    startMarker = mMap.addMarker(new MarkerOptions()
        .position(startLatLang)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        .title("Start here"));
    return true; // returns true response if it works well
  }

  private boolean showEndMarker() {
    if (endLatLang == null)
      return false;
    if (endMarker != null)
      endMarker.remove();
//    LatLng latLng = new LatLng(endLatLang.getLatitude(), endLatLang.getLongitude());
    endMarker = mMap.addMarker(new MarkerOptions()
        .position(endLatLang)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        .title("End here"));
    return true; // returns true response if it works well
  }

  public boolean showMarkers() {

    boolean S = showStartMarker();
    boolean E = showEndMarker();
    if (S && !E) {
      animateCamera(startLatLang);
    } else if (!S && E) {
      animateCamera(endLatLang);
    } else if (S && E) {
//      LatLng midP = new LatLng((startLatLang.latitude + endLatLang.latitude) / 2,
//          (startLatLang.longitude + endLatLang.longitude) / 2);
//      animateCamera(midP, 10);

      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      builder.include(startMarker.getPosition());
      builder.include(endMarker.getPosition());
      LatLngBounds bounds = builder.build();
      CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 3);
      mMap.animateCamera(cu);
      // https://stackoverflow.com/questions/14828217/android-map-v2-zoom-to-show-all-the-markers
      showRoute();
    } else {
      return false;
    }
    return true;
  }

  public void setStartLatLang(LatLng startLatLang) {
    this.startLatLang = startLatLang;
  }

  public void setEndLatLang(LatLng endLatLang) {
    this.endLatLang = endLatLang;
  }

  public LatLng getStartAddress() {
    return startLatLang;
  }

  public Marker getEndMarker() {
    return endMarker;
  }

  public boolean hasLatLangs() {
    return startLatLang != null && endLatLang != null;
  }

  private void mockLatLangs() {
    startLatLang = new LatLng(53.5273936, -113.52911929999999);
    endLatLang = new LatLng(53.5273536, -113.52911929999999);
  }

  public void animateCamera(LatLng latLng, int zoom) { //zoom 15
    CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(latLng)      // Sets the center of the map to Mountain View
        .zoom(zoom)                   // Sets the zoom
//         .bearing(90)                // Sets the orientation of the camera to east
//         .tilt(30)                   // Sets the tilt of the camera to 30 degrees
        .build();                   // Creates a CameraPosition from the builder
    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
  }

  public void animateCamera(LatLng latLng) {
    animateCamera(latLng, 17);
  }
  public void showRoute(){
        route.showOnMap(startLatLang, endLatLang);
  }
}
