package com.example.easyride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.example.easyride.ui.rider.rider_home;

public class user_profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // I DONT KNOW WHY THIS IS RED, BUT IT WORKS SO IDK.


        // Button editButton = findViewById(R.id.edit_contact_button);


        /*
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), rider_home.class);
                startActivity(i);
            }

        });

    */
    }

}
