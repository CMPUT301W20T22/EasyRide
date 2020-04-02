package com.example.easyride.ui.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.data.model.Rider;
import com.example.easyride.map.MarkerHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RideReview extends AppCompatActivity implements OnMapReadyCallback {

    private TextView pickUp, Destination, Fare, rider;
    private String mPickUp, mDestination, mFare, mRider;
    private Toolbar toolbar;
    private Button accept_pay_button;
    MarkerHandler mh;
    private boolean isMapLoaded = false;
    private boolean isRouteShown = false;
    private Ride rideReq;
    private Driver driver;
    private int position;
    private boolean isFinished;
    private ArrayList<Ride> DataList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_review);

        pickUp = findViewById(R.id.pickUpLocation);
        Destination = findViewById(R.id.Destination);
        Fare = findViewById(R.id.Fare);
        rider = findViewById(R.id.RiderUserName);
        toolbar = findViewById(R.id.reviewToolbar);
        accept_pay_button = (Button)findViewById(R.id.accept_pay_button);
        // Set up ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ride Request Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_ride_review);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(),getString(R.string.api_key));
        Intent intent = getIntent();

        position = intent.getIntExtra("position", 0);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        driver = new Driver(new EasyRideUser(userID)) {
            @Override
            public void onDataLoaded() {
                updateView();
            }
        };

        accept_pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RideReview.this, QR_Scan.class);
                startActivity(i);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RideReview.this, DriverHome.class));
                finish();
            }
        });

        // Set up variables
        mPickUp = getIntent().getStringExtra("pickUpLocation");
        mDestination = getIntent().getStringExtra("destination");
        mFare = getIntent().getStringExtra("fare");
        mRider = getIntent().getStringExtra("rider");

        // Set up TextView
        pickUp.setText(mPickUp);
        Destination.setText(mDestination);
        Fare.setText(mFare);
        rider.setText(mRider);

    }

    private void updateView() {
        DataList = driver.getActiveRequests();
        if (isFinished || !driver.isDataLoaded()){
            return;
        }
        rideReq = DataList.get(position);
        // Set up variables
        mPickUp = rideReq.getFrom();
        mDestination = rideReq.getTo();
        mFare = rideReq.getCost();
        mRider = rideReq.getUser();

        // Set up TextView
        pickUp.setText(mPickUp);
        Destination.setText(mDestination);
        Fare.setText(mFare);
        rider.setText(mRider);

        if (isMapLoaded && !isRouteShown){
            LatLng startLatLang = new LatLng(rideReq.getStartPoint().getLatitude(), rideReq.getStartPoint().getLongitude());
            LatLng endLatLang = new LatLng(rideReq.getEndPoint().getLatitude(), rideReq.getEndPoint().getLongitude());
            mh.setStartLatLang(startLatLang);
            mh.setEndLatLang(endLatLang);
            mh.showMarkers();
            isRouteShown = true;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (! isMapLoaded){
            mh = new MarkerHandler(googleMap, getString(R.string.api_key));
        }
        isMapLoaded = true;
        driver.updateList();
    }
}
