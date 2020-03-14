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

import com.example.easyride.R;
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
import com.google.android.material.textfield.TextInputEditText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * https://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
 * https://github.com/ManishAndroidIos/Master-Google-Place-API
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
//    GoogleMap mMap;
    MarkerHandler mh;
//    Place startPlace, endPlace;
//    String place_start_string, place_end_string;
//    PlacesClient placesClient;
    private int request_code_start = 1001;
    private int request_code_end = 1002;
    TextInputEditText start_location_edittext;
    TextInputEditText end_location_edittext;
    TextInputEditText location_edittext;
    Button sendRequestButton;
//    MarkerOptions markerOptions;
//    LatLng latLng, start_location, end_location;
//    private MarkerOptions options = new MarkerOptions();
//    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private boolean isMapLoaded = false;


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
        sendRequestButton = findViewById(R.id.create_request_button);
        start_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                autocompleteFragment(start_location_edittext);
                List<Field> fields = Arrays.asList(Field.ID, Field.NAME, Field.LAT_LNG);
                location_edittext = start_location_edittext;
                // Start the autocomplete intent.

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setInitialQuery("ETLC")
                    .build(MapsActivity.this);
//                intent.putExtra("isStart", "true");
                startActivityForResult(intent, request_code_start);
            }
        });

        end_location_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                autocompleteFragment(end_location_edittext);
                location_edittext = end_location_edittext;
//                autocompleteFragment(start_location_edittext);
                List<Field> fields = Arrays.asList(Field.ID, Field.NAME, Field.LAT_LNG);
//                fields.add(new Field())
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setInitialQuery("HUB mall")
                    .setCountry("CA")
                    .build(MapsActivity.this);
//                intent.putExtra("isStart", "false");
//                Log.d("data1",intent.getExtras().toString());
                startActivityForResult(intent, request_code_end);
            }
        });
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distance = mh.getRouteDistance();
                LatLng startPoint = mh.getStartLatLang();
                LatLng endPoint = mh.getEndLatLang();
//                PolylineOptions polylineOptions= mh.getRoutePolyline();
            }
        });
    }
//    @Override
//    public void onResume(){
//
//        super.onResume();
//        mh.showStartMarker();
//        mh.showEndMarker();
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
        if (! isMapLoaded){
            mh = new MarkerHandler(googleMap);
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
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
  }


