package com.example.rideonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.rideonapp.MainActivity.PREFS_NAME;
import static com.example.rideonapp.MainActivity.PREF_USERNAME;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds;
    TextView timer;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        timer = findViewById(R.id.timer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        user = sharedPreferences.getString(PREF_USERNAME,null);
        Button unlock = findViewById(R.id.unlock);
        Button endride = findViewById(R.id.endride);
        handler = new Handler() ;
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckConnectivity check = new CheckConnectivity();
                Boolean flag = check.isConnected(getApplicationContext());
                if(flag){
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    new getResponse1().execute("");
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
        endride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = timer.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username",user);
                    jsonObject.put("time",s1);
                }catch (Exception e){
                    Log.v("error","JSON Error in 2");
                }


                stop();
                CheckConnectivity check = new CheckConnectivity();
                Boolean flag = check.isConnected(getApplicationContext());
                if(flag){
                    new getResponse2().execute(jsonObject.toString());
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }
    public  void stop(){
        TimeBuff += MillisecondTime;
        handler.removeCallbacks(runnable);
        MillisecondTime = 0L ;
        StartTime = 0L ;
        TimeBuff = 0L ;
        UpdateTime = 0L ;
        Seconds = 0 ;
        Minutes = 0 ;
        MilliSeconds = 0 ;
        timer.setText("00:00:00");
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(30.3544, 76.3713);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Thapar University"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public class getResponse1 extends AsyncTask<String,Void,String>{

        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... strings) {
            //String url = "http://192.168.43.61:8080/demo";
            String result="";
            String inputLine;
            constants con = new constants();
            try{
                URL url = new URL(con.ip+"/cycle/unlock");
                //URL url = new URL(con.ip+"/user/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                //connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");



                OutputStreamWriter writer = new OutputStreamWriter(
                        connection.getOutputStream());
                //JSONObject jsonObject = new JSONObject(strings[0]);
                //System.out.println(jsonObject.toString());
                writer.write(strings[0]);

                writer.flush();
                writer.close();



                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder builder = new StringBuilder();

                // reading while line is not null
                while ((inputLine = reader.readLine()) != null) {
                    builder.append(inputLine);
                }

                reader.close();
                streamReader.close();

                result = builder.toString();
                System.out.println(result);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            JSONObject jsonresult = null;
            String data = "";
            //Log.v("message",data);
            try{
                jsonresult = new JSONObject(result);
                data = jsonresult.getString("message");

            }catch (Exception e){
                Toast toast = Toast.makeText(getApplicationContext(),"Some Error",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(data.equals("1")){
                Toast toast = Toast.makeText(getApplicationContext(),"Unlocked",Toast.LENGTH_SHORT);
                toast.show();

            }
            else{
                if(data.isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(),"No Response",Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        }
    }
    public class getResponse2 extends AsyncTask<String,Void,String>{
        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        String time = "";
        @Override
        protected String doInBackground(String... strings) {
            //String url = "http://192.168.43.61:8080/demo";
            String result="";
            String inputLine;
            constants con = new constants();
            time = strings[0];
            try{
                URL url = new URL(con.ip+"/cycle/endRide");
                //URL url = new URL(con.ip+"/user/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                //connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");



                OutputStreamWriter writer = new OutputStreamWriter(
                        connection.getOutputStream());
                //JSONObject jsonObject = new JSONObject(strings[0]);
                //System.out.println(jsonObject.toString());
                Log.v("timer",strings[0]);
                writer.write(strings[0]);

                writer.flush();
                writer.close();



                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder builder = new StringBuilder();

                // reading while line is not null
                while ((inputLine = reader.readLine()) != null) {
                    builder.append(inputLine);
                }

                reader.close();
                streamReader.close();

                result = builder.toString();
                System.out.println(result);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            JSONObject jsonresult = null;
            String data = "";
            Log.v("message",data);
            try{
                jsonresult = new JSONObject(result);
                data = jsonresult.getString("message");

            }catch (Exception e){
                Toast toast = Toast.makeText(getApplicationContext(),"Some Error",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(data.equals("1")){
                Toast toast = Toast.makeText(getApplicationContext(),"Ending Ride",Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(Map.this,Details.class);
                intent.putExtra("data",time);
                startActivity(intent);
            }
            else{
                if(data.isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(),"No Response",Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        }
    }
    public Runnable runnable = new Runnable() {



        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timer.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };
}
