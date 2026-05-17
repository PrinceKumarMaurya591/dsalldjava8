package com.dsa.dp;

// Problem: Unique Paths
// Link: https://leetcode.com/problems/unique-paths/
//
// There is a robot on an m x n grid. The robot is initially at the top-left corner.
// The robot tries to move to the bottom-right corner. It can only move down or right.
// How many possible unique paths are there?
//
// Approach: DP
// dp[i][j] = dp[i-1][j] + dp[i][j-1]
// Base: dp[0][j] = 1, dp[i][0] = 1
// Can be optimized to O(n) space using a 1D array.
//
// Time Complexity: O(m * n)
// Space Complexity: O(n)

public class UniquePaths {

    public static void main(String[] args) {
        System.out.println("Unique Paths (3x7): " + uniquePaths(3, 7)); // Expected: 28
        System.out.println("Unique Paths (3x2): " + uniquePaths(3, 2)); // Expected: 3
    }

    public static int uniquePaths(int m, int n) {
        int[] dp = new int[n];
        // Initialize first row
        for (int j = 0; j < n; j++) {
            dp[j] = 1;
        }

        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] = dp[j] + dp[j - 1];
            }
        }

        return dp[n - 1];
    }
}
