package com.example.easyride.ui.driver;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.ui.rider.CustomListForRider;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// SEARCH ACTIVITY. WHEN YOU CLICK ON THE SEARCH BUTTON, THIS SHOULD DISPLAY.
// Handles the Search Activity, you are able to search between Active Requests based on Geo Location.
// allowing users to search for the requests.

public class SearchRequestActivity extends AppCompatActivity {

    private Driver driver;
    private String userID;
    private FirebaseUser user;
    private HashMap<Integer, Integer> filteredToOriginal;
    ArrayList<Ride> filteredDataList;
    private ListView LV;
    private ArrayAdapter<Ride> rideAdapter;
    private String queryString = "";
    private static final String TAG = "FireLog";
//    private RecyclerView mRequestList;
//    private RideRequestListAdapter searchRequestAdapter;
//    private RecyclerView.LayoutManager layoutManager;
//    private FirebaseFirestore db;
//    private List<RideRequest> rideRequestList = new ArrayList<>();
//    private FirebaseFirestoreSettings settings;
    private Toolbar toolbar;
    private FusedLocationProviderClient client;
    private ArrayList<Ride> dataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_request);
        LV = findViewById(R.id.search_list);
//        DataList = new ArrayList<>();

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

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), RideReview.class);
                i.putExtra("position", filteredToOriginal.get(position));
                i.putExtra("mode", "search" );
                startActivity(i);
            }
        });

//        // init database
//        db = FirebaseFirestore.getInstance();
//        settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);


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

//        // initial views
//        mRequestList = findViewById(R.id.searchList);
//
//        // set recycler view properties
//        mRequestList.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        mRequestList.setLayoutManager(layoutManager);
//
//        // Set Adapter
//        searchRequestAdapter = new RideRequestListAdapter(rideRequestList);
//        mRequestList.setAdapter(searchRequestAdapter);
//
//        searchRequestAdapter.setOnClickLisnter(new RideRequestListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                RideRequest ride = rideRequestList.get(position);
//                Intent intent = new Intent(getApplicationContext(), AcceptRequestActivity.class);
//                intent.putExtra("ID", ride.getKey());
//                intent.putExtra("pickUpLocation", ride.getPickupPoint());
//                intent.putExtra("destination", ride.getTargetPoint());
//                intent.putExtra("fare", ride.getCost());
//                intent.putExtra("rider", ride.getRiderUserName());
//                startActivity(intent);
//                Log.d(TAG, "Item Click on item " + position);
//                Log.d(TAG, "RiderUserName: " + ride.getRiderUserName());
//            }
//        });
//
//        // show data in Recycler View
//        showData();
    }



    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        driver.updateList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    private void showData() {
//        // Get the data by geoLocation
//        // geoLocation is based on the cost of the trip, the closer the trip is the cheaper the cost
//        db.collection("RideRequest").whereEqualTo("rideAccepted", false)
//        .orderBy("cost", Query.Direction.ASCENDING)
//        .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null)  {
//                    Log.d(TAG, "Error : " + e.getMessage());
//                }
//                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
//                    if (doc.getType() == DocumentChange.Type.ADDED) {
//                        RideRequest rideRequest = new RideRequest(doc.getDocument().getId(),
//                            doc.getDocument().getString("user"),
//                            doc.getDocument().getString("from"),
//                            doc.getDocument().getString("to"),
//                            doc.getDocument().getString("cost"),
//                            doc.getDocument().getBoolean("rideAccepted"),
//                            doc.getDocument().getBoolean("rideCompleted"));
//                        rideRequestList.add(rideRequest);
//                        searchRequestAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });
//    }

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
        searchView.setQueryHint("Search by rider username");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // called when we pressed search button
                if (!query.isEmpty()) {
                    queryString = query;
                }
                else {
                    queryString = "";
                }
                refresh();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // called as and when we type
                if (!newText.isEmpty()) {
                    queryString = newText;
                }
                else {
                    queryString = "";
                }
                    refresh();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private boolean filter(Ride toBeChecked) {

        return  (toBeChecked.getFrom().toLowerCase().contains(queryString.toLowerCase()) && !toBeChecked.isRideAccepted());
    }
//        db.collection("RideRequest").whereEqualTo("user", query.toLowerCase())
//        .whereEqualTo("rideAccepted", false)
//        .get()
//        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                // called when searching is successful
//                rideRequestList.clear();
//                for (DocumentSnapshot doc : task.getResult()) {
//                    RideRequest rideRequest = new RideRequest(doc.getId(),
//                        doc.getString("user"),
//                        doc.getString("from"),
//                        doc.getString("to"),
//                        doc.getString("cost"),
//                        false,
//                        false);
//                    rideRequestList.add(rideRequest);
//                }
//
//                // adapter
//                searchRequestAdapter = new RideRequestListAdapter(rideRequestList);
//                // set adapter
//                mRequestList.setAdapter(searchRequestAdapter);
//
//            }
//        })
//        .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // called when there is any error
//                Toast.makeText(SearchRequestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });


    public void refresh() {
        dataList = driver.getActiveRequests();
        filteredDataList = new ArrayList<Ride>();
        filteredToOriginal = new HashMap<Integer, Integer>();
        Ride ride;
        int j =0;
        for (int i = 0; i< dataList.size(); i++) {
            ride = dataList.get(i);
            if (filter(ride)) {
                filteredToOriginal.put(j, i);
                filteredDataList.add(ride);
                j++;
            }
        }
        rideAdapter = new CustomListForRider(this, filteredDataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
        LV.setAdapter(rideAdapter);
    }
}
