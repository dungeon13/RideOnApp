package com.example.rideonapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class logout extends AsyncTask<String,Void,String> {
    private Context context;
    logout(Context context){
        this.context = context;
    }
    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String inputLine = "";
        try{
            URL url = new URL("http://192.168.43.61:8080/user/logout");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");



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
        }catch (Exception e){

        }
        return result;
    }
    @Override
    public  void onPostExecute(String result){
        super.onPostExecute(result);
        JSONObject jsonObject = null;
        String message = "";
        boolean flag = false;
        try{
            jsonObject = new JSONObject(result);
            message  = jsonObject.getString("message");
            if(message.equals("1")){
                flag = true;
            }
        }catch (Exception e){
            Toast toast  = Toast.makeText(context,"Error",Toast.LENGTH_SHORT);
            toast.show();
        }
        if(flag){
            Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(context,MainActivity.class);
            context.startActivity(intent);
        }
        else{
            Toast toast = Toast.makeText(context,"Error",Toast.LENGTH_SHORT);
            toast.show();
        }
        //Intent intent = new Intent(this,MainActivity.class);
    }
}
