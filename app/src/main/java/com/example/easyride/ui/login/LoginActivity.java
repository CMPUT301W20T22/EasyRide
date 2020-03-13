package com.example.easyride.ui.login;

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
import com.example.easyride.data.DataBaseManager;
import com.example.easyride.map.MapsActivity;
import com.example.easyride.ui.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button signUpbtn, loginBtn;
    String Mode;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpbtn = findViewById(R.id.signup);
        loginBtn = findViewById(R.id.login);
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);

        Intent intent = getIntent();
        Mode = intent.getStringExtra(MainActivity.mode);

        // init database
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        /**
         * Sign Up Button OnClickListener
         * @param View.OnClickListener
         */
        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("Mode", Mode);
                startActivity(intent);
            }
        });

        /**
         * Log In Button OnClickListener
         * @param View.OnClickListener
         */
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || !(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                    mEmail.setError("Please enter the correct email format");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required");
                    return;
                }

                if (password.length() < 5) {
                    mPassword.setError("Password Must Be >= 5 Characters");
                    return;
                }


                // authenticate the user and log in the application
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Add the data to the database
                            String user = fAuth.getCurrentUser().getDisplayName();
                            Map<String, Object> data = new HashMap<>();
                            data.put("Email: ", email);
                            data.put("Password: ", password);
                            data.put("Name: ", user);

                            db.collection(Mode).document(user)
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

                            // Start new Activity
                            Toast.makeText(LoginActivity.this, "Enjoy the App! Rate us 5 star", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            intent.putExtra("Mode", Mode);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
