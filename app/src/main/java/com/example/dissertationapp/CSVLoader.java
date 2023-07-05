package com.example.dissertationapp;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVLoader {

    public static List<edge> loadEdges(Context context, String fileName) {
        List<edge> edgeList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            int n = 0;
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                // Process each line of the CSV file
                if (n != 0) {
                    String[] values = line.split(",");
                    //System.out.println("ID: " + values[0] + " , value:" + values[1]);
                    //tiles.add(new tile(Integer.parseInt(values[0]), Float.valueOf(values[1])));
                    //edge(String source, String target, float length, float pollution)
                    edgeList.add(new edge(values[0],
                            values[1],
                            Float.valueOf(values[2]),
                            0.0F));

                    //graph.addVertex(values[0]);

                }
                n = n+1;
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return edgeList;
    }
    public static List<edge> loadEdgesBike(Context context, String fileName) {
        List<edge> edgeList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            int n = 0;
            String line = "";
            edge Edge;
            while ((line = bufferedReader.readLine()) != null) {
                // Process each line of the CSV file
                if (n != 0) {
                    String[] values = line.split(",");
                    //System.out.println("ID: " + values[0] + " , value:" + values[1]);
                    //tiles.add(new tile(Integer.parseInt(values[0]), Float.valueOf(values[1])));
                    //edge(String source, String target, float length, float pollution)
                    Edge = new edge(values[0], values[1], Float.valueOf(values[2]), 0.0F);
                    //Edge.setGrade(Integer.valueOf(values[3])); #deleted grade for the moment
                    Edge.setTurnDegree(values[3]); //or 4 depending if grade exists

                    edgeList.add(Edge);

                }
                n = n+1;
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return edgeList;
    }
    public static HashMap<String, node> loadNodes(Context context, String fileName) {
        List<node> nodesCList = new ArrayList<>();
        HashMap<String, node> nodesHashMap = new HashMap<String, node>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            int n = 0;
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                // Process each line of the CSV file
                if (n != 0) {
                    String[] values = line.split(",");
                    nodesCList.add(new node(values[0],
                            Float.valueOf(values[2]),
                            Float.valueOf(values[1]),
                            Integer.parseInt(values[3])));
                    //node(String ID, float longitude, float latitude, int grid )
                    //System.out.println("ID: " + values[0] + " , value:" + values[1]);
                    nodesHashMap.put(values[0], new node(values[0],
                            Float.valueOf(values[2]),
                            Float.valueOf(values[1]),
                            Integer.parseInt(values[3])));
                }
                n = n+1;
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return nodesCList;
        return nodesHashMap;
    }

    public static List<tile> loadTiles(Context context, String fileName) {
        List<tile> tilesList = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            int n = 0;
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                // Process each line of the CSV file
                if (n != 0) {
                    String[] values = line.split(",");
                    // tile(int ID, float value, String geometry)
                    tilesList.add(new tile(Integer.parseInt(values[0]),
                            Float.valueOf(values[1]),
                            values[2]));
                }
                n = n+1;
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tilesList;
    }
}
