package com.example.easyride;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

import com.example.easyride.data.model.UserDB;
import com.example.easyride.data.model.UserType;
import com.example.easyride.ui.driver.DriverHome;
import com.example.easyride.ui.rider.RiderHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.android.volley.VolleyLog.TAG;

// User profile screen that handles Name, Contact info, and rating.
// Should be able to edit contact info

public class user_profile extends AppCompatActivity  implements EditInfoFragment.myListener{

    private String userID, userEmail;
    private String mode;
    private DocumentReference docRef;
    private FirebaseFirestore db;
    private TextView riderName, Email, Phone, Rating, balance;
    private String balanceAmount, addedFunds;
    private UserDB userDB;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("User Profile");

        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        userEmail = intent.getStringExtra("email");
        mode = intent.getStringExtra("mode");
        Rating = findViewById(R.id.rating);
        // TextView assign
        riderName = findViewById(R.id.user_name);
        Email = findViewById(R.id.email);
        Phone = findViewById(R.id.ph);
        balance = findViewById(R.id.balance);

        if (mode.equals("rider")){
            userDB = new UserDB(UserType.RIDER, userEmail){
                @Override
                public void userDataLoaded(){
                    updateView();
                }
            };
            Rating.setVisibility(View.INVISIBLE);
        }else{
            userDB = new UserDB(UserType.DRIVER, userEmail){
                @Override
                public void userDataLoaded(){
                    updateView();
                }
            };
            Rating.setVisibility(View.VISIBLE);
            //TODO PLease display the rating for driver
        }


        Button addFunds = findViewById(R.id.add_balance);
        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFundsDialog();
            }
        });


        /**
         * Set up fragment to edit user's contact info.
         */
        Button editButton = findViewById(R.id.edit_contact_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditInfoFragment myDialog = new EditInfoFragment();
                myDialog.show(getSupportFragmentManager(), "My Custom Dialog");
            }
        });
    }
  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    userID = intent.getStringExtra("ID");
    userEmail = intent.getStringExtra("email");
    mode = intent.getStringExtra("mode");
    updateView();
  }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateInfo(String name, String phone, String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!phone.equals("")) {
            Phone.setText("Phone: " + phone);
            userDB.setPhone(phone);
            userDB.push();
        }
        if (!name.equals("")){
            userDB.setName(name);
            userDB.push();
        }
        if (!password.equals("")) {
            user.updatePassword(password);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Home Navigation Option
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Tip = input.getText().toString();
                if (!Tip.equals("")) {
                    userDB.increaseBalance(Double.valueOf(Tip));
                    userDB.push();
                    balance.setText("Balance: $" + formatBalance(userDB.getBalance().toString()));
                }
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
    public void updateView(){
        riderName.setText(userDB.getName());
        Email.setText("Email: " + userDB.getEmail());
        Phone.setText("Phone: " + userDB.getPhone());
        balanceAmount = userDB.getBalance().toString();
        String formattedBalance = formatBalance(balanceAmount);
        balance.setText("Balance: $" + formattedBalance);
        Rating.setText("");
        if (userDB.getUserType() == UserType.DRIVER) {
            try {
                int good = userDB.getGoodReviews();
                int bad = userDB.getBadReviews();
                int rate;
                if (good == 0 && bad == 0)
                    throw new Exception("Rating is 0");
                else
                    rate = good / (good + bad) * 100;

                String rateS = String.valueOf(rate);
                String reviw = " reveiws!";
                if ((good + bad) == 1)
                    reviw = " reveiw!";
                Rating.setText("Rate: " + rateS + "% based on " + Integer.toString(good + bad) + reviw);
            } catch (Exception e) {
                Log.e(TAG, "Error getting Ratings: ", e);
                Rating.setText("Rating: Driver has not been rated yet");
            }
        }
        if (balanceAmount == null){
            balanceAmount = "0";
        }
    }
}
