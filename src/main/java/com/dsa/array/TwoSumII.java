package com.dsa.array;

import java.util.Arrays;

/**
 * Problem 10: Two Sum II (Input Array Is Sorted)
 * 
 * Problem Statement:
 * Given a 1-indexed array of integers numbers that is already sorted
 * in non-decreasing order, find two numbers such that they add up to a specific
 * target number. Return the indices of the two numbers (1-indexed).
 * 
 * Assumptions:
 * - Array is sorted in non-decreasing order
 * - Exactly one solution exists
 * - Cannot use the same element twice
 * - Indices are 1-indexed (not 0-indexed)
 * 
 * Optimal Solution: O(n) time, O(1) space using two pointers
 * 
 * Algorithm Explanation:
 * 1. Initialize two pointers: left at start, right at end
 * 2. While left < right:
 *    - Calculate sum = numbers[left] + numbers[right]
 *    - If sum == target: return [left+1, right+1] (1-indexed)
 *    - If sum < target: move left pointer right (need larger sum)
 *    - If sum > target: move right pointer left (need smaller sum)
 * 
 * Dry Run Example:
 * Input: numbers = [2, 7, 11, 15], target = 9
 * 
 * Initial: left = 0, right = 3
 * Iteration 1: sum = 2 + 15 = 17 > 9 → right = 2
 * Iteration 2: sum = 2 + 11 = 13 > 9 → right = 1
 * Iteration 3: sum = 2 + 7 = 9 == 9 → return [1, 2] (1-indexed)
 * 
 * Result: [1, 2]
 */
public class TwoSumII {
    
    /**
     * Finds two numbers in a sorted array that add up to target
     * 
     * @param numbers the sorted input array (1-indexed)
     * @param target the target sum
     * @return an array containing the 1-indexed indices of the two numbers
     */
    public static int[] twoSum(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;
        
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            
            if (sum == target) {
                // Convert to 1-indexed
                return new int[] {left + 1, right + 1};
            } else if (sum < target) {
                // Need larger sum, move left pointer right
                left++;
            } else {
                // Need smaller sum, move right pointer left
                right--;
            }
        }
        
        return new int[] {-1, -1};
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] numbers1 = {2, 7, 11, 15};
        int target1 = 9;
        
        int[] numbers2 = {2, 3, 4};
        int target2 = 6;
        
        int[] numbers3 = {-1, 0};
        int target3 = -1;
        
        System.out.println("Two Sum II (Input Array Is Sorted) Problem:");
        
        System.out.println("\nTest 1:");
        System.out.println("Input: numbers = [2, 7, 11, 15], target = 9");
        int[] result1 = twoSum(numbers1, target1);
        System.out.println("Output: " + Arrays.toString(result1));
        System.out.println("Expected: [1, 2]");
        
        System.out.println("\nTest 2:");
        System.out.println("Input: numbers = [2, 3, 4], target = 6");
        int[] result2 = twoSum(numbers2, target2);
        System.out.println("Output: " + Arrays.toString(result2));
        System.out.println("Expected: [1, 3]");
        
        System.out.println("\nTest 3:");
        System.out.println("Input: numbers = [-1, 0], target = -1");
        int[] result3 = twoSum(numbers3, target3);
        System.out.println("Output: " + Arrays.toString(result3));
        System.out.println("Expected: [1, 2]");
    }
}