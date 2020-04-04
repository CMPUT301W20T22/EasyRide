package com.example.easyride.ui.driver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.map.MarkerHandler;
import com.example.easyride.map.Route;
import com.example.easyride.ui.rider.CustomListForRider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
    private GeoPoint deviceLocation;
    private GeoPoint defaultLocation = new GeoPoint(53.5232, -113.5263);
    private Toolbar toolbar;
    private FusedLocationProviderClient client;
    private ArrayList<Ride> dataList;
    private ArrayList<Ride> sortedDataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_request);
        LV = findViewById(R.id.search_list);
        toolbar = findViewById(R.id.searchToolBar);

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

        /*
        Driver driver = Driver.getInstance(new EasyRideUser("dumbby"));
        EasyRideUser user = driver.getCurrentDriverInfo();
        Log.d("User: ", user.getDisplayName());
        */

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

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), RideReview.class);
                String docid = sortedDataList.get(position).getID();
                i.putExtra("position", filteredToOriginal.get(position));
                i.putExtra("docID", docid);
                i.putExtra("mode", "search");
                startActivity(i);
            }
        });
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
                } else {
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
                } else {
                    queryString = "";
                }
                refresh();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private boolean filter(Ride toBeChecked) {

        return (toBeChecked.getFrom().toLowerCase().contains(queryString.toLowerCase()) && !toBeChecked.isRideAccepted());
    }

    public void refresh() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            deviceLocation = defaultLocation;
        } else {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            deviceLocation = new GeoPoint(latitude, longitude);
        }

        dataList = driver.getActiveRequests();
        filteredDataList = new ArrayList<Ride>();
        filteredToOriginal = new HashMap<Integer, Integer>();

        Ride ride;
        int j = 0;
        for (int i = 0; i < dataList.size(); i++) {
            ride = dataList.get(i);
            if (filter(ride)) {
                filteredToOriginal.put(j, i);
                filteredDataList.add(ride);
                j++;
            }
        }
        sortedDataList = filteredDataList;
        sortedDataList.sort(new Comparator<Ride>() {
            @Override
            public int compare(Ride o1, Ride o2) {
                Double diff = Route.distance(deviceLocation, o1.getStartPoint()) - Route.distance(deviceLocation, o2.getStartPoint());
                if (diff > 0)
                    return 1;
                else if (diff < 0)
                    return -1;
                else
                    return 0;
            }
        });
        rideAdapter = new CustomListForRider(this, sortedDataList); // Invokes the constructor from CustomList class and passes the data for it to be displayed in each row of the list view.
        LV.setAdapter(rideAdapter);
    }
}

