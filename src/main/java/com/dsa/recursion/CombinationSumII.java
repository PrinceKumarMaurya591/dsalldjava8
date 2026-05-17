package com.dsa.recursion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Problem: Combination Sum II
// Link: https://leetcode.com/problems/combination-sum-ii/
//
// Given a collection of candidate numbers (candidates) and a target number (target),
// find all unique combinations in candidates where the candidate numbers sum to target.
//
// Each number in candidates may only be used once in the combination.
// The solution set must not contain duplicate combinations.
//
// Approach: Backtracking with sorting
// - Sort the array to handle duplicates
// - Each element can be used only once (move to next index)
// - Skip duplicates at the same recursion level
//
// Time Complexity: O(2^n) worst case
// Space Complexity: O(n) - recursion depth

public class CombinationSumII {

    public static void main(String[] args) {
        System.out.println("=== Combination Sum II ===");
        int[] candidates1 = {10, 1, 2, 7, 6, 1, 5};
        System.out.println("Combinations for target 8: " + combinationSum2(candidates1, 8));
        // Expected: [[1,1,6],[1,2,5],[1,7],[2,6]]

        int[] candidates2 = {2, 5, 2, 1, 2};
        System.out.println("Combinations for target 5: " + combinationSum2(candidates2, 5));
        // Expected: [[1,2,2],[5]]
    }

    public static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates); // Sort to handle duplicates
        backtrack(candidates, target, 0, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(int[] candidates, int target, int index, int currentSum,
                                   List<Integer> current, List<List<Integer>> result) {
        if (currentSum == target) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (currentSum > target || index >= candidates.length) return;

        for (int i = index; i < candidates.length; i++) {
            // Skip duplicates at the same recursion level
            if (i > index && candidates[i] == candidates[i - 1]) continue;

            // Prune: if current element makes sum exceed target, skip remaining
            // (since array is sorted)
            if (currentSum + candidates[i] > target) break;

            current.add(candidates[i]);
            backtrack(candidates, target, i + 1, currentSum + candidates[i], current, result);
            current.remove(current.size() - 1);
        }
    }
}
