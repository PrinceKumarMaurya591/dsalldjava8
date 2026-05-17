package com.dsa.recursion;

// Problem: Word Search
// Link: https://leetcode.com/problems/word-search/
//
// Given an m x n grid of characters board and a string word, return true if word
// exists in the grid.
//
// The word can be constructed from letters of sequentially adjacent cells, where
// adjacent cells are horizontally or vertically neighboring. The same letter cell
// may not be used more than once.
//
// Approach: Backtracking (DFS)
// - For each cell matching the first character, start DFS
// - Mark visited cells by temporarily changing the character
// - Explore all 4 directions
// - Backtrack by restoring the character
//
// Time Complexity: O(m * n * 4^L) where L = word length
// Space Complexity: O(L) - recursion depth

public class WordSearch {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static void main(String[] args) {
        System.out.println("=== Word Search ===");
        char[][] board1 = {
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}
        };
        System.out.println("Search 'ABCCED': " + exist(board1, "ABCCED")); // true
        System.out.println("Search 'SEE': " + exist(board1, "SEE"));       // true
        System.out.println("Search 'ABCB': " + exist(board1, "ABCB"));     // false

        char[][] board2 = {{'a'}};
        System.out.println("Search 'a': " + exist(board2, "a"));           // true
        System.out.println("Search 'b': " + exist(board2, "b"));           // false
    }

    public static boolean exist(char[][] board, String word) {
        int m = board.length;
        int n = board[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == word.charAt(0)) {
                    if (dfs(board, word, i, j, 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean dfs(char[][] board, String word, int i, int j, int index) {
        if (index == word.length()) return true;

        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length
                || board[i][j] != word.charAt(index)) {
            return false;
        }

        // Mark as visited
        char temp = board[i][j];
        board[i][j] = '#';

        for (int[] dir : DIRECTIONS) {
            if (dfs(board, word, i + dir[0], j + dir[1], index + 1)) {
                return true;
            }
        }

        // Backtrack
        board[i][j] = temp;
        return false;
    }
}
