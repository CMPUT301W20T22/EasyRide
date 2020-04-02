package com.example.easyride.data.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easyride.ui.driver.RideRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;

/**
 * Driver class that captures user information for logged in users retrieved from LoginRepository
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 *
 * get rid of extend
 */
public class Driver extends EasyRideUser {

  private static Driver instance;
  private EasyRideUser currentDriverInfo;
  private ArrayList<Ride> activeRequests;
  private ArrayList<String> requestsID;
  private FirebaseFirestore db;
  private boolean dataLoaded = false;


  public Driver(final EasyRideUser user){
    super(user.getUserId());
    currentDriverInfo = user;

    db = FirebaseFirestore.getInstance();
    Log.e("HEYYYY", "IM HERE");

    requestsID = new ArrayList<>();
    activeRequests = new ArrayList<Ride>();

    Query q = db.collection("RideRequest").whereEqualTo("driverUserName", user.getUserId());

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
          activeRequests.add(document.toObject(Ride.class));
        }
        onDataLoaded();
        dataLoaded = true;
      }
    });
  }

  //return old instance or create a new one
  public static Driver getInstance(EasyRideUser user){
    if(instance == null){
      instance = new Driver(user);
    }
    return instance;
  }

  public void updateList() {

    Log.e("SIZE", Integer.toString(activeRequests.size()));
    db.collection("RideRequest")
        .whereEqualTo("driverUserName", currentDriverInfo.getUserId())
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
                Log.e("user", currentDriverInfo.getUserId());
                Log.e("SIZE", Integer.toString(activeRequests.size()));
              }
              onDataLoaded();
              dataLoaded = true;
            } else {
              Log.e(TAG, "Error getting documents: ", task.getException());
            }
          }
        });
  }

  public boolean updateRequest(int position) {
    if (position >= activeRequests.size()) return false;
    String documentID = requestsID.get(position);
    Ride updatedRequest = getActiveRequests().get(position);
    DocumentReference rideRequestRef = db.collection("RideRequest").document(documentID);
    rideRequestRef.update("rideAccepted", updatedRequest.isRideAccepted());
    rideRequestRef.update("rideCompleted", updatedRequest.isRideCompleted());
    rideRequestRef.update("ridePaid", updatedRequest.isRidePaid());
    return true;
  }

  public static void clear() {
    instance = null;
  }

  public ArrayList<Ride> getActiveRequests() {
    return activeRequests;
  }
  public EasyRideUser getCurrentDriverInfo(){ return currentDriverInfo; }
  public void onDataLoaded() { }
  public boolean isDataLoaded() {
    return dataLoaded;
  }

}
