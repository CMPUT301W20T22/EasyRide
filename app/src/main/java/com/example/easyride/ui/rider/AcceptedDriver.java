package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
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

import static com.android.volley.VolleyLog.TAG;


/**
 * Activity to show the profile of the driver who accepts the ride.
 * @author T22
 * @version 1.0
 */

public class AcceptedDriver extends AppCompatActivity {


    private String userID;
    private DocumentReference docRef;
    private FirebaseFirestore db;
    private TextView riderName, email, Phone, Rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_driver); // I DON'T KNOW WHY THIS IS RED, BUT IT WORKS SO IDK.
        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        //mode = intent.getStringExtra("mode");
        Log.e("Driver ID:" , userID);
        // init database
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("driver").document(userID);
        riderName = findViewById(R.id.user_name);
        email = findViewById(R.id.driver_email);
        Phone = findViewById(R.id.driver_ph);
        Rating = findViewById(R.id.driver_rating);
        db.collection("driver")
                .whereEqualTo("Email", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                updateView(document);
                                //riderName.setText();
                                riderName.setText(document.getString("Name") + "'s Profile");
                                email.setText("Email: " + document.getString("Email"));
                                Phone.setText("Phone: " + document.getString("Phone"));
                                try {
                                    int good = (int) document.get("good_reviews");
                                    int bad = (int) document.get("bad_reviews");
                                    int rate = (good/bad)*100;
                                    if (rate == 0){
                                        throw new Exception("Rating is 0");
                                    }
                                    String rateS = String.valueOf(rate);
                                    Rating.setText("Rate: " + rateS + "%");
                                } catch (Exception e){
                                    Log.e(TAG, "Error getting Ratings: ", e);
                                    Rating.setText("Rating: Driver has not been rated yet");
                                }


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

        //Button editButton = findViewById(R.id.edit_contact_button);

        //editButton.setVisibility(View.INVISIBLE);

    }

    /**
     * Method to show the view of the activity
     * @param document
     */

    private void updateView(DocumentSnapshot document){
        String name = document.getString("Name") + "'s Profile";
        String riderEmail = "Email: " + document.getString("Email");
        String riderPhone = "Phone: " + document.getString("Phone");

        String emailLink = "mailto:" + document.getString("Email");
        String phoneLink = "tel:" + document.getString("Phone");

        int emailLen = riderEmail.length();
        int phoneLen = riderPhone.length();

        SpannableString riderSpanEmail = new SpannableString(riderEmail);
        riderSpanEmail.setSpan(new URLSpan(emailLink), 7, emailLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString riderSpanPhone = new SpannableString(riderPhone);
        riderSpanPhone.setSpan(new URLSpan(phoneLink), 7, phoneLen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        riderName.setText(name);
        email.setText(riderSpanEmail);
        Phone.setText(riderSpanPhone);

        email.setMovementMethod(LinkMovementMethod.getInstance());
        Phone.setMovementMethod(LinkMovementMethod.getInstance());

        try{
            int good = (int) document.get("good_reviews");
            int bad = (int) document.get("bad_reviews");
            int rate = (good/bad)*100;
            if (rate == 0){
                throw new Exception("Rating is 0");
            }
            String rateS = String.valueOf(rate);
            Rating.setText("Rate: " + rateS + "%");
        }catch (Exception e){
            Log.e(TAG, "Error getting Ratings: ", e);
            Rating.setText("Rating: Driver has not been rated yet");
        }
    }

}
