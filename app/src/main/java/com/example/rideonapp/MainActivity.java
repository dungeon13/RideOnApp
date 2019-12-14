package com.example.rideonapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = findViewById(R.id.loginbutton);
        //Button signUp = findViewById(R.id.signbutton);
        TextView v1 = findViewById(R.id.signup);
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignup();
            }
        });
        final EditText emailbox = findViewById(R.id.email);
        final EditText passwordbox = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailbox.getText().toString();
                String password = passwordbox.getText().toString();
                if(email.length()!=0 && password.length()!=0){
//
                    JSONObject json = new JSONObject();
                    try {
                        json.put("username",email);
                        json.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new getResponse().execute(json.toString());
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Empty Field",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
    public void showSignup(){
        Intent intent = new Intent(this,Signup.class);//InitiaPos.class);
        startActivity(intent);
    }
    public  class getResponse extends AsyncTask<String,Void,String>{

        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... strings) {
            //String url = "http://192.168.43.61:8080/demo";
            String result="";
            String inputLine;
            try{
                URL url = new URL("http://192.168.43.61:8080/user/login");
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
                Toast toast = Toast.makeText(getApplicationContext(),"Loggin",Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(),choice.class);
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

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }

        }
    }
}
