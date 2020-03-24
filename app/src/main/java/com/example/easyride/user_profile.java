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

<<<<<<< HEAD
import com.example.easyride.R;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.example.easyride.ui.login.LoginActivity;
=======
import com.example.easyride.ui.driver.driver_home;
>>>>>>> master
import com.example.easyride.ui.rider.rider_home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;




// User profile screen that handles Name, Contact info, and rating.
// Should be able to edit contact info
<<<<<<< HEAD
public class user_profile extends AppCompatActivity{
=======
public class user_profile extends AppCompatActivity  implements EditInfoFragment.myListener{

    private String userID;
    private String mode;
    private DocumentReference docRef;
    private FirebaseFirestore db;
    private TextView riderName, Email, Phone, Rating;

>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        userID = intent.getStringExtra("ID");
        mode = intent.getStringExtra("mode");

        // init database
        db = FirebaseFirestore.getInstance();
        docRef = db.collection(mode).document(userID);

<<<<<<< HEAD

        //EasyRideUser user = getCurrentRiderInfo();

        //Rider settingInstance = Rider.getInstance(user);



        //Button editButton = findViewById(R.id.edit_contact_button);
=======
        // TextView assign
        riderName = findViewById(R.id.user_name);
        Email = findViewById(R.id.email);
        Phone = findViewById(R.id.ph);
        Rating = findViewById(R.id.rating);
>>>>>>> master

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
    public void updateInfo(String email, String phone) {

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
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(phone)
                    .build();
            user.updateProfile(profileChangeRequest);
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
            startActivity(new Intent(this, rider_home.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
