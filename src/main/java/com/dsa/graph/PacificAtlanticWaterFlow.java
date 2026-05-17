package com.dsa.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Problem: Pacific Atlantic Water Flow
// Link: https://leetcode.com/problems/pacific-atlantic-water-flow/
//
// There is an m x n rectangular island that borders both the Pacific Ocean
// and Atlantic Ocean. The Pacific Ocean touches the island's left and top edges,
// and the Atlantic Ocean touches the island's right and bottom edges.
//
// The island receives a lot of rain, and the rain water can flow to neighboring
// cells directly north, south, east, and west if the neighboring cell's height
// is less than or equal to the current cell's height.
//
// Return a list of grid coordinates where water can flow to both the Pacific
// and Atlantic oceans.
//
// Approach: DFS from ocean borders
// 1. Start DFS from all cells adjacent to Pacific Ocean (top row, left column).
// 2. Start DFS from all cells adjacent to Atlantic Ocean (bottom row, right column).
// 3. A cell can flow to both oceans if it's reachable from both starting sets.
//
// Time Complexity: O(m * n) - we visit each cell at most twice
// Space Complexity: O(m * n) - visited arrays and recursion stack

public class PacificAtlanticWaterFlow {

    public static void main(String[] args) {
        int[][] heights = {
            {1, 2, 2, 3, 5},
            {3, 2, 3, 4, 4},
            {2, 4, 5, 3, 1},
            {6, 7, 1, 4, 5},
            {5, 1, 1, 2, 4}
        };

        List<List<Integer>> result = pacificAtlantic(heights);
        System.out.println("Cells that can flow to both oceans:");
        for (List<Integer> cell : result) {
            System.out.println("[" + cell.get(0) + ", " + cell.get(1) + "]");
        }
        // Expected: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]
    }

    public static List<List<Integer>> pacificAtlantic(int[][] heights) {
        List<List<Integer>> result = new ArrayList<>();
        if (heights == null || heights.length == 0) return result;

        int rows = heights.length;
        int cols = heights[0].length;

        boolean[][] pacific = new boolean[rows][cols];
        boolean[][] atlantic = new boolean[rows][cols];

        // DFS from Pacific borders (top row and left column)
        for (int i = 0; i < rows; i++) {
            dfs(heights, i, 0, pacific, Integer.MIN_VALUE); // Left column
        }
        for (int j = 0; j < cols; j++) {
            dfs(heights, 0, j, pacific, Integer.MIN_VALUE); // Top row
        }

        // DFS from Atlantic borders (bottom row and right column)
        for (int i = 0; i < rows; i++) {
            dfs(heights, i, cols - 1, atlantic, Integer.MIN_VALUE); // Right column
        }
        for (int j = 0; j < cols; j++) {
            dfs(heights, rows - 1, j, atlantic, Integer.MIN_VALUE); // Bottom row
        }

        // Find cells reachable from both oceans
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (pacific[i][j] && atlantic[i][j]) {
                    result.add(Arrays.asList(i, j));
                }
            }
        }

        return result;
    }

    private static void dfs(int[][] heights, int i, int j,
                            boolean[][] visited, int prevHeight) {
        int rows = heights.length;
        int cols = heights[0].length;

        // Boundary check
        if (i < 0 || i >= rows || j < 0 || j >= cols) return;
        // Already visited or water cannot flow uphill
        if (visited[i][j] || heights[i][j] < prevHeight) return;

        visited[i][j] = true;

        // Explore all 4 directions
        dfs(heights, i - 1, j, visited, heights[i][j]); // Up
        dfs(heights, i + 1, j, visited, heights[i][j]); // Down
        dfs(heights, i, j - 1, visited, heights[i][j]); // Left
        dfs(heights, i, j + 1, visited, heights[i][j]); // Right
    }
}
