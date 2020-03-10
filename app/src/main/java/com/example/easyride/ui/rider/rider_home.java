package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/*
    This class is used to handle the rider home screen to display and navigate
    between active requests, as well as allowing users to add a new request.

 */

public class rider_home extends AppCompatActivity {

    private ListView LV;
    private ArrayAdapter<Ride> rideAdapter;
    private ArrayList<Ride> DataList;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);



        LV = findViewById(R.id.ride_list);

        DataList = new ArrayList<>();
        rideAdapter = new CustomList(this, DataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.

        LV.setAdapter(rideAdapter);



        // ADD RIDE REQUEST BUTTON
        FloatingActionButton add_ride_button = findViewById(R.id.add_ride_button);
        add_ride_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), add_ride.class);
                startActivity(i);

            }
        });





    }
}
