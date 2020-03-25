package com.example.easyride;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.ui.driver.driver_home;
import com.example.easyride.ui.rider.RiderHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



// User profile screen that handles Name, Contact info, and rating.
// Should be able to edit contact info
public class user_profile extends AppCompatActivity  implements EditInfoFragment.myListener{

    private String userID;
    private String mode;
    private DocumentReference docRef;
    private FirebaseFirestore db;
    private TextView riderName, Email, Phone, Rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // I DON'T KNOW WHY THIS IS RED, BUT IT WORKS SO IDK.

        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        mode = intent.getStringExtra("mode");

        // init database
        db = FirebaseFirestore.getInstance();
        docRef = db.collection(mode).document(userID);

        // TextView assign
        riderName = findViewById(R.id.user_name);
        Email = findViewById(R.id.email);
        Phone = findViewById(R.id.ph);
        Rating = findViewById(R.id.rating);

        // Getting the info from database
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    riderName.setText(doc.getString("Name:"));
                    Email.setText("Email: " + doc.getString("Email:"));
                    Phone.setText("Phone: " + doc.getString("Phone:"));
                }
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

    @SuppressLint("SetTextI18n")
    @Override
    public void updateInfo(String email, String phone, String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Update the information to database

        if (!email.equals("")) {
            Email.setText("Email: " + email);
            docRef.update("Email", email);
            user.updateEmail(email);
        }

        if (!phone.equals("")) {
            Phone.setText("Phone: " + phone);
            docRef.update("Phone", phone);
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
            startActivity(new Intent(this, driver_home.class));
            finish();
        }

        if (mode.equals("rider")){
            startActivity(new Intent(this, RiderHome.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
