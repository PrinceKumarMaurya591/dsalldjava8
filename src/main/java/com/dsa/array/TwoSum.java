package com.dsa.array;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem 1: Two Sum
 * 
 * Problem Statement:
 * Given an array of integers nums and an integer target,
 * return indices of the two numbers such that they add up to target.
 * 
 * Assumptions:
 * - Each input has exactly one solution
 * - Cannot use the same element twice
 * - Return answer in any order
 * 
 * Optimal Solution: O(n) time, O(n) space using HashMap
 * 
 * Algorithm Explanation:
 * 1. Create a HashMap to store number -> index mapping
 * 2. Iterate through the array
 * 3. For each number, calculate its complement (target - current number)
 * 4. Check if complement exists in the map
 * 5. If found, return the indices [map.get(complement), current index]
 * 6. If not found, store current number with its index in the map
 * 
 * Dry Run Example:
 * Input: nums = [2, 7, 11, 15], target = 9
 * 
 * Iteration 1: i = 0, nums[0] = 2
 *   complement = 9 - 2 = 7
 *   map doesn't contain 7 → store (2, 0)
 * 
 * Iteration 2: i = 1, nums[1] = 7
 *   complement = 9 - 7 = 2
 *   map contains 2 at index 0 → return [0, 1]
 * 
 * Result: [0, 1]
 */
public class TwoSum {
    
    /**
     * Finds two numbers in the array that add up to the target
     * 
     * @param nums the input array of integers
     * @param target the target sum
     * @return an array containing the indices of the two numbers
     */
    public static int[] twoSum(int[] nums, int target) {
        // HashMap to store number -> index mapping
        Map<Integer, Integer> numMap = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            // Check if complement exists in map
            if (numMap.containsKey(complement)) {
                return new int[] {numMap.get(complement), i};
            }
            
            // Store current number with its index
            numMap.put(nums[i], i);
        }
        
        // According to problem constraints, this should never be reached
        return new int[] {-1, -1};
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = twoSum(nums, target);
        
        System.out.println("Two Sum Problem:");
        System.out.println("Input: nums = [2, 7, 11, 15], target = 9");
        System.out.println("Output: [" + result[0] + ", " + result[1] + "]");
        System.out.println("Expected: [0, 1]");
    }
}