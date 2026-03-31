package com.dsa.string;

import java.util.ArrayList;
import java.util.List;

/**
 * Problem: Fizz Buzz
 * 
 * Problem Statement:
 * Given an integer n, return a string array answer (1-indexed) where:
 * - answer[i] == "FizzBuzz" if i is divisible by 3 and 5
 * - answer[i] == "Fizz" if i is divisible by 3
 * - answer[i] == "Buzz" if i is divisible by 5
 * - answer[i] == i (as a string) if none of the above conditions are true
 * 
 * Assumptions:
 * - 1 <= n <= 10^4
 * 
 * Optimal Solution: O(n) time, O(1) space (excluding output array)
 * 
 * Algorithm Explanation:
 * 1. Create a list to store the results
 * 2. Iterate from 1 to n (inclusive)
 * 3. For each number i:
 *    a. Check if divisible by both 3 and 5 → append "FizzBuzz"
 *    b. Else if divisible by 3 → append "Fizz"
 *    c. Else if divisible by 5 → append "Buzz"
 *    d. Else → append the number as string
 * 4. Return the list
 * 
 * Optimization: Use string concatenation to avoid checking both conditions separately
 * 
 * Dry Run Example:
 * Input: n = 5
 * 
 * i = 1: not divisible by 3 or 5 → "1"
 * i = 2: not divisible by 3 or 5 → "2"
 * i = 3: divisible by 3 → "Fizz"
 * i = 4: not divisible by 3 or 5 → "4"
 * i = 5: divisible by 5 → "Buzz"
 * 
 * Result: ["1", "2", "Fizz", "4", "Buzz"]
 */
public class FizzBuzz {
    
    /**
     * Basic FizzBuzz implementation using if-else conditions
     * 
     * @param n the upper limit (inclusive)
     * @return list of FizzBuzz strings from 1 to n
     */
    public static List<String> fizzBuzz(int n) {
        List<String> result = new ArrayList<>();
        
        for (int i = 1; i <= n; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                result.add("FizzBuzz");
            } else if (i % 3 == 0) {
                result.add("Fizz");
            } else if (i % 5 == 0) {
                result.add("Buzz");
            } else {
                result.add(String.valueOf(i));
            }
        }
        
        return result;
    }
    
    /**
     * Optimized FizzBuzz implementation using string concatenation
     * Avoids checking both conditions separately
     * 
     * @param n the upper limit (inclusive)
     * @return list of FizzBuzz strings from 1 to n
     */
    public static List<String> fizzBuzzOptimized(int n) {
        List<String> result = new ArrayList<>();
        
        for (int i = 1; i <= n; i++) {
            StringBuilder sb = new StringBuilder();
            
            // Check divisibility by 3
            if (i % 3 == 0) {
                sb.append("Fizz");
            }
            
            // Check divisibility by 5
            if (i % 5 == 0) {
                sb.append("Buzz");
            }
            
            // If neither condition was true, append the number
            if (sb.length() == 0) {
                sb.append(i);
            }
            
            result.add(sb.toString());
        }
        
        return result;
    }
    
    /**
     * FizzBuzz implementation without modulo operator
     * Uses counters to track divisibility
     * 
     * @param n the upper limit (inclusive)
     * @return list of FizzBuzz strings from 1 to n
     */
    public static List<String> fizzBuzzNoModulo(int n) {
        List<String> result = new ArrayList<>();
        int fizz = 0;
        int buzz = 0;
        
        for (int i = 1; i <= n; i++) {
            fizz++;
            buzz++;
            
            if (fizz == 3 && buzz == 5) {
                result.add("FizzBuzz");
                fizz = 0;
                buzz = 0;
            } else if (fizz == 3) {
                result.add("Fizz");
                fizz = 0;
            } else if (buzz == 5) {
                result.add("Buzz");
                buzz = 0;
            } else {
                result.add(String.valueOf(i));
            }
        }
        
        return result;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Basic example
        int n1 = 5;
        List<String> result1 = fizzBuzz(n1);
        System.out.println("Fizz Buzz Problem:");
        System.out.println("Input: n = " + n1);
        System.out.println("Output: " + result1);
        System.out.println("Expected: [1, 2, Fizz, 4, Buzz]");
        System.out.println();
        
        // Test case 2: Includes FizzBuzz
        int n2 = 15;
        List<String> result2 = fizzBuzzOptimized(n2);
        System.out.println("Input: n = " + n2);
        System.out.println("Output (Optimized): " + result2);
        System.out.println("Expected FizzBuzz at positions 15: " + result2.get(14));
        System.out.println();
        
        // Test case 3: No modulo implementation
        int n3 = 10;
        List<String> result3 = fizzBuzzNoModulo(n3);
        System.out.println("Input: n = " + n3);
        System.out.println("Output (No Modulo): " + result3);
        System.out.println("Expected: [1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz]");
        
        // Print first 15 FizzBuzz numbers for visualization
        System.out.println("\nFirst 15 FizzBuzz numbers:");
        List<String> first15 = fizzBuzz(15);
        for (int i = 0; i < first15.size(); i++) {
            System.out.println((i + 1) + ": " + first15.get(i));
        }
    }
}