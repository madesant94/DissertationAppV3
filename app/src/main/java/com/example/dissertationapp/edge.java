package com.example.dissertationapp;

import org.jgrapht.graph.DefaultWeightedEdge;

public class edge {
    private String source;
    private String target;
    private float length;
    private float pollution;
    private int grade;
    private String turnDegree;

    private DefaultWeightedEdge e1 = null;
    //private DefaultWeightedEdge e1 = null;

    // Constructor
    public edge(String source, String target, float length, float pollution) {
        this.source = source;
        this.target = target;
        this.length = length;
        this.pollution = pollution;
        this.e1 = e1;
        this.grade = grade;
        this.turnDegree = turnDegree;
    }

    // Getters and setters
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setDestination(String destination) {
        this.target = destination;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getPollution() {
        return pollution;
    }

    public void setPollution(float pollution) {
        this.pollution = pollution;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTurnDegree() {
        return turnDegree;
    }

    public void setTurnDegree(String turnDegree) {
        this.turnDegree = turnDegree;
    }

    public DefaultWeightedEdge getE1() {
        return e1;
    }

    public void setE1(DefaultWeightedEdge e1) {
        this.e1 = e1;
    }
}
