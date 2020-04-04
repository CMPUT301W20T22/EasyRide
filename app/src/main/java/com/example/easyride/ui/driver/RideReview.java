package com.example.easyride.ui.driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.data.model.Rider;
import com.example.easyride.map.MarkerHandler;
import com.example.easyride.ui.rider.QR_Pay;
import com.example.easyride.ui.rider.RiderHome;
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

import java.util.ArrayList;
import java.util.Objects;

public class RideReview extends AppCompatActivity implements OnMapReadyCallback {

    private TextView pickUp, Destination, Fare, rider;
    private String mPickUp, mDestination, mFare, mRider, user, email, phone;
    private Toolbar toolbar;
    private Button accept_pay_button;
    MarkerHandler mh;
    private ImageView userIcon;
    private boolean isMapLoaded = false;
    private boolean isRouteShown = false;
    private Ride rideReq;
    private Driver driver;
    private int position;
    private boolean isFinished;
    private ArrayList<Ride> DataList;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_review);

        pickUp = findViewById(R.id.pickUpLocation);
        Destination = findViewById(R.id.Destination);
        Fare = findViewById(R.id.Fare);
        rider = findViewById(R.id.RiderUserName);
        toolbar = findViewById(R.id.reviewToolbar);
        userIcon = findViewById(R.id.riderIcon);
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


        //accept_pay_button.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        Intent i = new Intent(RideReview.this, QR_Scan.class);
        //        startActivity(i);
        //    }
       // });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RideReview.this, DriverHome.class));
                finish();
            }
        });

        // Set up variables
        //mPickUp = getIntent().getStringExtra("pickUpLocation");
        //mDestination = getIntent().getStringExtra("destination");
        //mFare = getIntent().getStringExtra("fare");
        //mRider = getIntent().getStringExtra("rider");

        // Set up TextView
        pickUp.setText(mPickUp);
        Destination.setText(mDestination);
        Fare.setText(mFare);
        rider.setText(mRider);

        db = FirebaseFirestore.getInstance();
        // Set up rider profile fragment
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("rider").whereEqualTo("Email", mRider)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                        user = doc.getString("Name");
                                        email = doc.getString("Email");
                                        phone = doc.getString("Phone");
                                        Log.d("Document", doc.getId() + " => " + doc.getData());
                                        // Pass data to Profile Fragment

                                        String emailLink = "mailto:" + email;
                                        String phoneLink = "tel:" + phone;

                                        int emailLen = email.length();
                                        int phoneLen = phone.length();

                                        SpannableString riderSpanEmail = new SpannableString(email);
                                        riderSpanEmail.setSpan(new URLSpan(emailLink), 0, emailLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        SpannableString riderSpanPhone = new SpannableString(phone);
                                        riderSpanPhone.setSpan(new URLSpan(phoneLink), 0, phoneLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                                        RiderProfileFragment dialog = RiderProfileFragment.newInstance(user, email, phone);
                                        dialog.show(getSupportFragmentManager(), "My Profile Fragment");

                                    }
                                }
                            }
                        });
            }
        });

    }

    private void rideCompleteDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This will enable payment");
        // Set up the input
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                driver.getActiveRequests().get(position).setRideCompleted(true);
                driver.updateRequest(position);
            }
        });
        builder.show();
    }

    private void acceptButtonSetting(){
        if(rideReq.isRideConfirmAccepted() && !rideReq.isRideCompleted()) {
            String message = "You may commence thy ride" +"!";
            Toast.makeText(RideReview.this, message, Toast.LENGTH_LONG).show();
            accept_pay_button.setText("Press to complete the ride");
            accept_pay_button.setClickable(true);
            accept_pay_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rideCompleteDialog();
                }
            });
        }else if (!rideReq.isRideConfirmAccepted() && !rideReq.isRideConfirmAccepted()){
            accept_pay_button.setText("Waiting for the rider to confirm");
            accept_pay_button.setClickable(false);
        }else if (!rideReq.isRidePaid() && rideReq.isRideCompleted()){
            accept_pay_button.setText("Press to accept payment");
            //accept_pay_button.setTextColor(Color.GREEN);
            accept_pay_button.setClickable(true);
            accept_pay_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(RideReview.this, QR_Scan.class);
                    i.putExtra("position", position);
                    startActivity(i);
                }
            });
        }else{
            accept_pay_button.setText("Not Accepted");
            accept_pay_button.setClickable(false);
        }
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
        String emailLink = "mailto:" + mRider;

        int emailLen = mRider.length();

        SpannableString riderSpanEmail = new SpannableString(mRider);
        riderSpanEmail.setSpan(new URLSpan(emailLink), 0, emailLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);



        // Set up TextView
        pickUp.setText(mPickUp);
        Destination.setText(mDestination);
        Fare.setText(mFare);
        rider.setText(riderSpanEmail);

        rider.setMovementMethod(LinkMovementMethod.getInstance());

        if (isMapLoaded && !isRouteShown){
            LatLng startLatLang = new LatLng(rideReq.getStartPoint().getLatitude(), rideReq.getStartPoint().getLongitude());
            LatLng endLatLang = new LatLng(rideReq.getEndPoint().getLatitude(), rideReq.getEndPoint().getLongitude());
            mh.setStartLatLang(startLatLang);
            mh.setEndLatLang(endLatLang);
            mh.showMarkers();
            isRouteShown = true;
        }
        acceptButtonSetting();
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
