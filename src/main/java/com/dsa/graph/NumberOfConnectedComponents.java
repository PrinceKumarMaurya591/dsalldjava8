package com.dsa.graph;

// Problem: Number of Connected Components in an Undirected Graph
// Link: https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/ (Premium)
//       https://www.lintcode.com/problem/431/
//
// Given n nodes labeled from 0 to n - 1 and a list of undirected edges
// (each edge is a pair of nodes), write a function to find the number of
// connected components in an undirected graph.
//
// Approach: Union-Find (DSU)
// 1. Initialize each node as its own parent.
// 2. For each edge, union the two nodes.
// 3. Count the number of distinct roots (nodes that are their own parent).
//
// Time Complexity: O(n + E * α(n)) where α is the inverse Ackermann function
// Space Complexity: O(n)

public class NumberOfConnectedComponents {

    public static void main(String[] args) {
        // Test case 1
        int n1 = 5;
        int[][] edges1 = {{0, 1}, {1, 2}, {3, 4}};
        System.out.println("Connected Components: " + countComponents(n1, edges1));
        // Expected: 2

        // Test case 2
        int n2 = 5;
        int[][] edges2 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}};
        System.out.println("Connected Components: " + countComponents(n2, edges2));
        // Expected: 1
    }

    public static int countComponents(int n, int[][] edges) {
        int[] parent = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }

        for (int[] edge : edges) {
            union(parent, rank, edge[0], edge[1]);
        }

        // Count distinct roots
        int components = 0;
        for (int i = 0; i < n; i++) {
            if (parent[i] == i) {
                components++;
            }
        }

        return components;
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
