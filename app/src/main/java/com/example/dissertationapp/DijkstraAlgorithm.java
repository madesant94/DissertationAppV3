package com.example.dissertationapp;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

public class DijkstraAlgorithm {
    private Graph<String, edge> graph;
    private HashMap<String, Double> shortestMain = new HashMap<>();
    HashMap<String, Double> shortestSecond = new HashMap<>();
    private PriorityQueue<String> unvisitedNodes;
    private HashMap<String, String> predecessors = new HashMap<>();

    public DijkstraAlgorithm() {
        graph = new SimpleWeightedGraph<>(edge.class);
        // Initialize Priority Queue with comparator for shortest distance
        unvisitedNodes = new PriorityQueue<>(Comparator.comparingDouble(node -> shortestMain.get(node)));
    }

    public void addEdge(String source, String target,edge edge) {
        graph.addVertex(source);
        graph.addVertex(target);
        graph.addEdge(source, target, edge);
        //graph.setEdgeWeight(edge, weight);
    }

    public void execute(String startNode, String type) {
        // Set initial distances to infinity
        int edgeCount = graph.edgeSet().size();
        System.out.println("The graphV2 has " + edgeCount + " edges.");

        for (String vertex : graph.vertexSet()) {
            shortestMain.put(vertex, Double.MAX_VALUE);
            shortestSecond.put(vertex, Double.MAX_VALUE);
        }
        shortestMain.put(startNode, 0.0);
        shortestSecond.put(startNode, 0.0);
        unvisitedNodes.add(startNode);
        double tentativeWeight;
        double tentativeOtherValue;
        while (!unvisitedNodes.isEmpty()) {
            String currentNode = unvisitedNodes.poll();

            for (String neighbor : Graphs.neighborListOf(graph, currentNode)) {
                if (type.equals("Pollution")){
                    tentativeWeight = shortestMain.get(currentNode) + graph.getEdge(currentNode, neighbor).getPollution();
                    tentativeOtherValue = shortestSecond.get(currentNode) + graph.getEdge(currentNode, neighbor).getLength();
                }else{
                    tentativeWeight = shortestMain.get(currentNode) + graph.getEdge(currentNode, neighbor).getLength();
                    tentativeOtherValue = shortestSecond.get(currentNode) + graph.getEdge(currentNode, neighbor).getPollution();
                }
                //System.out.println("tentativeWeight " + tentativeWeight);
                if (tentativeWeight < shortestMain.get(neighbor)) {
                    shortestMain.put(neighbor, tentativeWeight);
                    shortestSecond.put(neighbor, tentativeOtherValue);
                    predecessors.put(neighbor, currentNode);

                    if (unvisitedNodes.contains(neighbor)) {
                        unvisitedNodes.remove(neighbor);
                    }
                    unvisitedNodes.add(neighbor);
                }
            }
        }
    }

    public List<String> getPath(String targetNode) {
        List<String> path = new ArrayList<>();
        String currentNode = targetNode;
        path.add(targetNode);

        while (predecessors.get(currentNode) != null) {
            currentNode = predecessors.get(currentNode);
            path.add(currentNode);
        }

        Collections.reverse(path);
        return path;
    }

    public Double getShortestMain(String targetNode) {
        return shortestMain.get(targetNode);
    }

    public Double getShortestSecond(String targetNode) {
        return shortestSecond.get(targetNode);
    }
}

