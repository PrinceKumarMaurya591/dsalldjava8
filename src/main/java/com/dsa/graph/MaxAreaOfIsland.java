package com.dsa.graph;

// Problem: Max Area of Island
// Link: https://leetcode.com/problems/max-area-of-island/
//
// You are given an m x n binary matrix grid. An island is a group of 1's
// (representing land) connected 4-directionally (horizontal or vertical.)
// Find the maximum area of an island in the grid. If there is no island, return 0.
//
// Approach: DFS
// 1. Iterate through each cell in the grid.
// 2. When we find a '1', perform DFS to explore the entire island,
//    counting the area and marking visited cells as '0'.
// 3. Keep track of the maximum area found.
//
// Time Complexity: O(m * n) - we visit each cell at most once
// Space Complexity: O(m * n) - recursion stack in worst case

public class MaxAreaOfIsland {

    public static void main(String[] args) {
        int[][] grid = {
            {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
            {0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0},
            {0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0}
        };
        System.out.println("Max Area of Island: " + maxAreaOfIsland(grid));
        // Expected: 6
    }

    public static int maxAreaOfIsland(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int maxArea = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    int area = dfs(grid, i, j);
                    maxArea = Math.max(maxArea, area);
                }
            }
        }
        return maxArea;
    }

    private static int dfs(int[][] grid, int i, int j) {
        // Boundary check and water check
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] == 0) {
            return 0;
        }

        // Mark as visited (sink the land)
        grid[i][j] = 0;

        // Count current cell + explore 4 directions
        int area = 1;
        area += dfs(grid, i - 1, j); // Up
        area += dfs(grid, i + 1, j); // Down
        area += dfs(grid, i, j - 1); // Left
        area += dfs(grid, i, j + 1); // Right

        return area;
    }
}
