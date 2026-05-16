package com.dsa.graph;

//Cycle detection in a directed and undirected graph 
// 1. For undirected graph, we can use DFS to detect a cycle. 
// We need to keep track of visited nodes and the parent node. 
// If we encounter a visited node that is not the parent, then there is a cycle.
// 2. For directed graph, we can use DFS to detect a cycle. 
// We need to keep track of visited nodes and the recursion stack. 
// If we encounter a visited node that is in the recursion stack, then there is a cycle.


public class Cycle {

    


    public static void main(String[] args) {

    // Undirected Graph
    Graph undirected = new Graph(4);

    undirected.addUndirectedEdge(0, 1);
    undirected.addUndirectedEdge(1, 2);
    undirected.addUndirectedEdge(2, 0);
    undirected.addUndirectedEdge(2, 3);

    System.out.println("Cycle in Undirected Graph: "
            + hasCycleUndirected(undirected));


    // Directed Graph
    Graph directed = new Graph(4);

    directed.addDirectedEdge(0, 1);
    directed.addDirectedEdge(1, 2);
    directed.addDirectedEdge(2, 0);
    directed.addDirectedEdge(2, 3);

    System.out.println("Cycle in Directed Graph: "
            + hasCycleDirected(directed));
}




    public static boolean hasCycleUndirected(Graph graph) {
        boolean[] visited = new boolean[graph.vertices];
        for (int i = 0; i < graph.vertices; i++) {
            if (!visited[i]) {
                if (dfsUndirected(graph, i, visited, -1)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean dfsUndirected(Graph graph, int vertex, boolean[] visited, int parent) {
        visited[vertex] = true;
        for (Integer neighbor : graph.adjList.get(vertex)) {
            if (!visited[neighbor]) {
                if (dfsUndirected(graph, neighbor, visited, vertex)) {
                    return true;
                }
            } else if (neighbor != parent) {
                return true;
            }
        }
        return false;
    }


    public static boolean hasCycleDirected(Graph graph) {
        boolean[] visited = new boolean[graph.vertices];
        boolean[] recStack = new boolean[graph.vertices];
        for (int i = 0; i < graph.vertices; i++) {
            if (!visited[i]) {
                if (dfsDirected(graph, i, visited, recStack)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static boolean dfsDirected(Graph graph, int vertex, boolean[] visited, boolean[] recStack) {
        visited[vertex] = true;
        recStack[vertex] = true;
        for (Integer neighbor : graph.adjList.get(vertex)) {
            if (!visited[neighbor]) {
                if (dfsDirected(graph, neighbor, visited, recStack)) {
                    return true;
                }
            } else if (recStack[neighbor]) {
                return true;
            }
        }
        recStack[vertex] = false;
        return false;
    }




   

}
