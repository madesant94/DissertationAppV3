package com.example.dissertationapp;

public class node {

    // Define node Class for Graphs
    private String ID;
    private float longitude;
    private float latitude;
    private int grid;

    private float value;
    private String label;

    // Constructor
    public node(String ID, float longitude, float latitude, int grid ) {
        this.ID = ID;
        this.longitude = longitude;
        this.latitude = latitude;
        this.grid = grid;
        this.value = -0.0F;
        //this.label = lab
    }

    // Getters and setters
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public int getGrid() {
        return grid;
    }

    public void setGrid(int grid) {
        this.grid = grid;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
