package com.dsa.graph;
import java.util.LinkedList;
import java.util.Queue;

// Step-by-step explanation of the code:
// 1.Sab Roots ko queue mein daalenge
// 2.fresh oranges ki count karenge.
// 3.Queue se ek ek rooten orange nikalenge
// 4.Uske adjacent cells ko check karenge
// 5.abstract agar koi fresh orange hai to usko rotten mark karenge aur queue mein daalenge
// 6. Time increase karenge har level ke baad
// 7. Agar fresh orange count zero ho jata hai to time return karenge, nahi to-1 return karenge.

public class RottenOrange {

    
    public static void main(String[] args) {
        int[][] grid = {
                {2, 1, 1},
                {1, 1, 0},
                {0, 1, 1}
        };
        System.out.println(orangesRotting(grid));
    }

    public static int orangesRotting(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int freshCount = 0;
        int minutes = 0;

        // Count fresh oranges and add rotten oranges to the queue
        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    freshCount++;
                } else if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                }
            }
        }

        // Directions for adjacent cells
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // BFS to rot adjacent fresh oranges
        while (!queue.isEmpty() && freshCount > 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] current = queue.poll();
                for (int[] dir : directions) {
                    int newRow = current[0] + dir[0];
                    int newCol = current[1] + dir[1];
                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && grid[newRow][newCol] == 1) {
                        grid[newRow][newCol] = 2; // Mark as rotten
                        queue.offer(new int[]{newRow, newCol});
                        freshCount--;
                    }
                }
            }
            minutes++;
        }

        return freshCount == 0 ? minutes : -1;
    }

}
