package com.dsa.greedy;

// Problem: Valid Parenthesis String
// Link: https://leetcode.com/problems/valid-parenthesis-string/
//
// Given a string s containing only three types of characters: '(', ')' and '*',
// return true if s is valid.
//
// The following rules define a valid string:
// - Any left parenthesis '(' must have a corresponding right parenthesis ')'.
// - Any right parenthesis ')' must have a corresponding left parenthesis '('.
// - Left parenthesis '(' must go before the corresponding right parenthesis ')'.
// - '*' could be treated as a single right parenthesis ')' or a single left
//   parenthesis '(' or an empty string "".
//
// Approach 1: Greedy (Two counters)
// - Track min and max possible number of open brackets
// - '*' can be '(', ')' or '' so it expands the range
// - If max < 0 at any point, invalid
// - At end, check if min == 0 (0 open brackets possible)
//
// Approach 2: Two-pass (Left-to-right and Right-to-left)
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class ValidParenthesisString {

    public static void main(String[] args) {
        System.out.println("=== Valid Parenthesis String ===");
        System.out.println("'()' : " + checkValidString("()"));       // true
        System.out.println("'(*)' : " + checkValidString("(*)"));     // true
        System.out.println("'(*))' : " + checkValidString("(*))"));   // true
        System.out.println("')(' : " + checkValidString(")("));       // false
        System.out.println("'((*)' : " + checkValidString("((*)"));   // true
        System.out.println("'((()))' : " + checkValidString("((()))")); // true
        System.out.println("'(((***)' : " + checkValidString("(((***)")); // true
    }

    // Greedy approach with min/max open bracket count
    public static boolean checkValidString(String s) {
        int minOpen = 0; // Minimum possible open brackets
        int maxOpen = 0; // Maximum possible open brackets

        for (char ch : s.toCharArray()) {
            if (ch == '(') {
                minOpen++;
                maxOpen++;
            } else if (ch == ')') {
                minOpen = Math.max(minOpen - 1, 0);
                maxOpen--;
                if (maxOpen < 0) return false;
            } else { // '*'
                minOpen = Math.max(minOpen - 1, 0); // '*' as ')'
                maxOpen++; // '*' as '('
            }
        }

        return minOpen == 0;
    }

    // Two-pass approach
    public static boolean checkValidStringTwoPass(String s) {
        // Left-to-right: treat '*' as '('
        int open = 0;
        for (char ch : s.toCharArray()) {
            if (ch == '(' || ch == '*') open++;
            else open--;
            if (open < 0) return false;
        }
        if (open == 0) return true;

        // Right-to-left: treat '*' as ')'
        int close = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            char ch = s.charAt(i);
            if (ch == ')' || ch == '*') close++;
            else close--;
            if (close < 0) return false;
        }

        return true;
    }
}
