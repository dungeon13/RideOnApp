package com.example.rideonapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Details extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        JSONObject jsonObject;
        String s="";
        try {
             jsonObject=new JSONObject( intent.getExtras().getString("data"));
             s= jsonObject.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final logout lg = new logout(getApplicationContext());
        Button logout = findViewById(R.id.logout);
        textView = findViewById(R.id.textView);

        textView.setText(s);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lg.execute("");
                finish();
            }
        });

    }
}
