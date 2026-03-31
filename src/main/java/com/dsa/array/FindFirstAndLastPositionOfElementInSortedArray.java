package com.dsa.array;

import java.util.Arrays;

/**
 * Problem 17: Find First and Last Position of Element in Sorted Array
 * 
 * Problem Statement:
 * Given an array of integers nums sorted in non-decreasing order,
 * find the starting and ending position of a given target value.
 * Return [-1, -1] if target is not found.
 * 
 * Assumptions:
 * - Array is sorted in non-decreasing order
 * - Must run in O(log n) time complexity
 * - If target not found, return [-1, -1]
 * 
 * Optimal Solution: O(log n) time, O(1) space using binary search
 * 
 * Algorithm Explanation:
 * 1. Perform two binary searches:
 *    - First search: find first occurrence of target
 *    - Second search: find last occurrence of target
 * 2. For first occurrence search:
 *    - When nums[mid] == target, continue searching left (right = mid - 1)
 * 3. For last occurrence search:
 *    - When nums[mid] == target, continue searching right (left = mid + 1)
 * 4. If first occurrence not found, target doesn't exist
 * 5. Return [first, last] positions
 * 
 * Dry Run Example:
 * Input: nums = [5, 7, 7, 8, 8, 10], target = 8
 * 
 * Find first occurrence:
 *   mid = 2, nums[2]=7 < 8 → left=3
 *   mid = 4, nums[4]=8 == 8 → bound=4, right=3
 *   mid = 3, nums[3]=8 == 8 → bound=3, right=2 → break
 *   First occurrence = 3
 * 
 * Find last occurrence:
 *   mid = 2, nums[2]=7 < 8 → left=3
 *   mid = 4, nums[4]=8 == 8 → bound=4, left=5
 *   mid = 5, nums[5]=10 > 8 → right=4 → break
 *   Last occurrence = 4
 * 
 * Result: [3, 4]
 */
public class FindFirstAndLastPositionOfElementInSortedArray {
    
    /**
     * Finds the first and last position of target in sorted array
     * 
     * @param nums the sorted input array
     * @param target the value to search for
     * @return an array containing [first, last] positions, or [-1, -1] if not found
     */
    public static int[] searchRange(int[] nums, int target) {
        int[] result = new int[] {-1, -1};
        
        // Find first occurrence
        result[0] = findBound(nums, target, true);
        
        // If first occurrence not found, target doesn't exist
        if (result[0] == -1) {
            return result;
        }
        
        // Find last occurrence
        result[1] = findBound(nums, target, false);
        
        return result;
    }
    
    /**
     * Helper method to find first or last occurrence of target
     * 
     * @param nums the sorted array
     * @param target the value to find
     * @param isFirst true to find first occurrence, false for last
     * @return the index of the bound, or -1 if not found
     */
    private static int findBound(int[] nums, int target, boolean isFirst) {
        int left = 0;
        int right = nums.length - 1;
        int bound = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                bound = mid;
                if (isFirst) {
                    // Search left for first occurrence
                    right = mid - 1;
                } else {
                    // Search right for last occurrence
                    left = mid + 1;
                }
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return bound;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Find First and Last Position of Element in Sorted Array Problem:");
        
        // Test 1: Target exists multiple times
        int[] nums1 = {5, 7, 7, 8, 8, 10};
        int target1 = 8;
        
        System.out.println("\nTest 1:");
        System.out.println("Input: " + Arrays.toString(nums1) + ", target = " + target1);
        int[] result1 = searchRange(nums1, target1);
        System.out.println("Output: " + Arrays.toString(result1));
        System.out.println("Expected: [3, 4]");
        
        // Test 2: Target doesn't exist
        int[] nums2 = {5, 7, 7, 8, 8, 10};
        int target2 = 6;
        
        System.out.println("\nTest 2:");
        System.out.println("Input: " + Arrays.toString(nums2) + ", target = " + target2);
        int[] result2 = searchRange(nums2, target2);
        System.out.println("Output: " + Arrays.toString(result2));
        System.out.println("Expected: [-1, -1]");
        
        // Test 3: Single occurrence
        int[] nums3 = {1, 2, 3, 4, 5};
        int target3 = 3;
        
        System.out.println("\nTest 3:");
        System.out.println("Input: " + Arrays.toString(nums3) + ", target = " + target3);
        int[] result3 = searchRange(nums3, target3);
        System.out.println("Output: " + Arrays.toString(result3));
        System.out.println("Expected: [2, 2]");
        
        // Test 4: Empty array
        int[] nums4 = {};
        int target4 = 0;
        
        System.out.println("\nTest 4:");
        System.out.println("Input: " + Arrays.toString(nums4) + ", target = " + target4);
        int[] result4 = searchRange(nums4, target4);
        System.out.println("Output: " + Arrays.toString(result4));
        System.out.println("Expected: [-1, -1]");
        
        // Test 5: All elements same
        int[] nums5 = {2, 2, 2, 2, 2};
        int target5 = 2;
        
        System.out.println("\nTest 5:");
        System.out.println("Input: " + Arrays.toString(nums5) + ", target = " + target5);
        int[] result5 = searchRange(nums5, target5);
        System.out.println("Output: " + Arrays.toString(result5));
        System.out.println("Expected: [0, 4]");
    }
}