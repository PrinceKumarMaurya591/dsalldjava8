package com.dsa.dp;

// Problem: Palindromic Substrings
// Link: https://leetcode.com/problems/palindromic-substrings/
//
// Given a string s, return the number of palindromic substrings in it.
// A string is a palindrome when it reads the same backward as forward.
//
// Approach: Expand Around Center
// For each position (and between positions), expand outward while the substring
// is a palindrome and count each one.
//
// Time Complexity: O(n^2)
// Space Complexity: O(1)

public class PalindromicSubstrings {

    public static void main(String[] args) {
        System.out.println("Palindromic Substrings (\"abc\"): "
                + countSubstrings("abc")); // Expected: 3 (a, b, c)

        System.out.println("Palindromic Substrings (\"aaa\"): "
                + countSubstrings("aaa")); // Expected: 6 (a, a, a, aa, aa, aaa)
    }

    public static int countSubstrings(String s) {
        if (s == null || s.length() == 0) return 0;

        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            // Odd length palindromes (center at i)
            count += expandAroundCenter(s, i, i);
            // Even length palindromes (center between i and i+1)
            count += expandAroundCenter(s, i, i + 1);
        }

        return count;
    }

    private static int expandAroundCenter(String s, int left, int right) {
        int count = 0;

        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            count++;
            left--;
            right++;
        }

        return count;
    }
}
