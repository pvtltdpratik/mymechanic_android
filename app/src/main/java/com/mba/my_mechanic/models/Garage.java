package com.mba.my_mechanic.models;

public class Garage {
    public String garage_name;
    public String garage_address;
    public double garage_latitude;
    public double garage_longitude;
    public String garage_phone_number;
    public double garage_ratings;

    public String logo_url;

    public Garage() {} // Required for Firestore

    public Garage(String garage_name, String garage_address, double garage_latitude,
                  double garage_longitude, String garage_phone_number, double garage_ratings, String logo_url) {
        this.garage_name = garage_name;
        this.garage_address = garage_address;
        this.garage_latitude = garage_latitude;
        this.garage_longitude = garage_longitude;
        this.garage_phone_number = garage_phone_number;
        this.garage_ratings = garage_ratings;
        this.logo_url = logo_url;
    }
}
