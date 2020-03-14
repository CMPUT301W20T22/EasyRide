package com.example.easyride.ui.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText mFullName, mEmail, mPassword;
    Button signUpBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    String Mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFullName = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        signUpBtn = findViewById(R.id.signUpBtn);

        Intent intent = getIntent();
        Mode = intent.getStringExtra("Mode");

        // init database
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.loading);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = mEmail.getText().toString().trim();
                final String FullName = mFullName.getText().toString().trim();
                final String Password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(Email) || !(Patterns.EMAIL_ADDRESS.matcher(Email).matches())) {
                    mEmail.setError("Please enter the correct format");
                    return;
                }

                if (TextUtils.isEmpty(FullName)) {
                    mFullName.setError("Name is Required");
                    return;
                }

                if (Password.length() < 5) {
                    mPassword.setError("Password Must Be >= 5 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                /**
                 * Create the user account
                 * Add data to the database
                 */

                fAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Add the data to the database
                            // Based on the Status (Rider/Driver)
                            // The document of the collection will be stored based on the created account ID
                            FirebaseUser user = fAuth.getCurrentUser();
                            String ID = user.getUid();
                            Map<String, Object> data = new HashMap<>();
                            data.put("Email: ", Email);
                            data.put("Password: ", Password);
                            data.put("Name: ", FullName);

                            db.collection(Mode).document(ID)
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // ignore this
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ERROR", e.getMessage());
                                        }
                                    });

                            Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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


