package com.example.easyride.ui.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.data.model.Rider;
import com.example.easyride.ui.rider.CustomListForRider;
import com.example.easyride.user_profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

// DRIVER HOME. THE FIRST PAGE YOU SHOULD SEE WHEN YOU SIGN IN AS A DRIVER.
// Handles the driver home screen to display and navigate between active requests, as well as
// allowing users to select and accept a new ride request.

public class DriverHome extends AppCompatActivity {

    private ListView LV;
    private ArrayAdapter<Ride> rideAdapter;
    private HashMap<Integer, Integer> filteredToOriginal;
    private FirebaseUser user;
    private String userID;
    private ArrayList<Ride> DataList;
    public static int maxDriverActiveRequests = 10;
    private Driver driver;
    private boolean offLine = false;
    private ArrayList<Ride> filteredDataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        FloatingActionButton searchBtn = findViewById(R.id.searchRequestBtn);
        getSupportActionBar().setTitle("Current Accepted Request");
        LV = findViewById(R.id.request_list);
        DataList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getEmail();
        driver = new Driver(new EasyRideUser(userID)) {
            @Override
            public void onDataLoaded() {
                refresh();
            }
        };
        EasyRideUser user = driver.getCurrentDriverInfo();
        driver.updateList();
        refresh();

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String docID = filteredDataList.get(position).getID();
                Intent i = new Intent(view.getContext(), RideReview.class);
                i.putExtra("position", filteredToOriginal.get(position));
                i.putExtra("docID", docID);
                startActivity(i);
            }
        });

        // Set OnclickListener for Search Btn
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!offLine) {
                    Intent intent = new Intent(DriverHome.this, SearchRequestActivity.class);
                    startActivity(intent);
//                    finish();
                } else {
                    Toast.makeText(DriverHome.this,
                        "You are in Offline Mode right now! You can't search for Request at the moment!", Toast.LENGTH_SHORT)
                        .show();
                }
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        driver.updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.navigation_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account: {
                Intent i = new Intent(DriverHome.this, user_profile.class);
                String ID = user.getUid();
                i.putExtra("mode", "driver");
                i.putExtra("ID", ID);
                i.putExtra("email",user.getEmail());
                startActivity(i);
                break;
            }
            case R.id.action_home: {
                Intent i = new Intent(DriverHome.this, DriverHome.class);
                startActivity(i);
                break;
            }
            case R.id.action_logout: {
                Rider.clear();
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signOut();
                Intent i = new Intent(DriverHome.this, MainActivity.class);
                startActivity(i);
                break;
            }
        }
        return true;
    }

    public void refresh() {
        DataList = driver.getActiveRequests();
        filteredDataList = new ArrayList<Ride>();
        filteredToOriginal = new HashMap<Integer, Integer>();
        Ride ride;
        int j =0;
        for (int i=0; i<DataList.size(); i++) {
            ride = DataList.get(i);
            if (ride.getDriverUserName()==null )
                continue;
            if (! ride.isRidePaid() && ride.getDriverUserName().equals(userID) ) {
                filteredToOriginal.put(j, i);
                filteredDataList.add(ride);
                j++;
            }
        }
        rideAdapter = new CustomListForDriver(this, filteredDataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
        LV.setAdapter(rideAdapter);
    }
}