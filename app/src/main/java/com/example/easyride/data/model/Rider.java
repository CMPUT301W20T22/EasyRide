package com.example.easyride.data.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;

interface DatabaseListener {
  void onDataLoaded();
}

/**
 * Rider class that captures user information for logged in users retrieved from LoginRepository
 *
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */

public class Rider extends EasyRideUser implements DatabaseListener {
  private static Rider instance;
  private EasyRideUser currentRiderInfo;
  private ArrayList<Ride> activeRequests;
  private ArrayList<String> requestsID;
  private FirebaseFirestore db;
  private boolean dataLoaded = false;

  public Rider(final EasyRideUser user) {
    super(user.getUserId());
    currentRiderInfo = user;
    db = FirebaseFirestore.getInstance();
    Log.e("HEYYYY", "IM HERE");

    requestsID = new ArrayList<>();
    activeRequests = new ArrayList<Ride>();

    Query q = db.collection("RideRequest").whereEqualTo("user", currentRiderInfo.getUserId());
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
  public static Rider getInstance(EasyRideUser user) {
    if (instance == null) {
      instance = new Rider(user);
    }
    return instance;
  }

  public static void clear() {
    instance = null;
  }

  public void updateList() {

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
              onDataLoaded();
              dataLoaded = true;
            } else {
              Log.e(TAG, "Error getting documents: ", task.getException());
            }
          }
        });
  }

  public void addRide(Ride rideInsert) {
    DocumentReference newCityRef = db.collection("RideRequest").document();
    newCityRef.set(rideInsert);
    instance.getActiveRequests().add(rideInsert);
  }

  public ArrayList<Ride> getActiveRequests() {
    return activeRequests;
  }

  public void removeAt(int position) {
    String documentID = requestsID.get(position);
    db.collection("RideRequest").document(documentID).delete();
    activeRequests.remove(position);
  }

  public EasyRideUser getCurrentRiderInfo() {
    return currentRiderInfo;
  }

  public void onDataLoaded() {
  }

  public boolean updateRequest(int position) {
    if (position >= activeRequests.size()) return false;
    String documentID = requestsID.get(position);
    Ride updatedRequest = getActiveRequests().get(position);
    DocumentReference rideRequestRef = db.collection("RideRequest").document(documentID);
    rideRequestRef.update("cost", updatedRequest.getCost());
    rideRequestRef.update("rideConfirmAccepted", updatedRequest.isRideConfirmAccepted());
    rideRequestRef.update("riderRating", updatedRequest.getRiderRating());
    return true;
  }

  public boolean isDataLoaded() {
    return dataLoaded;
  }
}
