package com.example.dissertationapp;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Algorithms {

    public static double calcDist(node src, node tar) {

        double lat1 = src.getLongitude();
        double lon1 = src.getLatitude();
        double lat2 = tar.getLongitude();
        double lon2 = tar.getLatitude();
        // Calculate Haversine Distance

        final double EARTH_RADIUS = 6371;

        double dLon = Math.toRadians(lon2 - lon1);//lonR2 - lonR1;
        double dLat = Math.toRadians(lat2 - lat1);//latR2 - latR1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c * 1000;
    }

    public static bnn bestNearestNeighbor(Graph<String, edge> graph, node src, node tar, float tarDistance, HashMap<String,
            node> nodesHashMap, List<edge> edgesList, String graphType) {
        // Run 2nd Phased Best Nearest Neighbour
        // define F value for bike and cycling networks

        float F_BIKE = 1.45f;
        F_BIKE = 1.545f;
        float F_WALK = 1.02f;
        F_WALK = 1.40636f;
        String srcString = src.getID();

        List<String> route = new ArrayList<>();
        double distance = 0;
        double pollution = 0;

        float f = 0f; // initialize simple f value

        node curr = src;
        String bestNearestNode = "";
        edge candidate = null;
        float polWithoutPenalty = 0;
        double pollutionReturn = 0;
        double lengthReturn = 0;

        String source = "";
        String target = "";
        float penalty = 1;
        float penalty_used = 1;

        Set<String> visitedVertices = new HashSet<>();
        List<String> visitedVerticesList = new ArrayList<>();

        // Here define the type of graph for the f value

        if (graphType.equals("multi")) {
            f = F_BIKE;
        } else {
            f = F_WALK;
        }

        while ((distance + (f * calcDist(curr, tar))) < tarDistance) {
            double smallestWeight = Double.POSITIVE_INFINITY;

            // DAME TODOS LOS EDGES DE ESTE NODE (PARA OBTENER SUS NEIGHBORS (TARGETS))
            if (graphType.equals("multi")) {
                for (edge edge : graph.edgesOf(curr.getID())) {
                    source = graph.getEdgeSource(edge);
                    target = graph.getEdgeTarget(edge);
                    penalty = 1;

                    if (source.equals(curr.getID())) {

                        if (visitedVerticesList.contains(target)) {
                            int count = 0;
                            // depending how many times it has been repeateed assign penalty
                            for (String visited : visitedVerticesList) {
                                //System.out.println(visited);
                                if (visited.equals(target)) {
                                    count++;
                                }
                            }

                            penalty = 2*count;
                        }
                        if ((edge.getPollution() * penalty) < smallestWeight) {
                            smallestWeight = edge.getPollution() * penalty;

                            polWithoutPenalty = edge.getGrade();
                            candidate = edge;
                            bestNearestNode = target;
                            penalty_used = penalty;

                        }
                    }
                }
            }
            if (graphType.equals("undirected")) {
                // since for undirected all other neighbors can be target the logic changes a bit
                for (edge edge : graph.edgesOf(curr.getID())) {

                    String source_ = graph.getEdgeSource(edge);
                    String target_ = graph.getEdgeTarget(edge);

                    if (source_.equals(curr.getID())) {
                        source = graph.getEdgeSource(edge);
                        target = graph.getEdgeTarget(edge);
                    } else {
                        source = graph.getEdgeTarget(edge);
                        target = graph.getEdgeSource(edge);
                    }
                    penalty = 1; // default penlty

                    if (visitedVerticesList.contains(target)) {
                        int count = 0;
                        // depending how many times it has been repeateed assign penalty
                        for (String visited : visitedVerticesList) {

                            if (visited.equals(target)) {
                                count++;
                            }
                        }

                        penalty = 2*count;
                    }
                    if ((edge.getPollution() * penalty) < smallestWeight) {
                        smallestWeight = edge.getPollution() * penalty;
                        polWithoutPenalty = edge.getGrade();
                        candidate = edge;
                        bestNearestNode = target;
                        penalty_used = penalty;
                    }

                }
            }

            visitedVertices.add(curr.getID());
            visitedVerticesList.add(curr.getID());
            curr = nodesHashMap.get(bestNearestNode);
            distance = distance + candidate.getLength();
            pollution = pollution + polWithoutPenalty;
            route.add(bestNearestNode);

        }

        // If already reached target then just go for dijkstra
        List<String> Path = new ArrayList<>();

        double PathPollution = 0;
        double PathLength = 0;

        if (graphType.equals("multi")) { //cycling graph

            // -----------

            DijkstraShortestPath<String, edge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
            GraphPath<String, edge> dijkstraPath = dijkstraShortestPath.getPath(curr.getID(), tar.getID());

            List<edge> edgeList = dijkstraPath.getEdgeList();
            pollutionReturn = 0;
            lengthReturn = 0;
            for (edge edge : edgeList) {

                pollutionReturn = pollutionReturn + edge.getGrade();
                lengthReturn =  lengthReturn + edge.getLength();
            }

            Path = dijkstraPath.getVertexList();

        } else { // walking graph
            DijkstraShortestPath<String, edge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
            GraphPath<String, edge> dijkstraPath = dijkstraShortestPath.getPath(curr.getID(), tar.getID());

            List<edge> edgeList = dijkstraPath.getEdgeList();
            lengthReturn  = 0;
            pollutionReturn = 0;
            for (edge edge : edgeList) {

                pollutionReturn = pollutionReturn + edge.getGrade();
                lengthReturn =  lengthReturn + edge.getLength();
            }

            Path = dijkstraPath.getVertexList();
        }

        // Append Dijkstra route to main route
        for (String node : Path) {
            route.add(node);
        }

        // create BNN object
        bnn BNN = new bnn(route, pollution + pollutionReturn, distance + lengthReturn);
        return BNN;
    }

}
