package com.katomaran.robotics.superadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Map;

public class Addusers extends AppCompatActivity {
    EditText ET_NAME,ET_EMAIL,ET_PHONE;
    Spinner ET_WINDFROM,ET_WINDMILL,ET_ROLE;
    String Name, Email, Phone, spinform, Role, spinmill;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    Savepref savepref = new Savepref();
    String Host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addusers);
        //toolbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //set toolbar titile
        getSupportActionBar().setTitle("Adduser");

        Host = savepref.getString(getApplicationContext(), "Host");
        // Get Reference to variables
        ET_NAME = findViewById(R.id.uname);
        ET_EMAIL =findViewById(R.id.uemail1);
        ET_PHONE = findViewById(R.id.uphone);
        ET_WINDFROM = findViewById(R.id.uspinform);
        ET_ROLE = findViewById(R.id.uspinner1);
        ET_WINDMILL = findViewById(R.id.uspinmill);
    }
    public void submit(View v) {

        // Get text from Name,Email,Phone,Windfrom,Role,Windmill field
        Name = ET_NAME.getText().toString();
        Email = ET_EMAIL.getText().toString();
        Phone = ET_PHONE.getText().toString();
        Role = ET_ROLE.getSelectedItem().toString();
        spinform = ET_WINDFROM.getSelectedItem().toString();
        spinmill = ET_WINDMILL.getSelectedItem().toString();
        // Initialize  AsyncLogin() class with email and password
     //   new AsyncLink().execute(Name,Email,Phone,Role,spinform);

    }
      /*  while(iterator.next())

    {

        Map.Entry pair = (Map.Entry) it.next();

        String title = pair.getKey().toString();

        if(title.equals("one"))

        {

//execute task1;

        }else if(title.equals("two"))

        {

//execute task2;

        }


    } */
      private class AsyncLogin extends AsyncTask<String, String, String> {
          // start progressDialog
          ProgressDialog pdLoading = new ProgressDialog(Addusers.this);
          HttpURLConnection conn;
          URL url = null;

          @Override
          protected void onPreExecute() {
              super.onPreExecute();

              //this method will be running on UI thread
              pdLoading.setMessage("\tLoading...");
              pdLoading.setCancelable(false);
              pdLoading.show();

          }

          @Override
          protected String doInBackground(String... params) {
              try {

                  //  URL address where your Rails file resides
                  url = new URL("http://192.168.0.104:3000/check");

              } catch (MalformedURLException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                  return "exception";
              }
              try {
                  // Setup HttpURLConnection class to send and receive data from rails and mysql
                  conn = (HttpURLConnection) url.openConnection();
                  conn.setReadTimeout(READ_TIMEOUT);
                  conn.setConnectTimeout(CONNECTION_TIMEOUT);
                  conn.setRequestMethod("POST");

                  // setDoInput and setDoOutput method depict handling of both send and receive
                  conn.setDoInput(true);
                  conn.setDoOutput(true);

                  // Append body to URL
                  Uri.Builder builder = new Uri.Builder()
                          .appendQueryParameter("name", params[0])
                          .appendQueryParameter("pass", params[1])
                          .appendQueryParameter("token", params[2]);
                  String query = builder.build().getEncodedQuery();

                  // Open connection for sending data
                  OutputStream os = conn.getOutputStream();
                  BufferedWriter writer = new BufferedWriter(
                          new OutputStreamWriter(os, "UTF-8"));
                  writer.write(query);
                  writer.flush();
                  writer.close();
                  os.close();
                  conn.connect();

              } catch (IOException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
                  return "exception";
              }

              try {

                  int response_code = conn.getResponseCode();

                  // Check if successful connection made
                  if (response_code == HttpURLConnection.HTTP_OK) {

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
              } finally {
                  conn.disconnect();
              }

          }


          @Override
          protected void onPostExecute(String s) {
              String singleParsed = "";
              String phone = "";
              String inu, suc;
              if (s == s) {

              }
// successfully received database details
              try {
                  JSONArray jsonArray = new JSONArray(s);
                  String[] rows = new String[jsonArray.length()];
                  for (int i = 0; i < jsonArray.length(); i++) {
                      JSONObject obj = jsonArray.getJSONObject(i);
                      String mod = obj.getString("role");
                      String num = obj.getString("phone");
                      singleParsed = singleParsed + (mod);
                      phone = phone + (num);
                  }
              } catch (JSONException e) {
                  e.printStackTrace();
              }
              //this method will be running on UI thread

              pdLoading.dismiss();
              String table = "true";

              if (singleParsed.equalsIgnoreCase("admin")) {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                  //start nxt activity
                  suc = "";
                  // invalid.setText(suc);
                  Intent intent = new Intent(Addusers.this, Addusers.class);
                  startActivity(intent);
                  // display the admin message
                  Toast.makeText(Addusers.this, "Admin", Toast.LENGTH_LONG).show();
                  finish();
              } else if (singleParsed.equalsIgnoreCase("superadmin")) {
                 /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                  Intent intent1 = new Intent(Addusers.this, Addusers.class);
                  startActivity(intent1);
                  //Display the superadmin message
                  Toast.makeText(Addusers.this, "super admin ", Toast.LENGTH_LONG).show();
                  finish();

              } else if (singleParsed.equalsIgnoreCase("user")) {
                 /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                  Intent intent = new Intent(Addusers.this, Addusers.class);
                  startActivity(intent);
                  //Display the user massage
                  Toast.makeText(Addusers.this, "normaluser", Toast.LENGTH_LONG).show();
                  finish();
              } else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

                  Toast.makeText(Addusers.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

              } else if (s.equalsIgnoreCase("error")) {

                  Toast.makeText(Addusers.this, "invalied password", Toast.LENGTH_LONG).show();

              } else if (singleParsed.equalsIgnoreCase("inu")) {
                  inu = "Invalied username or password";
                  //invalid.setText(inu);
                  Toast.makeText(Addusers.this, "invalied password", Toast.LENGTH_LONG).show();

              }
          }

      }
}
