package com.dsa.array;

/**
 * Problem 8: Find Minimum in Rotated Sorted Array
 * 
 * Problem Statement:
 * Given a sorted rotated array of unique elements, return the minimum element.
 * Array was sorted in ascending order and then rotated.
 * 
 * Assumptions:
 * - Array contains unique elements
 * - Array was originally sorted in ascending order
 * - Array has been rotated at some pivot
 * 
 * Optimal Solution: O(log n) time, O(1) space using modified binary search
 * 
 * Algorithm Explanation:
 * 1. Check if array is not rotated or has single element (nums[left] <= nums[right])
 * 2. Use binary search to find the rotation point (minimum element)
 * 3. At each step, check if mid is the minimum:
 *    - If nums[mid] < nums[mid-1], then nums[mid] is minimum
 *    - If nums[mid] > nums[mid+1], then nums[mid+1] is minimum
 * 4. Decide which half to search:
 *    - If nums[mid] > nums[0], search right half (minimum is in right)
 *    - Otherwise, search left half (minimum is in left)
 * 
 * Dry Run Example:
 * Input: nums = [4, 5, 6, 7, 0, 1, 2]
 * 
 * Initial: left = 0, right = 6
 * Iteration 1: mid = 3, nums[3] = 7
 *   7 > nums[0]=4 → search right: left = 4
 * Iteration 2: left = 4, right = 6, mid = 5, nums[5] = 1
 *   1 < nums[0]=4 → search left: right = 4
 * Iteration 3: left = 4, right = 4, mid = 4, nums[4] = 0
 *   Check: nums[4] < nums[3]? 0 < 7 → true → return 0
 * 
 * Result: 0
 */
public class FindMinimumInRotatedSortedArray {
    
    /**
     * Finds the minimum element in a rotated sorted array
     * 
     * @param nums the rotated sorted array
     * @return the minimum element in the array
     */
    public static int findMin(int[] nums) {
        int left = 0;
        int right = nums.length - 1;
        
        // If array is not rotated or has single element
        if (nums[left] <= nums[right]) {
            return nums[left];
        }
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            // Check if mid is the minimum (rotation point)
            if (mid > 0 && nums[mid] < nums[mid - 1]) {
                return nums[mid];
            }
            
            // Check if mid+1 is the minimum
            if (mid < nums.length - 1 && nums[mid] > nums[mid + 1]) {
                return nums[mid + 1];
            }
            
            // Decide which half to search
            if (nums[mid] > nums[0]) {
                // Minimum is in right half
                left = mid + 1;
            } else {
                // Minimum is in left half
                right = mid - 1;
            }
        }
        
        return nums[0];
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums1 = {4, 5, 6, 7, 0, 1, 2};
        int[] nums2 = {3, 4, 5, 1, 2};
        
        System.out.println("Find Minimum in Rotated Sorted Array Problem:");
        System.out.println("Input 1: [4, 5, 6, 7, 0, 1, 2]");
        System.out.println("Output 1: " + findMin(nums1));
        System.out.println("Expected: 0");
        
        System.out.println("\nInput 2: [3, 4, 5, 1, 2]");
        System.out.println("Output 2: " + findMin(nums2));
        System.out.println("Expected: 1");
    }
}