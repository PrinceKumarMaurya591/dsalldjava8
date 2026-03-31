package com.dsa.array;

/**
 * Problem 5: Product of Array Except Self
 * 
 * Problem Statement:
 * Given an integer array nums, return an array answer such that
 * answer[i] is equal to the product of all elements of nums except nums[i].
 * 
 * Constraints:
 * - Must run in O(n) time
 * - Cannot use division operation
 * - 2 <= nums.length <= 10^5
 * - -30 <= nums[i] <= 30
 * 
 * Optimal Solution: O(n) time, O(1) space (excluding output array)
 * using prefix and suffix products
 * 
 * Algorithm Explanation (Two Pass):
 * 1. Initialize result array with same length as nums
 * 2. First pass (left to right):
 *    - result[i] = product of all elements to the left of i
 *    - Use a running product variable
 * 3. Second pass (right to left):
 *    - Multiply result[i] by product of all elements to the right of i
 *    - Use another running product variable for suffix
 * 
 * Alternative Approaches:
 * 1. Using division: O(n) time, O(1) space (but division not allowed)
 * 2. Using left and right arrays: O(n) time, O(n) space
 * 
 * Dry Run Example:
 * Input: nums = [1, 2, 3, 4]
 * 
 * Prefix pass (left to right):
 *   result[0] = 1
 *   result[1] = 1 * 1 = 1
 *   result[2] = 1 * 2 = 2
 *   result[3] = 2 * 3 = 6
 *   result = [1, 1, 2, 6]
 * 
 * Suffix pass (right to left):
 *   suffix = 1
 *   i = 3: result[3] = 6 * 1 = 6, suffix = 1 * 4 = 4
 *   i = 2: result[2] = 2 * 4 = 8, suffix = 4 * 3 = 12
 *   i = 1: result[1] = 1 * 12 = 12, suffix = 12 * 2 = 24
 *   i = 0: result[0] = 1 * 24 = 24, suffix = 24 * 1 = 24
 * 
 * Result: [24, 12, 8, 6]
 */
public class ProductOfArrayExceptSelf {
    
    /**
     * Calculates product of array except self using prefix and suffix products
     * 
     * @param nums the input array of integers
     * @return array where each element is product of all other elements
     */
    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        
        // Calculate prefix products
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }
        
        // Calculate suffix products and combine with prefix
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] = result[i] * suffix;
            suffix *= nums[i];
        }
        
        return result;
    }
    
    /**
     * Alternative solution using left and right arrays (more intuitive)
     * Time: O(n), Space: O(n)
     */
    public static int[] productExceptSelfWithArrays(int[] nums) {
        int n = nums.length;
        int[] left = new int[n];
        int[] right = new int[n];
        int[] result = new int[n];
        
        // Calculate left products
        left[0] = 1;
        for (int i = 1; i < n; i++) {
            left[i] = left[i - 1] * nums[i - 1];
        }
        
        // Calculate right products
        right[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            right[i] = right[i + 1] * nums[i + 1];
        }
        
        // Combine left and right products
        for (int i = 0; i < n; i++) {
            result[i] = left[i] * right[i];
        }
        
        return result;
    }
    
    /**
     * Solution using division (not allowed in problem but included for completeness)
     * Time: O(n), Space: O(1) excluding output
     */
    public static int[] productExceptSelfWithDivision(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        
        // Calculate total product
        int totalProduct = 1;
        int zeroCount = 0;
        int zeroIndex = -1;
        
        for (int i = 0; i < n; i++) {
            if (nums[i] == 0) {
                zeroCount++;
                zeroIndex = i;
                if (zeroCount > 1) {
                    // If more than one zero, all products will be zero
                    return new int[n]; // array initialized to zeros
                }
            } else {
                totalProduct *= nums[i];
            }
        }
        
        // Handle zero cases
        if (zeroCount == 1) {
            result[zeroIndex] = totalProduct;
            return result;
        }
        
        // No zeros case
        for (int i = 0; i < n; i++) {
            result[i] = totalProduct / nums[i];
        }
        
        return result;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums1 = {1, 2, 3, 4};
        int[] nums2 = {-1, 1, 0, -3, 3};
        int[] nums3 = {0, 0};
        
        System.out.println("Product of Array Except Self Problem:");
        
        // Test case 1: Normal case
        System.out.println("\nTest 1 - Normal case:");
        System.out.println("Input: nums = [1, 2, 3, 4]");
        int[] result1 = productExceptSelf(nums1);
        System.out.print("Output: [");
        for (int i = 0; i < result1.length; i++) {
            System.out.print(result1[i] + (i < result1.length - 1 ? ", " : ""));
        }
        System.out.println("]");
        System.out.println("Expected: [24, 12, 8, 6]");
        
        // Test case 2: With zero
        System.out.println("\nTest 2 - With zero:");
        System.out.println("Input: nums = [-1, 1, 0, -3, 3]");
        int[] result2 = productExceptSelf(nums2);
        System.out.print("Output: [");
        for (int i = 0; i < result2.length; i++) {
            System.out.print(result2[i] + (i < result2.length - 1 ? ", " : ""));
        }
        System.out.println("]");
        System.out.println("Expected: [0, 0, 9, 0, 0]");
        
        // Test case 3: Multiple zeros
        System.out.println("\nTest 3 - Multiple zeros:");
        System.out.println("Input: nums = [0, 0]");
        int[] result3 = productExceptSelf(nums3);
        System.out.print("Output: [");
        for (int i = 0; i < result3.length; i++) {
            System.out.print(result3[i] + (i < result3.length - 1 ? ", " : ""));
        }
        System.out.println("]");
        System.out.println("Expected: [0, 0]");
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. Prefix/Suffix (optimal): O(n) time, O(1) space (excluding output)");
        System.out.println("2. Left/Right Arrays: O(n) time, O(n) space");
        System.out.println("3. With Division: O(n) time, O(1) space (but division not allowed)");
    }
}