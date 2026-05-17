package com.dsa.recursion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Problem: Subsets II
// Link: https://leetcode.com/problems/subsets-ii/
//
// Given an integer array nums that may contain duplicates, return all possible
// subsets (the power set). The solution set must not contain duplicate subsets.
// Return the solution in any order.
//
// Approach: Backtracking with sorting
// - Sort the array to handle duplicates
// - Skip duplicate elements at the same recursion level
// - When nums[i] == nums[i-1] and i > index, skip to avoid duplicates
//
// Time Complexity: O(n * 2^n)
// Space Complexity: O(n * 2^n)

public class SubsetsII {

    public static void main(String[] args) {
        System.out.println("=== Subsets II ===");
        int[] nums1 = {1, 2, 2};
        System.out.println("Subsets of [1,2,2]: " + subsetsWithDup(nums1));
        // Expected: [[], [1], [1,2], [1,2,2], [2], [2,2]]

        int[] nums2 = {0};
        System.out.println("Subsets of [0]: " + subsetsWithDup(nums2));
        // Expected: [[], [0]]

        int[] nums3 = {4, 4, 4, 1, 4};
        System.out.println("Subsets of [4,4,4,1,4]: " + subsetsWithDup(nums3));
    }

    public static List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Sort to handle duplicates
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, int index, List<Integer> current,
                                   List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = index; i < nums.length; i++) {
            // Skip duplicates at the same recursion level
            if (i > index && nums[i] == nums[i - 1]) continue;

            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
