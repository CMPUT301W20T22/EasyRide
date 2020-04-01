package com.example.easyride.ui.rider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.easyride.R;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.example.easyride.map.MapsActivity;
import com.example.easyride.map.MarkerHandler;
import com.example.easyride.ui.NotificationModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;
import static com.android.volley.VolleyLog.TAG;

// Handles viewing of a ride request. Don't need to be able to edit the request
public class EditRide extends AppCompatActivity implements OnMapReadyCallback {

    public ArrayList<Ride> DataList;
    private TextView from, to, cost, distance;
    private Button payButton, delete, viewProfile, addTip, back;
    private String fareWithTip;
    private String ride_cost;
    private Ride rideReq;
    private Rider alright;
    private int position;
    private boolean rideIsAccepted = true;
    private boolean isFinished = false;
    private boolean isMapLoaded = false;
    private boolean isRouteShown = false;
    MarkerHandler mh;
  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);
        from = findViewById(R.id.from_text);
        to = findViewById(R.id.to_text);
        cost = findViewById(R.id.cost_text);
        distance = findViewById(R.id.distance);
        payButton = findViewById(R.id.pay_button);
        payButton.setClickable(false);
        addTip = findViewById(R.id.tip_button);
        addTip.setClickable(false);
        delete = findViewById(R.id.delete_button);
        delete.setClickable(false);
        delete.setVisibility(View.INVISIBLE);
        back = findViewById(R.id.back_button);
        viewProfile = findViewById(R.id.profile_button);
        viewProfile.setClickable(false);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapViewEditRide);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(),getString(R.string.api_key));
        alright = new Rider(new EasyRideUser(userID)) {
            @Override
            public void onDataLoaded() {
                updateView();
            }
        };

      viewProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent i = new Intent(getApplicationContext(), AcceptedDriver.class);
          //i.putExtra("mode", "driver");
          i.putExtra("ID", rideReq.getDriverUserName());
          startActivity(i);
        }
      });

        addTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFinished = true;
                alright.removeAt(position);
                goBack();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack(){
        Intent i = new Intent(getApplicationContext(), RiderHome.class);
        startActivity(i);
        finish();
    }

    private void confirmRide(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure? You will no longer be able to cancel the ride");
        // Set up the input
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        // Set up the buttons
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alright.getActiveRequests().get(position).setRideConfirmAccepted(true);
                alright.updateRequest(position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    //https://stackoverflow.com/a/10904665/10861074
    private void tipDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Tip");
        // Set up the input
        final EditText input = new EditText(this);

        /* Specify the type of input expected; this, for example, sets the input as a password, and will mask the text*/
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setView(input);
        /* Set up the buttons*/
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Tip = input.getText().toString();
                fareWithTip = Double.toString((Double.valueOf(Tip) + Double.valueOf(ride_cost)));
                dialog.dismiss();
                if (!fareWithTip.equals("")) {
                    //ride_cost_short = fareWithTip.substring(0, 4);
                    cost.setText(fareWithTip);
                    alright.getActiveRequests().get(position).setCost(fareWithTip);
                    alright.updateRequest(position);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      if (! isMapLoaded){
        mh = new MarkerHandler(googleMap, getString(R.string.api_key));
      }
      isMapLoaded = true;
      alright.updateList();
    }
    private void ratePayDialog(){
        boolean[] review = new boolean[1];
        review[0] = true;
        final boolean[] goodReview = review;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please rate and Pay");
        final CharSequence[] array = {"Good" , "Bad"};
        builder.setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goodReview[0] = which == 0;
            }
        });

// Set up the buttons
        builder.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (goodReview[0]) {
                    alright.getActiveRequests().get(position).setRiderRating(1L);
                }
                else {
                    alright.getActiveRequests().get(position).setRiderRating(-1L);
                }
                alright.updateRequest(position);
                Intent i = new Intent(getApplicationContext(), QR_Pay.class);
                i.putExtra("cost", ride_cost);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void updateView(){
        DataList = alright.getActiveRequests();
        if (isFinished || !alright.isDataLoaded()){
            return;
        }
        getSupportActionBar().setTitle("Request");
        rideReq = DataList.get(position);
        String ride_distance = rideReq.getDistance();
        String ride_distance_short;
        if (ride_distance.length() > 4) {
            ride_distance_short = ride_distance.substring(0, 5);
        }else{
            ride_distance_short = ride_distance;
        }
        ride_cost= rideReq.getCost();
        int index = ride_cost.indexOf('.');
        String ride_cost_short;
        if (index == -1){
            ride_cost_short = ride_cost;
        }else {
            if (ride_cost.length() > 4 && index == 3) {
                ride_cost_short = ride_cost.substring(0, 3);
            } else if(ride_cost.length() > 4) {
                ride_cost_short = ride_cost.substring(0, 4);
            }else {
                ride_cost_short = ride_cost;
            }
        }
        if (isMapLoaded && !isRouteShown){
          LatLng startLatLang = new LatLng(rideReq.getStartPoint().getLatitude(), rideReq.getStartPoint().getLongitude());
          LatLng endLatLang = new LatLng(rideReq.getEndPoint().getLatitude(), rideReq.getEndPoint().getLongitude());
          mh.setStartLatLang(startLatLang);
          mh.setEndLatLang(endLatLang);
          mh.showMarkers();
          isRouteShown = true;
        }
        from.setText(rideReq.getFrom());
        to.setText(rideReq.getTo());
        cost.setText(ride_cost_short);
        distance.setText(ride_distance_short);

        /* Notifies the rider if the ride request is accepted*/
        if ( !rideIsAccepted && rideReq.isRideAccepted() ){
            String message = "Ride is accepted by " + rideReq.getDriverUserName() +"!";
            Toast.makeText(EditRide.this, message, Toast.LENGTH_LONG).show();
        }
        rideIsAccepted = rideReq.isRideAccepted();

        addTip.setClickable(true);
        if (!rideReq.isRideAccepted()){
            payButton.setText("Driver has not accepted");
            payButton.setClickable(false);
            viewProfile.setClickable(false);
            delete.setVisibility(View.VISIBLE);
            delete.setClickable(true);
        }
        else
        {
            viewProfile.setText("Driver Profile");
            viewProfile.setClickable(true);
            if (!rideReq.isRideConfirmAccepted()) {
                String tempString = "Confirm the request";
                SpannableString spanString = new SpannableString(tempString);
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                payButton.setText(spanString);
                payButton.setTextColor(Color.parseColor("#C82828"));
                payButton.setClickable(true);
                delete.setVisibility(View.VISIBLE);
                delete.setClickable(true);

                //This is where QR is called.
                payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmRide();
                    }
                });
            }
            else { //ride is accepted and confirmed
                delete.setVisibility(View.INVISIBLE);
                payButton.setText("Waiting for ride completion");
                payButton.setTextColor(Color.BLACK);
                payButton.setClickable(false);
                if (!rideReq.isRideCompleted()) {
                    payButton.setText("Waiting for ride completion");
                    payButton.setClickable(false);
                }
                else { //ride is accepted and confirmed and completed
                    if (!rideReq.isRidePaid()) {
                        payButton.setText("Rate and Pay!");
                        payButton.setClickable(true);
                        payButton.setTextColor(Color.parseColor("#7C1C1C"));
                        //This is where QR is called.
                        payButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ratePayDialog();
                            }
                        });
                    }
                    else { //ride is accepted and confirmed and completed and paid
                        payButton.setText("Ride is pied!");
                        payButton.setClickable(false);
                        addTip.setClickable(false);
                    }
                }
            }
        }
    }
}
