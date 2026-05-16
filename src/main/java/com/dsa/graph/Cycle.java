package com.dsa.graph;

import java.util.ArrayList;
import java.util.List;

public class Cycle {

    public static void main(String[] args) {
        int vertices = 4;

        List<List<Integer>> undirectedGraph = createGraph(vertices);
        addUndirectedEdge(undirectedGraph, 0, 1);
        addUndirectedEdge(undirectedGraph, 1, 2);
        addUndirectedEdge(undirectedGraph, 2, 0);
        addUndirectedEdge(undirectedGraph, 2, 3);

        System.out.println("Undirected graph has cycle: " + hasCycleInUndirectedGraph(undirectedGraph));

        List<List<Integer>> directedGraph = createGraph(vertices);
        addDirectedEdge(directedGraph, 0, 1);
        addDirectedEdge(directedGraph, 1, 2);
        addDirectedEdge(directedGraph, 2, 3);
        addDirectedEdge(directedGraph, 3, 1);

        System.out.println("Directed graph has cycle: " + hasCycleInDirectedGraph(directedGraph));
    }

    private static List<List<Integer>> createGraph(int vertices) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void addUndirectedEdge(List<List<Integer>> graph, int source, int destination) {
        graph.get(source).add(destination);
        graph.get(destination).add(source);
    }

    private static void addDirectedEdge(List<List<Integer>> graph, int source, int destination) {
        graph.get(source).add(destination);
    }

    public static boolean hasCycleInUndirectedGraph(List<List<Integer>> graph) {
        boolean[] visited = new boolean[graph.size()];

        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (!visited[vertex] && hasUndirectedCycle(graph, vertex, -1, visited)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasUndirectedCycle(List<List<Integer>> graph, int vertex, int parent, boolean[] visited) {
        visited[vertex] = true;

        for (Integer neighbor : graph.get(vertex)) {
            if (!visited[neighbor]) {
                if (hasUndirectedCycle(graph, neighbor, vertex, visited)) {
                    return true;
                }
            } else if (neighbor != parent) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasCycleInDirectedGraph(List<List<Integer>> graph) {
        boolean[] visited = new boolean[graph.size()];
        boolean[] recursionStack = new boolean[graph.size()];

        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (!visited[vertex] && hasDirectedCycle(graph, vertex, visited, recursionStack)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasDirectedCycle(List<List<Integer>> graph, int vertex, boolean[] visited,
            boolean[] recursionStack) {
        visited[vertex] = true;
        recursionStack[vertex] = true;

        for (Integer neighbor : graph.get(vertex)) {
            if (!visited[neighbor]) {
                if (hasDirectedCycle(graph, neighbor, visited, recursionStack)) {
                    return true;
                }
            } else if (recursionStack[neighbor]) {
                return true;
            }
        }

        recursionStack[vertex] = false;
        return false;
    }
}
