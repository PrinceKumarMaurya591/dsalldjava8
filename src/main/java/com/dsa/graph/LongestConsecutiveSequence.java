package com.dsa.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Problem: Longest Consecutive Sequence (Union-Find approach)
// Link: https://leetcode.com/problems/longest-consecutive-sequence/
//
// Given an unsorted array of integers nums, return the length of the longest
// consecutive elements sequence.
// You must write an algorithm that runs in O(n) time.
//
// Approach: Union-Find (DSU)
// 1. Add all numbers to a set for O(1) lookup.
// 2. For each number, union it with (num+1) if (num+1) exists in the set.
// 3. Track the size of each component to find the largest consecutive sequence.
//
// Time Complexity: O(n) amortized
// Space Complexity: O(n)

public class LongestConsecutiveSequence {

    public static void main(String[] args) {
        int[] nums1 = {100, 4, 200, 1, 3, 2};
        System.out.println("Longest Consecutive Sequence: " + longestConsecutive(nums1));
        // Expected: 4

        int[] nums2 = {0, 3, 7, 2, 5, 8, 4, 6, 0, 1};
        System.out.println("Longest Consecutive Sequence: " + longestConsecutive(nums2));
        // Expected: 9
    }

    public static int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        Set<Integer> set = new HashSet<>();
        for (int num : nums) set.add(num);

        Map<Integer, Integer> parent = new HashMap<>();
        Map<Integer, Integer> size = new HashMap<>();

        // Initialize each number as its own parent
        for (int num : set) {
            parent.put(num, num);
            size.put(num, 1);
        }

        int maxSize = 1;

        // Union consecutive numbers
        for (int num : set) {
            if (set.contains(num + 1)) {
                maxSize = Math.max(maxSize, union(parent, size, num, num + 1));
            }
        }

        return maxSize;
    }

    private static int find(Map<Integer, Integer> parent, int x) {
        if (parent.get(x) != x) {
            parent.put(x, find(parent, parent.get(x)));
        }
        return parent.get(x);
    }

    private static int union(Map<Integer, Integer> parent, Map<Integer, Integer> size, int x, int y) {
        int rootX = find(parent, x);
        int rootY = find(parent, y);

        if (rootX == rootY) return size.get(rootX);

        if (size.get(rootX) < size.get(rootY)) {
            parent.put(rootX, rootY);
            size.put(rootY, size.get(rootY) + size.get(rootX));
            return size.get(rootY);
        } else {
            parent.put(rootY, rootX);
            size.put(rootX, size.get(rootX) + size.get(rootY));
            return size.get(rootX);
        }
    }
}
