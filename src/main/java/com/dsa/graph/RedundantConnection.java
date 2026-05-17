package com.dsa.graph;

// Problem: Redundant Connection
// Link: https://leetcode.com/problems/redundant-connection/
//
// In this problem, a tree is an undirected graph that is connected and has no cycles.
// You are given a graph that started as a tree with n nodes labeled from 1 to n,
// with one additional edge added. The added edge has two different vertices chosen
// from 1 to n, and was not an edge that already existed.
//
// Return an edge that can be removed so that the resulting graph is a tree of n nodes.
// If there are multiple answers, return the edge that occurs last in the input.
//
// Approach: Union-Find (Disjoint Set Union)
// 1. Initialize parent array where each node is its own parent.
// 2. For each edge (u, v), find the root of u and v.
// 3. If they have the same root, this edge creates a cycle → return it.
// 4. Otherwise, union the two sets.
//
// Time Complexity: O(n * α(n)) where α is the inverse Ackermann function
// Space Complexity: O(n)

public class RedundantConnection {

    public static void main(String[] args) {
        // Test case 1
        int[][] edges1 = {{1, 2}, {1, 3}, {2, 3}};
        int[] result1 = findRedundantConnection(edges1);
        System.out.println("Redundant edge: [" + result1[0] + ", " + result1[1] + "]");
        // Expected: [2, 3]

        // Test case 2
        int[][] edges2 = {{1, 2}, {2, 3}, {3, 4}, {1, 4}, {1, 5}};
        int[] result2 = findRedundantConnection(edges2);
        System.out.println("Redundant edge: [" + result2[0] + ", " + result2[1] + "]");
        // Expected: [1, 4]
    }

    public static int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1]; // 1-indexed nodes
        int[] rank = new int[n + 1];

        // Initialize each node as its own parent
        for (int i = 1; i <= n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];

            // If u and v are already connected, this edge is redundant
            if (find(parent, u) == find(parent, v)) {
                return edge;
            }

            // Union the two sets
            union(parent, rank, u, v);
        }

        return new int[0]; // Should never reach here for valid input
    }

    private static int find(int[] parent, int x) {
        // Path compression
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    private static void union(int[] parent, int[] rank, int x, int y) {
        int rootX = find(parent, x);
        int rootY = find(parent, y);

        if (rootX == rootY) return;

        // Union by rank
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
