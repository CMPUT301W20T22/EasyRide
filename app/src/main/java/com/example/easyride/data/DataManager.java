package com.example.easyride.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

/**
 * This class will handle the communication with database
 */
public class DataManager {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    CollectionReference driverRef = database.collection("driver");
    CollectionReference riderRef = database.collection("rider");


    public boolean isDriver(String userID){
        //https://stackoverflow.com/a/53335711
        DocumentReference docIdRef = driverRef.document(userID);
        final boolean[] result = new boolean[1];
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                        result[0] = true;
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        result[0] = false;
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                    result[0] = false;
                }
            }
        });
        return result[0];
    }

    public EasyRideUser getDriver(String userID){
        //make the user class
        EasyRideUser driver;
        if(!isDriver(userID)){
            throw new IllegalArgumentException("Invalid username");
        }else{
            DocumentSnapshot driverSnapshot = driverRef.document(userID).get().getResult();
            driver = driverSnapshot.toObject(EasyRideUser.class);
        }
        return driver;
    }

    public void insertDriver(Driver driver){
        EasyRideUser driverInfo = driver.getCurrentDriverInfo();
        driverRef.document(driverInfo.getUserId()).set(driverInfo);
    }

    public boolean isRider(String userID){
        DocumentReference docIdRef = riderRef.document(userID);
        final boolean[] result = new boolean[1];
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document exists!");
                        result[0] = true;
                    } else {
                        Log.d(TAG, "Document does not exist!");
                        result[0] = false;
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                    result[0] = false;
                }
            }
        });
        return result[0];

    }

    public EasyRideUser getRider(String userID){
        //make the user class
        EasyRideUser rider;
        if(!isRider(userID)){
            throw new IllegalArgumentException("Invalid username");
        }else{
            DocumentSnapshot riderSnapshot = riderRef.document(userID).get().getResult();
            rider = riderSnapshot.toObject(EasyRideUser.class);
        }
        return rider;
    }

    public void insertRider(Rider driver){
        EasyRideUser riderInfo = driver.getCurrentRiderInfo();
        driverRef.document(riderInfo.getUserId()).set(riderInfo);
    }




}
