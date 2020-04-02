package com.example.easyride.ui.driver;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.map.MarkerHandler;
import com.example.easyride.ui.NotificationModel;
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

import java.util.Objects;


/**
 * The Activity will be called when the User want to see the details of a RideRequest,
 * and maybe accept the RideRequest
 * @author T22
 * @version 1.0
 */

public class AcceptRequestActivity extends AppCompatActivity {

    private TextView from, to, cost;
    private String mFrom, mTo, mCost, mRider;
    private String user, email, phone;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    Button riderProfile, mAccept;
    private boolean isMapLoaded = false;
    private boolean isRouteShown = false;
    private boolean isFinished;
    private Driver driver;
    MarkerHandler mh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drive);

        from = findViewById(R.id.from_text);
        to = findViewById(R.id.to_text);
        cost = findViewById(R.id.cost_text);
        riderProfile = findViewById(R.id.rider_button);
        mAccept = findViewById(R.id.accept_pay_button);

        // init database
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        // ActionBAr
        getSupportActionBar().setTitle("Ride Request Details");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
        Places.initialize(getApplicationContext(),getString(R.string.api_key));
        Intent intent = getIntent();

        // Set up variable
        mFrom = getIntent().getStringExtra("pickUpLocation");
        mTo = getIntent().getStringExtra("destination");
        mCost = getIntent().getStringExtra("fare");
        mRider = getIntent().getStringExtra("rider");

        // Set up TextView
        from.setText(mFrom);
        to.setText(mTo);
        cost.setText(mCost);


        // Button to see Rider Profile
        // invoke RiderProfileFragment whenever the button is clicked
        riderProfile.setOnClickListener(new View.OnClickListener() {
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
                                        RiderProfileFragment dialog = RiderProfileFragment.newInstance(user, email, phone);
                                        dialog.show(getSupportFragmentManager(), "My Profile Fragment");
                                    }
                                }
                            }
                        });
            }
        });

        // Button to Accept RideRequest
        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = getIntent().getStringExtra("ID");
                assert ID != null;
                db.collection("RideRequest").document(ID).update("rideAccepted", true);
                final String driver_email = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
                db.collection("RideRequest").document(ID).update("driverUserName",
                        driver_email);
                final String[] id = new String[1];
                db.collection("rider").whereEqualTo("Email", mRider)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                        NotificationModel notificationModel = new NotificationModel(
                                                mFrom + " to " + mTo, driver_email);
                                        //Log.e("This is the id:", id[0]);

                                        db.collection("rider").document(doc.getId()).collection("notification")
                                        .document().set(notificationModel);

                                    }
                                }
                            }
                        });
                startActivity(new Intent(getApplicationContext(), DriverHome.class));
                finish();
            }
        });
    }
    private void updateView() {
        if (isFinished || !driver.isDataLoaded()){
            return;
        }
        if (isMapLoaded && !isRouteShown){
            LatLng startLatLang = new LatLng(rideReq.getStartPoint().getLatitude(), rideReq.getStartPoint().getLongitude());
            LatLng endLatLang = new LatLng(rideReq.getEndPoint().getLatitude(), rideReq.getEndPoint().getLongitude());
            mh.setStartLatLang(startLatLang);
            mh.setEndLatLang(endLatLang);
            mh.showMarkers();
            isRouteShown = true;
        }
    }
}
