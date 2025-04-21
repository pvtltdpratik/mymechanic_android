package com.mba.my_mechanic.models;

import java.io.Serializable;

public class Garage implements Serializable {
    public String garage_name;
    public String garage_address;
    public double garage_latitude;
    public double garage_longitude;
    public String garage_phone_number;
    public double garage_ratings;
    private String state;
    private String docId;

    public String logo_url;

    public Garage() {} // Required for Firestore

    public Garage(String garage_name, String garage_address, double garage_latitude,
                  double garage_longitude, String garage_phone_number, double garage_ratings, String logo_url, String state, String docId) {
        this.garage_name = garage_name;
        this.garage_address = garage_address;
        this.garage_latitude = garage_latitude;
        this.garage_longitude = garage_longitude;
        this.garage_phone_number = garage_phone_number;
        this.garage_ratings = garage_ratings;
        this.logo_url = logo_url;
        this.state = state;
        this.docId = docId;
    }

    public String getGarageName() {
        return garage_name;
    }

    public String getGarageAddress() {
        return garage_address;
    }

    public double getGarage_latitude() {
        return garage_latitude;
    }

    public double getGarage_longitude() {
        return garage_longitude;
    }

    public String getGarage_phone_number() {
        return garage_phone_number;
    }

    public double getGarage_ratings() {
        return garage_ratings;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public String getState() {
        return state;
    }

    public String getDocId() {
        return docId;
    }

}
