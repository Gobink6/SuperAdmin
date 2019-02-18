package com.katomaran.robotics.superadmin;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.ArrayList;
import java.util.List;

public class Verified_Details extends AppCompatActivity implements OnMapReadyCallback {
    TextView TV_QRDETAILS, TV_PHONE, TV_STATUS, TV_SF_NO, TV_HTFC_NO, TV_WINDFORM, TV_CUSTOMER;
    String barcode;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private static FragmentManager fragmentManager;
    private GoogleMap mMap;
    List<LatLng> latLngList = new ArrayList<LatLng>();
    String Host;
    Savepref savepref = new Savepref();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified__details);
        // Get QR Code Details
        barcode = getIntent().getStringExtra("code");
        Host = savepref.getString(getApplicationContext(), "Host");
        // TV_QRDETAILS = (TextView) findViewById(R.id.QR_detailss);
        TV_PHONE = (TextView) findViewById(R.id.QR_phone);
        TV_STATUS = (TextView) findViewById(R.id.QR_status);
        TV_SF_NO = (TextView) findViewById(R.id.QR_sf_no);
        TV_HTFC_NO = (TextView) findViewById(R.id.QR_htsc_no);
        TV_WINDFORM = (TextView) findViewById(R.id.QR_wind_form);
        TV_CUSTOMER = (TextView) findViewById(R.id.QR_customer_name);
        //Back button on tiitle bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            setTitle("Verification");

        }

        actionBar.setDisplayHomeAsUpEnabled(true);

        getJSON("https://" + Host + "/api/v1/wind_mills/" + barcode);
        //  TV_QRDETAILS.setText(barcode);

        // TV_QRDETAILS.setText("It's Not a Windmill Number Format");

        //Find the map on activity
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.QR_map);
        mapFragment.getMapAsync((OnMapReadyCallback) Verified_Details.this);
        fragmentManager = getSupportFragmentManager();
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
                    details(s);
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

    public void details(String s) throws JSONException {
        boolean status;
        String message = s;
        if (s != null) {
            if (barcode != null) {

                try {
                    JSONObject reader = new JSONObject(s);
                    status = reader.getBoolean("status");

                    if (status) {
                        JSONObject obj = reader.getJSONObject("data");
                        String phone = obj.getString("phone");
                        String latitude = obj.getString("latitude");
                        String longitude = obj.getString("longitude");
                        String mill_status = obj.getString("status");
                        String sf_no = obj.getString("sf_no");
                        String htsc_no = obj.getString("htsc_no");
                        String windform = obj.getString("name");
                        JSONObject obj_farm = obj.getJSONObject("wind_farm");
                        String location = obj_farm.getString("location");
                        if (status) {
                            TV_PHONE.setText(phone);
                            TV_STATUS.setText(mill_status);
                            TV_SF_NO.setText(sf_no);
                            TV_HTFC_NO.setText(htsc_no);
                            TV_WINDFORM.setText(windform);
                            TV_CUSTOMER.setText(location);
                            LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            latLngList.add(point);
                            createMarker(latLngList.get(0).latitude, latLngList.get(0).longitude, phone, mill_status, location);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngList.get(0).latitude, latLngList.get(0).longitude), 15.0f));

                        }
                    } else {
                        message = reader.getString("message");
                        TV_PHONE.setText(message);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        BitmapDescriptor iconr = BitmapDescriptorFactory.fromResource(R.drawable.wind_run);
//        googleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(10.241004,77.483413))
//                .icon(iconr)
//                .title("1234567890"));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.241004,77.483413),15.0f));

    }

    protected Marker createMarker(double latitude, double longitude, String s, String statusw, String title) {
        if (statusw.equals("running")) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.wind_run);
            // marker.showInfoWindow();
            return mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(icon)
                    .title(s)
                    .visible(true));
        } else {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.windmill_stop);

            return mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(icon)
                    .title(s)
                    .visible(true));
        }

        //.anchor(0.5f, 0.5f));
//
//                .snippet(snippet)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

