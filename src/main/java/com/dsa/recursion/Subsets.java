package com.dsa.recursion;

import java.util.ArrayList;
import java.util.List;

// Problem: Subsets
// Link: https://leetcode.com/problems/subsets/
//
// Given an integer array nums of unique elements, return all possible subsets
// (the power set). The solution set must not contain duplicate subsets.
// Return the solution in any order.
//
// Approach 1: Cascading (Iterative)
// - Start with empty subset
// - For each number, add it to all existing subsets to create new ones
//
// Approach 2: Backtracking (Recursive)
// - At each index, we have two choices: include or exclude the element
// - Recurse to next index after each choice
//
// Time Complexity: O(n * 2^n) - 2^n subsets, each takes O(n) to copy
// Space Complexity: O(n * 2^n) - store all subsets

public class Subsets {

    public static void main(String[] args) {
        System.out.println("=== Subsets ===");
        int[] nums1 = {1, 2, 3};
        System.out.println("Subsets of [1,2,3]: " + subsets(nums1));
        // Expected: [[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]

        int[] nums2 = {0};
        System.out.println("Subsets of [0]: " + subsets(nums2));
        // Expected: [[], [0]]

        System.out.println("\n=== Subsets (Backtracking) ===");
        System.out.println("Subsets of [1,2,3]: " + subsetsBacktrack(nums1));
    }

    // Cascading (Iterative) approach
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); // Start with empty subset

        for (int num : nums) {
            int size = result.size();
            for (int i = 0; i < size; i++) {
                List<Integer> subset = new ArrayList<>(result.get(i));
                subset.add(num);
                result.add(subset);
            }
        }
        return result;
    }

    // Backtracking approach
    public static List<List<Integer>> subsetsBacktrack(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, int index, List<Integer> current,
                                   List<List<Integer>> result) {
        // Add current subset to result
        result.add(new ArrayList<>(current));

        // Try including each remaining element
        for (int i = index; i < nums.length; i++) {
            current.add(nums[i]);
            backtrack(nums, i + 1, current, result);
            current.remove(current.size() - 1); // Backtrack
        }
    }
}
