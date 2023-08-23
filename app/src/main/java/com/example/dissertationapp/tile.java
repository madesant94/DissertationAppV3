package com.example.dissertationapp;


public class tile {
    // Define tile Class for pollution Grid
    private int ID;
    private String geometry;
    private double longitude;
    private double latitude;
    private float value;

    // Constructor
    public tile(int ID, float value, String geometry) {
        this.ID = ID;
        this.value = value;
        this.geometry = geometry;
        //this.point = null;
        this.latitude = 0;
        this.longitude = 0;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
