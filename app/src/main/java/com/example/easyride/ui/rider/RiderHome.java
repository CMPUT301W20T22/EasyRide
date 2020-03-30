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
import com.example.easyride.data.model.Rider;
import com.example.easyride.map.MapsActivity;
import com.example.easyride.user_profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;

// RIDER HOME. THE FIRST PAGE YOU SHOULD SEE WHEN YOU SIGN IN AS A RIDER.
// Handles the rider home screen to display and navigate between active requests, as well as
// allowing users to add a new request.

public class RiderHome extends AppCompatActivity {

  public ListView LV;
  public ArrayAdapter<Ride> rideAdapter;
  public ArrayList<Ride> DataList;
  private FirebaseUser user;
  private String userID;
  private Rider alright;

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
    userID = user.getEmail();
//        Rider alright = Rider.getInstance(new EasyRideUser(userID));
    alright = new Rider(new EasyRideUser(userID)) {
      @Override
      public void onDataLoaded() {
        updateList2();
      }
    };
    EasyRideUser user = alright.getCurrentRiderInfo();
    if (user.getDisplayName() != null) {
      Log.e("HEYYYY", user.getDisplayName());
    }
    alright.updateList();
    DataList = alright.getActiveRequests();
        /*new Thread(new Runnable() {
            public void run() {
                FirebaseFirestore db;
                db = FirebaseFirestore.getInstance();
                // a potentially time consuming task
                Rider alright = Rider.getInstance(new EasyRideUser("man@man.ca"));
                EasyRideUser user = alright.getCurrentRiderInfo();
                Task<QuerySnapshot> hi = db.collection("RideRequest")
                        .whereEqualTo("user", user.getUserId())
                        .get();
                Log.e("rider_home", Boolean.toString(hi.isSuccessful()));

            }
        }).start();*/

    rideAdapter = new CustomListForRider(this, DataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
    LV.setAdapter(rideAdapter);


    // EDIT ITEM FROM ARRAY LIST
    LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(view.getContext(), EditRide.class);
        i.putExtra("position", position);
        startActivity(i);

      }
    });


        /*// DELETE ITEM ON LONG CLICK.
        LV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DataList.remove(position);
                rideAdapter.notifyDataSetChanged();
                SingleRide instance = SingleRide.getInstance();
                instance.removeAt(position);
                Toast.makeText(RiderHome.this, "Item Deleted", Toast.LENGTH_LONG).show();

                return true;
            }
        });
        */
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
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
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
        i.putExtra("ID", ID);
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

  public void updateList2() {
    DataList = alright.getActiveRequests();
    rideAdapter = new CustomListForRider(this, DataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
    LV.setAdapter(rideAdapter);
  }
}
