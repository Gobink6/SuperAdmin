package com.katomaran.robotics.superadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.Arrays;

public class Addu_a3Activity extends AppCompatActivity {
    EditText ET_NAME,ET_EMAIL,ET_PHONE,ET_WINDID;
    Spinner ET_ROLE,ET_SPINNER;
    String Name,Email,Phone,Role,Windid,user;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addu_a3);
        //toolbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //set toolbar titile
        getSupportActionBar().setTitle("Register");
        // Get Reference to variables
        ET_NAME = findViewById(R.id.name);
        ET_EMAIL =findViewById(R.id.email1);
        ET_PHONE = findViewById(R.id.phone);
        ET_SPINNER = findViewById(R.id.spinnerform);
        ET_ROLE = findViewById(R.id.spinner1);
        getJSON("http://192.168.0.106:3000/getallforms");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
    // Triggers when LOGIN Button clicked
    public void submit(View v) {

        // Get text from email and passord field
          Name = ET_NAME.getText().toString();
          Email = ET_EMAIL.getText().toString();
          Phone= ET_PHONE.getText().toString();
         // Windid= ET_SPINNER.getSelectedItem().toString();
          Role = ET_ROLE.getSelectedItem().toString();
        String email = ET_EMAIL.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern))
        {
            // Initialize  AsyncLogin() class with email and password
            new AsyncLink().execute(Name,Email,Phone,Role,user);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
        }


    }
    public class AsyncLink extends AsyncTask<String,String,String> {

        HttpURLConnection conn;
        URL url = null;
        ProgressDialog pdLoading = new ProgressDialog(Addu_a3Activity.this);
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(true);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your ph p file resides
                url = new URL("http://192.168.0.106:3000/userregister");

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
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", params[0])
                        .appendQueryParameter("email", params[1])
                        .appendQueryParameter("phone", params[2])
                        .appendQueryParameter("role", params[3])
                        .appendQueryParameter("id", params[4]);
                Log.i("Windform4Activity", Name+ Email+ Phone+ Windid + Role);
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
            }
            finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //this method will be running on UI thread
            try {
                loadspinners(s);
                pdLoading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
        }
    }
    private void getJSON(final String urlWebService) {

        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                try {
                    loadspinners(s);
                    } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {

                try {


                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {

                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }
    public void loadspinners(String json) throws JSONException {

        if (json != null) {

                JSONArray jsonArray = new JSONArray(json);
                String[] username = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    username[i] = obj.getString("user");
                    // spinner2.setAdapter(new ArrayAdapter<String>(Windform4Activity.this, android.R.layout.simple_spinner_dropdown_item, username));
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, username);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                   ET_SPINNER.setAdapter(dataAdapter);
                  ET_SPINNER.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItemText = (String) parent.getItemAtPosition(position);
                            // get the selected item text
                            user = "";
                            user = ET_SPINNER.getSelectedItem().toString();

                            Log.i("Windform4Activity", user);
                            Toast.makeText
                                    (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                                    .show();
                            //  method = "done";
                            // excute();
                            Log.i("Windform4Activity", user);

                            }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }
        }

    }
