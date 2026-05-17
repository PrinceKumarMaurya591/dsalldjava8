package com.dsa.dp;

// Problem: Regular Expression Matching
// Link: https://leetcode.com/problems/regular-expression-matching/
//
// Given an input string s and a pattern p, implement regular expression matching
// with support for '.' (matches any single character) and '*' (matches zero or more
// of the preceding element).
//
// Approach: DP (2D table)
// dp[i][j] = true if s[0..i-1] matches p[0..j-1]
//
// Cases:
// 1. If p[j-1] == s[i-1] or p[j-1] == '.': dp[i][j] = dp[i-1][j-1]
// 2. If p[j-1] == '*':
//    a. Zero occurrences: dp[i][j] = dp[i][j-2]
//    b. One or more: if p[j-2] == s[i-1] or p[j-2] == '.': dp[i][j] = dp[i-1][j]
//
// Time Complexity: O(m * n)
// Space Complexity: O(m * n)

public class RegularExpressionMatching {

    public static void main(String[] args) {
        System.out.println("isMatch(\"aa\", \"a\"): " + isMatch("aa", "a"));       // Expected: false
        System.out.println("isMatch(\"aa\", \"a*\"): " + isMatch("aa", "a*"));     // Expected: true
        System.out.println("isMatch(\"ab\", \".*\"): " + isMatch("ab", ".*"));     // Expected: true
        System.out.println("isMatch(\"aab\", \"c*a*b\"): " + isMatch("aab", "c*a*b")); // Expected: true
    }

    public static boolean isMatch(String s, String p) {
        int m = s.length();
        int n = p.length();

        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true; // Empty string matches empty pattern

        // Handle patterns like a*, a*b*, a*b*c* matching empty string
        for (int j = 2; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 2];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char sc = s.charAt(i - 1);
                char pc = p.charAt(j - 1);

                if (pc == sc || pc == '.') {
                    // Current characters match
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (pc == '*') {
                    // '*' matches zero or more of the preceding element
                    char prevChar = p.charAt(j - 2);

                    // Zero occurrences: skip the preceding char and '*'
                    dp[i][j] = dp[i][j - 2];

                    // One or more occurrences
                    if (prevChar == sc || prevChar == '.') {
                        dp[i][j] = dp[i][j] || dp[i - 1][j];
                    }
                }
            }
        }

        return dp[m][n];
    }
}
