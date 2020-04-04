package com.example.easyride.ui.driver;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Ride;
import com.example.easyride.ui.rider.QR_Pay;
import com.example.easyride.ui.rider.RiderHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.ArrayList;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;


/**
 * Activity to scan the QR code whenever the Driver finishes the ride,
 * and want to accept the payment.
 * @author T22
 * @version 1.0
 */
public class QR_Scan extends AppCompatActivity {
    public ArrayList<Ride> DataList;
    private Ride rideReq;
    private int position;
    private Button qr_scan_button;
    private String cost;
    private Driver driver;
    private String userID;
    private String id;
    private String balanceAmount, addedFunds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        qr_scan_button = (Button)findViewById(R.id.qr_scan_button);
        final Activity activity = this;
        qr_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator =  new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan the QR code from Rider");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        Intent intent = getIntent();

        position = intent.getIntExtra("position", 0);

        userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        driver = new Driver(new EasyRideUser(userID)) {
            @Override
            public void onDataLoaded() {
                loadRide();
            }
        };

    }

    /**
     * Handle the result of scanning.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents() == null){
            Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            cost = result.getContents();
            driver.getActiveRequests().get(position).setRidePaid(true);
            driver.updateRequest(position);
            getDriverProfile();
            //updateBalance();
            //updateRating();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Method to check for the ride and update the status after the payment is processed.
     */
    private void loadRide(){
        DataList = driver.getActiveRequests();
        rideReq = DataList.get(position);
        if (rideReq.isRidePaid()){
            rideFinished();
        }
    }

    /**
     * Method to update the balance of the driver.
     */
    private void updateBalance(){
        DocumentReference doc = FirebaseFirestore.getInstance().collection("driver").document(id);
        doc.update("Balance", addedFunds);
    }

    /**
     * Method to update the rating of the driver.
     */
    private void updateRating(){
        DocumentReference doc = FirebaseFirestore.getInstance().collection("driver").document(id);
        Long rating = rideReq.getRiderRating();
        if (rating == 1L){
            doc.update("good_reviews", rating);
        }else{
            doc.update("bad_reviews", rating);
        }
    }

    /**
     * Method to get the driver info.
     */
    private void getDriverProfile(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("driver")
                .whereEqualTo("Email", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                try{
                                    balanceAmount = document.getString("Balance");
                                }catch (Exception e){
                                    balanceAmount = "0";
                                }
                                if (balanceAmount == null){
                                    balanceAmount = "0";
                                }
                                id = document.getId();
                                addedFunds = Double.toString((Double.valueOf(cost) + Double.valueOf(balanceAmount)));
                                if (!addedFunds.equals("")) {
                                    //ride_cost_short = fareWithTip.substring(0, 4);

                                    //alright.getActiveRequests().get(position).setCost(fareWithTip);
                                    //alright.updateRequest(position);
                                }
                            }

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //can be put into driver


    }

    /**
     * Show the dialog after the driver is paid.
     */
    private void rideFinished(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YAY! QRBucks has been added to your account");
        // Set up the input
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        // Set up the buttons
        builder.setPositiveButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(QR_Scan.this, DriverHome.class);
                dialog.dismiss();
                finish();
                startActivity(i);
            }
        });
        builder.show();
    }


}
