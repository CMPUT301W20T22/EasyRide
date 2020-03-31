package com.example.easyride.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easyride.R;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.example.easyride.ui.driver.driver_home;
import com.example.easyride.ui.rider.RiderHome;
import com.example.easyride.ui.signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Map;
import java.util.Objects;

/**
 * Login Activity where the user can log in to the app using their username and password.
 * @author T22
 * @version 1.0
 */
public class LoginActivity extends AppCompatActivity {

    public static final String ID = "";
    EditText mEmail, mPassword;
    Button signUpbtn, loginBtn;
    String Mode;
    FirebaseAuth fAuth;
    boolean isUser;
    FirebaseFirestore db;
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Log In");


        signUpbtn = findViewById(R.id.signup);
        loginBtn = findViewById(R.id.login);
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);

        Intent intent = getIntent();
        Mode = intent.getStringExtra("mode");
        isUser = false;

        // init database
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Sign Up Button OnClickListener
        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connected = isNetworkAvailable();
                if (connected) {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    intent.putExtra("Mode", Mode);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(LoginActivity.this,
                            "No internet connection! You can't sign up at the moment", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // Log In Button OnClickListener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connected = isNetworkAvailable();

                if (connected) {

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

                    // Authenticate the user and log in the application based on the Status (Rider/Driver)
                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = fAuth.getCurrentUser();
                                final String ID = user.getUid();

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

                                            assert document != null;
                                            Map<String, Object> data = document.getData();
                                            assert data != null;
                                            String userEmail = (String) data.get("Email");
                                            String displayname = (String) data.get("Name");
                                            //String password = (String) data.get("Password: ");
                                            EasyRideUser user = new EasyRideUser(userEmail);
                                            //user.setPassword(password);
                                            user.setDisplayName(displayname);
                                            FirebaseInstanceId.getInstance().getInstanceId()
                                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                            if (!task.isSuccessful()) {
                                                                Log.w("Shit: ", "getInstanceId failed", task.getException());
                                                                return;
                                                            }

                                                            // Get new Instance ID token
                                                            String token = Objects.requireNonNull(task.getResult()).getToken();
                                                            db.collection(Mode).document(ID).update("token", token);
                                                        }
                                                    });

                                            /*Log.d("User: ", user.getDisplayName());*/


                                            isUser = document.exists();
                                            if (!isUser) {
                                                Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                                                mEmail.setText("");
                                                mPassword.setText("");
                                            }
                                            // Start new Activity if the user is correct
                                            else if (isUser && Mode.equals("rider")) {
                                                Toast.makeText(LoginActivity.this, "Enjoy the App! Rate us 5 star", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, RiderHome.class);
                                                //user = FirebaseAuth.getInstance().getCurrentUser();
                                                //assert user != null;
                                                //userID = user.getEmail();
                                                Rider alright = Rider.getInstance(user);
                                                intent.putExtra("Mode", Mode);
                                                intent.putExtra("ID", ID);
                                                startActivity(intent);
                                                finish();
                                            } else if (isUser && Mode.equals("driver")) {
                                                Toast.makeText(LoginActivity.this, "Welcome back driver!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, driver_home.class);

                                                intent.putExtra("Mode", Mode);
                                                intent.putExtra("ID", ID);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "Failed with: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mEmail.setText("");
                                mPassword.setText("");
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(LoginActivity.this,
                            "No internet connection! You can't login at the moment!", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /**
     * Method to check the internet connectivity of the device
     * @return boolean
     */
    // https://stackoverflow.com/questions/9570237/android-check-internet-connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

