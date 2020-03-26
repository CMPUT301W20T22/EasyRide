package com.example.easyride.ui.rider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.easyride.R;

// This class is when a rider wants to add a new ride request.
// Layout is activity_add_ride

public class AddRide extends RiderHome {

    private EditText fromName;
    private EditText toName;
    private TextView costName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);



        fromName = findViewById(R.id.from_text);
        toName = findViewById(R.id.to_text);
        costName = findViewById(R.id.cost_text);
        final Button saveButton = findViewById(R.id.save_button);



        saveButton.setOnClickListener(new View.OnClickListener() {
            String from = fromName.getText().toString();
            String to = toName.getText().toString();
            String cost = costName.getText().toString();
            String username = "test";



            // TODO: Be able to update rider_home with newly added ride requests.




            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RiderHome.class);
                startActivity(i);
            }

        });




    }

}
