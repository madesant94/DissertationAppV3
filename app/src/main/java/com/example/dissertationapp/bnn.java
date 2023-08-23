package com.example.dissertationapp;

import java.util.List;

public class bnn {

    // Define BNN Class for Algorithm BNN

    private List<String> route;
    private double pollution;
    private double length;

    // Constructor
    public bnn(List<String> route, double pollution, double length) {
        this.route = route;
        this.pollution = pollution;
        this.length = length;
    }

    public List<String> getRoute() {
        return route;
    }

    public void setRoute(List<String>  route) {
        this.route = route;
    }

    public double getPollution() {
        return pollution;
    }

    public void setPollution(double pollution) {
        this.pollution = pollution;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }


}
