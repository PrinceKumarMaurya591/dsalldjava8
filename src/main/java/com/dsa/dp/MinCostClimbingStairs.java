package com.dsa.dp;

// Problem: Min Cost Climbing Stairs
// Link: https://leetcode.com/problems/min-cost-climbing-stairs/
//
// You are given an integer array cost where cost[i] is the cost of i-th step
// on a staircase. Once you pay the cost, you can either climb one or two steps.
// You can start from index 0 or index 1.
// Return the minimum cost to reach the top of the floor.
//
// Approach: DP (bottom-up)
// dp[i] = cost[i] + min(dp[i-1], dp[i-2])
// Result = min(dp[n-1], dp[n-2])
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class MinCostClimbingStairs {

    public static void main(String[] args) {
        int[] cost1 = {10, 15, 20};
        System.out.println("Min Cost: " + minCostClimbingStairs(cost1)); // Expected: 15

        int[] cost2 = {1, 100, 1, 1, 1, 100, 1, 1, 100, 1};
        System.out.println("Min Cost: " + minCostClimbingStairs(cost2)); // Expected: 6
    }

    public static int minCostClimbingStairs(int[] cost) {
        int n = cost.length;
        if (n == 0) return 0;
        if (n == 1) return cost[0];

        int prev2 = cost[0];
        int prev1 = cost[1];

        for (int i = 2; i < n; i++) {
            int curr = cost[i] + Math.min(prev1, prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return Math.min(prev1, prev2);
    }
}
