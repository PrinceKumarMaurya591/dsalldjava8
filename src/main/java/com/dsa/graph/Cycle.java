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



// Method to detect cycle in an undirected graph using DFS
// This method takes a graph as input and returns true if there is a cycle in the graph, otherwise false.
    public static boolean hasCycleUndirected(Graph graph) {
        boolean[] visited = new boolean[graph.vertices]; // Create a boolean array to keep track of visited nodes
        for (int i = 0; i < graph.vertices; i++) {
            if (!visited[i]) { // If the node has not been visited, perform DFS from that node
                if (dfsUndirected(graph, i, visited, -1)) {  // If a cycle is detected during DFS, return true
                    return true;
                }
            }
        }
        return false;
    }

// Helper method to perform DFS for cycle detection in an undirected graph
// This method takes the graph, the current vertex, the visited array, and the parent vertex as parameters. 
// It marks the current vertex as visited and iterates through its neighbors. 
// If a neighbor has not been visited, it recursively calls itself with the neighbor as the current vertex and the current vertex as the parent. 
// If a neighbor has been visited and is not the parent, it means there is a cycle, and the method returns true. 
// If no cycle is detected after checking all neighbors, it returns false.

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
