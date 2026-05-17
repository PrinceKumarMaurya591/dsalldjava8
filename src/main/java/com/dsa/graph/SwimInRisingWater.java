package com.dsa.graph;

import java.util.PriorityQueue;

// Problem: Swim in Rising Water
// Link: https://leetcode.com/problems/swim-in-rising-water/
//
// You are given an n x n integer matrix grid where each value grid[i][j] represents
// the elevation at that point (i, j).
//
// The rain starts to fall. At time t, the depth of the water everywhere is t.
// You can swim from a square to another 4-directionally adjacent square if and
// only if the elevation of both squares individually are at most t.
//
// Return the least time until you can reach the bottom right square (n-1, n-1)
// starting from the top left square (0, 0).
//
// Approach: Dijkstra's Algorithm (modified)
// 1. Use a min-heap to always explore the cell with the smallest elevation.
// 2. The time needed is the maximum elevation along the path.
// 3. Track visited cells to avoid revisiting.
//
// Time Complexity: O(n^2 log n)
// Space Complexity: O(n^2)

public class SwimInRisingWater {

    public static void main(String[] args) {
        // Test case 1
        int[][] grid1 = {
            {0, 2},
            {1, 3}
        };
        System.out.println("Swim time: " + swimInWater(grid1));
        // Expected: 3

        // Test case 2
        int[][] grid2 = {
            {0, 1, 2, 3, 4},
            {24, 23, 22, 21, 5},
            {12, 13, 14, 15, 16},
            {11, 17, 18, 19, 20},
            {10, 9, 8, 7, 6}
        };
        System.out.println("Swim time: " + swimInWater(grid2));
        // Expected: 16
    }

    public static int swimInWater(int[][] grid) {
        int n = grid.length;
        if (n == 0) return 0;

        boolean[][] visited = new boolean[n][n];
        // Min-heap: [maxElevationSoFar, row, col]
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{grid[0][0], 0, 0});
        visited[0][0] = true;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int maxElevation = current[0];
            int row = current[1];
            int col = current[2];

            // If we reached the bottom-right corner
            if (row == n - 1 && col == n - 1) {
                return maxElevation;
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n
                        && !visited[newRow][newCol]) {
                    visited[newRow][newCol] = true;
                    // The time needed is the max elevation along the path
                    int newMax = Math.max(maxElevation, grid[newRow][newCol]);
                    pq.offer(new int[]{newMax, newRow, newCol});
                }
            }
        }

        return 0;
    }
}
