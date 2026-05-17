package com.dsa.dp;

// Problem: Longest Palindromic Substring
// Link: https://leetcode.com/problems/longest-palindromic-substring/
//
// Given a string s, return the longest palindromic substring in s.
//
// Approach: Expand Around Center
// For each position (and between positions), expand outward while the substring
// is a palindrome. Track the longest one found.
//
// Time Complexity: O(n^2)
// Space Complexity: O(1)

public class LongestPalindromicSubstring {

    public static void main(String[] args) {
        System.out.println("Longest Palindromic Substring (\"babad\"): "
                + longestPalindrome("babad")); // Expected: "bab" or "aba"
        System.out.println("Longest Palindromic Substring (\"cbbd\"): "
                + longestPalindrome("cbbd")); // Expected: "bb"
    }

    public static String longestPalindrome(String s) {
        if (s == null || s.length() < 2) return s;

        int start = 0, maxLen = 1;

        for (int i = 0; i < s.length(); i++) {
            // Odd length palindrome (center at i)
            int len1 = expandAroundCenter(s, i, i);
            // Even length palindrome (center between i and i+1)
            int len2 = expandAroundCenter(s, i, i + 1);

            int len = Math.max(len1, len2);
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }

        return s.substring(start, start + maxLen);
    }

    private static int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        // Return length of palindrome
        return right - left - 1;
    }
}
