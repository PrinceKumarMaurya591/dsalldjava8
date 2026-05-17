package com.dsa.greedy;

import java.util.Arrays;

// Problem: Largest Number
// Link: https://leetcode.com/problems/largest-number/
//
// Given a list of non-negative integers nums, arrange them such that they form
// the largest number and return it as a string.
// Since the result may be very large, return it as a string.
//
// Approach: Greedy (Custom Sorting)
// - Convert numbers to strings
// - Sort using custom comparator: (a, b) -> (b + a).compareTo(a + b)
//   This ensures that concatenation that gives larger number comes first
// - Handle edge case: if largest number is "0", return "0"
//
// Time Complexity: O(n log n) - sorting
// Space Complexity: O(n)

public class LargestNumber {

    public static void main(String[] args) {
        System.out.println("=== Largest Number ===");
        int[] nums1 = {10, 2};
        System.out.println("Largest number: " + largestNumber(nums1));
        // Expected: "210"

        int[] nums2 = {3, 30, 34, 5, 9};
        System.out.println("Largest number: " + largestNumber(nums2));
        // Expected: "9534330"

        int[] nums3 = {0, 0};
        System.out.println("Largest number: " + largestNumber(nums3));
        // Expected: "0"

        int[] nums4 = {1};
        System.out.println("Largest number: " + largestNumber(nums4));
        // Expected: "1"

        int[] nums5 = {999999998, 999999997, 999999999};
        System.out.println("Largest number: " + largestNumber(nums5));
    }

    public static String largestNumber(int[] nums) {
        // Convert to String array
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            strs[i] = String.valueOf(nums[i]);
        }

        // Sort with custom comparator
        // For two strings a and b, compare a+b vs b+a
        Arrays.sort(strs, (a, b) -> (b + a).compareTo(a + b));

        // If the largest number is "0", result is "0"
        if (strs[0].equals("0")) return "0";

        // Build result
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            sb.append(s);
        }

        return sb.toString();
    }
}
