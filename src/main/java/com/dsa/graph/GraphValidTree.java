package com.dsa.graph;

import java.util.ArrayList;
import java.util.List;

// Problem: Graph Valid Tree
// Link: https://leetcode.com/problems/graph-valid-tree/ (Premium)
//       https://www.lintcode.com/problem/178/
//
// Given n nodes labeled from 0 to n-1 and a list of undirected edges
// (each edge is a pair of nodes), write a function to check whether these
// edges make up a valid tree.
//
// A valid tree must:
// 1. Be fully connected (all nodes reachable from any node).
// 2. Have exactly n-1 edges (no cycles).
//
// Approach: Union-Find (DSU)
// 1. A valid tree must have exactly n-1 edges.
// 2. Use Union-Find to detect cycles. If adding an edge connects two nodes
//    already in the same set, there's a cycle.
// 3. After processing all edges, all nodes should be in one component.
//
// Time Complexity: O(n * α(n)) where α is the inverse Ackermann function
// Space Complexity: O(n)

public class GraphValidTree {

    public static void main(String[] args) {
        // Test case 1: Valid tree
        int n1 = 5;
        int[][] edges1 = {{0, 1}, {0, 2}, {0, 3}, {1, 4}};
        System.out.println("Valid tree (5 nodes, 4 edges): " + validTree(n1, edges1));
        // Expected: true

        // Test case 2: Has cycle
        int n2 = 5;
        int[][] edges2 = {{0, 1}, {1, 2}, {2, 3}, {1, 3}, {1, 4}};
        System.out.println("Valid tree (5 nodes, cycle): " + validTree(n2, edges2));
        // Expected: false

        // Test case 3: Not connected
        int n3 = 4;
        int[][] edges3 = {{0, 1}, {2, 3}};
        System.out.println("Valid tree (4 nodes, disconnected): " + validTree(n3, edges3));
        // Expected: false
    }

    public static boolean validTree(int n, int[][] edges) {
        // A valid tree must have exactly n-1 edges
        if (edges.length != n - 1) return false;

        int[] parent = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];

            // If u and v are already connected, adding this edge creates a cycle
            if (find(parent, u) == find(parent, v)) {
                return false;
            }

            union(parent, rank, u, v);
        }

        return true;
    }

    private static int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    private static void union(int[] parent, int[] rank, int x, int y) {
        int rootX = find(parent, x);
        int rootY = find(parent, y);

        if (rootX == rootY) return;

        if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }
}
