package com.dsa.graph;

import java.util.PriorityQueue;

// Problem: Min Cost to Connect All Points
// Link: https://leetcode.com/problems/min-cost-to-connect-all-points/
//
// You are given an array points representing integer coordinates of some points
// on a 2D plane, where points[i] = [xi, yi].
//
// The cost of connecting two points [xi, yi] and [xj, yj] is the Manhattan distance:
// |xi - xj| + |yi - yj|.
//
// Return the minimum cost to make all points connected. All points are connected
// if there is exactly one simple path between any two points.
//
// Approach: Prim's Algorithm (Minimum Spanning Tree)
// 1. Start from any point (index 0).
// 2. Use a min-heap to always pick the edge with smallest cost to an unvisited point.
// 3. Add the cost and mark the point as visited.
//
// Time Complexity: O(V^2 log V) where V = number of points
// Space Complexity: O(V)

public class MinCostToConnectAllPoints {

    public static void main(String[] args) {
        // Test case 1
        int[][] points1 = {{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}};
        System.out.println("Min Cost: " + minCostConnectPoints(points1));
        // Expected: 20

        // Test case 2
        int[][] points2 = {{3, 12}, {-2, 5}, {-4, 1}};
        System.out.println("Min Cost: " + minCostConnectPoints(points2));
        // Expected: 18
    }

    public static int minCostConnectPoints(int[][] points) {
        int n = points.length;
        if (n <= 1) return 0;

        boolean[] visited = new boolean[n];
        // Min-heap: [cost, pointIndex]
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, 0}); // Start from point 0 with cost 0

        int totalCost = 0;
        int edgesUsed = 0;

        while (!pq.isEmpty() && edgesUsed < n) {
            int[] current = pq.poll();
            int cost = current[0];
            int point = current[1];

            if (visited[point]) continue;

            visited[point] = true;
            totalCost += cost;
            edgesUsed++;

            // Add all unvisited neighbors to the heap
            for (int i = 0; i < n; i++) {
                if (!visited[i]) {
                    int manhattanDist = Math.abs(points[point][0] - points[i][0])
                            + Math.abs(points[point][1] - points[i][1]);
                    pq.offer(new int[]{manhattanDist, i});
                }
            }
        }

        return totalCost;
    }
}
