package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.user_profile;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

// Handles viewing of a ride request. Don't need to be able to edit the request
public class edit_ride extends AppCompatActivity {

    public static ArrayList<Ride> DataList;
    private TextView from, to, cost, distance;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);
        Intent intent = getIntent();
        final int position = intent.getIntExtra("position", 0);
        DataList = new ArrayList<>();
        SingleRide instance = SingleRide.getInstance();
        DataList = instance.getRide();
        Ride rideReq = DataList.get(position);
        String ride_distance = rideReq.getDistance();
        String ride_distance_short = ride_distance.substring(0, 5);
        String ride_cost= rideReq.getCost();
        String ride_cost_short = ride_cost.substring(0, 5);

        from = findViewById(R.id.from_text);
        to = findViewById(R.id.to_text);
        cost = findViewById(R.id.cost_text);
        distance = findViewById(R.id.distance);

        from.setText(rideReq.getFrom());
        to.setText(rideReq.getTo());
        cost.setText(ride_cost_short);
        distance.setText(ride_distance_short);

        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QR_Pay.class);
                startActivity(i);
            }




        });


        Button viewProfile = findViewById(R.id.profile_button);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), user_profile.class);
                startActivity(i);
            }




        });

        Button delete = findViewById(R.id.delete_button);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleRide instance = SingleRide.getInstance();
                instance.removeAt(position);
                Intent i = new Intent(getApplicationContext(), rider_home.class);
                startActivity(i);
            }
        });

    }
}
