package com.example.rideonapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class selectpoint extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    getResponse response = new getResponse();
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final logout lg = new logout(getApplicationContext());
        setContentView(R.layout.activity_selectpoint);
        Button submit = findViewById(R.id.submit);
        Button logout = findViewById(R.id.logout);
        final Spinner sp1 = findViewById(R.id.sp1);
        final Spinner sp2 = findViewById(R.id.sp2);
        sp1.setOnItemSelectedListener(this);
        sp2.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.points,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lg.execute("");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startpoint = sp1.getSelectedItem().toString();
                String endpoint = sp2.getSelectedItem().toString();
                JSONObject jsonObject = new JSONObject();
                if(!startpoint.equals(endpoint)){
                    try{
                        jsonObject.put("spoint",startpoint);
                        jsonObject.put("epoint",endpoint);
                        new getResponse().execute(jsonObject.toString());

                    }catch (Exception e){
                        Toast toast = Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Destination and Starting Points Same",Toast.LENGTH_SHORT);
                    toast.show();
                    //showbook2();
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position,long id){
        //On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

    }
    public void onNothingSelected(AdapterView<?> arg0){
        //To
    }


    public  class getResponse extends AsyncTask<String,Void,String> {
        String js="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        @Override
        protected String doInBackground(String... strings) {
            String result="";
            String inputLine;
            try{
                URL url = new URL("http://192.168.43.61:8080/cycle/book");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                //connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");



                OutputStreamWriter writer = new OutputStreamWriter(
                        connection.getOutputStream());
                JSONObject jsonObject = new JSONObject(strings[0]);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonresult = null;
            String data = "";
            // here the result is in the form of string s
            try{
                jsonresult = new JSONObject(s);
                data = jsonresult.getString("message");

            }catch (Exception e){
                Toast toast = Toast.makeText(getApplicationContext(),"Some Error",Toast.LENGTH_SHORT);
                toast.show();
            }
            if(data.equals("1")){  //
                Toast toast = Toast.makeText(getApplicationContext(),"Booked",Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(),timer.class);
                startActivity(intent);

            }
            else if(!data.isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"No Response",Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
