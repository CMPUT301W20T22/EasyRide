package com.example.easyride.ui.rider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.example.easyride.data.model.EasyRideUser;
import com.example.easyride.data.model.Rider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

// Allows Rider to pay for ride with QR code.

public class QR_Pay extends AppCompatActivity {
    public ArrayList<Ride> DataList;
    private Ride rideReq;
    private int position;
    private Rider alright;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pay);
        Intent intent = getIntent();
        String cost = intent.getStringExtra("cost");
        Button backButton = findViewById(R.id.qr_pay_button);
        position = intent.getIntExtra("position", 0);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        alright = new Rider(new EasyRideUser(userID)) {
            @Override
            public void onDataLoaded() {
                loadRide();
            }
        };
        ImageView image;
        image = findViewById(R.id.QRView);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(cost, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            //Bitmap bitmap = StringToBitMap("PAID");

            image.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * https://stackoverflow.com/questions/39959747/how-to-convert-string-into-image-android-studio
    **/
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    private void loadRide(){
        DataList = alright.getActiveRequests();
        rideReq = DataList.get(position);
        if (rideReq.isRidePaid()){
            rideFinished();
        }
    }

    private void rideFinished(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("YAY! The driver accepted your pay. Press ok to go back to home screen");
        // Set up the input
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        // Set up the buttons
        builder.setPositiveButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getApplicationContext(), RiderHome.class);
                startActivity(i);
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }


}
