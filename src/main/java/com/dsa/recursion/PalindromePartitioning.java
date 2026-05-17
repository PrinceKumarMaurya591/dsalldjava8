package com.dsa.recursion;

import java.util.ArrayList;
import java.util.List;

// Problem: Palindrome Partitioning
// Link: https://leetcode.com/problems/palindrome-partitioning/
//
// Given a string s, partition s such that every substring of the partition is a
// palindrome. Return all possible palindrome partitioning of s.
//
// Approach: Backtracking
// - At each step, try all possible substrings starting from current index
// - If the substring is a palindrome, add it to current partition and recurse
// - Backtrack when done
//
// Time Complexity: O(n * 2^n) worst case
// Space Complexity: O(n) - recursion depth

public class PalindromePartitioning {

    public static void main(String[] args) {
        System.out.println("=== Palindrome Partitioning ===");
        System.out.println("Partitions of 'aab': " + partition("aab"));
        // Expected: [["a","a","b"],["aa","b"]]

        System.out.println("Partitions of 'a': " + partition("a"));
        // Expected: [["a"]]

        System.out.println("Partitions of 'abba': " + partition("abba"));
        // Expected: [["a","b","b","a"],["a","bb","a"],["abba"]]
    }

    public static List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(String s, int start, List<String> current,
                                   List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                current.add(s.substring(start, end + 1));
                backtrack(s, end + 1, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    private static boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }
}
