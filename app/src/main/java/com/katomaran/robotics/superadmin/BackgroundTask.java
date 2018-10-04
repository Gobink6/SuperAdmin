package com.katomaran.robotics.superadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BackgroundTask extends AsyncTask<String,String,String> {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public  String Result;
    BackgroundAsyncTask backgroundAsyncTask;
        HttpURLConnection conn;
        URL url = null;
       // ProgressDialog pdLoading = new ProgressDialog(BackgroundTask.this);


    protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your ph p file resides
                url = new URL(params[0]);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                //conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");


                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                // JSONArray wind_form=new JSONArray();
                //wind_form.p

//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("name", params[0]);
                //.appendQueryParameter("windmill", params[2])
                //.appendQueryParameter("millactive", params[3]);
                // Log.i("Windform4Activity", Name+ Email+ Phone+ Windid + Role);
                // String query = wind_form..getEncodedQuery();

                // Open connection for sending data
               // OutputStream os = conn.getOutputStream();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                String jsons = params[1].toString();
                os = new BufferedOutputStream(conn.getOutputStream());
                os.write(jsons.getBytes());
                //clean up
                os.flush();
                //writer.write(String.valueOf(manJson));
                // writer.flush();
                // writer.close();
                os.close();
                //BufferedWriter writer = new BufferedWriter(
                //new OutputStreamWriter(os, "UTF-8"));
//                os.write(params[1].getBytes());//writer.write(String.valueOf(wind_form));
//                os.flush();
//                os.close();
//                // writer.flush();
//                //writer.close();
//                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_CREATED) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);

                    }
                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
            finally {
                conn.disconnect();
            }
        }


    @Override
        protected void onPostExecute(String s) {

            Result = s;
       //     backgroundAsyncTask.bacgroundAsyncTask(String.valueOf(s));

        }
    public String getResult() {
        return Result;
    }

}

