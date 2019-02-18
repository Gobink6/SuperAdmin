package com.katomaran.robotics.superadmin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class AddWinform extends AppCompatActivity {
    EditText ET_FROMID, ET_FROMNAME,ET_FROMLOCATION;
    ListView listView;
    Button btn_addfrom;
    String getFromid, getfromname,getfromlocation;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    Context ctx;
    TextView invalidme;
    String response;
   // String windform_url;
    String Windform;
    BackgroundTask backgroundTask = new BackgroundTask();
    //SET ERROR MESSAGE
    boolean cancel_error = false;
    View focusView = null;
    //pop window
    Dialog dialog;
    Button BT_add, btnCancel;
    TextView DIALOG_TX_TEXT;
    Savepref savepref = new Savepref();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super2);

        //---get the EditText and Button view
        btn_addfrom = findViewById(R.id.fromclick);
        ///ET_FROMID = findViewById(R.id.windfromid);
        ET_FROMNAME = findViewById(R.id.fromname);
        ET_FROMLOCATION = findViewById(R.id.fromlocation);
        invalidme = findViewById(R.id.invalidme);
        //pop window
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Custom Alert Dialog");
        btnCancel = (Button) dialog.findViewById(R.id.cancel);
        DIALOG_TX_TEXT = dialog.findViewById(R.id.editText);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //toolbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //set toolbar titile
        getSupportActionBar().setTitle("Windfarm");
        btn_addfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from email and passord field
                //  getFromid = ET_FROMID.getText().toString();

                getfromname = ET_FROMNAME.getText().toString();
                getfromlocation = ET_FROMLOCATION.getText().toString();

                JSONObject json = new JSONObject();
                JSONObject manJson = new JSONObject();
                try {
                    manJson.put("name", getfromname);
                    manJson.put("location",getfromlocation);
                    json.put("wind_farm", manJson);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Windform = json.toString();
                String Host = savepref.getString(getApplicationContext(), "Host");
               String  windform_url = "https://"+Host+"/api/v1/wind_farms";
                if (TextUtils.isEmpty(getfromname) || TextUtils.isEmpty(getfromlocation)) {
                    if (TextUtils.isEmpty(getfromname)){
                        ET_FROMNAME.setError("This WindFarm Name is required");
                        focusView = ET_FROMNAME;
                        cancel_error = true;
                    }
                    else if (TextUtils.isEmpty(getfromlocation)){
                        ET_FROMLOCATION.setError("This WindFarm Location is required");
                        focusView = ET_FROMLOCATION;
                        cancel_error = true;
                    }

                }else
                {
                    new AsyncLink().execute(windform_url, Windform);
                }
                    /*    try {
                            windform_url = "http://sendan.in/api/v1/wind_forms";
                            response =   backgroundTask.execute(windform_url, Windform).get();
                            windform_url = "";
                            Windform = "";

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (CancellationException e ){
                            e.printStackTrace();
                        } */

                // url


               // Log.i("reponse", response);
            }
        });


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

    public class AsyncLink extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;
        ProgressDialog pdLoading = new ProgressDialog(AddWinform.this);


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your Rails file resides
                url = new URL(params[0]);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from rails
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //Json_convert String
                String jsons = params[1].toString();
                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsons);
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
                if (response_code == HttpURLConnection.HTTP_CREATED || (response_code == HttpURLConnection.HTTP_UNAUTHORIZED) || (response_code == 422)) {
                    InputStream input = null;
                    if (response_code == HttpURLConnection.HTTP_CREATED) {
                        // Read data sent from server
                        input = conn.getInputStream();
                    }
                    if (response_code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        input = conn.getErrorStream();
                    }
                    if (response_code == 422) {
                        input = conn.getErrorStream();
                    }
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
            String Status = "";
            String inu = "";
            String error_msg = "";

            boolean status;
            pdLoading.dismiss();
            //this method will be running on UI thread
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            try {
                JSONObject reader = new JSONObject(s);
              //JSONParser jParser = new JSONParser();
                JSONObject jsonObject = new JSONObject(s);
              //  String mod = sys.getString("name");
                status = jsonObject.getBoolean("status");
                String message = jsonObject.getString("message");

                if (!status) {
                    inu = "Windfrom Already Register";
                    invalidme.setText(inu);
                    invalidme.setTextColor(Color.parseColor("#FF0000"));
                    DIALOG_TX_TEXT.setText(message);
                    dialog.show();
                    if (message.equalsIgnoreCase("Invalid Auth Token")) {
                        Intent intent = new Intent(AddWinform.this, Loginpage.class);
                        Toast.makeText(AddWinform.this, "Seesion Timeout Login Again", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), Status, Toast.LENGTH_SHORT).show();
                } else if (status) {
                    inu = "WindFrom Successfully added";
                    invalidme.setText(inu);
                    invalidme.setTextColor(Color.parseColor("#0000FF"));
                    DIALOG_TX_TEXT.setText(message);
                    dialog.show();
                    Toast.makeText(getApplicationContext(), Status, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //this method will be running on UI thread


            //  Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
        }
    }

    public void details() throws JSONException {
        String Result = backgroundTask.getResult();

        if (Result != null) {
            JSONArray jsonArray = new JSONArray(Result);
            String[] mail = new String[jsonArray.length()];
            String[] Phone = new String[jsonArray.length()];
            String[] Role = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                mail[i] = obj.getString("message");
            }
            StringBuffer resultmail = new StringBuffer();
            StringBuffer resultphone = new StringBuffer();
            StringBuffer resultrole = new StringBuffer();
            for (int i = 0; i < mail.length; i++) {
                resultmail.append(mail[0]);
                resultphone.append(Phone[0]);
                resultrole.append(Role[0]);
                //result.append( optional separator );
            }


            // method = "link";
            // mailidx = mailidx + Arrays.toString(mail);
            //  phone = phone + Arrays.toString(Phone);
            // role = role + Arrays.toString(Role);
            // ET_ROLE.setText(role);
            //  ET_EMAIL.setText(mailidx);
            //ET_PHONE.setText(phone);

        } else {
            // liner();
        }
    }
}
