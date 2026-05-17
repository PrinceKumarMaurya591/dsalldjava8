package com.dsa.recursion;

import java.util.ArrayList;
import java.util.List;

// Problem: N-Queens
// Link: https://leetcode.com/problems/n-queens/
//
// The n-queens puzzle is the problem of placing n queens on an n x n chessboard
// such that no two queens attack each other.
//
// Given an integer n, return all distinct solutions to the n-queens puzzle.
// Each solution contains a distinct board configuration of the n-queens' placement,
// where 'Q' and '.' indicate a queen and an empty space, respectively.
//
// Approach: Backtracking
// - Place queens row by row
// - For each row, try all columns
// - Check if the position is safe (no other queen in same column, diagonal)
// - Use sets to track occupied columns and diagonals for O(1) check
//
// Time Complexity: O(n!) - factorial
// Space Complexity: O(n) - recursion depth + sets

import java.util.HashSet;
import java.util.Set;

public class NQueens {

    public static void main(String[] args) {
        System.out.println("=== N-Queens ===");
        System.out.println("Solutions for n=4: " + solveNQueens(4));
        // Expected: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]

        System.out.println("Solutions for n=1: " + solveNQueens(1));
        // Expected: [["Q"]]

        System.out.println("Number of solutions for n=8: " + solveNQueens(8).size());
        // Expected: 92
    }

    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];

        // Initialize board with '.'
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }

        // Sets for O(1) conflict checking
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>(); // row - col (constant for main diagonal)
        Set<Integer> diag2 = new HashSet<>(); // row + col (constant for anti-diagonal)

        backtrack(board, 0, n, cols, diag1, diag2, result);
        return result;
    }

    private static void backtrack(char[][] board, int row, int n,
                                   Set<Integer> cols, Set<Integer> diag1,
                                   Set<Integer> diag2, List<List<String>> result) {
        if (row == n) {
            // Convert board to list of strings
            List<String> solution = new ArrayList<>();
            for (char[] rowArr : board) {
                solution.add(new String(rowArr));
            }
            result.add(solution);
            return;
        }

        for (int col = 0; col < n; col++) {
            int d1 = row - col; // Main diagonal index
            int d2 = row + col; // Anti-diagonal index

            if (cols.contains(col) || diag1.contains(d1) || diag2.contains(d2)) {
                continue; // Conflict
            }

            // Place queen
            board[row][col] = 'Q';
            cols.add(col);
            diag1.add(d1);
            diag2.add(d2);

            // Recurse to next row
            backtrack(board, row + 1, n, cols, diag1, diag2, result);

            // Backtrack
            board[row][col] = '.';
            cols.remove(col);
            diag1.remove(d1);
            diag2.remove(d2);
        }
    }
}
