package com.example.easyride.ui.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.ui.login.LoginActivity;
import com.google.android.gms.maps.MapView;

public class ride_review extends AppCompatActivity {

    private TextView pickUp, Destination, Fare, rider;
    private MapView mMap; // need to implement map
    private String mPickUp, mDestination, mFare, mRider;
    private Toolbar toolbar;
    private Button accept_pay_button;

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

        accept_pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ride_review.this, QR_Scan.class);
                startActivity(i);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ride_review.this, driver_home.class));
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
}
