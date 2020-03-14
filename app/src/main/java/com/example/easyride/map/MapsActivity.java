package com.example.easyride.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.toolbox.HttpResponse;
import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.example.easyride.ui.login.LoginActivity;
import com.example.easyride.ui.rider.Ride;
import com.example.easyride.ui.rider.SingleRide;
import com.example.easyride.ui.rider.rider_home;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * https://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
 * https://github.com/ManishAndroidIos/Master-Google-Place-API
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String start_location_string, end_location_string;
    private int request_code = 1001;
    private TextInputEditText location_edittext, start_location_edittext,end_location_edittext;
    private MarkerOptions markerOptions;
    private LatLng latLng;
    private FloatingActionButton fab;
    private float distance, cost;
    private double start_location_latitude, start_location_longitude;
    private double end_location_latitude, end_location_longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(),getString(R.string.api_key));

        start_location_edittext = findViewById(R.id.start_location_EditText);
        end_location_edittext = findViewById(R.id.end_location_EditText);

        start_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutocompleteActivity(start_location_edittext);
            }
        });

        end_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutocompleteActivity(end_location_edittext);
            }
        });

        fab = findViewById(R.id.create_request_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabClicked();
            }
        });
    }

    private void fabClicked(){
        distance = getDistance(start_location_latitude, start_location_longitude, end_location_latitude, end_location_longitude);
        distance = distance/1000;
        String distance_string = Float.toString(distance);
        Log.e("DISTANCE : ", distance_string);
        cost = getFare(distance);
        String cost_string = Float.toString(cost);
        Log.e("COST : ", cost_string);

        SingleRide instance = SingleRide.getInstance();

        Rider riderInstance = Rider.getInstance(new EasyRideUser("userid"));
        Ride rideInsert = new Ride(start_location_string, end_location_string, cost_string, "me", distance_string);

        instance.addRide(rideInsert);

        Intent i = new Intent(MapsActivity.this, rider_home.class);
        startActivity(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);


        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15);
            mMap.animateCamera(yourLocation);
        }

    }

    private void startAutocompleteActivity(TextInputEditText x) {
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID,
                com.google.android.libraries.places.api.model.Place.Field.NAME);
        location_edittext = x;
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
        startActivityForResult(intent, request_code);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                location_edittext.setText(place.getName());

                if(place.toString()!=null && !place.toString().equals("")){
                    new MapsActivity.GeocoderTask().execute(place.toString());
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(MapsActivity.this, "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            //mMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = (Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map

                if(location_edittext == start_location_edittext) {
                    Log.e("From Adress : ", address.getAddressLine(0));
                    start_location_string = address.getAddressLine(0);
                    start_location_latitude = address.getLatitude();
                    start_location_longitude = address.getLongitude();

                }else if(location_edittext == end_location_edittext){
                    Log.e("To Adress : ", address.getAddressLine(0));
                    end_location_string = address.getAddressLine(0);
                    end_location_latitude = address.getLatitude();
                    end_location_longitude = address.getLongitude();
                }
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(address.getAddressLine(0));
                mMap.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }}


    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
        Location A = new Location("Starting");
        Location B = new Location("Ending");
        A.setLatitude(lat1);
        A.setLongitude(lon1);
        B.setLatitude((lat2));
        B.setLongitude(lon2);
        return A.distanceTo(B);
    }
//    /**
//     * https://stackoverflow.com/questions/22609087/how-to-find-distance-by-road-between-2-geo-points-in-android-application-witho
//     */
//    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
//        String result_in_kms = "";
//        String urlString = "http://maps.google.com/maps/api/directions/xml?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&units=metric&key=AIzaSyAbWZnSh3B5IqASEwuQ7-5kLNbH__K681k";
//        String tag[] = {"value"};
//        try {
//            URL url = new URL(urlString);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
//            //urlConnection.disconnect();
//            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            Document doc = builder.parse(is);
//            if (doc != null) {
//                NodeList nl;
//                ArrayList args = new ArrayList();
//                for (String s : tag) {
//                    nl = doc.getElementsByTagName(s);
//                    if (nl.getLength() > 0) {
//                        Node node = nl.item(nl.getLength() - 1);
//                        args.add(node.getTextContent());
//                    } else {
//                        args.add(" - ");
//                    }
//                }
//                result_in_kms =String.valueOf( args.get(0));
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        Float f=Float.valueOf(result_in_kms);
//        return f;
//    }

    public float getFare(float distance) {
        float fareMultiplier = 2.5f;
        float fare = distance*fareMultiplier;
        return fare;
    }


}
