package com.example.easyride.data.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easyride.ui.driver.RideRequest;
import com.example.easyride.ui.rider.Ride;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;
import java.lang.reflect.*;

interface DatabaseListener{
  void onDataLoaded();
}

/**
 * Rider class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */

public class Rider extends EasyRideUser implements DatabaseListener{
  private ArrayList<Ride> activeRequests;
  private ArrayList<String> requestsID;
  private EasyRideUser currentRiderInfo;
  private static Rider instance;
  private FirebaseFirestore db;

  /**
   * Class constructor
   * @param user
   */
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

    Query q = db.collection("RideRequest").whereEqualTo("user", user.getUserId());

    q.addSnapshotListener(new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            if (e != null) {
              Log.w(TAG, "Listen failed.", e);
              return;
            }
            requestsID.clear();
            activeRequests.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
              requestsID.add(document.getId());
              activeRequests.add( document.toObject(Ride.class));
              //Log.e("SIZE", user.getUserId());
              //Log.e("SIZE", Integer.toString(activeRequests.size()));
            }
            onDataLoaded();
          }
        });


    currentRiderInfo = user;
    //updateList();

    //TODO: add activeRequests
  }

  /**
   * Return old instance or create a new one.
   * @param user
   * @return driver
   */
  public static Rider getInstance(EasyRideUser user){
    if(instance == null){
      instance = new Rider(user);
    }
    return instance;
  }

  /**
   * Clear the instance
   */
  public static void clear(){
    instance = null;
  }


  /**
   * Update the list
   */
  public void updateList() {

    //requestsID = new ArrayList<>();
    //activeRequests = new ArrayList<Ride>();
    //requestsID.clear();
    //activeRequests.clear();
    //new ArrayList<>(activeRequests);
    Log.e("SIZE", Integer.toString(activeRequests.size()));
    db.collection("RideRequest")
// <<<<<<< HEAD
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
                activeRequests.add( document.toObject(Ride.class));
                Log.e("user", currentRiderInfo.getUserId());
                Log.e("SIZE", Integer.toString(activeRequests.size()));
              }
              onDataLoaded();
            } else {
              Log.e(TAG, "Error getting documents: ", task.getException());
            }
          }
        });

  }

  /**
   * Adding the RideRequest to the activeRequest list.
   * @param rideInsert
   */
  public void addRide(Ride rideInsert){
    DocumentReference newCityRef = db.collection("RideRequest").document();
    newCityRef.set(rideInsert);
    instance.getActiveRequests().add(rideInsert);
  }

  /**
   * Getting all the active requests.
   * @return activeRequests
   */
  public ArrayList<Ride> getActiveRequests() {
    //updateList();
    return activeRequests;
  }

  /**
   * Remove the ride request.
   * @param position
   */
  public void removeAt(int position){
    String documentID = requestsID.get(position);
    db.collection("RideRequest").document(documentID).delete();
    activeRequests.remove(position);
  }

  /**
   * Get the current rider info.
   * @return EasyRideUser
   */
  public EasyRideUser getCurrentRiderInfo(){return currentRiderInfo;}

  /**
   * Update the list and handle the UI whenever there is an update in the database.
   */
  public void onDataLoaded() {}

  /**
   * Update the ride request information.
   * @param position
   * @return boolean
   */
  public boolean updateRequest(int position){
    if (position >= activeRequests.size()) return false;
    String documentID = requestsID.get(position);
    Ride updatedRequest = getActiveRequests().get(position);
    DocumentReference rideRequestRef = db.collection("RideRequest").document(documentID);
    rideRequestRef.update("cost", updatedRequest.getCost() );
    rideRequestRef.update("rideConfirmAccepted", updatedRequest.isRideConfirmAccepted());
    rideRequestRef.update("riderRating",updatedRequest.getRiderRating() );
    return  true;
  }
}
