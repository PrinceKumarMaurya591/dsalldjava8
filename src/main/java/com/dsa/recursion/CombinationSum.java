package com.dsa.recursion;

import java.util.ArrayList;
import java.util.List;

// Problem: Combination Sum
// Link: https://leetcode.com/problems/combination-sum/
//
// Given an array of distinct integers candidates and a target integer target,
// return a list of all unique combinations of candidates where the chosen numbers
// sum to target. You may return the combinations in any order.
//
// The same number may be chosen from candidates an unlimited number of times.
// Two combinations are unique if the frequency of at least one of the chosen
// numbers is different.
//
// Approach: Backtracking
// - Sort candidates (optional, helps with pruning)
// - At each step, we can either include the current element or skip it
// - Since we can reuse elements, we stay at the same index after including
// - Prune: if current sum exceeds target, stop
//
// Time Complexity: O(n^(target/min)) - exponential
// Space Complexity: O(target/min) - recursion depth

public class CombinationSum {

    public static void main(String[] args) {
        System.out.println("=== Combination Sum ===");
        int[] candidates1 = {2, 3, 6, 7};
        System.out.println("Combinations for target 7: " + combinationSum(candidates1, 7));
        // Expected: [[2,2,3],[7]]

        int[] candidates2 = {2, 3, 5};
        System.out.println("Combinations for target 8: " + combinationSum(candidates2, 8));
        // Expected: [[2,2,2,2],[2,3,3],[3,5]]

        int[] candidates3 = {2};
        System.out.println("Combinations for target 1: " + combinationSum(candidates3, 1));
        // Expected: []
    }

    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
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

        // Option 1: Include the current element (can reuse, so stay at same index)
        current.add(candidates[index]);
        backtrack(candidates, target, index, currentSum + candidates[index], current, result);
        current.remove(current.size() - 1);

        // Option 2: Skip the current element
        backtrack(candidates, target, index + 1, currentSum, current, result);
    }
}
