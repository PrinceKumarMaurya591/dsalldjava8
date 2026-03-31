package com.dsa.array;

/**
 * Problem 7: Maximum Product Subarray
 * 
 * Problem Statement:
 * Given an integer array nums, find the contiguous subarray
 * that has the largest product and return the product.
 * 
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -10 <= nums[i] <= 10
 * 
 * Optimal Solution: O(n) time, O(1) space tracking min and max products
 * 
 * Algorithm Explanation:
 * 1. Initialize maxSoFar, minSoFar, and result to nums[0]
 * 2. Iterate through array starting from index 1
 * 3. For each element:
 *    - Calculate potential products: current, maxSoFar * current, minSoFar * current
 *    - Update maxSoFar = max(current, maxSoFar * current, minSoFar * current)
 *    - Update minSoFar = min(current, maxSoFar * current, minSoFar * current)
 *    - Update result = max(result, maxSoFar)
 * 4. Return result
 * 
 * Key Insight:
 * - Need to track both max and min because negative * negative = positive
 * - When encountering a negative number, the min becomes max and max becomes min
 * 
 * Dry Run Example:
 * Input: nums = [2, 3, -2, 4]
 * 
 * i = 0: max = 2, min = 2, result = 2
 * i = 1: current = 3
 *   tempMax = max(3, max(2*3=6, 2*3=6)) = 6
 *   min = min(3, min(2*3=6, 2*3=6)) = 3
 *   max = 6, result = max(2, 6) = 6
 * i = 2: current = -2
 *   tempMax = max(-2, max(6*-2=-12, 3*-2=-6)) = -2
 *   min = min(-2, min(6*-2=-12, 3*-2=-6)) = -12
 *   max = -2, result = max(6, -2) = 6
 * i = 3: current = 4
 *   tempMax = max(4, max(-2*4=-8, -12*4=-48)) = 4
 *   min = min(4, min(-2*4=-8, -12*4=-48)) = -48
 *   max = 4, result = max(6, 4) = 6
 * 
 * Result: 6 (subarray [2, 3])
 */
public class MaximumProductSubarray {
    
    /**
     * Finds maximum product subarray using dynamic programming approach
     * 
     * @param nums the input array of integers
     * @return maximum product of any contiguous subarray
     */
    public static int maxProduct(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int maxSoFar = nums[0];
        int minSoFar = nums[0];
        int result = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            int current = nums[i];
            
            // Store previous max before updating
            int tempMax = Math.max(current, Math.max(maxSoFar * current, minSoFar * current));
            minSoFar = Math.min(current, Math.min(maxSoFar * current, minSoFar * current));
            
            maxSoFar = tempMax;
            result = Math.max(result, maxSoFar);
        }
        
        return result;
    }
    
    /**
     * Alternative solution using two passes (forward and backward)
     * Handles cases with zeros and negative numbers
     * Time: O(n), Space: O(1)
     */
    public static int maxProductTwoPass(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int maxProduct = Integer.MIN_VALUE;
        int product = 1;
        
        // Forward pass
        for (int i = 0; i < nums.length; i++) {
            product *= nums[i];
            maxProduct = Math.max(maxProduct, product);
            if (product == 0) {
                product = 1; // Reset for next subarray
            }
        }
        
        product = 1;
        // Backward pass
        for (int i = nums.length - 1; i >= 0; i--) {
            product *= nums[i];
            maxProduct = Math.max(maxProduct, product);
            if (product == 0) {
                product = 1; // Reset for next subarray
            }
        }
        
        return maxProduct;
    }
    
    /**
     * Brute force solution
     * Time: O(n²), Space: O(1)
     */
    public static int maxProductBruteForce(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int maxProduct = Integer.MIN_VALUE;
        
        for (int i = 0; i < nums.length; i++) {
            int product = 1;
            for (int j = i; j < nums.length; j++) {
                product *= nums[j];
                maxProduct = Math.max(maxProduct, product);
            }
        }
        
        return maxProduct;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums1 = {2, 3, -2, 4};
        int[] nums2 = {-2, 0, -1};
        int[] nums3 = {-2, 3, -4};
        int[] nums4 = {0, 2};
        
        System.out.println("Maximum Product Subarray Problem:");
        
        // Test case 1
        System.out.println("\nTest 1:");
        System.out.println("Input: nums = [2, 3, -2, 4]");
        int result1 = maxProduct(nums1);
        System.out.println("Maximum product: " + result1);
        System.out.println("Expected: 6");
        System.out.println("Two-pass solution: " + maxProductTwoPass(nums1));
        
        // Test case 2
        System.out.println("\nTest 2:");
        System.out.println("Input: nums = [-2, 0, -1]");
        int result2 = maxProduct(nums2);
        System.out.println("Maximum product: " + result2);
        System.out.println("Expected: 0");
        System.out.println("Two-pass solution: " + maxProductTwoPass(nums2));
        
        // Test case 3
        System.out.println("\nTest 3:");
        System.out.println("Input: nums = [-2, 3, -4]");
        int result3 = maxProduct(nums3);
        System.out.println("Maximum product: " + result3);
        System.out.println("Expected: 24");
        System.out.println("Two-pass solution: " + maxProductTwoPass(nums3));
        
        // Test case 4
        System.out.println("\nTest 4:");
        System.out.println("Input: nums = [0, 2]");
        int result4 = maxProduct(nums4);
        System.out.println("Maximum product: " + result4);
        System.out.println("Expected: 2");
        System.out.println("Two-pass solution: " + maxProductTwoPass(nums4));
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. Dynamic Programming: O(n) time, O(1) space");
        System.out.println("2. Two-pass Solution: O(n) time, O(1) space");
        System.out.println("3. Brute Force: O(n²) time, O(1) space");
    }
}