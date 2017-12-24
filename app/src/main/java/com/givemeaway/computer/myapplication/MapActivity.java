package com.givemeaway.computer.myapplication;

/**
 * Created by дом on 24/12/2017.
 */

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.givemeaway.computer.myapplication.AdditionalClasses.ObjectsConverter;
import com.givemeaway.computer.myapplication.AdditionalClasses.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class MapActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int[] COLORS = new int[]{R.color.colorPrimary,R.color.red,R.color.colorAccent,R.color.green,R.color.colorPrimaryDark,R.color.yellow};

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    Place selectedPlace;
    ArrayList<Place> allPlaces = new ArrayList<>();
    private ObjectsConverter objectsConverter = new ObjectsConverter();

    private OkHttpClient client = new OkHttpClient();
    private FusedLocationProviderClient mFusedLocationClient;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = getBaseContext();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        Intent intent = getIntent();
        String placeJson = intent.getStringExtra("json");
        String allPlaceJSON;


        if (placeJson == null) {
            allPlaceJSON = intent.getStringExtra("json_all");
            allPlaces = objectsConverter.ConvertToPlaceArrayList(allPlaceJSON);
        } else {
            Place place = GSON.fromJson(placeJson, Place.class);
            allPlaces.add(place);
        }
        for (Place place : allPlaces) {
            map.addMarker(new MarkerOptions().position(new LatLng(
                    place.getnCoordinate(), place.geteCoordinate()
            )).title(place.getName()));
        }
        selectedPlace = allPlaces.get(0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                selectedPlace.getnCoordinate(), selectedPlace.geteCoordinate()
        ), 12.0f));
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        map.setOnMarkerClickListener(this);
        enableMyLocation();
    }
    public void buildOneLeg(final ArrayList<LatLng> geoValues, final int color){
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ArrayList<Polyline> polylines = new ArrayList<>();
                //add route(s) to the map.
                for (int i = 0; i <geoValues.size()-1; i++) {

                    Log.i("Polylines", geoValues.get(i).toString());

                    PolylineOptions polyOptions = new PolylineOptions();
                    polyOptions.color(getResources().getColor(color));
                    polyOptions.width(10);

                    polyOptions.add(geoValues.get(i));
                    polyOptions.add(geoValues.get(i+1));

                    Polyline polyline = mMap.addPolyline(polyOptions);
                    polylines.add(polyline);
                }
            }
        });
    }
    public void buildWayFromPosition(double longitude, double latitude){

        String key = "AIzaSyAL3tu9tquV685UamSaV3fRLMD0Fc4mV2M";
        Place lastPlace = allPlaces.get(allPlaces.size()-1);
        String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?&origin=" +
                latitude + "," + longitude
                + "&destination="
                + lastPlace.getnCoordinate()+","+lastPlace.geteCoordinate();
        if(allPlaces.size()>1){
            requestUrl += "&waypoints=";
            for(int i=0; i<allPlaces.size()-1; i++){
                if(i>0) requestUrl += "|";
                requestUrl += allPlaces.get(i).getnCoordinate()+","+allPlaces.get(i).geteCoordinate();
            }
        }
        requestUrl+="&key="+key;
        Log.i("MyRequest",requestUrl);
        //"&waypoints=Charlestown,MA|Lexington,MA&key="+key;

        final Request request = new Request.Builder().url(requestUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("MyRequest","Failure!");
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
//                Log.i("MyRequest",response.body().string());

                JSONArray legs;
                try{
                    JSONObject obj = new JSONObject(response.body().string());
                    legs = new JSONArray((new JSONArray(obj
                                    .get("routes").toString()).getJSONObject(0)
                                    .get("legs").toString()));
                    for(int i=0; i<legs.length(); i++) {
                        JSONArray steps = new JSONArray(legs.getJSONObject(i).get("steps").toString());
                        ArrayList<LatLng> geoValues = new ArrayList<>();

                        JSONObject pair = new JSONObject(steps.getJSONObject(0).get("start_location").toString());
                        double lat = Double.parseDouble(pair.get("lat").toString());
                        double lng = Double.parseDouble(pair.get("lng").toString());
                        geoValues.add(new LatLng(lat, lng));

                        for(int j=0; j<steps.length(); j++){
                            pair = new JSONObject(steps.getJSONObject(j).get("end_location").toString());
                            lat = Double.parseDouble(pair.get("lat").toString());
                            lng = Double.parseDouble(pair.get("lng").toString());
                            geoValues.add(new LatLng(lat, lng));
                        }
                        int colorIndex = i % COLORS.length;
                        buildOneLeg(geoValues, COLORS[colorIndex]);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void buildWay(View view) {
//        Uri gmmIntentUri = Uri.parse("google.navigation:q="+selectedPlace.getnCoordinate()+","+selectedPlace.geteCoordinate());
        /*Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                +selectedPlace.getnCoordinate()+","+selectedPlace.geteCoordinate());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);*/

//        String key = "AIzaSyA_NwytTz386dNYGTmN7k2n83hQLqsSUD0";

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
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                        try{
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();
                            buildWayFromPosition(longitude, latitude);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permissions", "Requesting permissions: " + Manifest.permission.ACCESS_FINE_LOCATION);
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            Log.i("Permissions", "Somehow it has been granted access");
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }
    public void viewPlace(View view){
        Intent intent = new Intent(getBaseContext(), ViewActivity.class);
        intent.putExtra("json", GSON.toJson(selectedPlace));
        startActivity(intent);
    }



    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.i("MyLocation","Toast should be shown here");
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        Log.i("Permissions","onRequest called");
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.i("Permissions", "We are granted with "+ TextUtils.join(",",permissions));
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        for(Place place : allPlaces){
            if(place.getName().equals(title)){
                selectedPlace = place;
                break;
            }
        }
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}
