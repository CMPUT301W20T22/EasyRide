package com.example.easyride.ui.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyride.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Activity to scan the QR code whenever the Driver finishes the ride,
 * and want to accept the payment.
 * @author T22
 * @version 1.0
 */

public class QR_Scan extends AppCompatActivity {
    private Button qr_scan_button;
    private String cost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        qr_scan_button = (Button)findViewById(R.id.qr_scan_button);
        final Activity activity = this;
        qr_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator =  new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan the QR code from Rider");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

    }

    /**
     * Handle the result of scanning.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result.getContents() == null){
            Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            cost = result.getContents();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
