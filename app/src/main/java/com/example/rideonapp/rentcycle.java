package com.example.rideonapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class rentcycle extends AppCompatActivity {
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentcycle);
        image = findViewById(R.id.image);
        image.setImageResource(R.drawable.comingsoon);
    }
}
