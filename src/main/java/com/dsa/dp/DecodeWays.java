package com.dsa.dp;

// Problem: Decode Ways
// Link: https://leetcode.com/problems/decode-ways/
//
// A message containing letters from A-Z can be encoded into numbers using the mapping:
// 'A' -> "1", 'B' -> "2", ..., 'Z' -> "26"
// Given a string s containing only digits, return the number of ways to decode it.
//
// Approach: DP
// dp[i] = number of ways to decode substring s[0..i-1]
// dp[0] = 1 (empty string)
// If s[i-1] is valid (1-9): dp[i] += dp[i-1]
// If s[i-2..i-1] is valid (10-26): dp[i] += dp[i-2]
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class DecodeWays {

    public static void main(String[] args) {
        System.out.println("Decode Ways (\"12\"): " + numDecodings("12"));     // Expected: 2 (AB or L)
        System.out.println("Decode Ways (\"226\"): " + numDecodings("226"));   // Expected: 3 (BZ, VF, BBF)
        System.out.println("Decode Ways (\"06\"): " + numDecodings("06"));     // Expected: 0
    }

    public static int numDecodings(String s) {
        if (s == null || s.length() == 0 || s.charAt(0) == '0') return 0;

        int n = s.length();
        int prev2 = 1; // dp[i-2]
        int prev1 = 1; // dp[i-1]

        for (int i = 2; i <= n; i++) {
            int curr = 0;

            // Single digit (s[i-1])
            int oneDigit = s.charAt(i - 1) - '0';
            if (oneDigit >= 1 && oneDigit <= 9) {
                curr += prev1;
            }

            // Two digits (s[i-2..i-1])
            int twoDigits = Integer.parseInt(s.substring(i - 2, i));
            if (twoDigits >= 10 && twoDigits <= 26) {
                curr += prev2;
            }

            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }
}
