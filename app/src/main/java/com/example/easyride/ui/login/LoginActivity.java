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
import android.widget.Toast;

import com.example.easyride.MainActivity;
import com.example.easyride.R;
import com.example.easyride.data.model.Driver;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.example.easyride.map.MapsActivity;
import com.example.easyride.ui.driver.SearchRequestActivity;
import com.example.easyride.ui.driver.driver_home;
import com.example.easyride.ui.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String ID = "";
    EditText mEmail, mPassword;
    Button signUpbtn, loginBtn;
    String Mode;
    FirebaseAuth fAuth;
    boolean isUser;
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
        isUser = false;

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


                // authenticate the user and log in the application based on the Status (Rider/Driver)
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            final String ID = user.getUid();
                            final Intent driver_intent = new Intent(LoginActivity.this, driver_home.class);
                            driver_intent.putExtra("Mode", Mode);
                            driver_intent.putExtra("ID", ID);
                            /*
                             Check if the user existed in the collection (Rider/Driver)
                             If it's not then deny the access to the application
                             https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection/53335711
                            */

                            db.collection(Mode).document(ID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        Map<String, Object> data = document.getData();
                                        String userEmail = (String) data.get("Email: ");
                                        String displayname = (String) data.get("Name: ");
                                        String password = (String) data.get("Password: ");
                                        EasyRideUser user = new EasyRideUser(userEmail);
                                        user.setPassword(password);
                                        user.setDisplayName(displayname);
                                        Log.d("User: ", user.getDisplayName());


                                        isUser = document.exists();
                                        if (!isUser) {
                                            Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                                            mEmail.setText("");
                                            mPassword.setText("");
                                        }
                                        // Start new Activity if the user is correct
                                        else if (isUser && Mode.equals("rider")) {
                                            Toast.makeText(LoginActivity.this, "Enjoy the App! Rate us 5 star", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(LoginActivity.this, rider_home.class);


                                            Rider settingInstance = Rider.getInstance(user);

                                            intent.putExtra("Mode", Mode);
                                            intent.putExtra("ID", ID);

                                            startActivity(intent);
                                            finish();
                                        }

                                        else if (isUser && Mode.equals("driver")) {
                                            Toast.makeText(LoginActivity.this, "Welcome back driver!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(LoginActivity.this, driver_home.class);
                                            Driver settingInstance = Driver.getInstance(user);

                                            intent.putExtra("Mode", Mode);
                                            intent.putExtra("ID", ID);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this,
                                                "Failed with: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mEmail.setText("");
                            mPassword.setText("");
                        }

                    }
                });
            }
        });
    }
}
