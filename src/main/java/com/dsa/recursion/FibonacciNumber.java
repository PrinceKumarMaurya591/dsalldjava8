package com.dsa.recursion;

// Problem: Fibonacci Number
// Link: https://leetcode.com/problems/fibonacci-number/
//
// The Fibonacci numbers, commonly denoted F(n), form a sequence called the
// Fibonacci sequence such that each number is the sum of the two preceding ones,
// starting from 0 and 1.
//
// F(0) = 0, F(1) = 1
// F(n) = F(n - 1) + F(n - 2), for n > 1
//
// Approach 1: Recursive (naive) - O(2^n)
// Approach 2: Recursive with memoization - O(n)
// Approach 3: Iterative DP - O(n)
//
// This file demonstrates the recursive pattern identification:
// - Base case: F(0) = 0, F(1) = 1
// - Recursive case: F(n) = F(n-1) + F(n-2)
// - The problem has overlapping subproblems (optimal for DP)

import java.util.HashMap;
import java.util.Map;

public class FibonacciNumber {

    public static void main(String[] args) {
        System.out.println("=== Fibonacci Number ===");
        System.out.println("F(0) = " + fibRecursive(0));     // 0
        System.out.println("F(1) = " + fibRecursive(1));     // 1
        System.out.println("F(2) = " + fibRecursive(2));     // 1
        System.out.println("F(5) = " + fibRecursive(5));     // 5
        System.out.println("F(10) = " + fibMemoized(10));    // 55
        System.out.println("F(20) = " + fibIterative(20));   // 6765
        System.out.println("F(30) = " + fibIterative(30));   // 832040
    }

    // Naive recursive - O(2^n)
    public static int fibRecursive(int n) {
        if (n <= 1) return n;
        return fibRecursive(n - 1) + fibRecursive(n - 2);
    }

    // Memoized recursive - O(n)
    public static int fibMemoized(int n) {
        Map<Integer, Integer> memo = new HashMap<>();
        return fibMemoHelper(n, memo);
    }

    private static int fibMemoHelper(int n, Map<Integer, Integer> memo) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);

        int result = fibMemoHelper(n - 1, memo) + fibMemoHelper(n - 2, memo);
        memo.put(n, result);
        return result;
    }

    // Iterative DP - O(n), O(1) space
    public static int fibIterative(int n) {
        if (n <= 1) return n;

        int prev2 = 0;
        int prev1 = 1;

        for (int i = 2; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
