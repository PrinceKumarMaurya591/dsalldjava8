package com.dsa.array;

import java.util.Arrays;

/**
 * Problem 15: Next Permutation
 * 
 * Problem Statement:
 * Given an array of integers, rearrange numbers into the lexicographically
 * next greater permutation. If not possible, rearrange to lowest possible order.
 * 
 * Assumptions:
 * - Array contains integers
 * - Permutation should be done in-place
 * - If no greater permutation exists, rearrange to smallest permutation (ascending order)
 * 
 * Optimal Solution: O(n) time, O(1) space
 * 
 * Algorithm Explanation:
 * 1. Find the first decreasing element from the right (nums[i] < nums[i+1])
 *    - This is the pivot point where we can make a larger permutation
 * 2. If found (i >= 0):
 *    - Find the element just larger than nums[i] from the right (nums[j] > nums[i])
 *    - Swap nums[i] and nums[j]
 * 3. Reverse the suffix starting from i+1 to get the smallest possible suffix
 *    - This ensures we get the next permutation, not just any larger permutation
 * 
 * Why this works:
 * - To get the next permutation, we need to:
 *   1. Find the rightmost position where we can increase the value (pivot)
 *   2. Replace it with the smallest larger value from the right
 *   3. Make the suffix as small as possible (by reversing, since it's in descending order)
 * 
 * Dry Run Example:
 * Input: nums = [1, 2, 3]
 * 
 * Step 1: i = 1 (nums[1]=2 < nums[2]=3)
 * Step 2: j = 2 (nums[2]=3 > nums[1]=2)
 *   Swap: nums = [1, 3, 2]
 * Step 3: Reverse suffix from index 2: already reversed
 * 
 * Result: [1, 3, 2]
 */
public class NextPermutation {
    
    /**
     * Rearranges numbers into the next greater permutation
     * 
     * @param nums the array to rearrange
     */
    public static void nextPermutation(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return;
        }
        
        // Step 1: Find first decreasing element from right
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }
        
        // Step 2: If found, find element just larger than nums[i] from right
        if (i >= 0) {
            int j = nums.length - 1;
            while (j >= 0 && nums[j] <= nums[i]) {
                j--;
            }
            // Swap nums[i] and nums[j]
            swap(nums, i, j);
        }
        
        // Step 3: Reverse the suffix starting from i+1
        reverse(nums, i + 1, nums.length - 1);
    }
    
    /**
     * Swaps two elements in an array
     */
    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
    
    /**
     * Reverses a portion of an array
     */
    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            swap(nums, start, end);
            start++;
            end--;
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Next Permutation Problem:");
        
        // Test 1: Normal case
        int[] nums1 = {1, 2, 3};
        
        System.out.println("\nTest 1:");
        System.out.println("Input: " + Arrays.toString(nums1));
        nextPermutation(nums1);
        System.out.println("Output: " + Arrays.toString(nums1));
        System.out.println("Expected: [1, 3, 2]");
        System.out.println("Explanation: Next permutation after [1,2,3] is [1,3,2]");
        
        // Test 2: Last permutation (should wrap to first)
        int[] nums2 = {3, 2, 1};
        
        System.out.println("\nTest 2:");
        System.out.println("Input: " + Arrays.toString(nums2));
        nextPermutation(nums2);
        System.out.println("Output: " + Arrays.toString(nums2));
        System.out.println("Expected: [1, 2, 3]");
        System.out.println("Explanation: No greater permutation exists, so wrap to smallest [1,2,3]");
        
        // Test 3: Single element
        int[] nums3 = {1};
        
        System.out.println("\nTest 3:");
        System.out.println("Input: " + Arrays.toString(nums3));
        nextPermutation(nums3);
        System.out.println("Output: " + Arrays.toString(nums3));
        System.out.println("Expected: [1]");
        System.out.println("Explanation: Single element array remains unchanged");
        
        // Test 4: Example from problem
        int[] nums4 = {1, 1, 5};
        
        System.out.println("\nTest 4:");
        System.out.println("Input: " + Arrays.toString(nums4));
        nextPermutation(nums4);
        System.out.println("Output: " + Arrays.toString(nums4));
        System.out.println("Expected: [1, 5, 1]");
        System.out.println("Explanation: Next permutation after [1,1,5] is [1,5,1]");
        
        // Test 5: Complex case
        int[] nums5 = {1, 3, 5, 4, 2};
        
        System.out.println("\nTest 5:");
        System.out.println("Input: " + Arrays.toString(nums5));
        nextPermutation(nums5);
        System.out.println("Output: " + Arrays.toString(nums5));
        System.out.println("Expected: [1, 4, 2, 3, 5]");
        System.out.println("Explanation: Step-by-step:");
        System.out.println("  1. Find pivot: i=1 (nums[1]=3 < nums[2]=5)");
        System.out.println("  2. Find j: j=3 (nums[3]=4 > nums[1]=3)");
        System.out.println("  3. Swap: [1, 4, 5, 3, 2]");
        System.out.println("  4. Reverse suffix from i+1=2: [1, 4, 2, 3, 5]");
        
        // Test 6: All same elements
        int[] nums6 = {2, 2, 2};
        
        System.out.println("\nTest 6:");
        System.out.println("Input: " + Arrays.toString(nums6));
        nextPermutation(nums6);
        System.out.println("Output: " + Arrays.toString(nums6));
        System.out.println("Expected: [2, 2, 2]");
        System.out.println("Explanation: All elements same, no greater permutation");
    }
}