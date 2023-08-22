package com.example.dissertationapp;

import java.util.HashMap;
import java.util.List;

public class Utilities {

    public static void setWeightsEdges(List<edge> edgesList, HashMap<String, node> nodesHashMap, String weightType) {
        //public static void setWeightsEdges(List<edge> edgesList, List<node> nodesList, DirectedWeightedMultigraph<String, DefaultWeightedEdge> graph) {

        for  (edge edge : edgesList) {

            String edgeSource = edge.getSource();
            String edgeTarget = edge.getTarget();

            float nodeSourceWeight = 0.0F;
            float nodeTargetWeight = 0.0F;

            nodeSourceWeight = nodesHashMap.get(edgeSource).getValue();
            nodeTargetWeight = nodesHashMap.get(edgeTarget).getValue();

            float edgeWeight = (nodeSourceWeight + nodeTargetWeight) / 2;
            //edgeWeight = edgeWeight * edge.getLength();
            //float edgeWeight = (nodeSourceWeight + nodeTargetWeight) / edge.getLength();
            edge.setPollution(edgeWeight);

        }
        //return graph;
    }

    //public static void setWeightsNodes(List<node> nodesList, List<tile> tilesList) {
    public static void setWeightsNodes(HashMap<String, node> nodesHashMap, List<tile> tilesList) {
    //
    //public static void setWeightsNodes(HashMap<String, node> nodesHashMap, HashMap<String, tile> tilesList) {
        // Find Node in graph

        for  (tile tile : tilesList) {

            int tileID = tile.getID();
            Float tileValue = tile.getValue();

            for (node eNode : nodesHashMap.values()){
                if (eNode.getGrid() == tileID){
                    eNode.setValue(tileValue);
                }
            }
        }

        /*for  (String stringTile : tilesList.keySet()) {
            tile tile = tilesList.get(stringTile);
            int tileID = tile.getID();
            Float tileValue = tile.getValue();

            for (node eNode : nodesHashMap.values()){
                if (eNode.getGrid() == tileID){
                    eNode.setValue(tileValue);
                }
            }
        }*/
    }

    //public static node findNearestNode(List<node> nodesCList, double pointX, double pointY) {
    public static node findNearestNode(HashMap<String, node> nodesHashMap, double pointX, double pointY) {
        // Find Nearest Node in graph

        double minDistance = Double.MAX_VALUE;
        node closestNode = null;

        for (node node : nodesHashMap.values()) {

            double distance = calculateHaversine(node.getLongitude(), node.getLatitude(), pointX, pointY);

            if (distance < minDistance) {
                minDistance = distance;
                closestNode = node;
            }
        }
        return closestNode ;
    }

    public static node findNode(List<node> nodesCList, String nodeID) {
        // Find Node in graph

        node searchedNode = null;

        for (node node : nodesCList) {
            if (nodeID.equals(node.getID())) {
                searchedNode = node;
                break;
            }
        }
        return searchedNode ;
    }

    public static double calculateHaversine(double lon1, double lat1, double lon2, double lat2) {
        // Calculate Haversine Distance

        final double EARTH_RADIUS = 6371;

        double dLon = Math.toRadians(lon2 - lon1);//lonR2 - lonR1;
        double dLat = Math.toRadians(lat2 - lat1);//latR2 - latR1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static void setWeightsEdgesBeta(List<edge> edgesList, HashMap<String, node> nodesHashMap, String weightType, float beta) {
        //public static void setWeightsEdges(List<edge> edgesList, List<node> nodesList, DirectedWeightedMultigraph<String, DefaultWeightedEdge> graph) {
        float sumPollution = 0;
        float sumLength = 0;

        for  (edge edge : edgesList) {
            //sumPollution = sumPollution + edge.getPollution();
            sumLength = sumLength + edge.getLength();

        }

        for (node node : nodesHashMap.values()){
            sumPollution = sumPollution + node.getValue();
        }

        if (weightType.equals("WD") || weightType.equals("WD+Mult")){
            System.out.println("Beta: " + beta);
        }

        if (weightType.equals("WD+Mult")){
            sumPollution = 0;
            for  (edge edge : edgesList) {
                String edgeSource = edge.getSource();
                String edgeTarget = edge.getTarget();
                float nodeSourceWeight = 0.0F;
                float nodeTargetWeight = 0.0F;
                nodeSourceWeight = nodesHashMap.get(edgeSource).getValue();
                nodeTargetWeight = nodesHashMap.get(edgeTarget).getValue();
                float edgeWeight = (nodeSourceWeight + nodeTargetWeight) / 2;
                edgeWeight = edgeWeight*edge.getLength()/10;
                sumPollution = sumPollution + edgeWeight;
            }

        }


        for  (edge edge : edgesList) {

            String edgeSource = edge.getSource();
            String edgeTarget = edge.getTarget();

            float nodeSourceWeight = 0.0F;
            float nodeTargetWeight = 0.0F;

            nodeSourceWeight = nodesHashMap.get(edgeSource).getValue();
            nodeTargetWeight = nodesHashMap.get(edgeTarget).getValue();

            //original

            edge.setGrade((nodeSourceWeight + nodeTargetWeight) / 2);
            float edgeWeight = (nodeSourceWeight + nodeTargetWeight) / 2;
            //float FACTOR = 40;
            //edge.setGrade(((nodeSourceWeight + nodeTargetWeight) / 2)*(edge.getLength()/FACTOR));
            //float edgeWeight = ((nodeSourceWeight + nodeTargetWeight) / 2)*(edge.getLength()/FACTOR);


            if (weightType.equals("Mult")){
                edgeWeight = edgeWeight*edge.getLength()/10;
            }
            if (weightType.equals("WD")){

                edgeWeight = (beta*(1000000* edgeWeight/sumPollution)) + ((1-beta)*(1000000*edge.getLength()/sumLength));

            }
            if (weightType.equals("WD+Mult")){
                edgeWeight = edgeWeight*edge.getLength()/10;
                //System.out.println("pol: " + 1000000*edgeWeight/sumPollution + " length: "+ 1000000*edge.getLength()/sumLength );

                edgeWeight = (beta*(1000000* edgeWeight/sumPollution)) + ((1-beta)*(1000000*edge.getLength()/sumLength));


            }

            //System.out.println("pollution" + 1000000*edgeWeight/sumPollution + " length: "+ 1000000*edge.getLength()/sumLength );
            edge.setPollution(edgeWeight);

        }
        //return graph;
    }

}
