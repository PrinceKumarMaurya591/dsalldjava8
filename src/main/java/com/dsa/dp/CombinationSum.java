package com.dsa.dp;

import java.util.ArrayList;
import java.util.List;

// Problem: Combination Sum IV (actually Combination Sum - DP version)
// Link: https://leetcode.com/problems/combination-sum-iv/
//
// Given an array of distinct integers nums and a target integer target, return the
// number of possible combinations that add up to target.
//
// Note: This is Combination Sum IV - counting combinations (order matters).
// The original Combination Sum (I) uses backtracking.
//
// Approach: DP (bottom-up)
// dp[i] = number of ways to make sum i
// dp[i] = sum(dp[i - num]) for each num in nums where num <= i
//
// Time Complexity: O(target * n)
// Space Complexity: O(target)

public class CombinationSum {

    public static void main(String[] args) {
        int[] nums1 = {1, 2, 3};
        System.out.println("Combination Sum: " + combinationSum4(nums1, 4)); // Expected: 7

        int[] nums2 = {9};
        System.out.println("Combination Sum: " + combinationSum4(nums2, 3)); // Expected: 0
    }

    public static int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1; // One way to make sum 0: choose nothing

        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (num <= i) {
                    dp[i] += dp[i - num];
                }
            }
        }

        return dp[target];
    }
}
