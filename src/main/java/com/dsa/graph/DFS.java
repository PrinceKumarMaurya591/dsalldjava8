package com.dsa.graph;

import java.util.LinkedList;

public class DFS {

    public static void main(String[] args) {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);

        System.out.println("DFS Traversal:");
        graph.dfs(0);
    }

    public static class Graph {
        private int V; // number of vertices
        private LinkedList<Integer> adjList[]; // adjacency list

        public Graph(int v) {
            V = v;
            adjList = new LinkedList[v];
            for (int i = 0; i < v; i++) {
                adjList[i] = new LinkedList();
            }
        }

        public void addEdge(int src, int dest) {
            adjList[src].add(dest);
            adjList[dest].add(src); // For undirected graph
        }

        public void dfs(int start) {
            boolean visited[] = new boolean[V];
            dfsUtil(start, visited);
        }

        private void dfsUtil(int vertex, boolean visited[]) {
            visited[vertex] = true;
            System.out.print(vertex + " ");

            for (Integer neighbor : adjList[vertex]) {
                if (!visited[neighbor]) {
                    dfsUtil(neighbor, visited);
                }
            }
        }
    }

}
