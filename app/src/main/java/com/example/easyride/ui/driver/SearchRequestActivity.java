package com.example.easyride.ui.driver;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;


import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.ui.login.LoginActivity;
import com.example.easyride.ui.rider.Ride;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// DRIVER HOME. THE FIRST PAGE YOU SHOULD SEE WHEN YOU SIGN IN AS A DRIVER.
// Handles the driver home screen to display and navigate between active requests, as well as
// allowing users to select and accept a new ride request.



public class SearchRequestActivity extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private RecyclerView mRequestList;
    private RideRequestListAdapter searchRequestAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private List<RideRequest> rideRequestList = new ArrayList<>();
    private String mode;
    FirebaseAuth fAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_request);

        // init database
        db = FirebaseFirestore.getInstance();
        Driver driver = Driver.getInstance(new EasyRideUser("dumbby"));
        EasyRideUser user = driver.getCurrentDriverInfo();
        Log.d("User: ", user.getDisplayName());

        // initial views
        mRequestList = findViewById(R.id.request_list);

        // set recycler view properties
        mRequestList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRequestList.setLayoutManager(layoutManager);

        // Set Adapter
        searchRequestAdapter = new RideRequestListAdapter(rideRequestList);
        mRequestList.setAdapter(searchRequestAdapter);


        // show data in Recycler View
        showData();

    }

    private void showData() {


        // Get the data by geoLocation
        // geoLocation is based on the cost of the trip, the closer the trip is the cheaper the cost
        db.collection("RideRequest").whereEqualTo("isAccepted", false)
                .orderBy("cost", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error : " + e.getMessage());
                }
                else {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            RideRequest rideRequest = new RideRequest(doc.getDocument().getString("riderUserName"),
                                    doc.getDocument().getString("pickupPoint"),
                                    doc.getDocument().getString("targetPoint"),
                                    doc.getDocument().getLong("cost").intValue(),
                                    false,
                                    false);

                            rideRequestList.add(rideRequest);
                            searchRequestAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflating driver_menu
        getMenuInflater().inflate(R.menu.driver_menu, menu);
        /*
         SearchView and implement the search bar
         https://stackoverflow.com/questions/47093176/android-searchview-casting-exception
        */
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
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

    private void searchData(String query) {
        // search data
        db.collection("RideRequest").whereEqualTo("riderUserName", query.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // called when searching is successful
                        rideRequestList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            RideRequest rideRequest = new RideRequest(doc.getString("riderUserName"),
                                    doc.getString("pickupPoint"),
                                    doc.getString("targetPoint"),
                                    doc.getLong("cost").intValue(),
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
