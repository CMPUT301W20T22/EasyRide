package com.example.easyride.map;

import androidx.annotation.Nullable;
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
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.easyride.MainActivity;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place.Field;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * https://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
 * https://github.com/ManishAndroidIos/Master-Google-Place-API
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    MarkerHandler mh;
    private int request_code_start = 1001;
    private int request_code_end = 1002;
    private TextInputEditText start_location_edittext, end_location_edittext, location_edittext;
    private FloatingActionButton sendRequestButton;
    private boolean isMapLoaded = false;
    private LatLng latLng;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(),getString(R.string.api_key));

        start_location_edittext = findViewById(R.id.start_location_EditText);
        end_location_edittext = findViewById(R.id.end_location_EditText);
        sendRequestButton = findViewById(R.id.create_request_button);
        start_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Field> fields = Arrays.asList(Field.ID, Field.NAME, Field.LAT_LNG);
                location_edittext = start_location_edittext;

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setInitialQuery("ETLC")
                    .build(MapsActivity.this);
                startActivityForResult(intent, request_code_start);
            }
        });

        end_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location_edittext = end_location_edittext;
                List<Field> fields = Arrays.asList(Field.ID, Field.NAME, Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setInitialQuery("HUB mall")
                    .setCountry("CA")
                    .build(MapsActivity.this);
                startActivityForResult(intent, request_code_end);
            }
        });

        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distance = mh.getRouteDistance();

                String ID = null;
                try {
                    ID = createID();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*
                   LatLng startPoint = mh.getStartLatLang();
                   LatLng endPoint = mh.getEndLatLang();
                */
                Double cost = distance/1000+7;
                DecimalFormat df = new DecimalFormat("#.##");
                cost = Double.valueOf(df.format(cost));
                String cost_string = Double.toString(cost);
                String distance_string = Double.toString(distance);
                Log.e("COST : ", Double.toString(cost));
//                PolylineOptions polylineOptions= mh.getRoutePolyline();

                String start_location_string = "Dice!";
                String end_location_string = "HUB";


                HashMap<String, Object> data = new HashMap<>();
                data.put("riderUserName", user.getDisplayName());
                data.put("pickupPoint", start_location_string);
                data.put("targetPoint", end_location_string);
                data.put("cost", cost);
                data.put("isAccepted", false);
                data.put("isCompleted", false);

                db.collection("RideRequest").document(ID).set(data);

                Rider riderInstance = Rider.getInstance(new EasyRideUser("userid"));
                Ride rideInsert = new Ride(start_location_string, end_location_string, cost_string, "me", distance_string);
                SingleRide instance = SingleRide.getInstance();
                instance.addRide(rideInsert);

                Intent i = new Intent(MapsActivity.this, rider_home.class);
                startActivity(i);
            }
        });

    }






    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Gwoogle Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
        if (! isMapLoaded){
            mh = new MarkerHandler(googleMap, getString(R.string.api_key));
        }
        isMapLoaded = true;

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
            mh.animateCamera(coordinate);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code_start | requestCode == request_code_end) {
            if (resultCode == RESULT_OK) {
//                Log.d("data",data.getExtras().toString());
                Place place = Autocomplete.getPlaceFromIntent(data);

                if (requestCode == request_code_start){
                    start_location_edittext.setText(place.getName());
                    if(place.toString()!=null && !place.toString().equals("")) {
                        mh.setStartLatLang(place.getLatLng());
                        mh.showMarkers();
                    }
                }
                else if (requestCode == request_code_end){
                    end_location_edittext.setText(place.getName());
                    if( !place.toString().equals("")) {
                        mh.setEndLatLang(place.getLatLng());
                        mh.showMarkers();
                    }
                }
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                // TODO: Handle the error.
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                location_edittext.setText(place.getName());
//
//                if(place.toString()!=null && !place.toString().equals("")){
//                    new MapsActivity.GeocoderTask().execute(place.toString());
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    public String createID() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}


//  }
//}
