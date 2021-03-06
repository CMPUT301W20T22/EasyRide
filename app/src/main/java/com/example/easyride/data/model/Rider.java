package com.example.easyride.data.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.easyride.map.MapsActivity;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;

//interface DatabaseListener {
//  void onDataLoaded();
//}

/**
 * Rider class that captures user information for logged in users retrieved from LoginRepository
 *
 * @author T22
 * @version 1.0
 * @see EasyRideUser
 */

//public class Rider extends EasyRideUser implements DatabaseListener {
public class Rider extends EasyRideUser {
  private static Rider instance;
  private EasyRideUser currentRiderInfo;
  private ArrayList<Ride> activeRequests;
  private ArrayList<String> requestsID;
  private FirebaseFirestore db;
  private boolean dataLoaded = false;
  private HashMap<String, Integer> map;

  public Rider(final EasyRideUser user) {
    super(user.getUserId());
    currentRiderInfo = user;
    db = FirebaseFirestore.getInstance();
    Log.e("HEYYYY", "IM HERE");

    requestsID = new ArrayList<>();
    activeRequests = new ArrayList<Ride>();
    map = new HashMap<String, Integer>();

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
        map.clear();
        int i = 0;
        String docId;
        map = new HashMap<String, Integer>();
        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
          docId = document.getId();
          requestsID.add(docId);
          Ride ride = document.toObject(Ride.class);
          ride.setID(docId);
          activeRequests.add(ride);
          map.put(docId, i);
          i++;
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
              map.clear();
              int i = 0;
              String docId;
              map = new HashMap<String, Integer>();
              for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                docId = document.getId();
                Ride ride = document.toObject(Ride.class);
                ride.setID(docId);
                activeRequests.add(ride);
                map.put(docId, i);
                i++;
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
    instance.activeRequests.add(rideInsert);
  }

  public Ride getActiveRequest(int pos) {
    return activeRequests.get(pos);
  }

  public Ride getActiveRequest(String docID) {
    return activeRequests.get(map.get(docID));
  }

  public ArrayList<Ride> getActiveRequests() {return activeRequests; }

  public void removeAt(int position) {
    String documentID = requestsID.get(position);
    db.collection("RideRequest").document(documentID).delete();
    activeRequests.remove(position);
    //TODO remove record from map
  }

  public void removeAt(String documentID) {
    activeRequests.remove(map.get(documentID));
    map.remove(documentID);
    db.collection("RideRequest").document(documentID).delete();
  }


  public EasyRideUser getCurrentRiderInfo() {
    return currentRiderInfo;
  }

  public void onDataLoaded() {
  }

  public boolean updateRequest(int position) {
    if (position >= activeRequests.size()) return false;
//    String documentID = requestsID.get(position);
    Ride updatedRequest = activeRequests.get(position);
    return updateRequest(updatedRequest);
  }

  public boolean updateRequest(Ride updatedRequest) {
    String documentID =  updatedRequest.getID();
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
