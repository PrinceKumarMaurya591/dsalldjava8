package com.dsa.greedy;

// Problem: Merge Triplets to Form Target Triplet
// Link: https://leetcode.com/problems/merge-triplets-to-form-target-triplet/
//
// A triplet is an array of three integers. You are given a 2D integer array
// triplets, where triplets[i] = [a_i, b_i, c_i] describes the ith triplet.
// You are also given an integer array target = [a, b, c].
//
// In one operation, you can merge two triplets by taking the maximum of each
// corresponding element. For example, [1, 2, 3] merged with [3, 1, 2] = [3, 2, 3].
//
// Return true if it is possible to obtain the target triplet by applying any
// number of merge operations, otherwise false.
//
// Approach: Greedy
// - A triplet is "valid" if none of its elements exceed the target
// - Merge all valid triplets by taking max of each position
// - If after merging all valid triplets we reach the target, return true
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class MergeTripletsToFormTargetTriplet {

    public static void main(String[] args) {
        System.out.println("=== Merge Triplets to Form Target Triplet ===");
        int[][] triplets1 = {{2, 5, 3}, {1, 8, 4}, {1, 7, 5}};
        int[] target1 = {2, 7, 5};
        System.out.println("Can form target: " + mergeTriplets(triplets1, target1));
        // Expected: true (merge [2,5,3] and [1,7,5] -> [2,7,5])

        int[][] triplets2 = {{3, 4, 5}, {4, 5, 6}};
        int[] target2 = {3, 2, 5};
        System.out.println("Can form target: " + mergeTriplets(triplets2, target2));
        // Expected: false (b=2 can't be formed since all b's are >= 4)

        int[][] triplets3 = {{2, 3, 4}, {1, 2, 3}, {3, 1, 1}};
        int[] target3 = {3, 3, 4};
        System.out.println("Can form target: " + mergeTriplets(triplets3, target3));
        // Expected: true

        int[][] triplets4 = {{1, 3, 5}, {5, 3, 1}};
        int[] target4 = {5, 3, 5};
        System.out.println("Can form target: " + mergeTriplets(triplets4, target4));
        // Expected: true
    }

    public static boolean mergeTriplets(int[][] triplets, int[] target) {
        int[] merged = {0, 0, 0};

        for (int[] triplet : triplets) {
            // Only consider triplets where no element exceeds target
            if (triplet[0] <= target[0] && triplet[1] <= target[1] && triplet[2] <= target[2]) {
                merged[0] = Math.max(merged[0], triplet[0]);
                merged[1] = Math.max(merged[1], triplet[1]);
                merged[2] = Math.max(merged[2], triplet[2]);
            }
        }

        return merged[0] == target[0] && merged[1] == target[1] && merged[2] == target[2];
    }
}
