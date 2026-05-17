package com.dsa.dp;

// Problem: House Robber II
// Link: https://leetcode.com/problems/house-robber-ii/
//
// Same as House Robber, but houses are arranged in a circle.
// The first and last houses are adjacent, so you cannot rob both.
//
// Approach: DP (run House Robber twice)
// Since houses are in a circle, we have two cases:
// 1. Rob houses 0 to n-2 (exclude last)
// 2. Rob houses 1 to n-1 (exclude first)
// Return max of both cases.
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class HouseRobberII {

    public static void main(String[] args) {
        int[] nums1 = {2, 3, 2};
        System.out.println("House Robber II: " + rob(nums1)); // Expected: 3

        int[] nums2 = {1, 2, 3, 1};
        System.out.println("House Robber II: " + rob(nums2)); // Expected: 4

        int[] nums3 = {1, 2, 3};
        System.out.println("House Robber II: " + rob(nums3)); // Expected: 3
    }

    public static int rob(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        if (nums.length == 2) return Math.max(nums[0], nums[1]);

        // Case 1: Exclude last house
        int max1 = robLinear(nums, 0, nums.length - 2);
        // Case 2: Exclude first house
        int max2 = robLinear(nums, 1, nums.length - 1);

        return Math.max(max1, max2);
    }

    private static int robLinear(int[] nums, int start, int end) {
        int prev2 = nums[start];
        int prev1 = Math.max(nums[start], nums[start + 1]);

        for (int i = start + 2; i <= end; i++) {
            int curr = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
