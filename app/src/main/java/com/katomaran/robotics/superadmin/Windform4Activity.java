package com.katomaran.robotics.superadmin;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.Attributes;

public class Windform4Activity extends AppCompatActivity {
    private static final String TAG = Windform4Activity.class.getSimpleName();
    Context mContext;


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    String windfrom, user, windmill2, Email, Phone, Name, Last_Name, email;
    int select_role, select_form;
    Spinner spinner, spinner2, spinner3;
    Button mOrder;
    EditText ET_EMAIL, ET_PHONE, ET_NAME, ET_LAST_NAME;
    Spinner ET_ROLE;
    ArrayList<Windform_details> windform_details = new ArrayList<>();
    ArrayList<Windmill_details> windmill_details = new ArrayList<>();
    TextView mItemSelected, mItemSelected1;
    String[] windmill6, windmill, windmille;
    //Windform_id & Windmill_id
    String windform_id, Windmill_id;
    boolean GETJS = false;
    boolean[] check;
    String reg = "";
    String method = "correct";
    String selectedItem_role, Roles;
    //pop window
    Dialog dialog;
    Button BT_add, btnCancel;
    TextView DIALOG_TX_TEXT;
    //SET ERROR MESSAGE
    boolean cancel_error = false;
    View focusView = null;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    ArrayList<String[]> listdata = new ArrayList<String[]>();
    ArrayList<String[]> mylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windform4);
        mOrder = (Button) findViewById(R.id.button3);
        mItemSelected = (TextView) findViewById(R.id.tvItemSelected);
        ET_PHONE = findViewById(R.id.ulphone);
        ET_NAME = findViewById(R.id.ulname);
        ET_EMAIL = findViewById(R.id.ulemail);
        ET_ROLE = findViewById(R.id.ulspinner1);
        ET_LAST_NAME = findViewById(R.id.ulname_last);
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
        getSupportActionBar().setTitle("Windfrom");
        spinner = (Spinner) findViewById(R.id.spinner1);


        ET_ROLE.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem_role = parent.getItemAtPosition(position).toString();
                select_role = ET_ROLE.getSelectedItemPosition() - 1;
                if (selectedItem_role.equals("Select Role")) {
                    spinner.setEnabled(false);
                } else if (selectedItem_role.equals("SuperAdmin") || selectedItem_role.equals("Admin")) {
                    spinner.setEnabled(true);
                    mOrder.setEnabled(false);
                    method = "windform";
                    GETJS = false;
                    getJSON("http://192.168.0.103:3000/api/v1/wind_forms");
                } else if (selectedItem_role.equals("User")) {
                    spinner.setEnabled(true);
                    mOrder.setEnabled(true);
                    method = "Allmill";
                    GETJS = false;
                    getJSON("http://192.168.0.103:3000/api/v1/wind_forms");


                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    //Execute a function when all Async Http Requests are finished
    //Create interface TaskComplete

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
                    loadspinner(s);

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

    private void getJSONS(final String urlWebService) {

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
                    loadIntoListView(s);
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

    public void loadspinner(String json) throws JSONException {
        if (json != null) {
            if (!GETJS) {
                JSONObject jsonobject = new JSONObject(json);
                JSONArray array = jsonobject.getJSONArray("data");
                String[] windformid = new String[array.length()];
                windform_details.clear();
                for (int i = 0; i < array.length(); i++) {

                    JSONObject obj = array.getJSONObject(i);
                    windform_details.add(new Windform_details(obj.getString("name"), obj.getString("id")));
                }
                ArrayAdapter<Windform_details> adapter =
                        new ArrayAdapter<Windform_details>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, windform_details);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object numbers = spinner.getItemAtPosition(position);

                        windform_id = windform_details.get(position).Windform_id;
                        GETJS = true;
                        getJSONS("http://192.168.0.103:3000/api/v1/wind_forms/" + windform_id + "/wind_mills");
                        Toast.makeText(parent.getContext(), "Selected: " + windform_id, Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
            }
        }
    }

    public void loadIntoListView(String json) throws JSONException {
        if (json != null) {
            JSONObject windmills = new JSONObject(json);
            JSONArray array = windmills.getJSONArray("data");
            final String[] windmill1 = new String[array.length()];
            final String[] wind_id = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                windmill1[i] = obj.getString("phone");
                wind_id[i] = obj.getString("id");
                windmill_details.add(new Windmill_details(obj.getString("name"), obj.getString("id")));
                check = new boolean[windmill1.length];
                Log.i("Windform4Activity", "arr: " + Arrays.toString(windmill1));
                //Printing string array list values on screen.
            }
            // arrayList.add(0,"Select Windid");
            mOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Windform4Activity.this);
                    mBuilder.setTitle("Windmills");

                    mBuilder.setMultiChoiceItems(windmill1, check, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                            if (isChecked) {
                                mUserItems.add(position);
                            } else {
                                mUserItems.remove((Integer.valueOf(position)));
                            }
                        }
                    });

                    mBuilder.setCancelable(false);
                    mBuilder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            String item = "";
                            String item_name = "";

                            for (int i = 0; i < mUserItems.size(); i++) {
                                item = item + wind_id[mUserItems.get(i)];
                                item_name = item_name + windmill1[mUserItems.get(i)];

                                if (i != mUserItems.size() - 1) {
                                    item = item + ",";
                                    item_name = item_name + ",";

                                }
                            }

                            mItemSelected.setText(item_name);
                            Windmill_id = item;
                            windmill2 = mItemSelected.getText().toString();
                            Log.i("Windform4Activity", windmill2);
                        }
                    });

                    mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    mBuilder.setNeutralButton("clear all", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            for (int i = 0; i < check.length; i++) {
                                check[i] = false;
                                mUserItems.clear();
                                mItemSelected.setText("");
                            }
                        }
                    });

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            });


            //  spinner.setAdapter(new ArrayAdapter<String>(Windform4Activity.this, android.R.layout.simple_spinner_dropdown_item, windform));
            //spinner3.setAdapter(new ArrayAdapter<String>(Windform4Activity.this, android.R.layout.simple_list_item_multiple_choice, windmill));
            // adapter = new ArrayAdapter<String>(this,
            //      android.R.layout.simple_list_item_multiple_choice, windmill);
            //listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            // listView.setAdapter(adapter);

        } else {

        }
    }

    public void userlink(View view) {
        //Email = ET_EMAIL.getText().toString();
        Phone = ET_PHONE.getText().toString();
        Name = ET_NAME.getText().toString();
        Last_Name = ET_LAST_NAME.getText().toString();
        email = ET_EMAIL.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Roles = ET_ROLE.getSelectedItem().toString();
        String windform = spinner.getSelectedItem().toString();
        if (TextUtils.isEmpty(Phone)) {
            ET_PHONE.setError("This Mobile_No is required");
            focusView = ET_PHONE;
            cancel_error = true;
        } else if (TextUtils.isEmpty(Name)) {
            ET_NAME.setError("This First Name is required");
            focusView = ET_NAME;
            cancel_error = true;
        } else if (TextUtils.isEmpty(Last_Name)) {
            ET_LAST_NAME.setError("This Last Name is required");
            focusView = ET_LAST_NAME;
            cancel_error = true;
        }else if (TextUtils.isEmpty(email)) {
            ET_EMAIL.setError("This Mail_id Name is required");
            focusView = ET_EMAIL;
            cancel_error = true;
        } else if (!(Phone.length() == 10)) {
            ET_PHONE.setError("This Mobile_No is not a correct format");
            focusView = ET_PHONE;
            cancel_error = true;
        } else if (!email.matches(emailPattern)) {
            ET_EMAIL.setError("This Mobile_No is not a correct format");
            focusView = ET_EMAIL;
            cancel_error = true;

        } else {
            new AsyncLink().execute();
            //Toast.makeText(getApplicationContext(), "Invalid email (or) Phone no", Toast.LENGTH_SHORT).show();
        }
    }

    public class AsyncLink extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;
        ProgressDialog pdLoading = new ProgressDialog(Windform4Activity.this);

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

                // Enter URL address where your ruby file resides
                url = new URL("http://192.168.0.103:3000/api/v1/users");

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
                conn.setRequestProperty("Content-Type", "application/json");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                JSONObject user_details = new JSONObject();
                JSONObject params_key = new JSONObject();
                try {
                    params_key.put("first_name", Name);
                    params_key.put("last_name", Last_Name);
                    params_key.put("email", email);
                    params_key.put("phone", Phone);
                    params_key.put("role", select_role);
                    params_key.put("wind_form_id", windform_id);
                    params_key.put("wind_mill_ids", Windmill_id);
                    user_details.put("user", params_key);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String customer_json = user_details.toString();
                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(customer_json);
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
            //this method will be running on UI thread
            String Message = "";
            boolean status;
            pdLoading.dismiss();

            try {
                JSONObject reader = new JSONObject(s);

                /// JSONObject sys  = reader.getJSONObject("data");//JSONParser jParser = new JSONParser();
                JSONObject jsonObject = new JSONObject(s);
                //  String mod = sys.getString("name");
                status = jsonObject.getBoolean("status");
                String message = jsonObject.getString("message");
                if (status) {
                    DIALOG_TX_TEXT.setText(message);
                    dialog.show();
                    Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();

                } else {
                    if (message.equalsIgnoreCase("Invalid Auth Token")) {
                        Intent intent = new Intent(Windform4Activity.this, Loginpage.class);
                        Toast.makeText(Windform4Activity.this, "Seesion Timeout Login Again", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    DIALOG_TX_TEXT.setText(message);
                    dialog.show();
                }
                Message = message + message;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (s.equalsIgnoreCase("exception") || s.equalsIgnoreCase("unsuccessful")) {
                DIALOG_TX_TEXT.setText("Connection Problem!");
                dialog.show();
                Toast.makeText(Windform4Activity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }

    public void details(String json) throws JSONException {
        String mailidx = "";
        String phone = "";
        String role = "";
        if (json != null) {
            JSONArray jsonArray = new JSONArray(json);
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

    private class Windform_details {
        private String WindForm_name;
        private String Windform_id;

        public String getWindForm_name() {
            return WindForm_name;
        }

        public String getWindform_id() {
            return Windform_id;
        }

        public void setWindForm_name(String windForm_name) {
            WindForm_name = windForm_name;
        }

        public void setWindform_id(String windform_id) {
            Windform_id = windform_id;
        }

        public Windform_details(String windform_name, String windform_id) {
            this.WindForm_name = windform_name;

            this.Windform_id = windform_id;
        }

        @Override
        public String toString() {
            return WindForm_name;
        }
    }

    private class Windmill_details {
        private String Windmill_name;
        private String Windmill_id;


        public String getWindmill_name() {
            return Windmill_name;
        }

        public void setWindmill_name(String windmill_name) {
            Windmill_name = windmill_name;
        }


        public String getWindmill_id() {
            return Windmill_id;
        }

        public void setWindmill_id(String windmill_id) {
            Windmill_id = windmill_id;
        }

        public Windmill_details(String windmill_name, String windmill_id) {
            this.Windmill_name = windmill_name;

            this.Windmill_id = windmill_id;
        }

        @Override
        public String toString() {
            return Windmill_name;
        }
    }

}





