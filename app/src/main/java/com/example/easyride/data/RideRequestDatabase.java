package com.example.easyride.data;

import com.example.easyride.data.model.RideRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RideRequestDatabase {

    private FirebaseFirestore db;
    //private FirebaseAuth mAuth;

    final CollectionReference requestsRef;

    //DatabaseReference driverRef;


    public RideRequestDatabase(){
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //driverRef = database.getReference("driver");
        //mAuth = FirebaseAuth.getInstance();
        //this.database = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        this.requestsRef = db.collection("requests");
    }

    public void addRequest(RideRequest requestToBeAdded){
        DocumentReference addHere = requestsRef.document();
        addHere.set(requestToBeAdded);
    }

    

}
