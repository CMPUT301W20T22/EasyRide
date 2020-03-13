package com.example.easyride.data;

import androidx.annotation.NonNull;

import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.RideRequest;
import com.example.easyride.data.model.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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

    public ArrayList<RideRequest> getRequestByRiderID (String userID) {

        ArrayList<RideRequest> listOfRequest = new ArrayList<>();
        Query w = requestsRef.whereEqualTo("riderUserName", userID);
        //requestsRef.get().where()


//        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
  //          System.out.println(document.getId());
    //    }

        return listOfRequest;
    }

}
