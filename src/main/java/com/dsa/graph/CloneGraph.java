package com.dsa.graph;

//Steps to clone a graph:

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//1. Create a mapping of original nodes to their clones using a HashMap.
//2. Use a queue to perform a breadth-first traversal of the graph.
//3. For each node, if it has not been cloned, create a clone and add it to the mapping.
//4. For each neighbor of the current node, if it has not been cloned, create a clone and add it to the mapping. Then, add the clone of the neighbor to the neighbors of the clone of the current node.
//5. Continue this process until all nodes have been visited and cloned.

public class CloneGraph {

    static class Node {
            public int val;
            public List<Node> neighbors;
    
            public Node(int val) {
                this.val = val;
                this.neighbors = new ArrayList<>();
            }
        }

        public static void main(String[] args) {
            
            Node node1 = new Node(1);
            Node node2 = new Node(2);
            Node node3 = new Node(3);
            Node node4 = new Node(4);
            node1.neighbors.add(node2);
            node1.neighbors.add(node4);
            node2.neighbors.add(node1);
            node2.neighbors.add(node3);
            node3.neighbors.add(node2);
            node3.neighbors.add(node4);
            node4.neighbors.add(node1);
            node4.neighbors.add(node3);
            Node clonedGraph = cloneGraph(node1);
            System.out.println("Original Node: " + node1.val);
            System.out.println("Cloned Node: " + clonedGraph.val);

        }
    
        public static Node cloneGraph(Node node) {
            if (node == null) return null;
        Map<Node, Node> map = new HashMap<>();
        return cloneGraphHelper(node, map);
    }

    // Helper method to perform the cloning using DFS
    // This method takes a node and the mapping of original nodes to their clones as parameters. 
    // It checks if the node has already been cloned (i.e., if it exists in the map). 
    // If it has, it returns the cloned node from the map. 
    // If it hasn't, it creates a new clone of the node, adds it to the map, 
    // and then recursively clones all of its neighbors, 
    // adding the cloned neighbors to the neighbors list of the cloned node. 
    // Finally, it returns the cloned node.
    private static Node cloneGraphHelper(Node node, Map<Node, Node> map) {
      
        
        if(map.containsKey(node)) {
            return map.get(node);
        }
        
        // Create a clone of the starting node and add it to the map
        Node clone = new Node(node.val);
        map.put(node, clone);

        //Clone the neighbors
        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(cloneGraphHelper(neighbor, map));
        }

        return clone;
    }
        

}
