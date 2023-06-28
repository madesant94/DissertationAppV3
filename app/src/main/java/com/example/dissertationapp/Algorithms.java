package com.example.dissertationapp;

import org.jgrapht.Graph;

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
    // Incomplete
    public static void runDijkstra(Graph<String, edge> graph, node sourceNearestNode, node targetNearestNode ){

        String startVertex = sourceNearestNode.getID();
        Map<String, Double> cleanestPollution = new HashMap<>();
        Map<String, Double> cleanestLength = new HashMap<>();
        Map<String, String> previousVertices = new HashMap<>();

        dijkstra(graph, sourceNearestNode.getID(), cleanestPollution, cleanestLength,
                previousVertices, "Pollution");

        // Custom Method
        List<String> cleanestPath = getShortestPath(startVertex, targetNearestNode.getID(), previousVertices);
        double cleanestPathPollution = cleanestPollution.get(targetNearestNode.getID());
        double cleanestPathLength = cleanestLength.get(targetNearestNode.getID());

        //return ()
    }
}
