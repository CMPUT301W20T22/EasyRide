package com.example.easyride.data.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.easyride.ui.driver.RideRequest;
import com.example.easyride.ui.rider.Ride;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;
import java.lang.reflect.*;

/**
 * Rider class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */
public class Rider extends EasyRideUser{
  private ArrayList<Ride> activeRequests;
  private ArrayList<String> requestsID;
  private EasyRideUser currentRiderInfo;
  private static Rider instance;
  private FirebaseFirestore db;

  public Rider(final EasyRideUser user){
    super(user.getUserId());
    //super(userId);
    // UserDatabaseManager database = new UserDatabaseManager();
    // boolean exists = database.isRider("hi");
    // if (!exists){ currentRiderInfo = null; }
    //else {
      // currentRiderInfo = database.getRider("hi");

      // activeRequests = new ArrayList<>();
    //}
    db = FirebaseFirestore.getInstance();
    Log.e("HEYYYY", "IM HERE");

    requestsID = new ArrayList<>();
    activeRequests= new ArrayList<Ride>();

    db.collection("RideRequest")
            .whereEqualTo("user", user.getUserId())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    requestsID.add(document.getId());
                    activeRequests.add(document.toObject(Ride.class));
                    //Log.e("SIZE", user.getUserId());
                    //Log.e("SIZE", Integer.toString(activeRequests.size()));

                  }
                } else {
                  Log.e(TAG, "Error getting documents: ", task.getException());
                }
              }
            });
    currentRiderInfo = user;
    //updateList();

    //TODO: add activeRequests
  }
  //return old instance or create a new one
  public static Rider getInstance(EasyRideUser user){
    if(instance == null){
      instance = new Rider(user);
    }
    return instance;
  }

  public static void clear(){
    instance = null;
  }

  public void updateList() {

    //requestsID = new ArrayList<>();
    //activeRequests = new ArrayList<Ride>();
    //requestsID.clear();
    //activeRequests.clear();
    //new ArrayList<>(activeRequests);
    Log.e("SIZE", Integer.toString(activeRequests.size()));
    db.collection("RideRequest")
            .whereEqualTo("user", currentRiderInfo.getUserId())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  activeRequests.clear();
                  requestsID.clear();
                  for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    requestsID.add(document.getId());
                    activeRequests.add(document.toObject(Ride.class));
                    Log.e("user", currentRiderInfo.getUserId());
                    Log.e("SIZE", Integer.toString(activeRequests.size()));

                  }
                } else {
                  Log.e(TAG, "Error getting documents: ", task.getException());
                }
              }
            });


  }

  public void addRide(Ride rideInsert){
    DocumentReference newCityRef = db.collection("RideRequest").document();
    newCityRef.set(rideInsert);
    instance.getActiveRequests().add(rideInsert);
  }

  public ArrayList<Ride> getActiveRequests() {
    //updateList();
    return activeRequests;
  }

  public void removeAt(int position){
    String documentID = requestsID.get(position);
    db.collection("RideRequest").document(documentID).delete();
    activeRequests.remove(position);
  }

  public EasyRideUser getCurrentRiderInfo(){return currentRiderInfo;}

}
