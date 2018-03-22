package com.daniel.placesapp;

/**
 * Created by Daniel on 3/8/2018.
 */

public class Place{

    private String _country;
    private String _city;
    private Double _latitude;
    private Double _longitude;

    public Place(String country, String city, Double latitude, Double longitude){

        _city=city;
        _country=country;
        _latitude=latitude;
        _longitude=longitude;


    }
    public String getCountry(){
        return _country;
    }
    public String getCity() {
        return _city;
    }
    public Double getLatitude(){ return _latitude;}
    public Double getLongitude(){return _longitude;}
}
