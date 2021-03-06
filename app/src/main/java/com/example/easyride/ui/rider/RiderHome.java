package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.data.model.Rider;
import com.example.easyride.map.MapsActivity;
import com.example.easyride.map.Route;
import com.example.easyride.user_profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

// RIDER HOME. THE FIRST PAGE YOU SHOULD SEE WHEN YOU SIGN IN AS A RIDER.
// Handles the rider home screen to display and navigate between active requests, as well as
// allowing users to add a new request.

public class RiderHome extends AppCompatActivity {

  private ListView LV;
  public ArrayAdapter<Ride> rideAdapter;
  public ArrayList<Ride> DataList;
  private HashMap<Integer, Integer> filteredToOriginal;
  private FirebaseUser user;
  private String riderEmail;
  private Rider rider;
  private ArrayList<Ride> filteredDataList;
  public static int maxRiderActiveRequests = 10;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rider_home);

    getSupportActionBar().setTitle("Current Active Requests");

    LV = findViewById(R.id.ride_list);

    DataList = new ArrayList<>();
    DataList.clear();
    //SingleRide instance = SingleRide.getInstance();
    //DataList = instance.getRide();
    //TODO get the current list of ride requests by user
    //DataList.add(new Ride("testFrom", "testTo", "10", "USER")); // Added test item.
    user = FirebaseAuth.getInstance().getCurrentUser();
    // assert user != null;
    riderEmail = user.getEmail();
//        Rider alright = Rider.getInstance(new EasyRideUser(userID));
    rider = new Rider(new EasyRideUser(riderEmail)) {
      @Override
      public void onDataLoaded() {
        refresh();
      }
    };
    EasyRideUser user = rider.getCurrentRiderInfo();
    if (user.getDisplayName() != null) {
      Log.e("HEYYYY", user.getDisplayName());
    }
    rider.updateList();

    // EDIT ITEM FROM ARRAY LIST
    LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String docID = filteredDataList.get(position).getID();
        Intent i = new Intent(view.getContext(), EditRide.class);
        i.putExtra("position", filteredToOriginal.get(position));
        i.putExtra("docID", docID);
        startActivity(i);
      }
    });

    // onClickListener for FloatingActionButton
    FloatingActionButton add_ride_button = findViewById(R.id.add_ride_button);
    add_ride_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (rider.getActiveRequests().size() >= maxRiderActiveRequests){
          Toast.makeText(RiderHome.this, "You cannot have more than one active request!", Toast.LENGTH_LONG).show();
          return;
        }
        Intent i = new Intent(v.getContext(), MapsActivity.class);
        startActivity(i);
      }
    });
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    rider.updateList();
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
        Intent i = new Intent(RiderHome.this, user_profile.class);
        String ID = user.getUid();
        i.putExtra("mode", "rider");
        i.putExtra("email", riderEmail);
        startActivity(i);
        break;
      }
      case R.id.action_home: {
        Intent i = new Intent(RiderHome.this, RiderHome.class);
        startActivity(i);
        break;
      }
      case R.id.action_logout: {
        Rider.clear();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent i = new Intent(RiderHome.this, MainActivity.class);
        startActivity(i);
        break;
      }
    }
    return true;
  }

  public void refresh() {
    DataList = rider.getActiveRequests();
    filteredDataList = new ArrayList<Ride>();
    filteredToOriginal = new HashMap<Integer, Integer>();
    Ride ride;
    int j =0;
    for (int i=0; i<DataList.size(); i++) {
      ride = DataList.get(i);
      if (! ride.isRidePaid()) {
        filteredToOriginal.put(j, i);
        filteredDataList.add(ride);
        j++;
      }
    }
    rideAdapter = new CustomListForRider(this, filteredDataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
    LV.setAdapter(rideAdapter);
  }
}
