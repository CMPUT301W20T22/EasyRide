package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.easyride.R;
import com.example.easyride.data.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import com.example.easyride.data.model.UserDB;
import static com.android.volley.VolleyLog.TAG;


/**
 * Activity to show the profile of the driver who accepts the ride.
 * @author T22
 * @version 1.0
 */

public class AcceptedDriver extends AppCompatActivity {

    private String userEmail;
    private TextView riderName, email, Phone, Rating;
    private UserDB driverUserDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_driver); // I DON'T KNOW WHY THIS IS RED, BUT IT WORKS SO IDK.
        Intent intent = getIntent();
        onNewIntent(intent);
        userEmail = intent.getStringExtra("ID");
        riderName = findViewById(R.id.user_name);
        email = findViewById(R.id.driver_email);
        Phone = findViewById(R.id.driver_ph);
        Rating = findViewById(R.id.driver_rating);
        driverUserDB = new UserDB(UserType.DRIVER, userEmail){
            @Override
            public void userDataLoaded() {
                updateView();
            }
        };
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        userEmail = intent.getStringExtra("ID");
    }
    /**
     * Method to show the view of the activity
     */
    private void updateView(){
        driverUserDB.getEmail();
        String name = driverUserDB.getName() + "'s Profile";
        String riderEmail = "Email: " + driverUserDB.getEmail();
        String riderPhone = "Phone: " + driverUserDB.getPhone();
        String emailLink = "mailto:" + driverUserDB.getEmail();
        String phoneLink = "tel:" + driverUserDB.getPhone();

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
            int good = driverUserDB.getGoodReviews();
            int bad = driverUserDB.getBadReviews();
            int rate;
            if (good==0 && bad==0)
                throw new Exception("Rating is 0");
            else
                rate = good/(good+bad)*100;

            String rateS = String.valueOf(rate);
            String reviw = " reveiws!";
            if ((good+bad)==1)
                reviw =  " reveiw!";
            Rating.setText("Rate: " + rateS + "% based on " + Integer.toString(good + bad) + reviw);
        }catch (Exception e){
            Log.e(TAG, "Error getting Ratings: ", e);
            Rating.setText("Rating: Driver has not been rated yet");
        }
    }

}
