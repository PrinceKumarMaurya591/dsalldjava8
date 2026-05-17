package com.dsa.stack;

import java.util.*;

/**
 * Longest Valid Parentheses
 * 
 * Given a string containing just the characters '(' and ')', return the length
 * of the longest valid (well-formed) parentheses substring.
 * 
 * Example: ")()())" -> 4 (the substring "()()")
 * Example: "(()" -> 2 (the substring "()")
 * Example: "" -> 0
 * 
 * Approach 1: Stack-based
 * - Push -1 as initial base index
 * - When we see '(', push its index
 * - When we see ')', pop the top
 *   - If stack is empty, push current index as new base
 *   - If stack is not empty, calculate length = i - stack.peek()
 * 
 * Approach 2: Two-pass (without extra space)
 * - Left to right: count open and close, reset when close > open
 * - Right to left: count open and close, reset when open > close
 * 
 * Approach 3: Dynamic Programming
 * - dp[i] = length of longest valid substring ending at i
 * - If s[i] = ')' and s[i-1] = '(': dp[i] = dp[i-2] + 2
 * - If s[i] = ')' and s[i-1] = ')' and s[i-dp[i-1]-1] = '(':
 *   dp[i] = dp[i-1] + dp[i-dp[i-1]-2] + 2
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n) for stack/DP, O(1) for two-pass
 */
public class LongestValidParentheses {

    // =============================================
    // Approach 1: Stack-based
    // =============================================
    public static int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1); // Base index
        int maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i); // New base
                } else {
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }

        return maxLen;
    }

    // =============================================
    // Approach 2: Two-pass (O(1) space)
    // =============================================
    public static int longestValidParenthesesTwoPass(String s) {
        int maxLen = 0;

        // Left to right
        int open = 0, close = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                open++;
            } else {
                close++;
            }

            if (open == close) {
                maxLen = Math.max(maxLen, open + close);
            } else if (close > open) {
                open = 0;
                close = 0;
            }
        }

        // Right to left
        open = 0;
        close = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '(') {
                open++;
            } else {
                close++;
            }

            if (open == close) {
                maxLen = Math.max(maxLen, open + close);
            } else if (open > close) {
                open = 0;
                close = 0;
            }
        }

        return maxLen;
    }

    // =============================================
    // Approach 3: Dynamic Programming
    // =============================================
    public static int longestValidParenthesesDP(String s) {
        int n = s.length();
        if (n < 2) return 0;

        int[] dp = new int[n];
        int maxLen = 0;

        for (int i = 1; i < n; i++) {
            if (s.charAt(i) == ')') {
                if (s.charAt(i - 1) == '(') {
                    // Case: "...()"
                    dp[i] = (i >= 2 ? dp[i - 2] : 0) + 2;
                } else {
                    // Case: "...))"
                    int prevLen = dp[i - 1];
                    int matchingIndex = i - prevLen - 1;
                    if (matchingIndex >= 0 && s.charAt(matchingIndex) == '(') {
                        dp[i] = dp[i - 1] + 2;
                        if (matchingIndex - 1 >= 0) {
                            dp[i] += dp[matchingIndex - 1];
                        }
                    }
                }
                maxLen = Math.max(maxLen, dp[i]);
            }
        }

        return maxLen;
    }

    // =============================================
    // Approach 4: Using array as stack
    // =============================================
    public static int longestValidParenthesesArray(String s) {
        int n = s.length();
        int[] stack = new int[n + 1];
        int top = 0;
        stack[top] = -1;
        int maxLen = 0;

        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == '(') {
                stack[++top] = i;
            } else {
                top--;
                if (top < 0) {
                    stack[++top] = i;
                } else {
                    maxLen = Math.max(maxLen, i - stack[top]);
                }
            }
        }

        return maxLen;
    }

    /**
     * Extension: Return the longest valid parentheses substring itself
     */
    public static String longestValidParenthesesSubstring(String s) {
        int n = s.length();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int maxLen = 0;
        int start = 0;

        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    int len = i - stack.peek();
                    if (len > maxLen) {
                        maxLen = len;
                        start = stack.peek() + 1;
                    }
                }
            }
        }

        return s.substring(start, start + maxLen);
    }

    /**
     * Extension: Count all valid parentheses substrings
     */
    public static int countValidParenthesesSubstrings(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int count = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    // Each valid substring ending at i contributes
                    // (i - stack.peek()) / 2 new valid substrings
                    count += (i - stack.peek()) / 2;
                }
            }
        }

        return count;
    }

    public static void main(String[] args) {
        System.out.println("Longest Valid Parentheses\n");

        // Test cases
        String[] testCases = {
            "(()",           // Expected: 2
            ")()())",        // Expected: 4
            "",              // Expected: 0
            "()(()",         // Expected: 2
            "()(())",        // Expected: 6
            "())()()",       // Expected: 4
            "((()))",        // Expected: 6
            "()()()",        // Expected: 6
            "((()))())",     // Expected: 8
            ")(",            // Expected: 0
            "((()))()",      // Expected: 8
            "(()())",        // Expected: 6
        };

        int[] expected = {2, 4, 0, 2, 6, 4, 6, 6, 8, 0, 8, 6};

        System.out.println("--- Test Cases ---");
        for (int i = 0; i < testCases.length; i++) {
            int r1 = longestValidParentheses(testCases[i]);
            int r2 = longestValidParenthesesTwoPass(testCases[i]);
            int r3 = longestValidParenthesesDP(testCases[i]);
            int r4 = longestValidParenthesesArray(testCases[i]);
            String substring = longestValidParenthesesSubstring(testCases[i]);
            int count = countValidParenthesesSubstrings(testCases[i]);

            System.out.println("Input: \"" + testCases[i] + "\"");
            System.out.println("  Stack:   " + r1 + " (expected: " + expected[i] + ")");
            System.out.println("  TwoPass: " + r2);
            System.out.println("  DP:      " + r3);
            System.out.println("  Array:   " + r4);
            System.out.println("  Substring: \"" + substring + "\"");
            System.out.println("  Count of valid substrings: " + count);
            System.out.println();
        }

        // Edge cases
        System.out.println("--- Edge Cases ---");
        String[] edgeCases = {
            "",
            "(",
            ")",
            "(((((",
            ")))))",
            "()",
            "(()()()())",  // 10
        };
        for (String test : edgeCases) {
            System.out.println("Input: \"" + test + "\" -> " + 
                             longestValidParentheses(test));
        }

        System.out.println();

        // Performance comparison
        System.out.println("--- Performance Comparison ---");
        StringBuilder sb = new StringBuilder();
        Random rand = new Random(42);
        for (int i = 0; i < 100000; i++) {
            sb.append(rand.nextBoolean() ? '(' : ')');
        }
        String largeInput = sb.toString();

        long start = System.nanoTime();
        longestValidParentheses(largeInput);
        System.out.println("Stack:   " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        longestValidParenthesesTwoPass(largeInput);
        System.out.println("TwoPass: " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        longestValidParenthesesDP(largeInput);
        System.out.println("DP:      " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        longestValidParenthesesArray(largeInput);
        System.out.println("Array:   " + (System.nanoTime() - start) / 1_000_000.0 + " ms");
    }
}
