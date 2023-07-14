package com.example.dissertationapp;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Algorithms {
    public static String findMinWeightsVertex(Map<String, Double> distances, Set<String> visitedVertices) {
        String minVertex = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (String vertex : distances.keySet()) {
            if (!visitedVertices.contains(vertex)) {
                double distance = distances.get(vertex);
                if (minVertex == null || distance < minDistance) {
                    minVertex = vertex;
                    minDistance = distance;
                }
            }
        }

        return minVertex;
    }

    public static List<String> getShortestPath(String startVertex, String endVertex, Map<String, String> previousVertices) {
        List<String> path = new ArrayList<>();
        String currentVertex = endVertex;

        while (currentVertex != null) {
            path.add(0, currentVertex);
            currentVertex = previousVertices.get(currentVertex);
        }

        if (!path.isEmpty() && path.get(0).equals(startVertex)) {
            return path;
        } else {
            return Collections.emptyList(); // No path exists
        }
    }

    public static void dijkstra(Graph<String, edge> graph, String startVertex,
                                 Map<String, Double> weights, Map<String, Double> otherValue, Map<String, String> previousVertices, String dijsktraTypeString) {
        for (String vertex : graph.vertexSet()) {
            weights.put(vertex, Double.POSITIVE_INFINITY);
            otherValue.put(vertex, Double.POSITIVE_INFINITY);
            previousVertices.put(vertex, null);
        }
        //double newValue = 0.0;
        weights.put(startVertex, 0.0);
        otherValue.put(startVertex,0.0);
        double edgeOtherValue;
        double edgeWeight;

        Set<String> visitedVertices = new HashSet<>();

        while (visitedVertices.size() < graph.vertexSet().size()) {
            String currentVertex = findMinWeightsVertex(weights, visitedVertices);
            visitedVertices.add(currentVertex);

            //for (DefaultWeightedEdge outgoingEdge : graph.outgoingEdgesOf(currentVertex)) {
            for (edge outgoingEdge : graph.outgoingEdgesOf(currentVertex)) {

                String neighbor = graph.getEdgeTarget(outgoingEdge);

                if (!visitedVertices.contains(neighbor)) {
                    //double edgeWeight = graph.getEdgeWeight(outgoingEdge);
                    if (dijsktraTypeString.equals("Pollution")){
                        edgeWeight = outgoingEdge.getPollution();
                        edgeOtherValue = outgoingEdge.getLength();
                    }else{
                        edgeWeight = outgoingEdge.getLength();
                        edgeOtherValue = outgoingEdge.getPollution();
                    }
                    double newDistance = weights.get(currentVertex) + edgeWeight;
                    double newValue = otherValue.get(currentVertex) + edgeOtherValue;

                    if (newDistance < weights.get(neighbor)) {
                        weights.put(neighbor, newDistance);
                        otherValue.put(neighbor, newValue);
                        previousVertices.put(neighbor, currentVertex);
                    }
                }
            }
        }
        //return newValue;
    }

    public static bnn bestNearestNeighbor(Graph<String, edge> graph, node src, node tar, float tarDistance, HashMap<String,
            node> nodesHashMap, List<edge> edgesList, String graphType) {
        String srcString = src.getID();

        List<String> route = new ArrayList<>();
        double distance = 0;
        double pollution = 0;

        // f is a constant
        // lets try with f_ryan = 1.32025
        float f = 1.3205f;

        node curr = src;
        String bestNearestNode = "";
        edge candidate = null;
        float polWithoutPenalty = 0;
        double pollutionReturn = 0;
        double lengthReturn = 0;

        //System.out.println("SourceNearestNode: " + src.getID());

        String source = "";
        String target = "";
        float penalty = 1;
        float penalty_used = 1;

        Set<String> visitedVertices = new HashSet<>();

        while ((distance + (f * calcDist(curr, tar))) < tarDistance)
        {
            double smallestWeight = Double.POSITIVE_INFINITY;

            // DAME TODOS LOS EDGES DE ESTE NODE (PARA OBTENER SUS NEIGHBORS (TARGETS))
            if (graphType.equals("multi")){
                for (edge edge : graph.edgesOf(curr.getID())) {
                    source = graph.getEdgeSource(edge);
                    target = graph.getEdgeTarget(edge);
                    penalty = 1;

                    if (source.equals(curr.getID())) {
                        //neighbors.add(target);
                        if (visitedVertices.contains(target)) {
                            penalty = 2;
                        }
                        if ((edge.getPollution() * penalty) < smallestWeight) {
                            smallestWeight = edge.getPollution() * penalty;

                            polWithoutPenalty = edge.getPollution();
                            candidate = edge;
                            bestNearestNode = target;
                            penalty_used = penalty;
                            //System.out.println("   src: " + source + ",target: " + bestNearestNode + ", pollution: " + String.valueOf(edge.getPollution()) + ", penalty: " + String.valueOf(penalty) + ", penalized: " + String.valueOf(edge.getPollution()*penalty) +", length:" + String.valueOf(edge.getLength()) );
                        }
                    }
                }
            }
            if (graphType.equals("undirected")){
                for (edge edge : graph.edgesOf(curr.getID())) {
                    // get edges

                    for (String neighbor : Graphs.neighborListOf(graph, curr.getID())) {
                        System.out.println("Curr: " + curr.getID() + ", Neighbor: " + neighbor);
                    }

                    String source_ = graph.getEdgeSource(edge);
                    String target_ = graph.getEdgeTarget(edge);

                    if (source_.equals(curr.getID())) {
                        source = graph.getEdgeSource(edge);
                        target = graph.getEdgeTarget(edge);
                    }
                    else{
                        source = graph.getEdgeTarget(edge);
                        target = graph.getEdgeSource(edge);
                    }
                    penalty = 1;


                    if (visitedVertices.contains(target)) {
                        penalty = 2;
                    }
                    if ((edge.getPollution() * penalty) < smallestWeight) {
                        smallestWeight = edge.getPollution() * penalty;
                        polWithoutPenalty = edge.getPollution();
                        candidate = edge;
                        bestNearestNode = target;
                        penalty_used = penalty;
                        //System.out.println("   src: " + source + ",target: " + bestNearestNode + ", pollution: " + String.valueOf(edge.getPollution()) + ", penalty: " + String.valueOf(penalty) + ", penalized: " + String.valueOf(edge.getPollution()*penalty) +", length:" + String.valueOf(edge.getLength()) );
                    }

                }
            }

            System.out.println("src: " + source + ",target: " + bestNearestNode + ", pollution: " + String.valueOf(candidate.getPollution()) +", length:" + String.valueOf(candidate.getLength()) );
            //System.out.println("Current distance traversed:" + distance);
            //System.out.println("Penalty used this loop: " + String.valueOf(penalty_used));
            visitedVertices.add(curr.getID());
            curr = nodesHashMap.get(bestNearestNode);
            distance = distance + candidate.getLength();
            pollution = pollution + polWithoutPenalty;
            route.add(bestNearestNode);
            //System.out.println("lat: "+ String.valueOf(tar.getLatitude()) +", Long:"+String.valueOf( tar.getLongitude()));
            //System.out.println("Distancia: " + distance + ", Reserva regreso: " + String.valueOf(f * calcDist(curr, tar)));
        }
        // If already reached target then just go for dijkstra
        List<String> Path = new ArrayList<>();

        double PathPollution = 0;
        double PathLength = 0;

        if (graphType.equals("multi")) {
            String startVertex = curr.getID();
            Map<String, Double> cleanestPollution = new HashMap<>();
            Map<String, Double> cleanestLength = new HashMap<>();
            Map<String, String> previousVertices = new HashMap<>();

            dijkstra(graph, curr.getID(), cleanestPollution, cleanestLength,
                    previousVertices, "Pollution");

            Path = getShortestPath(curr.getID(), tar.getID(), previousVertices);
            pollutionReturn = cleanestPollution.get(tar.getID());
            lengthReturn = cleanestLength.get(tar.getID());
        }
        else{
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
            for (edge edge : edgesList) {
                dijkstra.addEdge(edge.getSource(), edge.getTarget(), edge);
            }
            dijkstra.execute(curr.getID(), "Pollution");

            Path = dijkstra.getPath(tar.getID());
            pollutionReturn = dijkstra.getShortestMain(tar.getID());
            lengthReturn = dijkstra.getShortestSecond(tar.getID());
        }

        System.out.println("------------------------------");
        System.out.println("-----------BNN-------------");

        System.out.println(route);

        System.out.println(Path);

        System.out.println("Distance Ida:" + String.valueOf(distance));
        System.out.println("Distance Regreso:" + String.valueOf(PathLength));
        //System.out.println("Distance Regreso:" + String.valueOf(PathPollution));

        for (String node : Path) {
            route.add(node);
        }

        bnn BNN = new bnn(route, pollution + pollutionReturn,distance + lengthReturn);
        return BNN;
    }

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
}
