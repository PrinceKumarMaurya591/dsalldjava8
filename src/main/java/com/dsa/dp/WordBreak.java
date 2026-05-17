package com.dsa.dp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Problem: Word Break
// Link: https://leetcode.com/problems/word-break/
//
// Given a string s and a dictionary of strings wordDict, return true if s can be
// segmented into a space-separated sequence of one or more dictionary words.
//
// Approach: DP
// dp[i] = true if substring s[0..i-1] can be segmented
// dp[i] = true if dp[j] && s[j..i-1] is in wordDict for some j < i
//
// Time Complexity: O(n^2 * m) where n = s.length(), m = max word length
// Space Complexity: O(n)

public class WordBreak {

    public static void main(String[] args) {
        String s1 = "leetcode";
        List<String> wordDict1 = Arrays.asList("leet", "code");
        System.out.println("Word Break: " + wordBreak(s1, wordDict1)); // Expected: true

        String s2 = "applepenapple";
        List<String> wordDict2 = Arrays.asList("apple", "pen");
        System.out.println("Word Break: " + wordBreak(s2, wordDict2)); // Expected: true

        String s3 = "catsandog";
        List<String> wordDict3 = Arrays.asList("cats", "dog", "sand", "and", "cat");
        System.out.println("Word Break: " + wordBreak(s3, wordDict3)); // Expected: false
    }

    public static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true; // Empty string

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }
}
