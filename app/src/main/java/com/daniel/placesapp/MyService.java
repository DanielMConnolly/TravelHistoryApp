package com.daniel.placesapp;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyService extends Service {
    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase _database;

    @Override
    public void onCreate() {
        super.onCreate();

        _database=this.openOrCreateDatabase("places", Context.MODE_PRIVATE, null);
        _database.execSQL("CREATE TABLE IF NOT EXISTS places(country VARCHAR, city VARCHAR, latitude REAL, longitude REAL )");
        final DatabaseHelper databaseHelper=new DatabaseHelper(_database);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                ArrayList<Place> places=MapsActivity.placesList;
                boolean shouldWeAdd=true;
                for(Place place:places){
                    double latitudeDistance=Math.abs(place.getLatitude()-location.getLatitude());
                    double longitudeDistance=Math.abs(place.getLongitude()-location.getLongitude());
                    if(latitudeDistance<.01&&longitudeDistance<.01){
                        shouldWeAdd=false;
                    }
                }
                if(shouldWeAdd){
                    double latitude=location.getLatitude();
                    double longitude=location.getLongitude();
                    String countryName="";
                    String cityName="";
                    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        List<Address> addressList = gcd.getFromLocation(latitude, longitude, 1);
                        countryName = addressList.get(0).getCountryName().toString();
                        cityName = addressList.get(0).getLocality().toString();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    Place place=new Place(countryName, cityName, latitude, longitude);
                databaseHelper.write(place);
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);



    }

    public MyService() {
        Log.i("Position", "My service");

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
