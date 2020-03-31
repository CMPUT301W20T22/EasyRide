package com.example.easyride.ui.driver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.user_profile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Drive Home, the first page you should see when you sign in as a driver.
 * Handles the driver home screen to display and navigate between active requests, as well as
 * allowing users to select and accept a new ride request.
 * @author T22
 * @version 1.0
 */
public class driver_home extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private RecyclerView mRequestList;
    private RideRequestListAdapter rideRequestListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private List<RideRequest> rideRequestList = new ArrayList<>();
    private FloatingActionButton searchBtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestoreSettings settings;
    private boolean connected = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        searchBtn = findViewById(R.id.searchRequestBtn);

        // init database
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        /*
        Driver driver = Driver.getInstance(new EasyRideUser("dumbby"));
        EasyRideUser user = driver.getCurrentDriverInfo();
        Log.d("User: ", user.getDisplayName());
        */
        //ActionBar
        getSupportActionBar().setTitle("Current Accepted Request");

        // initial views
        mRequestList = findViewById(R.id.request_list);

        // set recycler view properties
        mRequestList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRequestList.setLayoutManager(layoutManager);

        rideRequestListAdapter = new RideRequestListAdapter(rideRequestList);


        // set OnclickListener for RecyclerView
        // Passing RideRequest to ride_review activity
        rideRequestListAdapter.setOnClickLisnter(new RideRequestListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                RideRequest ride = rideRequestList.get(position);
                Intent intent = new Intent(getApplicationContext(), ride_review.class);
                intent.putExtra("pickUpLocation", ride.getPickupPoint());
                intent.putExtra("destination", ride.getTargetPoint());
                intent.putExtra("fare", ride.getCost());
                intent.putExtra("rider", ride.getRiderUserName());
                startActivity(intent);
                Log.d(TAG, "Item Click on item " + position);
            }
        });


        // Set onClickListener for the Search Button
        // Go to SearchRequestActivity if the Button is clicked
        // and the app is not on OffLine Mode
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connected = isNetworkAvailable();
                if (connected) {
                    Intent intent = new Intent(driver_home.this, SearchRequestActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(driver_home.this,
                            "You are in Offline Mode right now! You can't search for Request at the moment!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // show data in Recycler View
        showData();

    }


    /**
     * Method to display the data to RecyclerView from the database,
     * depend on the driver, the data displayed on the driver_home will be different.
     */
    public void showData() {

        // Get the data by geoLocation
        // geoLocation is based on the cost of the trip, the closer the trip is the cheaper the cost
        db.collection("RideRequest").whereEqualTo("rideAccepted", true)
                .whereEqualTo("driverUserName", fAuth.getCurrentUser().getEmail())
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen error", e);
                            return;
                        }

                        for (DocumentChange doc : querySnapshot.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                RideRequest rideRequest = new RideRequest(doc.getDocument().getId(),
                                        doc.getDocument().getString("user"),
                                        doc.getDocument().getString("from"),
                                        doc.getDocument().getString("to"),
                                        doc.getDocument().getString("cost"),
                                        true,
                                        false);
                                rideRequestList.add(rideRequest);
                            }
                            // Set Adapter
                            mRequestList.setAdapter(rideRequestListAdapter);
                        }
                    }
                });
    }

    /**
     * Method to check the internet connectivity of the device
     * @return boolean
     */
    // https://stackoverflow.com/questions/9570237/android-check-internet-connection
    // Author: https://stackoverflow.com/users/975292/seshu-vinay
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * Create option Menu for driver_home.
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.navigation_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Method to handle the Menu item onClick Event.
     * For each option, there will be a different result to it.
     * @param item
     * @return boolean
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_account: {
                Intent i = new Intent(driver_home.this, user_profile.class);
                FirebaseUser user = fAuth.getCurrentUser();
                String ID = user.getUid();
                i.putExtra("ID", ID);
                i.putExtra("mode", "driver");
                startActivity(i);
                break;
            }
            case R.id.action_logout: {
                // Sign out of the account
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
            }
            case R.id.action_home: {
                startActivity(new Intent(getApplicationContext(), driver_home.class));
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
