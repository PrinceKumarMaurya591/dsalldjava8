package com.dsa.graph;

// Problem: Number of Provinces
// Link: https://leetcode.com/problems/number-of-provinces/
//
// There are n cities. Some of them are connected, while some are not.
// If city a is connected directly with city b, and city b is connected directly
// with city c, then city a is connected indirectly with city c.
//
// A province is a group of directly or indirectly connected cities and no other
// cities outside of the group.
//
// You are given an n x n matrix isConnected where isConnected[i][j] = 1 if the
// i-th city and the j-th city are directly connected, and isConnected[i][j] = 0 otherwise.
//
// Return the total number of provinces.
//
// Approach: DFS
// 1. Iterate through each city.
// 2. If a city is not visited, start a DFS to mark all connected cities as visited.
// 3. Increment province count for each new DFS.
//
// Time Complexity: O(n^2) - we traverse the adjacency matrix
// Space Complexity: O(n) - visited array + recursion stack

public class NumberOfProvinces {

    public static void main(String[] args) {
        // Test case 1
        int[][] isConnected1 = {
            {1, 1, 0},
            {1, 1, 0},
            {0, 0, 1}
        };
        System.out.println("Number of Provinces: " + findCircleNum(isConnected1));
        // Expected: 2

        // Test case 2
        int[][] isConnected2 = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        };
        System.out.println("Number of Provinces: " + findCircleNum(isConnected2));
        // Expected: 3
    }

    public static int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int provinces = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                provinces++;
                dfs(isConnected, visited, i);
            }
        }

        return provinces;
    }

    private static void dfs(int[][] isConnected, boolean[] visited, int city) {
        visited[city] = true;

        for (int neighbor = 0; neighbor < isConnected.length; neighbor++) {
            if (isConnected[city][neighbor] == 1 && !visited[neighbor]) {
                dfs(isConnected, visited, neighbor);
            }
        }
    }
}
