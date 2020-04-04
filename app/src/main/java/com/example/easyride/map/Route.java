package com.example.easyride.map;
//https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;

import com.example.easyride.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

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

public  class  Route {
  private PolylineOptions polylineOptions;
  private Polyline polyline;
  protected GoogleMap gMap;
  private double distance;
  private String apiKey;

  public Route(GoogleMap gMap, String apiKey) {
    this.gMap = gMap;
    this.apiKey = apiKey;
  }

  public PolylineOptions getPolylineOptions() {
    return polylineOptions;
  }

  public double getDistance() {
    return distance;
  }

  public void showOnMap(LatLng start, LatLng end){
    if (polyline!=null){
      distance = 0.0;
      polyline.remove();
      polylineOptions = null;
    }
    String url = getDirectionsUrl(start,end);
    DownloadTask downloadTask = new DownloadTask();
    downloadTask.execute(url);

  }
  private class DownloadTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... url) {

      String data = "";

      try {
        data = downloadUrl(url[0]);
      } catch (Exception e) {
        Log.d("Background Task", e.toString());
      }
      return data;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      ParserTask parserTask = new ParserTask();
      parserTask.execute(result);
    }
  }

  private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;

      try {
        jObject = new JSONObject(jsonData[0]);
        DirectionsJSONParser parser = new DirectionsJSONParser();

        routes = parser.parse(jObject);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return routes;
    }


    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
      ArrayList points = null;
      PolylineOptions lineOptions = null;
      MarkerOptions markerOptions = new MarkerOptions();

      for (int i = 0; i < result.size(); i++) {
        points = new ArrayList();
        lineOptions = new PolylineOptions();

        List<HashMap<String, String>> path = result.get(i);

        for (int j = 0; j < path.size(); j++) {
          HashMap point = path.get(j);

          double lat = Double.parseDouble(point.get("lat").toString());
          double lng = Double.parseDouble(point.get("lng").toString());
          LatLng position = new LatLng(lat, lng);

          points.add(position);
        }

        lineOptions.addAll(points);
        lineOptions.width(12);
        lineOptions.color(Color.BLUE);
        lineOptions.geodesic(true);

      }
      polylineOptions = lineOptions;
      distance = lengthOfPolyline(polylineOptions.getPoints());

// Drawing polyline in the Google Map for the i-th route


      LatLngBounds.Builder builder = new LatLngBounds.Builder();
      for (LatLng latLang: polylineOptions.getPoints()) {
        builder.include(latLang);
      }
      LatLngBounds bounds = builder.build();
      CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 3);
      gMap.animateCamera(cu);
      polyline = gMap.addPolyline(lineOptions);
      }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

      // Origin of route
      String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

      // Destination of route
      String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

      // Sensor enabled
      String sensor = "sensor=false";
      String mode = "mode=driving";
      String key = "key=" + apiKey;
      // Building the parameters to the web service
      String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

      // Output format
      String output = "json";

      // Building the url to the web service
      String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
      return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
      String data = "";
      InputStream iStream = null;
      HttpURLConnection urlConnection = null;
      try {
        URL url = new URL(strUrl);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.connect();

        iStream = urlConnection.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

        StringBuffer sb = new StringBuffer();

        String line = "";
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }

        data = sb.toString();

        br.close();

      } catch (Exception e) {
        Log.d("Exception", e.toString());
      } finally {
        iStream.close();
        urlConnection.disconnect();
      }
      return data;
    }


  public static LatLng geoPointToLatLng(GeoPoint geoPoint){
    return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
  }

  public static double distance(GeoPoint geoPoint1, GeoPoint geoPoint2){

    return distance(geoPointToLatLng(geoPoint1), geoPointToLatLng(geoPoint2));

  }

  // Author https://stackoverflow.com/users/502162/david-george
  // https://stackoverflow.com/a/16794680
  public static double distance(LatLng latLng1, LatLng latLng2) {
    final int R = 6371; // Radius of the earth
    double latDistance = Math.toRadians(latLng2.latitude - latLng1.latitude);
    double lonDistance = Math.toRadians(latLng2.longitude - latLng1.longitude);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(latLng1.latitude)) * Math.cos(Math.toRadians(latLng2.latitude))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000; // convert to meters

    distance = Math.pow(distance, 2);

    return Math.sqrt(distance);
  }
  public static double  lengthOfPolyline(List<LatLng> latLngs ){
    Double routeLength = 0.0;
    for (int i=0; i < latLngs.size();i++) {
      if (i!=0){
        routeLength += distance(latLngs.get(i), latLngs.get(i-1));
      }
    }
    return  routeLength;
  }
}

