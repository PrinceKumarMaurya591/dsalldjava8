package com.dsa.dp;

// Problem: Jump Game
// Link: https://leetcode.com/problems/jump-game/
//
// You are given an integer array nums. You are initially positioned at the array's
// first index, and each element in the array represents your maximum jump length
// at that position. Return true if you can reach the last index.
//
// Approach: Greedy
// Track the furthest reachable index. Iterate through the array, updating the
// furthest reachable. If at any point i > furthest, return false.
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class JumpGame {

    public static void main(String[] args) {
        int[] nums1 = {2, 3, 1, 1, 4};
        System.out.println("Jump Game: " + canJump(nums1)); // Expected: true

        int[] nums2 = {3, 2, 1, 0, 4};
        System.out.println("Jump Game: " + canJump(nums2)); // Expected: false
    }

    public static boolean canJump(int[] nums) {
        int furthest = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i > furthest) return false;
            furthest = Math.max(furthest, i + nums[i]);
            if (furthest >= nums.length - 1) return true;
        }

        return false;
    }
}
