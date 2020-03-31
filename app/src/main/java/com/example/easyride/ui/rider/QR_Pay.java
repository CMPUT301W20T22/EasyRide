package com.example.easyride.ui.rider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Allow rider to pay the ride with QR code.
 * @author T22
 * @version 1.0
 */
public class QR_Pay extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pay);
        Intent intent = getIntent();
        String cost = intent.getStringExtra("cost");
        Button backButton = findViewById(R.id.qr_pay_button);

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

}
