package com.dsa.graph;

// Problem: Surrounded Regions
// Link: https://leetcode.com/problems/surrounded-regions/
//
// Given an m x n matrix board containing 'X' and 'O', capture all regions that
// are 4-directionally surrounded by 'X'. A region is captured by flipping all
// 'O's into 'X's in that surrounded region.
//
// Approach: DFS from border
// 1. Any 'O' connected to the border cannot be surrounded.
// 2. Start DFS from all border 'O's, marking them as visited (e.g., '#').
// 3. After DFS, flip all remaining 'O's to 'X' (they are surrounded).
// 4. Flip all '#' back to 'O' (they are not surrounded).
//
// Time Complexity: O(m * n)
// Space Complexity: O(m * n) - recursion stack in worst case

public class SurroundedRegions {

    public static void main(String[] args) {
        char[][] board = {
            {'X', 'X', 'X', 'X'},
            {'X', 'O', 'O', 'X'},
            {'X', 'X', 'O', 'X'},
            {'X', 'O', 'X', 'X'}
        };

        solve(board);

        System.out.println("Surrounded Regions result:");
        for (char[] row : board) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        // Expected:
        // X X X X
        // X X X X
        // X X X X
        // X O X X
    }

    public static void solve(char[][] board) {
        if (board == null || board.length == 0) return;

        int rows = board.length;
        int cols = board[0].length;

        // Mark all border-connected 'O's
        for (int i = 0; i < rows; i++) {
            if (board[i][0] == 'O') dfs(board, i, 0);           // Left border
            if (board[i][cols - 1] == 'O') dfs(board, i, cols - 1); // Right border
        }
        for (int j = 0; j < cols; j++) {
            if (board[0][j] == 'O') dfs(board, 0, j);           // Top border
            if (board[rows - 1][j] == 'O') dfs(board, rows - 1, j); // Bottom border
        }

        // Flip remaining 'O' to 'X' and '#' back to 'O'
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X'; // Surrounded region
                } else if (board[i][j] == '#') {
                    board[i][j] = 'O'; // Border-connected, restore
                }
            }
        }
    }

    private static void dfs(char[][] board, int i, int j) {
        int rows = board.length;
        int cols = board[0].length;

        if (i < 0 || i >= rows || j < 0 || j >= cols || board[i][j] != 'O') {
            return;
        }

        // Mark as border-connected
        board[i][j] = '#';

        dfs(board, i - 1, j); // Up
        dfs(board, i + 1, j); // Down
        dfs(board, i, j - 1); // Left
        dfs(board, i, j + 1); // Right
    }
}
