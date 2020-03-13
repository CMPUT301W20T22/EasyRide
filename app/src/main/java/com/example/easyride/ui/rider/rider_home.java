package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/*
    This class is used to handle the rider home screen to display and navigate
    between active requests, as well as allowing users to add a new request.

 */

public class rider_home extends AppCompatActivity {

    public static ListView LV;
    public static ArrayAdapter<Ride> rideAdapter;
    public static ArrayList<Ride> DataList;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_home);


        LV = findViewById(R.id.ride_list);

        DataList = new ArrayList<>();
        DataList.add(new Ride("testFrom", "testTo", "10", "USER")); // Added test item.

        rideAdapter = new CustomList(this, DataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
        LV.setAdapter(rideAdapter);






        // EDIT ITEM FROM ARRAY LIST
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), edit_ride.class);
                startActivity(i);

            }
        });


        // DELETE ITEM ON LONG CLICK.
        LV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DataList.remove(position);
                rideAdapter.notifyDataSetChanged();

                Toast.makeText(rider_home.this, "Item Deleted", Toast.LENGTH_LONG).show();

                return true;
            }
        });


        // onClickListener for FloatingActionButton
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
