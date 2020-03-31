package com.example.easyride.ui.rider;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Class to handle Notification
 * @author T22
 * @version 1.0
 */
public class NotificationService extends FirebaseMessagingService {
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    /**
     * Constructor
     */
    public NotificationService() {
    }

    /**
     * Create a notification token
     * @param s
     */
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = fAuth.getCurrentUser();
        final String ID = user.getUid();
        db.collection("rider").document(ID).update("token", s).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Notification Service", "token updated");
            }
        });
    }
}
