package com.example.easyride.map;
//https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
  protected GoogleMap gMap;

  public Route(GoogleMap gMap) {
    this.gMap = gMap;
  }


//  public abstract void setMap();

  public PolylineOptions makeRoute(LatLng start, LatLng end){
    String url = getDirectionsUrl(start,end);
    DownloadTask downloadTask = new DownloadTask();
    downloadTask.execute(url);
    return polylineOptions;
  }
  public void showOnMap(LatLng start, LatLng end){
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
// Drawing polyline in the Google Map for the i-th route
      gMap.addPolyline(lineOptions);
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
      String key = "key=AIzaSyAbWZnSh3B5IqASEwuQ7-5kLNbH__K681k";
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
}

