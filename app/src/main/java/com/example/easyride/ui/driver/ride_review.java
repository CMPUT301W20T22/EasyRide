package com.example.easyride.ui.driver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * Allow the driver to Review the Details of the RideRequest he wants to see.
 * Also, he can choose to accept the payment when he finishes the ride.
 * @author T22
 * @version 1.0
 */

public class ride_review extends AppCompatActivity {

    private TextView pickUp, Destination, Fare, rider;
    private ImageView userIcon;
    private MapView mMap; // need to implement map
    private String mPickUp, mDestination, mFare, mRider, user, email;
    private Toolbar toolbar;
    private Button accept_pay_button;
    private FirebaseFirestore db;
    private boolean connected;

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
        accept_pay_button = (Button) findViewById(R.id.accept_pay_button);


        // Set up ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ride Request Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init database
        db = FirebaseFirestore.getInstance();

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


        // Set up rider profile fragment
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("rider").whereEqualTo("Name", mRider)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                        user = doc.getString("Name");
                                        email = doc.getString("Email");
                                        Log.d("Document", doc.getId() + " => " + doc.getData());
                                        // Pass data to Profile Fragment
                                        RiderProfileFragment dialog = RiderProfileFragment.newInstance(user, email, null);
                                        dialog.show(getSupportFragmentManager(), "My Profile Fragment");
                                    }
                                }
                            }
                        });
            }
        });

        // Accept payment button onClickListener
        accept_pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connected = isNetworkAvailable();
                if (connected) {
                    Intent i = new Intent(ride_review.this, QR_Scan.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(ride_review.this,
                            "You are in Offline Mode right now! You can't accept the payment at the moment!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });


        // Toolbar navigation listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ride_review.this, driver_home.class));
                finish();
            }
        });
    }

    /**
     * Method to check the internet connectivity of the device
     * @return boolean
     */
    // https://stackoverflow.com/questions/9570237/android-check-internet-connection
    // Author: https://stackoverflow.com/users/975292/seshu-vinay
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
