package com.example.easyride.ui.rider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

// Allows Rider to pay for ride with QR code.

public class QR_Pay extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pay);
        Intent intent = getIntent();
        String cost = intent.getStringExtra("cost");


        ImageView image;
        image = findViewById(R.id.QRView);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
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
}
