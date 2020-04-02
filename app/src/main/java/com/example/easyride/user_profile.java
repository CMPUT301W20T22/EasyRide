package com.example.easyride;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.ui.driver.DriverHome;
import com.example.easyride.ui.rider.RiderHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * User profile screen that handles Name, Contact info, and rating.
 * Should be able to edit contact info
 * @author T22
 * @version 1.0
 */

public class user_profile extends AppCompatActivity  implements EditInfoFragment.myListener{

    private String userID;
    private String mode;
    private DocumentReference docRef;
    private FirebaseFirestore db;
    private TextView riderName, Email, Phone, Rating, balance;
    private String balanceAmount, addedFunds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("User Profile");

        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        mode = intent.getStringExtra("mode");

        if (mode.equals("rider")){
            findViewById(R.id.rating).setVisibility(View.INVISIBLE);
        }else{
            Rating = findViewById(R.id.rating);
            //TODO PLease display the rating for driver
        }

        // init database
        db = FirebaseFirestore.getInstance();
        docRef = db.collection(mode).document(userID);

        // TextView assign
        riderName = findViewById(R.id.user_name);
        Email = findViewById(R.id.email);
        Phone = findViewById(R.id.ph);
        balance = findViewById(R.id.balance);



        // Getting the info from database
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    riderName.setText(doc.getString("Name"));
                    Email.setText("Email: " + doc.getString("Email"));
                    Phone.setText("Phone: " + doc.getString("Phone"));

                    try{
                        balanceAmount = doc.getString("Balance");
                    }catch (Exception e){
                        balanceAmount = "0";
                    }
                    if (balanceAmount == null){
                        balanceAmount = "0";
                    }
                    String formattedBalance = formatBalance(balanceAmount);
                    balance.setText("Balance: $" + formattedBalance);
                }
            }
        });

        Button addFunds = findViewById(R.id.add_balance);
        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFundsDialog();
            }
        });


        // Set up Fragment to edit user's info

        Button editButton = findViewById(R.id.edit_contact_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditInfoFragment myDialog = new EditInfoFragment();
                myDialog.show(getSupportFragmentManager(), "My Custom Dialog");
            }
        });
    }

    /**
     * Method to update the information to the database and display it in the activity
     * @param phone
     * @param password
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void updateInfo(String phone, String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Update the information to database

       /* if (!email.equals("")) {
            Email.setText("Email: " + email);
            docRef.update("Email", email);
            user.updateEmail(email);
        }*/

        if (!phone.equals("")) {
            Phone.setText("Phone: " + phone);
            docRef.update("Phone", phone);
        }

        if (!password.equals("")) {
            user.updatePassword(password);
        }
    }

    /**
     * Create option Menu for the activity
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Home Navigation Option selection handle
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mode.equals("driver")) {
            startActivity(new Intent(this, DriverHome.class));
            finish();
        }

        if (mode.equals("rider")){
            startActivity(new Intent(this, RiderHome.class));
            finish();
        }
        
        return super.onOptionsItemSelected(item);
    }

    private String formatBalance(String ride_cost){
        String ride_cost_short;
        int index = ride_cost.indexOf('.');
        if (index == -1){
            ride_cost_short = ride_cost;
        }else {
            if (ride_cost.length() > 4 && index == 3) {
                ride_cost_short = ride_cost.substring(0, 3);
            }else if (ride_cost.length() > 4 && index > 3) {
                ride_cost_short = ride_cost.substring(0, index - 3) + "." + ride_cost.substring(index - 2, index) + "k";
            } else if(ride_cost.length() > 4) {
                ride_cost_short = ride_cost.substring(0, 4);
            }else {
                ride_cost_short = ride_cost;
            }
        }
        return ride_cost_short;
    }

    private void addFundsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Money");
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
                addedFunds = Double.toString((Double.valueOf(Tip) + Double.valueOf(balanceAmount)));
                dialog.dismiss();
                if (!addedFunds.equals("")) {
                    //ride_cost_short = fareWithTip.substring(0, 4);
                    String shortFunds = formatBalance(addedFunds);
                    balance.setText("Balance: $" + shortFunds);
                    updateBalance(addedFunds);
                    //alright.getActiveRequests().get(position).setCost(fareWithTip);
                    //alright.updateRequest(position);
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

    private void updateBalance(String funds){
        docRef.update("Balance", funds);
    }
}
