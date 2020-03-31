package com.example.easyride.ui.driver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.easyride.R;
import com.google.android.gms.maps.MapView;

/**
 * Allow the driver to Review the Details of the RideRequest he wants to see.
 * Also, he can choose to accept the payment when he finishes the ride.
 * @author T22
 * @version 1.0
 */

public class ride_review extends AppCompatActivity {

    private TextView pickUp, Destination, Fare, rider;
    private MapView mMap; // need to implement map
    private String mPickUp, mDestination, mFare, mRider;
    private Toolbar toolbar;
    private Button accept_pay_button;
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
        accept_pay_button = (Button)findViewById(R.id.accept_pay_button);
        // Set up ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ride Request Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
