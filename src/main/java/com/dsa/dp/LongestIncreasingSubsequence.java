package com.dsa.dp;

import java.util.Arrays;

// Problem: Longest Increasing Subsequence
// Link: https://leetcode.com/problems/longest-increasing-subsequence/
//
// Given an integer array nums, return the length of the longest strictly increasing
// subsequence.
//
// Approach 1: DP (O(n^2))
// dp[i] = LIS ending at index i
// dp[i] = max(dp[j] + 1) for all j < i where nums[j] < nums[i]
//
// Approach 2: Patience Sorting (O(n log n)) - shown below
// Maintain piles where each pile's top is the smallest possible ending value
// for an increasing subsequence of that length.
//
// Time Complexity: O(n log n)
// Space Complexity: O(n)

public class LongestIncreasingSubsequence {

    public static void main(String[] args) {
        int[] nums1 = {10, 9, 2, 5, 3, 7, 101, 18};
        System.out.println("LIS: " + lengthOfLIS(nums1)); // Expected: 4

        int[] nums2 = {0, 1, 0, 3, 2, 3};
        System.out.println("LIS: " + lengthOfLIS(nums2)); // Expected: 4

        int[] nums3 = {7, 7, 7, 7, 7, 7};
        System.out.println("LIS: " + lengthOfLIS(nums3)); // Expected: 1
    }

    public static int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;

        for (int num : nums) {
            // Binary search to find the first tail >= num
            int left = 0, right = size;

            while (left < right) {
                int mid = left + (right - left) / 2;
                if (tails[mid] < num) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            tails[left] = num;

            if (left == size) {
                size++;
            }
        }

        return size;
    }
}
