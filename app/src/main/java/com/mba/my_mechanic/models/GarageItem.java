package com.mba.my_mechanic.models;

public class GarageItem {
    String name, id, phone, address;
    double latitude, longitude, distance;

    public GarageItem(String id, String name, String address, String phone, double latitude, double longitude, double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

//    create getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return name + " (" + String.format("%.1f", distance) + " km)";
    }
}