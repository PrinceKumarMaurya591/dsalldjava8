package com.dsa.dp;

// Problem: Jump Game II
// Link: https://leetcode.com/problems/jump-game-ii/
//
// You are given a 0-indexed array of integers nums of length n. You are initially
// positioned at nums[0]. Each element nums[i] represents the maximum jump length
// from that position. Return the minimum number of jumps to reach nums[n-1].
//
// Approach: Greedy (BFS-like)
// Track the current jump's end and the furthest reachable position.
// When we reach the end of the current jump, increment jumps count.
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class JumpGameII {

    public static void main(String[] args) {
        int[] nums1 = {2, 3, 1, 1, 4};
        System.out.println("Jump Game II: " + jump(nums1)); // Expected: 2

        int[] nums2 = {2, 3, 0, 1, 4};
        System.out.println("Jump Game II: " + jump(nums2)); // Expected: 2
    }

    public static int jump(int[] nums) {
        int jumps = 0;
        int currentEnd = 0;
        int furthest = 0;

        for (int i = 0; i < nums.length - 1; i++) {
            furthest = Math.max(furthest, i + nums[i]);

            if (i == currentEnd) {
                jumps++;
                currentEnd = furthest;
            }
        }

        return jumps;
    }
}
