package com.example.easyride.ui.driver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import com.example.easyride.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * Search Activity. When you click on the Search Button, this should display.
 * Handles the Search Activity, you are able to search between Active Requests based on Geo Location.
 * allowing users to search for the requests.
 * @author T22
 * @version 1.0
 */

public class SearchRequestActivity extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private RecyclerView mRequestList;
    private RideRequestListAdapter searchRequestAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private List<RideRequest> rideRequestList = new ArrayList<>();
    private FirebaseFirestoreSettings settings;
    private Toolbar toolbar;
    private FusedLocationProviderClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_request);


        // init database
        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        // GET CURRENT LOCATION OF USER
        //client = LocationServices.getFusedLocationProviderClient(this);
        //client.getLastLocation();

        /*
        Driver driver = Driver.getInstance(new EasyRideUser("dumbby"));
        EasyRideUser user = driver.getCurrentDriverInfo();
        Log.d("User: ", user.getDisplayName());
        */

        toolbar = findViewById(R.id.searchToolBar);
        // Add Support ActionBar
        // https://stackoverflow.com/questions/31311612/how-to-catch-navigation-icon-click-on-toolbar-from-fragment
        // Author: https://stackoverflow.com/users/6645645/badr-el-amrani
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search for Active Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchRequestActivity.this, DriverHome.class));
                finish();
            }
        });

        // initial views
        mRequestList = findViewById(R.id.searchList);

        // set recycler view properties
        mRequestList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRequestList.setLayoutManager(layoutManager);

        // Set Adapter
        searchRequestAdapter = new RideRequestListAdapter(rideRequestList);
        mRequestList.setAdapter(searchRequestAdapter);

        // Set on item onClickListener to the list
        searchRequestAdapter.setOnClickLisnter(new RideRequestListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                RideRequest ride = rideRequestList.get(position);
                Intent intent = new Intent(getApplicationContext(), AcceptRequestActivity.class);
                intent.putExtra("ID", ride.getKey());
                intent.putExtra("pickUpLocation", ride.getPickupPoint());
                intent.putExtra("destination", ride.getTargetPoint());
                intent.putExtra("fare", ride.getCost());
                intent.putExtra("rider", ride.getRiderUserName());
                startActivity(intent);
                Log.d(TAG, "Item Click on item " + position);
                Log.d(TAG, "RiderUserName: " + ride.getRiderUserName());
            }
        });

        // show data in Recycler View
        showData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Method to display the data to RecyclerView from the database,
     * depend on the driver, the data displayed on the activity will be different.
     */
    public void showData() {

        // Get the data by geoLocation
        // geoLocation is based on the cost of the trip, the closer the trip is the cheaper the cost
        db.collection("RideRequest").whereEqualTo("rideAccepted", false)
        .orderBy("cost", Query.Direction.ASCENDING)
        .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)  {
                    Log.d(TAG, "Error : " + e.getMessage());
                }
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        RideRequest rideRequest = new RideRequest(doc.getDocument().getId(),
                            doc.getDocument().getString("user"),
                            doc.getDocument().getString("from"),
                            doc.getDocument().getString("to"),
                            doc.getDocument().getString("cost"),
                            doc.getDocument().getBoolean("rideAccepted"),
                            doc.getDocument().getBoolean("rideCompleted"));
                        rideRequestList.add(rideRequest);
                        searchRequestAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    /**
     * Create Search Bar for Search Activity.
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflating driver_menu
        getMenuInflater().inflate(R.menu.driver_menu, menu);

        // SearchView and implement the search bar
        // https://stackoverflow.com/questions/47093176/android-searchview-casting-exception
        // Author : https://stackoverflow.com/users/7666442/nilesh-rathod

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search by rider username");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // called when we pressed search button
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // called as and when we type
                if (newText.isEmpty()) {
                    rideRequestList.clear();
                    showData();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * method created to do the searching in the Database
     * based on the query the driver input in the Search Bar
     * and display it on the RecyclerView so that the driver can see the results.
     * @param query
     */
    public void searchData(String query) {
        // search data
        db.collection("RideRequest").whereEqualTo("user", query.toLowerCase())
        .whereEqualTo("rideAccepted", false)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // called when searching is successful
                rideRequestList.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    RideRequest rideRequest = new RideRequest(doc.getId(),
                        doc.getString("user"),
                        doc.getString("from"),
                        doc.getString("to"),
                        doc.getString("cost"),
                        false,
                        false);
                    rideRequestList.add(rideRequest);
                }

                // adapter
                searchRequestAdapter = new RideRequestListAdapter(rideRequestList);
                // set adapter
                mRequestList.setAdapter(searchRequestAdapter);

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // called when there is any error
                Toast.makeText(SearchRequestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
