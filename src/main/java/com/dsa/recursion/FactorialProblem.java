package com.dsa.recursion;

// Problem: Factorial Problem
//
// Factorial of a non-negative integer n, denoted by n!, is the product of all
// positive integers less than or equal to n.
//
// n! = n * (n-1) * (n-2) * ... * 1
// 0! = 1 (by definition)
//
// Approach: Recursive
// - Base case: n == 0 -> return 1
// - Recursive case: n * factorial(n-1)
//
// Time Complexity: O(n)
// Space Complexity: O(n) - recursion stack

public class FactorialProblem {

    public static void main(String[] args) {
        System.out.println("=== Factorial ===");
        System.out.println("0! = " + factorial(0));     // 1
        System.out.println("1! = " + factorial(1));     // 1
        System.out.println("5! = " + factorial(5));     // 120
        System.out.println("10! = " + factorial(10));   // 3628800

        System.out.println("\n=== Factorial (Iterative) ===");
        System.out.println("5! = " + factorialIterative(5));   // 120
        System.out.println("10! = " + factorialIterative(10)); // 3628800
    }

    // Recursive approach
    public static long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n == 0) return 1;
        return n * factorial(n - 1);
    }

    // Iterative approach
    public static long factorialIterative(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
