package com.dsa.dp;

// Problem: Climbing Stairs
// Link: https://leetcode.com/problems/climbing-stairs/
//
// You are climbing a staircase. It takes n steps to reach the top.
// Each time you can either climb 1 or 2 steps. In how many distinct ways
// can you climb to the top?
//
// Approach: DP (Fibonacci)
// dp[i] = dp[i-1] + dp[i-2]
// Base: dp[0] = 1, dp[1] = 1
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class ClimbingStairs {

    public static void main(String[] args) {
        System.out.println("Climbing Stairs (n=2): " + climbStairs(2)); // Expected: 2
        System.out.println("Climbing Stairs (n=3): " + climbStairs(3)); // Expected: 3
        System.out.println("Climbing Stairs (n=5): " + climbStairs(5)); // Expected: 8
    }

    public static int climbStairs(int n) {
        if (n <= 1) return 1;

        int prev2 = 1; // dp[0]
        int prev1 = 1; // dp[1]

        for (int i = 2; i <= n; i++) {
            int curr = prev1 + prev2;
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
