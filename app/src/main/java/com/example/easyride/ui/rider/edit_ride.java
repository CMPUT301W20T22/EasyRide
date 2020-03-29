package com.example.easyride.ui.rider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.example.easyride.user_profile;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

// Handles viewing of a ride request. Don't need to be able to edit the request
public class edit_ride extends AppCompatActivity {

    public ArrayList<Ride> DataList;
    private TextView from, to, cost, distance;
    String fareWithTip;
    String ride_cost_short;
    String ride_distance_short;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);
        Intent intent = getIntent();
        final int position = intent.getIntExtra("position", 0);
        DataList = new ArrayList<>();
       // SingleRide instance = SingleRide.getInstance();
       // DataList = instance.getRide();
        Rider alright = Rider.getInstance(new EasyRideUser("kk"));
        getSupportActionBar().setTitle("Request");
        DataList = alright.getActiveRequests();
        final Ride rideReq = DataList.get(position);
        String ride_distance = rideReq.getDistance();
        if (ride_distance.length() > 4) {
            ride_distance_short = ride_distance.substring(0, 5);
        }else{
            ride_distance_short = ride_distance;
        }
        String ride_cost= rideReq.getCost();
        if (ride_cost.length() > 4) {
            ride_cost_short = ride_cost.substring(0, 4);
        }else {
            ride_cost_short = ride_cost;
        }
        from = findViewById(R.id.from_text);
        to = findViewById(R.id.to_text);
        cost = findViewById(R.id.cost_text);
        distance = findViewById(R.id.distance);

        from.setText(rideReq.getFrom());
        to.setText(rideReq.getTo());
        cost.setText(ride_cost_short);
        distance.setText(ride_distance_short);

        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QR_Pay.class);
                startActivity(i);
            }

        });

        Button addTip = findViewById(R.id.tip_button);
        addTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipDialog();
            }
        });

        final Rider instance = Rider.getInstance(new EasyRideUser("kk"));
        Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.removeAt(position);
                goBack();
            }
        });

        if (rideReq.isRideAccepted()) {
            Button viewProfile = findViewById(R.id.profile_button);
            viewProfile.setText("Driver Profile");
            viewProfile.setClickable(true);
            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), user_profile.class);
                    startActivity(i);
                }
            });
        }
        Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.removeAt(position);
                goBack();
            }
        });

        Button save = findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fareWithTip != null){
                    rideReq.setCost(fareWithTip);
                    instance.removeAt(position);
                    instance.addRide(rideReq);
                }
                goBack();
            }
        });

        Button back = findViewById(R.id.back_button);

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
    }

    //https://stackoverflow.com/a/10904665/10861074
    private void tipDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Tip");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fareWithTip = input.getText().toString();
                dialog.dismiss();
                if (!fareWithTip.equals("")) {
                    //ride_cost_short = fareWithTip.substring(0, 4);
                    cost.setText(fareWithTip);
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
}
