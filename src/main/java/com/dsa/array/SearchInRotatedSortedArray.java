package com.dsa.array;

/**
 * Problem 9: Search in Rotated Sorted Array
 * 
 * Problem Statement:
 * Given a sorted rotated array of distinct integers and a target value,
 * return the index of target if it is in the array, otherwise return -1.
 * 
 * Assumptions:
 * - Array contains distinct elements
 * - Array was originally sorted in ascending order
 * - Array has been rotated at some pivot
 * 
 * Optimal Solution: O(log n) time, O(1) space using modified binary search
 * 
 * Algorithm Explanation:
 * 1. Use binary search to find the target
 * 2. At each step, check if nums[mid] equals target
 * 3. Determine which half is sorted:
 *    - If nums[left] <= nums[mid], left half is sorted
 *    - Otherwise, right half is sorted
 * 4. Based on which half is sorted and where target lies:
 *    - If left half is sorted and target is in [nums[left], nums[mid]), search left
 *    - Otherwise, search right
 *    - If right half is sorted and target is in (nums[mid], nums[right]], search right
 *    - Otherwise, search left
 * 
 * Dry Run Example:
 * Input: nums = [4, 5, 6, 7, 0, 1, 2], target = 0
 * 
 * Initial: left = 0, right = 6
 * Iteration 1: mid = 3, nums[3] = 7 ≠ 0
 *   nums[0]=4 ≤ nums[3]=7 → left half is sorted
 *   Check: nums[0]=4 ≤ 0 < nums[3]=7? false → search right: left = 4
 * Iteration 2: left = 4, right = 6, mid = 5, nums[5] = 1 ≠ 0
 *   nums[4]=0 ≤ nums[5]=1 → left half is sorted
 *   Check: nums[4]=0 ≤ 0 < nums[5]=1? true → search left: right = 4
 * Iteration 3: left = 4, right = 4, mid = 4, nums[4] = 0 == target → return 4
 * 
 * Result: 4
 */
public class SearchInRotatedSortedArray {
    
    /**
     * Searches for a target in a rotated sorted array
     * 
     * @param nums the rotated sorted array
     * @param target the value to search for
     * @return the index of target if found, -1 otherwise
     */
    public static int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                return mid;
            }
            
            // Check which half is sorted
            if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    // Target is in left sorted half
                    right = mid - 1;
                } else {
                    // Target is in right half
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    // Target is in right sorted half
                    left = mid + 1;
                } else {
                    // Target is in left half
                    right = mid - 1;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums = {4, 5, 6, 7, 0, 1, 2};
        
        System.out.println("Search in Rotated Sorted Array Problem:");
        System.out.println("Input: [4, 5, 6, 7, 0, 1, 2]");
        
        System.out.println("\nSearch for target = 0:");
        System.out.println("Output: " + search(nums, 0));
        System.out.println("Expected: 4");
        
        System.out.println("\nSearch for target = 3:");
        System.out.println("Output: " + search(nums, 3));
        System.out.println("Expected: -1");
        
        System.out.println("\nSearch for target = 6:");
        System.out.println("Output: " + search(nums, 6));
        System.out.println("Expected: 2");
    }
}