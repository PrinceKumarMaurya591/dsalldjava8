package com.dsa.dp;

import java.util.ArrayList;
import java.util.List;

// Problem: Generate Parentheses
// Link: https://leetcode.com/problems/generate-parentheses/
//
// Given n pairs of parentheses, write a function to generate all combinations of
// well-formed parentheses.
//
// Approach: Backtracking (DP with recursion)
// At each step, we can add '(' if open < n, or ')' if close < open.
//
// Time Complexity: O(4^n / sqrt(n)) - Catalan number
// Space Complexity: O(n) - recursion stack

public class GenerateParentheses {

    public static void main(String[] args) {
        System.out.println("Generate Parentheses (n=3): " + generateParenthesis(3));
        // Expected: ["((()))","(()())","(())()","()(())","()()()"]

        System.out.println("Generate Parentheses (n=1): " + generateParenthesis(1));
        // Expected: ["()"]
    }

    public static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private static void backtrack(List<String> result, StringBuilder current,
                                   int open, int close, int max) {
        if (current.length() == max * 2) {
            result.add(current.toString());
            return;
        }

        if (open < max) {
            current.append('(');
            backtrack(result, current, open + 1, close, max);
            current.deleteCharAt(current.length() - 1);
        }

        if (close < open) {
            current.append(')');
            backtrack(result, current, open, close + 1, max);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
