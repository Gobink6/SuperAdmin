package com.katomaran.robotics.superadmin;

import android.app.Dialog;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Addwindmill extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    private Marker marker, markln;
    LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    EditText ET_Millid, ET_CUSTOMER_NAME, ET_SF_N0, ET_HTFC_NO, ET_VILLAGE;
    TextView TV_Address;
    Button btn_location, btn_addmill;
    Spinner SP_wflist;
    ArrayList<Contact> contacts = new ArrayList<>();
    String millid, formid, customer_name, sf_no, htfc_no, village;
    double la, li;
    private static ArrayList<LauncherActivity.ListItem> listItems = new ArrayList<>();
    String lattitude, longitude = "";
    String windform_id;
    //pop window
    Dialog dialog;
    Button BT_add, btnCancel;
    TextView DIALOG_TX_TEXT;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwindmill);
        //toolbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //set toolbar titile
        getSupportActionBar().setTitle("Add Windmill");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(Addwindmill.this);
        //pop window
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Custom Alert Dialog");
        btnCancel = (Button) dialog.findViewById(R.id.cancel);
        DIALOG_TX_TEXT = dialog.findViewById(R.id.editText);
        ET_Millid = findViewById(R.id.name0);
        SP_wflist = findViewById(R.id.sp_wf_list);
        btn_addmill = findViewById(R.id.sub);
        ET_CUSTOMER_NAME = findViewById(R.id.customer_no);
        ET_SF_N0 = findViewById(R.id.sf_no);
        ET_HTFC_NO = findViewById(R.id.htfc_no);
        ET_VILLAGE = findViewById(R.id.village);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_addmill.setEnabled(false);
        getJSON("http://192.168.0.103:3000/api/v1/wind_forms");
        //check if the GPS On or Off
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (statusOfGPS == true) {
            Log.i("gps", String.valueOf(statusOfGPS));
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location mCurrentLocation = locationResult.getLastLocation();
                    LatLng myCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    //  String cityName = getCityName(myCoordinates);
                    //String name = longh(myCoordinates);
                    Double latitude = myCoordinates.latitude;
                    //Toast.makeText(Addwindmill.this,  cityName, Toast.LENGTH_SHORT).show();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 13.0f));
                    if (marker == null) {
                        marker = mMap.addMarker(new MarkerOptions().position(myCoordinates));
                    } else
                        marker.setPosition(myCoordinates);
                }

            };
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocationCallback = new LocationCallback();
            Toast.makeText(Addwindmill.this, "please Turn On GPS ", Toast.LENGTH_LONG).show();
            //   openSettings();
        }
        // Spinner click listener
        //   SP_wflist.setOnItemSelectedListener(this);

        defineButton();

    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    public void defineButton() {
        // Create the next level button, which tries to show an interstitial when clicked.
        findViewById(R.id.location).setOnClickListener(buttonClickListeenr);
        findViewById(R.id.sub).setOnClickListener(buttonClickListeenr);
    }

    public View.OnClickListener buttonClickListeenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.location:
                    //check if the GPS On or Off
                    closeKeyboard();
                    LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (statusOfGPS == true) {
                        // switch (view.getId()) {
                        // btn_addmill.setEnabled(true);
                        btn_addmill.setEnabled(true);
                        btn_addmill.setBackgroundColor(Color.RED);
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Log.d("mylog", "Not granted");

                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else
                                requestLocation();

                        } else
                            requestLocation();

                    } else {
                        Toast.makeText(Addwindmill.this, "please Turn On GPS ", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.sub:
                    customer_name = ET_CUSTOMER_NAME.getText().toString();
                    sf_no = ET_SF_N0.getText().toString();
                    htfc_no = ET_HTFC_NO.getText().toString();
                    village = ET_VILLAGE.getText().toString();
                    longh();
            }
        }
    };

    public void longh() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location != null) {
            double latti = location.getLatitude();
            double longi = location.getLongitude();

            lattitude = String.valueOf(latti);
            longitude = String.valueOf(longi);
            //   Log.i("long", String.valueOf(longitude));
            millid = ET_Millid.getText().toString();
            new Asyncwindmill().execute(windform_id);
        } else if (location1 != null) {
            double latti = location1.getLatitude();
            double longi = location1.getLongitude();
            lattitude = String.valueOf(latti);
            longitude = String.valueOf(longi);
            millid = ET_Millid.getText().toString();
            formid = SP_wflist.getSelectedItem().toString();
            new Asyncwindmill().execute(windform_id);

        } else if (location2 != null) {
            double latti = location2.getLatitude();
            double longi = location2.getLongitude();
            lattitude = String.valueOf(latti);
            longitude = String.valueOf(longi);
            millid = ET_Millid.getText().toString();
            formid = SP_wflist.getSelectedItem().toString();
            new Asyncwindmill().execute(windform_id);
        } else {

            Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();

        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getCityName(LatLng myCoordinates) {

        String address = "";
        double longi = 0;
        double latti = 0;
        Geocoder geocoder = new Geocoder(Addwindmill.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(myCoordinates.latitude, myCoordinates.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            latti = addresses.get(0).getLatitude();
            longi = addresses.get(0).getLongitude();
            // String text =latti.getText().toString();
            //  TV_Address.setText(address + "\n" + "Latitude" + latti+ "\n" + "Longtitude" + longi);

            Log.d("mylog", "Complete Address: " + addresses.toString());
            Log.d("mylog", "Address: " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address + latti + longi;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("mylog", "In Requesting Location");
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= 1000 * 2) {
            LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
            // String cityName = getCityName(myCoordinates);
            // Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show();
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Log.d("mylog", "Last location too old getting new location!");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback, Looper.myLooper());
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

    public void loadIntoListView(String json) throws JSONException {
        if (json != null) {

            JSONObject jsonObject = new JSONObject(json);
            //  JSONArray jsonArray = new JSONArray(json);
            JSONArray array = jsonObject.getJSONArray("data");
            contacts.clear();
            String[] from = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                //JSONArray object= array.getJSONArray(i);
                JSONObject obj = array.getJSONObject(i);
                from[i] = obj.getString("name");
                Log.i("Windform4Activity", "arr: " + Arrays.toString(from));


                contacts.add(new Contact(obj.getString("name"), obj.getString("id")));


                //Printing string array list values on screen.
            }

            ArrayAdapter<Contact> adapter =
                    new ArrayAdapter<Contact>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, contacts);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SP_wflist.setAdapter(adapter);
            SP_wflist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object numbers = SP_wflist.getItemAtPosition(position);

                    windform_id = contacts.get(position).contact_id;

                    Toast.makeText(parent.getContext(), "Selected: " + windform_id, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

        } else {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class Contact {
        private String contact_name;
        private String contact_id;

        public Contact() {
        }

        public Contact(String contact_name, String contact_id) {
            this.contact_name = contact_name;
            this.contact_id = contact_id;

        }

        public String getContact_name() {
            return contact_name;
        }

        public void setContact_name(String contact_name) {
            this.contact_name = contact_name;
        }

        public String getContact_id() {
            return contact_id;
        }

        public void setContact_id(String contact_id) {
            this.contact_id = contact_id;
        }

        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        @Override
        public String toString() {
            return contact_name;
        }

    }

    public class Asyncwindmill extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL urls = null;
        ProgressDialog pdLoading = new ProgressDialog(Addwindmill.this);

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

                // Enter URL address where your rails file resides
                urls = new URL("http://192.168.0.103:3000/api/v1/wind_forms/" + windform_id + "/wind_mills");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) urls.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //Append body to URl
                JSONObject json = new JSONObject();
                JSONObject manJson = new JSONObject();
                try {
//                    manJson.put("wind_form_id", windform_id);
                    manJson.put("phone", millid);
                    manJson.put("latitude", lattitude);
                    manJson.put("longitude", longitude);
                    manJson.put("status", 1);
                    manJson.put("name", customer_name);
                    manJson.put("sf_no", sf_no);
                    manJson.put("htsc_no", htfc_no);
                    manJson.put("village", village);
                    json.put("wind_mill", manJson);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String jsons_body = json.toString();
                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsons_body);
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
                if (response_code == HttpURLConnection.HTTP_CREATED || (response_code == 422) || (response_code == HttpURLConnection.HTTP_UNAUTHORIZED)) {

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
                        Intent intent = new Intent(Addwindmill.this, Loginpage.class);
                        Toast.makeText(Addwindmill.this, "Seesion Timeout Login Again", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Addwindmill.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}
