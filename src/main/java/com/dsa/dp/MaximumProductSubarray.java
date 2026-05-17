package com.dsa.dp;

// Problem: Maximum Product Subarray
// Link: https://leetcode.com/problems/maximum-product-subarray/
//
// Given an integer array nums, find a contiguous non-empty subarray within the
// array that has the largest product, and return the product.
//
// Approach: DP (Kadane's Algorithm variation)
// Track both max and min product ending at current position, because a negative
// number can turn a minimum into a maximum.
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class MaximumProductSubarray {

    public static void main(String[] args) {
        int[] nums1 = {2, 3, -2, 4};
        System.out.println("Max Product: " + maxProduct(nums1)); // Expected: 6

        int[] nums2 = {-2, 0, -1};
        System.out.println("Max Product: " + maxProduct(nums2)); // Expected: 0
    }

    public static int maxProduct(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        int maxSoFar = nums[0];
        int minSoFar = nums[0];
        int result = nums[0];

        for (int i = 1; i < nums.length; i++) {
            int curr = nums[i];

            // Store maxSoFar before it's updated
            int tempMax = Math.max(curr, Math.max(maxSoFar * curr, minSoFar * curr));
            minSoFar = Math.min(curr, Math.min(maxSoFar * curr, minSoFar * curr));

            maxSoFar = tempMax;
            result = Math.max(result, maxSoFar);
        }

        return result;
    }
}
