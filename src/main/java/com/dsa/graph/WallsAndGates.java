package com.dsa.graph;

import java.util.LinkedList;
import java.util.Queue;

// Problem: Walls and Gates
// Link: https://leetcode.com/problems/walls-and-gates/ (Premium)
//
// You are given an m x n grid rooms initialized with these three possible values:
// -1 : A wall or an obstacle.
// 0 : A gate.
// INF : Infinity means an empty room.
// Fill each empty room with the distance to its nearest gate. If it is impossible
// to reach a gate, it should be filled with INF.
//
// Approach: Multi-source BFS
// 1. Add all gates (0) to the queue as starting points.
// 2. Perform BFS level by level. For each cell, update its distance
//    as the current level (distance from nearest gate).
// 3. Only update empty rooms (INF) that haven't been visited yet.
//
// Time Complexity: O(m * n) - we visit each cell at most once
// Space Complexity: O(m * n) - queue space

public class WallsAndGates {

    public static void main(String[] args) {
        int INF = Integer.MAX_VALUE;
        int[][] rooms = {
            {INF, -1, 0, INF},
            {INF, INF, INF, -1},
            {INF, -1, INF, -1},
            {0, -1, INF, INF}
        };

        wallsAndGates(rooms);

        System.out.println("Walls and Gates result:");
        for (int[] row : rooms) {
            for (int val : row) {
                if (val == INF) {
                    System.out.print("INF ");
                } else {
                    System.out.print(val + "   ");
                }
            }
            System.out.println();
        }
        // Expected:
        // 3   -1   0    1
        // 2    2   1   -1
        // 1   -1   2   -1
        // 0   -1   3    4
    }

    public static void wallsAndGates(int[][] rooms) {
        if (rooms == null || rooms.length == 0) return;

        int rows = rooms.length;
        int cols = rooms[0].length;
        Queue<int[]> queue = new LinkedList<>();

        // Add all gates to the queue
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (rooms[i][j] == 0) {
                    queue.offer(new int[]{i, j});
                }
            }
        }

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // BFS from all gates simultaneously
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                // Skip if out of bounds or not an empty room
                if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols
                        || rooms[newRow][newCol] != Integer.MAX_VALUE) {
                    continue;
                }

                // Update distance
                rooms[newRow][newCol] = rooms[row][col] + 1;
                queue.offer(new int[]{newRow, newCol});
            }
        }
    }
}
