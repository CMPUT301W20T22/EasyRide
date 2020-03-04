package com.example.easyride;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button driver_mode_button = findViewById(R.id.driver_mode_button);

    driver_mode_button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setContentView(R.layout.activity_login);

      }
    });



  }
}
