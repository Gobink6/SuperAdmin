package com.katomaran.robotics.superadmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class Loginpage extends Activity {
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    //String singleParsed = "";
    private EditText etEmail;
    private EditText etPassword;
    TextView invalid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        // Get Reference to variables
        etEmail = (EditText) findViewById(R.id.fusername);
        etPassword = (EditText) findViewById(R.id.fpassword);
        invalid =  findViewById(R.id.invalid);
    }
    public void Logins(View arg0) {

        // Get text from email and passord field
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(email,password);
    }
private class AsyncLogin extends AsyncTask<String, String,String>
{
    ProgressDialog pdLoading = new ProgressDialog(Loginpage.this);
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

            // Enter URL address where your php file resides
            url = new URL("http://192.168.0.106:3000/check");

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
                    .appendQueryParameter("pass", params[1]);
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
       String singleParsed = "";
       String inu,suc;
        if(s == s){

        }
// successfully received database details
        try {
            JSONArray jsonArray = new JSONArray(s);
            String[] rows = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String mod = obj.getString("role");
                singleParsed = singleParsed + (mod);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //this method will be running on UI thread

        pdLoading.dismiss();


        if(singleParsed.equalsIgnoreCase("admin"))
        {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
            //start nxt activity
            suc = "";
            invalid.setText(suc);
            Intent intent = new Intent(Loginpage.this,MainActivity.class);
            startActivity(intent);

            // display the admin message
            Toast.makeText(Loginpage.this, "Admin", Toast.LENGTH_LONG).show();
        }else if (singleParsed.equalsIgnoreCase("superadmin")){
                 /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
            Intent intent1 = new Intent(Loginpage.this,MainActivity.class);
            startActivity(intent1);
            //Display the superadmin message
            Toast.makeText(Loginpage.this, "super admin ", Toast.LENGTH_LONG).show();


        } else if (singleParsed.equalsIgnoreCase("user")) {
                 /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                          Intent intent = new Intent(Loginpage.this,MainActivity.class);
                          startActivity(intent);

            //Display the user massage
            Toast.makeText(Loginpage.this, "normaluser", Toast.LENGTH_LONG).show();

        }else if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {

            Toast.makeText(Loginpage.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

        }else if (s.equalsIgnoreCase("error")) {

            Toast.makeText(Loginpage.this, "invalied password", Toast.LENGTH_LONG).show();

        }else if (singleParsed.equalsIgnoreCase("inu")) {
             inu = "Invalied username or password";
            invalid.setText(inu);
            Toast.makeText(Loginpage.this, "invalied password", Toast.LENGTH_LONG).show();

        }
    }

}
}
