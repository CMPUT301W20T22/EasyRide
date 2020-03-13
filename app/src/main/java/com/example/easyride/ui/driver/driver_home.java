package com.example.easyride.ui.driver;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyride.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class driver_home extends AppCompatActivity {


    private static final String TAG = "FireLog";
    private RecyclerView requestList;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        requestList = findViewById(R.id.request_list);

        db = FirebaseFirestore.getInstance();

        db.collection("RideRequest").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "Error : " + e.getMessage());
                }

                for (DocumentSnapshot doc : queryDocumentSnapshots) {

                    String rider = doc.getString("rider");
                    String pickupLocation = doc.getString("Pickup Location");
                    String destination = doc.getString("Destination");
                    String fee = doc.getString("fee");

                    Log.d(TAG, "Rider: " + rider);
                }
            }
        });

    }
}
