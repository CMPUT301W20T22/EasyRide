package com.example.easyride.ui.driver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.data.model.Rider;
import com.example.easyride.data.model.UserDB;
import com.example.easyride.data.model.UserType;
import com.example.easyride.map.MarkerHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Objects;

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
    private ImageView profileImage;
    private UserDB driverUserDB;
    private UserDB riderUserDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_review);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        pickUp = findViewById(R.id.pickUpLocation);
        Destination = findViewById(R.id.Destination);
        Fare = findViewById(R.id.Fare);
        rider = findViewById(R.id.RiderUserName);
        toolbar = findViewById(R.id.reviewToolbar);
        accept_pay_button = (Button)findViewById(R.id.accept_pay_button);
        accept_pay_button.setClickable(false);
        profileImage = findViewById(R.id.profile_image);

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
        driverUserDB = new UserDB(UserType.DRIVER, userID);

//        driverUserDB = new UserDB(UserType.DRIVER, userID) {
//            @Override
//            public void userDataLoaded(){;}
//
//        };

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RideReview.this, DriverHome.class));
                finish();
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = riderUserDB.getName();
                String email = riderUserDB.getEmail();
                String phone = riderUserDB.getPhone();
                // Pass data to Profile Fragment
                RiderProfileFragment dialog = RiderProfileFragment.newInstance(user, email, phone);
                dialog.show(getSupportFragmentManager(), "My Profile Fragment");
            }
        });

    }

    private void updateView() {
        DataList = driver.getActiveRequests();
        if (isFinished || !driver.isDataLoaded()){
            return;
        }
        rideReq = DataList.get(position);
        riderUserDB = new UserDB(UserType.RIDER, rideReq.getUser());
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

        if (!rideReq.isRideAccepted()){
            accept_pay_button.setText("Accept");
            accept_pay_button.setClickable(true);
            accept_pay_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    driver.getActiveRequests().get(position).setRideAccepted(true);
                    driver.updateRequest(position);
                }
            });
        }

        else
        {
            if (!rideReq.isRideConfirmAccepted()) {
                accept_pay_button.setText("Ride is not confirmed!");
                accept_pay_button.setClickable(false);
            }
            else { //ride is accepted and confirmed
                if (!rideReq.isRideCompleted()) {
                    accept_pay_button.setText("Make ride complete");
                    accept_pay_button.setClickable(true);
                    accept_pay_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            driver.getActiveRequests().get(position).setRideCompleted(true);
                            driver.updateRequest(position);
                        }
                    });
                }
                else { //ride is accepted and confirmed and completed
                    if (!rideReq.isRidePaid()) {
                        accept_pay_button.setText("Get Money");
                        accept_pay_button.setClickable(true);
                        accept_pay_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent i = new Intent(RideReview.this, QR_Scan.class);
//                                startActivity(i);
                                IntentIntegrator integrator =  new IntentIntegrator(RideReview.this);
                                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                                integrator.setPrompt("Scan the QR code from Rider");
                                integrator.setCameraId(0);
                                integrator.setBeepEnabled(false);
                                integrator.setBarcodeImageEnabled(false);
                                integrator.initiateScan();
                            }
                        });
                    }
                    else { //ride is accepted and confirmed and completed and paid
                        accept_pay_button.setText("Ride is paid");
                        accept_pay_button.setClickable(false);
                    }
                }
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents() == null){
            Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
        }
        else{
            String cost = result.getContents();
            Toast.makeText(this, cost, Toast.LENGTH_LONG).show();
            if (Double.valueOf(cost) >= Double.valueOf(driver.getActiveRequests().get(position).getCost())){
                driver.getActiveRequests().get(position).setRidePaid(true);
                driver.updateRequest(position);
                driverUserDB.increaseBalance( Double.valueOf(cost));
                Long rating = driver.getActiveRequests().get(position).getRiderRating();
                driverUserDB.updateReviews(rating);
                driverUserDB.push();
                UserDB riderUser = new UserDB(UserType.RIDER,driver.getActiveRequests().get(position).getUser());
                riderUser.decreaseBalance(Double.valueOf(cost));
                riderUser.push();
            }
            else
                Toast.makeText(this, "Payment was not successful", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
