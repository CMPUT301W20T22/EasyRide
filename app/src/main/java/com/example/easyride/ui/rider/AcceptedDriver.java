package com.example.easyride.ui.rider;

import android.annotation.SuppressLint;
import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import static android.view.View.GONE;
import static com.android.volley.VolleyLog.TAG;

public class AcceptedDriver extends AppCompatActivity {


    private String userID;

    private DocumentReference docRef;
    private FirebaseFirestore db;
    private TextView riderName, email, Phone, Rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // I DON'T KNOW WHY THIS IS RED, BUT IT WORKS SO IDK.

        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        //mode = intent.getStringExtra("mode");
        Log.e("Driver ID:" , userID);
        // init database
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("driver").document(userID);
        riderName = findViewById(R.id.user_name);
        email = findViewById(R.id.email);
        Phone = findViewById(R.id.ph);
        Rating = findViewById(R.id.rating);
        //userID = "sqle@ualberta.ca";
        db.collection("driver")
                .whereEqualTo("Email", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                                //riderName.setText();
                                email.setText("Email: " + document.getString("Email"));
                                Phone.setText("Phone: " + document.getString("Phone"));

                                //Log.e("SIZE", user.getUserId());
                                //Log.e("SIZE", Integer.toString(activeRequests.size()));

                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        // TextView assign

/*
        // Getting the info from database
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    riderName.setText(doc.getString("Name: "));
                    Email.setText("Email: " + doc.getString("Email: "));
                    Phone.setText("Phone: " + doc.getString("Phone: "));
                }
            }
        });*/

        Button editButton = findViewById(R.id.edit_contact_button);

        editButton.setVisibility(GONE);

    }

}
