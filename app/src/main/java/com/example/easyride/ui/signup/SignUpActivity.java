package com.example.easyride.ui.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.easyride.R;
import com.example.easyride.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Sign Up Activity where the user can create a new account for them.
 * @author T22
 * @version 1.0
 */

public class SignUpActivity extends AppCompatActivity {

    EditText mFullName, mEmail, mPassword, mPhone;
    Button signUpBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    String Mode;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.mEmail);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.Phone);
        signUpBtn = findViewById(R.id.signUpBtn);

        Intent intent = getIntent();
        Mode = intent.getStringExtra("Mode");


        // ActionBar
        toolbar = findViewById(R.id.actionBar);
        // Add Support ActionBar
        // https://stackoverflow.com/questions/31311612/how-to-catch-navigation-icon-click-on-toolbar-from-fragment
        // Author: https://stackoverflow.com/users/6645645/badr-el-amrani
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        // init database
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.loading);

        // Set onClickListener for the SignUpButton
        // create new User Profile whenever the button is clicked
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = mEmail.getText().toString().trim();
                final String FullName = mFullName.getText().toString().trim();
                final String Password = mPassword.getText().toString().trim();
                final String Phone = mPhone.getText().toString().trim();

                if (TextUtils.isEmpty(Email) || !(Patterns.EMAIL_ADDRESS.matcher(Email).matches())) {
                    mEmail.setError("Please enter the correct format");
                    mEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(FullName)) {
                    mFullName.setError("Name is Required");
                    mFullName.requestFocus();
                    return;
                }


                if (TextUtils.isEmpty(Phone) || Phone.length() != 10) {
                    mPhone.setError("Phone number is required");
                    mPhone.requestFocus();
                    return;
                }

                if (Password.length() < 5) {
                    mPassword.setError("Password Must Be >= 5 Characters");
                    mPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Create the user account
                // Add data to the database
                fAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Add the data to the database
                            // Based on the Status (Rider/Driver)
                            // The document of the collection will be stored based on the created account ID
                            FirebaseUser user = fAuth.getCurrentUser();

                            // Update the DisplayName
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(FullName)
                                    .build();

                            user.updateProfile(profileChangeRequest);

                            // get user ID
                            String ID = user.getUid();

                            Map<String, Object> data = new HashMap<>();
                            data.put("Email", Email);
                            data.put("Name", FullName);
                            data.put("Phone", Phone);

                            db.collection(Mode).document(ID)
                                    .set(data)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ERROR", e.getMessage());
                                        }
                                    });

                            Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                            i.putExtra("mode", Mode );
                            startActivity(i);
                            finish();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this,"Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

}


