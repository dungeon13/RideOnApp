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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button signup = findViewById(R.id.signbutton);
        final EditText fnamebox = findViewById(R.id.fname);
        final EditText lnamebox = findViewById(R.id.fname);
        final EditText emailbox = findViewById(R.id.email);
        final EditText passwordbox = findViewById(R.id.password);
        final EditText rollbox = findViewById(R.id.roll);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject details = new JSONObject();
                String fname = fnamebox.getText().toString();
                String lname = lnamebox.getText().toString();
                //String email = emailbox.getText().toString();
                String password = passwordbox.getText().toString();
                String roll = rollbox.getText().toString();
                try{
                    details.put("firstname",fname );
                    details.put("lastname",lname);
                    //details.put("username",email);
                    details.put("password",password);
                    details.put("roll",roll);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                //Toast toast = Toast.makeText(getApplicationContext(),details.toString(),Toast.LENGTH_SHORT);
                //toast.show();
                new getResponse().execute(details.toString());
            }
        });

    }
    public class getResponse extends AsyncTask<String,Void,String>{
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

                URL url = new URL(con.ip+"/user/signup");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");



                OutputStreamWriter writer = new OutputStreamWriter(
                        connection.getOutputStream());

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
                JSONObject jsonresult = null;
                try{
                    jsonresult = new JSONObject(result);
                    //Log.v("Yolaaa",result);
                    //Log.v("Yola", jsonresult.getString("message"));
//                    JSONObject error = new JSONObject(jsonresult.toString());
//                    Log.v("YOLA",error.getString("name"));

                }catch(Exception e){
                    Log.v("Error","Some Exception");
                }
//                Toast toast = Toast.makeText(getApplicationContext(),jsonresult.getString("message"),Toast.LENGTH_SHORT);
//                toast.show();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;

        }
        @Override
        public  void onPostExecute(String result){
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            boolean flag = false;
            String message = "";
            try{
                jsonObject = new JSONObject(result);
                message  = jsonObject.getString("message");
                if(message.equals("1")){
                    flag = true;
                }
            }catch (Exception e){

            }
            if(flag){
                Toast toast = Toast.makeText(getApplicationContext(),"SignUp Succes",Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
