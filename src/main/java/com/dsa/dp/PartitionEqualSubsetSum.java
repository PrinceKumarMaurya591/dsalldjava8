package com.dsa.dp;

// Problem: Partition Equal Subset Sum
// Link: https://leetcode.com/problems/partition-equal-subset-sum/
//
// Given an integer array nums, return true if you can partition the array into
// two subsets such that the sum of elements in both subsets is equal.
//
// Approach: DP (0/1 Knapsack)
// If total sum is odd, return false.
// Find if there's a subset with sum = totalSum / 2.
// dp[i] = true if sum i can be formed using some elements.
//
// Time Complexity: O(n * sum)
// Space Complexity: O(sum)

public class PartitionEqualSubsetSum {

    public static void main(String[] args) {
        int[] nums1 = {1, 5, 11, 5};
        System.out.println("Can Partition: " + canPartition(nums1)); // Expected: true

        int[] nums2 = {1, 2, 3, 5};
        System.out.println("Can Partition: " + canPartition(nums2)); // Expected: false
    }

    public static boolean canPartition(int[] nums) {
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        // If total sum is odd, cannot partition equally
        if (totalSum % 2 != 0) return false;

        int target = totalSum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            // Traverse backwards to avoid reusing the same element
            for (int sum = target; sum >= num; sum--) {
                if (dp[sum - num]) {
                    dp[sum] = true;
                }
            }
        }

        return dp[target];
    }
}
