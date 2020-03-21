package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.map.MapsActivity;
import com.example.easyride.ui.login.LoginActivity;
import com.example.easyride.user_profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

// RIDER HOME. THE FIRST PAGE YOU SHOULD SEE WHEN YOU SIGN IN AS A RIDER.
// Handles the rider home screen to display and navigate between active requests, as well as
// allowing users to add a new request.

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
        SingleRide instance = SingleRide.getInstance();
        DataList = instance.getRide();
        //TODO get the current list of ride requests by user
        //DataList.add(new Ride("testFrom", "testTo", "10", "USER")); // Added test item.

        rideAdapter = new custom_list_for_rider(this, DataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
        LV.setAdapter(rideAdapter);




        // EDIT ITEM FROM ARRAY LIST
        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), edit_ride.class);
                i.putExtra("position", position);
                startActivity(i);

            }
        });


        // DELETE ITEM ON LONG CLICK.
        LV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DataList.remove(position);
                rideAdapter.notifyDataSetChanged();
                SingleRide instance = SingleRide.getInstance();
                instance.removeAt(position);
                Toast.makeText(rider_home.this, "Item Deleted", Toast.LENGTH_LONG).show();

                return true;
            }
        });


        // onClickListener for FloatingActionButton
        FloatingActionButton add_ride_button = findViewById(R.id.add_ride_button);
        add_ride_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MapsActivity.class);
                startActivity(i);

            }
        });





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflating driver_menu
        getMenuInflater().inflate(R.menu.navigation_menu, menu);





        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_account: {
                Intent i = new Intent(rider_home.this, user_profile.class);
                startActivity(i);
                break;
            }
            case R.id.action_home: {
                Intent i = new Intent(rider_home.this, rider_home.class);
                startActivity(i);
                break;
            }
            case R.id.action_logout: {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(rider_home.this, LoginActivity.class);
                startActivity(i);
                break;
            }

        }
        return true;
    }

}
