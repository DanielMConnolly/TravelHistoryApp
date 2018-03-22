package com.daniel.placesapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Adapter;

import com.cocoahero.android.geojson.GeoJSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper _databaseHelper;
    static ArrayList<Place> placesList=new ArrayList<Place>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //We need to get persmission to access the user's location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else{
            //We now need to start the service
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
            Log.i("Now we wait", "Waiting");

        }

        //Let's get this database up and running
        SQLiteDatabase database;
        database=this.openOrCreateDatabase("places", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS places(country VARCHAR, city VARCHAR, latitude REAL, longitude REAL )");
        _databaseHelper=new DatabaseHelper(database);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<Place> places=_databaseHelper.read();
        for(Place place: places){
            LatLng location=new LatLng(place.getLatitude(), place.getLongitude());
            addPin(location);
        }





        //set a long click listener
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
               String[] countryCity= addPin(latLng);
                Place place=new Place(countryCity[0], countryCity[1], latLng.latitude, latLng.longitude);
                _databaseHelper.write(place);

            }
        });



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    public String[] addPin(LatLng latLng){

        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList =gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String countryName=addressList.get(0).getCountryName().toString();
            String cityName=addressList.get(0).getLocality().toString();

            //Add a pin to the map
            mMap.addMarker(new MarkerOptions().position(latLng).title(cityName));

            if(addressList!=null&&addressList.size()>0&&(!placesList.contains(countryName))){

                placesList.add(new Place(countryName, cityName,latLng.latitude, latLng.longitude));

            }
            String retVal[] ={countryName, cityName};
            return retVal;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
