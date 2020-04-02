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
    public static int maxRiderActiveRequests = 10;
    private Driver driver;



//    private static final String TAG = "FireLog";
//    private RecyclerView mRequestList;
//    private RideRequestListAdapter rideRequestListAdapter;
//    private RecyclerView.LayoutManager layoutManager;
//    private FirebaseFirestore db;
//    private List<RideRequest> rideRequestList = new ArrayList<>();
//    private FirebaseAuth fAuth;
//    private FirebaseFirestoreSettings settings;
    private boolean offLine = false;


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
                Intent i = new Intent(view.getContext(), RideReview.class);
                i.putExtra("position", filteredToOriginal.get(position));
                startActivity(i);
            }
        });

        // set recycler view properties
//        mRequestList.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        mRequestList.setLayoutManager(layoutManager);

//        rideRequestListAdapter = new RideRequestListAdapter(rideRequestList);

        // set OnclickListener for RecyclerView
        // Passing RideRequest to ride_review activity
        // https://stackoverflow.com/questions/768969/passing-a-bundle-on-startactivity
//        rideRequestListAdapter.setOnClickLisnter(new RideRequestListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                RideRequest ride = rideRequestList.get(position);
//                Intent intent = new Intent(getApplicationContext(), ride_review.class);
//                intent.putExtra("pickUpLocation", ride.getPickupPoint());
//                intent.putExtra("destination", ride.getTargetPoint());
//                intent.putExtra("fare", ride.getCost());
//                intent.putExtra("rider", ride.getRiderUserName());
//                startActivity(intent);
//                Log.d(TAG, "Item Click on item " + position);
//            }
//        });

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

        // show data in Recycler View
//        showData();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        driver.updateList();
    }

//    private void showData() {
//
//
//        // Get the data by geoLocation
//        // geoLocation is based on the cost of the trip, the closer the trip is the cheaper the cost
//        db.collection("RideRequest").whereEqualTo("rideAccepted", true)
//        .whereEqualTo("driverUserName", fAuth.getCurrentUser().getEmail())
//        .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot querySnapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "Listen error", e);
//                    return;
//                }
//
//                for (DocumentChange doc : querySnapshot.getDocumentChanges()) {
//                    if (doc.getType() == DocumentChange.Type.ADDED) {
//                        RideRequest rideRequest = new RideRequest(doc.getDocument().getId(),
//                                doc.getDocument().getString("user"),
//                                doc.getDocument().getString("from"),
//                                doc.getDocument().getString("to"),
//                                doc.getDocument().getString("cost"),
//                                true,
//                                false);
//                        rideRequestList.add(rideRequest);
//                    }
//                        // Set Adapter
//                        mRequestList.setAdapter(rideRequestListAdapter);
//                }
//
//                String source = querySnapshot.getMetadata().isFromCache() ?
//                            "local cache" : "server";
//
//                // Check to see if the application is in offline mode or not
//                // https://stackoverflow.com/questions/49068084/about-firestore-is-there-some-flag-i-can-check-if-the-data-is-on-off-line-data
//                if (source.equals("local cache")) {
//                    offLine = true;
//                }
//                else {
//                    offLine = false;
//                }
//                Log.d(TAG, "Data fetched from " + source);
//            }
//        });
//    }

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
        ArrayList<Ride> filteredDataList = new ArrayList<Ride>();
        filteredToOriginal = new HashMap<Integer, Integer>();
        Ride ride;
        int j =0;
        for (int i=0; i<DataList.size(); i++) {
            ride = DataList.get(i);
            if (! ride.isRidePaid() && ride.getDriverUserName().equals(userID) ) {
                filteredToOriginal.put(j, i);
                filteredDataList.add(ride);
                j++;
            }
        }
        rideAdapter = new CustomListForRider(this, filteredDataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
        LV.setAdapter(rideAdapter);
    }
}
