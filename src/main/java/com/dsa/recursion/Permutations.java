package com.dsa.recursion;

import java.util.ArrayList;
import java.util.List;

// Problem: Permutations
// Link: https://leetcode.com/problems/permutations/
//
// Given an array nums of distinct integers, return all the possible permutations.
// You can return the answer in any order.
//
// Approach 1: Backtracking with used array
// - Track which elements are used
// - At each step, try all unused elements
//
// Approach 2: Backtracking with swapping
// - Swap elements to generate permutations in-place
// - More efficient, no extra space for used array
//
// Time Complexity: O(n * n!) - n! permutations, each takes O(n) to copy
// Space Complexity: O(n * n!)

public class Permutations {

    public static void main(String[] args) {
        System.out.println("=== Permutations ===");
        int[] nums1 = {1, 2, 3};
        System.out.println("Permutations of [1,2,3]: " + permute(nums1));
        // Expected: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]

        int[] nums2 = {0, 1};
        System.out.println("Permutations of [0,1]: " + permute(nums2));
        // Expected: [[0,1],[1,0]]

        int[] nums3 = {1};
        System.out.println("Permutations of [1]: " + permute(nums3));
        // Expected: [[1]]

        System.out.println("\n=== Permutations (Swap approach) ===");
        System.out.println("Permutations of [1,2,3]: " + permuteSwap(nums1));
    }

    // Backtracking with used array
    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] nums, boolean[] used, List<Integer> current,
                                   List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    // Backtracking with swapping (more efficient)
    public static List<List<Integer>> permuteSwap(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        permuteSwapHelper(nums, 0, result);
        return result;
    }

    private static void permuteSwapHelper(int[] nums, int index, List<List<Integer>> result) {
        if (index == nums.length) {
            List<Integer> permutation = new ArrayList<>();
            for (int num : nums) permutation.add(num);
            result.add(permutation);
            return;
        }

        for (int i = index; i < nums.length; i++) {
            swap(nums, index, i);
            permuteSwapHelper(nums, index + 1, result);
            swap(nums, index, i); // Backtrack
        }
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
