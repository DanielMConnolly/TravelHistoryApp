package com.daniel.placesapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;



/**
 * Created by Daniel on 3/22/2018.
 */

public class DatabaseHelper  {
        SQLiteDatabase _database;
    public  DatabaseHelper(SQLiteDatabase database) {
        _database=database;

    }



    public ArrayList<Place> read(){
        ArrayList<Place> placesList=new ArrayList<>();
            try {
                //get the database up and running


                //add to the arrayList
                Cursor c = _database.rawQuery("SELECT * FROM places", null);
                int countryIndex = c.getColumnIndex("country");
                int cityIndex=c.getColumnIndex("city");
                int latIndex=c.getColumnIndex("latitude");
                int longIndex=c.getColumnIndex("longitude");
                c.moveToFirst();
                Log.i("we are at this point", c.getString(countryIndex));

                while (c != null) {
                    Log.i("Ok, c is not null", "That is for sure");
                    placesList.add(new Place(c.getString(countryIndex), c.getString(cityIndex), c.getDouble(latIndex), c.getDouble(longIndex)));
                    Log.i("City", c.getString(cityIndex));


                    c.moveToNext();
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }




        return placesList;
    }
    public void write(Place place ){
        String countryName=place.getCountry();
        String cityName=place.getCity();
        double longitude=place.getLongitude();
        double latitude=place.getLatitude();
        _database.execSQL("INSERT INTO places (country, city, latitude, longitude) VALUES ('"+countryName+"', '"+cityName+"' , "+latitude+","+longitude+")");

    }
}
