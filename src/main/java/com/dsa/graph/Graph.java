package com.dsa.graph;
import java.util.ArrayList;


public class Graph {


    int vertices;
    ArrayList<ArrayList<Integer>> adjList;

    public Graph(int vertices) {
        this.vertices = vertices;
        this.adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int destination) {
        adjList.get(source).add(destination);
        adjList.get(destination).add(source);
    }

    void printGraph() {
        for (int i = 0; i < vertices; i++) {
            System.out.print(i + " -> ");
            for (Integer neighbor : adjList.get(i)) {
                System.out.print(neighbor + " ");
            }
            System.out.println();
        }
    }


    void removeEdge(int source, int destination) {
        adjList.get(source).remove(Integer.valueOf(destination));
        adjList.get(destination).remove(Integer.valueOf(source));
    }

    void removeVertex(int vertex) {
        // Remove all edges associated with the vertex
        for (Integer neighbor : adjList.get(vertex)) {
            adjList.get(neighbor).remove(Integer.valueOf(vertex));
        }
        // Clear the adjacency list for the vertex
        adjList.get(vertex).clear();
    }


    void dfs(int vertex,boolean[] visited){
        visited[vertex] = true;
        System.out.print(vertex + " ");
        for(Integer neighbor : adjList.get(vertex)){
            if(!visited[neighbor]){
                dfs(neighbor,visited);
            }
        }
    }


    void bfs(int startVertex){
        boolean[] visited = new boolean[vertices];
        ArrayList<Integer> queue = new ArrayList<>();
        visited[startVertex] = true;
        queue.add(startVertex);

        while(!queue.isEmpty()){
            int vertex = queue.remove(0);
            System.out.print(vertex + " ");
            for(Integer neighbor : adjList.get(vertex)){
                if(!visited[neighbor]){
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }


    public static void main(String[] args) {
        int vertices = 4;
     Graph graph = new Graph(vertices);
     graph.addEdge(0,1);
     graph.addEdge(0,2);
     graph.addEdge(1,2);
     graph.addEdge(2,0);
     graph.addEdge(2,3);
     graph.addEdge(3,3);

     graph.printGraph();

     graph.removeEdge(2,3);
     graph.printGraph();
    //  System.out.println(graph);

     graph.removeVertex(3);
     graph.printGraph();
    //DFS Traversal
    System.out.println("DFS Traversal:");
    graph.dfs(0,new boolean[vertices]);
    //BFS Traversal
    System.out.println("\nBFS Traversal:");
    graph.bfs(0);
    }


    


}