package com.example.easyride.ui.driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.easyride.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// DRIVER HOME. THE FIRST PAGE YOU SHOULD SEE WHEN YOU SIGN IN AS A DRIVER.
// Handles the driver home screen to display and navigate between active requests, as well as
// allowing users to select and accept a new ride request.



public class driver_home extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private RecyclerView mRequestList;
    private RideRequestListAdapter rideRequestListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private List<RideRequest> rideRequestList = new ArrayList<>();
    private String mode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        // init database
        db = FirebaseFirestore.getInstance();

        // initial views
        mRequestList = findViewById(R.id.request_list);

        // set recycler view properties
        mRequestList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRequestList.setLayoutManager(layoutManager);

        // Set Adapter
        rideRequestListAdapter = new RideRequestListAdapter(driver_home.this, rideRequestList);
        mRequestList.setAdapter(rideRequestListAdapter);


        // show data in Recycler View
        showData();

    }

    private void showData() {

        db.collection("RideRequest").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "Error : " + e.getMessage());
                }
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        RideRequest rideRequest = new RideRequest(doc.getDocument().getString("riderUserName"),
                                doc.getDocument().getString("pickupPoint"),
                                doc.getDocument().getString("targetPoint"),
                                doc.getDocument().getString("cost"));

                        rideRequestList.add(rideRequest);
                        rideRequestListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
