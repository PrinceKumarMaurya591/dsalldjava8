package com.dsa.greedy;

import java.util.ArrayList;
import java.util.List;

// Problem: Partition Labels
// Link: https://leetcode.com/problems/partition-labels/
//
// You are given a string s. We want to partition the string into as many parts
// as possible so that each letter appears in at most one part.
//
// Return a list of integers representing the size of these parts.
//
// Approach: Greedy (Two Pointers)
// - First pass: record the last occurrence index of each character
// - Second pass: track the current partition's end (max last occurrence seen)
//   When current index reaches partition end, we've found a valid partition
//
// Time Complexity: O(n)
// Space Complexity: O(1) - fixed 26 characters

public class PartitionLabels {

    public static void main(String[] args) {
        System.out.println("=== Partition Labels ===");
        System.out.println("Partition sizes of 'ababcbacadefegdehijhklij': "
                + partitionLabels("ababcbacadefegdehijhklij"));
        // Expected: [9, 7, 8]
        // Explanation: "ababcbaca", "defegde", "hijhklij"

        System.out.println("Partition sizes of 'eccbbbbdec': "
                + partitionLabels("eccbbbbdec"));
        // Expected: [10]

        System.out.println("Partition sizes of 'abc': "
                + partitionLabels("abc"));
        // Expected: [1, 1, 1]

        System.out.println("Partition sizes of 'caedbdedda': "
                + partitionLabels("caedbdedda"));
        // Expected: [1, 9]
    }

    public static List<Integer> partitionLabels(String s) {
        // Record last occurrence of each character
        int[] lastOccurrence = new int[26];
        for (int i = 0; i < s.length(); i++) {
            lastOccurrence[s.charAt(i) - 'a'] = i;
        }

        List<Integer> result = new ArrayList<>();
        int partitionStart = 0;
        int partitionEnd = 0;

        for (int i = 0; i < s.length(); i++) {
            // Extend partition end to include the last occurrence of current char
            partitionEnd = Math.max(partitionEnd, lastOccurrence[s.charAt(i) - 'a']);

            // If we've reached the end of the current partition
            if (i == partitionEnd) {
                result.add(partitionEnd - partitionStart + 1);
                partitionStart = i + 1;
            }
        }

        return result;
    }
}
