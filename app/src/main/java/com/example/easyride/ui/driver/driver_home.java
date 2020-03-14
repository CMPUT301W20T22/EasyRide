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
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.ui.login.LoginActivity;
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

public class driver_home extends AppCompatActivity {

    private static final String TAG = "FireLog";
    private RecyclerView mRequestList;
    private RideRequestListAdapter rideRequestListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    private List<RideRequest> rideRequestList = new ArrayList<>();
    private String mode;
    FirebaseAuth fAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        /*
        Intent intent = getIntent();
        String ID = intent.getStringExtra(LoginActivity.ID);
        Log.d(TAG, "THe ID: " + ID);
        db.collection("driver").document(ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String,Object> data = document.getData();
                        Log.d(TAG, "DocumentSnapshot data: " + data);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });*/

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
        rideRequestListAdapter = new RideRequestListAdapter(driver_home.this, rideRequestList);
        mRequestList.setAdapter(rideRequestListAdapter);


        // show data in Recycler View
        showData();

    }

    private void showData() {

        db.collection("RideRequest").orderBy("cost", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                doc.getDocument().getLong("cost").intValue());

                        rideRequestList.add(rideRequest);
                        rideRequestListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
