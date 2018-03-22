package com.daniel.placesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    static ArrayList<Place> placesList;
    static ArrayAdapter<String> adapter;
    static SQLiteDatabase database;

    private RelativeLayout _layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //getting the list view all set up

        _layout=(RelativeLayout)findViewById(R.id.layout);
        ListView listView=(ListView)findViewById(R.id.listview);
        placesList =new ArrayList<Place>();


       try {
           //get the database up and running
           database = this.openOrCreateDatabase("placeData", MODE_PRIVATE, null);
           database.execSQL("CREATE TABLE IF NOT EXISTS placeData(country VARCHAR, city VARCHAR, )");


           //add to the arrayList
           Cursor c = database.rawQuery("SELECT * FROM placeData", null);
           int countryIndex = c.getColumnIndex("country");
           int cityIndex=c.getColumnIndex("city");
           c.moveToFirst();

           while (c != null) {
              // placesList.add(new Place(c.getString(countryIndex), c.getString(cityIndex)));
               c.moveToNext();
           }

       }
       catch(Exception e){
           e.printStackTrace();
       }



        //listener for listview
        //might not need this
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mapsIntent=new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapsIntent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case(R.id.mapStart):
                Intent myIntent=new Intent(MainActivity.this, MapsActivity.class);
                startActivity(myIntent);
                break;

            case(R.id.country):
                _layout.removeAllViews();
                ListView countryList=new ListView(this);
                _layout.addView(countryList);
                ArrayList<String> countries=new ArrayList<String>();
                for(Place p: placesList){

                    if(!countries.contains(p.getCountry())) {
                        countries.add(p.getCountry());
                    }

                }

                Collections.sort(countries);
                ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1, countries);
                countryList.setAdapter(adapter);
                break;
            case(R.id.city):
                _layout.removeAllViews();
                Log.i("cities", "This is running");
                ListView cityList=new ListView(this);
                _layout.addView(cityList);
                ArrayList<String> cities=new ArrayList<String>();
                for(Place p: placesList){

                    if(!cities.contains(p.getCity())) {
                        cities.add(p.getCity());
                    }

                }

                Collections.sort(cities);
                ArrayAdapter cityAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,cities);
                cityList.setAdapter(cityAdapter);
                break;

        }


        return super.onOptionsItemSelected(item);
    }
}
