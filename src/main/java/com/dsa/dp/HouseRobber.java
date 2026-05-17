package com.dsa.dp;

// Problem: House Robber
// Link: https://leetcode.com/problems/house-robber/
//
// You are a professional robber planning to rob houses along a street.
// Each house has a certain amount of money stashed. Adjacent houses have
// security systems connected, so if you rob two adjacent houses, the police
// will be alerted. Return the maximum amount you can rob without alerting police.
//
// Approach: DP
// dp[i] = max(dp[i-1], dp[i-2] + nums[i])
// Either skip current house (dp[i-1]) or rob it (dp[i-2] + nums[i])
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class HouseRobber {

    public static void main(String[] args) {
        int[] nums1 = {1, 2, 3, 1};
        System.out.println("House Robber: " + rob(nums1)); // Expected: 4

        int[] nums2 = {2, 7, 9, 3, 1};
        System.out.println("House Robber: " + rob(nums2)); // Expected: 12
    }

    public static int rob(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int prev2 = nums[0];
        int prev1 = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            int curr = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
