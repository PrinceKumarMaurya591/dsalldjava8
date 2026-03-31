package com.dsa.array;

import java.util.Arrays;

/**
 * Problem 16: Remove Duplicates from Sorted Array
 * 
 * Problem Statement:
 * Given a sorted array nums, remove duplicates in-place such that each
 * element appears only once and return the new length.
 * 
 * Assumptions:
 * - Array is sorted in non-decreasing order
 * - Must modify array in-place with O(1) extra memory
 * - Relative order of elements must be preserved
 * - Return new length, elements beyond new length don't matter
 * 
 * Optimal Solution: O(n) time, O(1) space using two pointers
 * 
 * Algorithm Explanation:
 * 1. If array is empty or has single element, return its length
 * 2. Initialize uniqueIndex = 0 (pointer for unique elements)
 * 3. Iterate through array starting from index 1
 * 4. For each element:
 *    - If current element != element at uniqueIndex:
 *      - Increment uniqueIndex
 *      - Copy current element to nums[uniqueIndex]
 *    - If equal, skip (duplicate)
 * 5. Return uniqueIndex + 1 (new length)
 * 
 * Dry Run Example:
 * Input: nums = [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]
 * 
 * Initial: uniqueIndex = 0
 * i = 1: nums[1]=0 == nums[0]=0 → skip
 * i = 2: nums[2]=1 != nums[0]=0 → uniqueIndex=1, nums[1]=1
 * i = 3: nums[3]=1 == nums[1]=1 → skip
 * i = 4: nums[4]=1 == nums[1]=1 → skip
 * i = 5: nums[5]=2 != nums[1]=1 → uniqueIndex=2, nums[2]=2
 * i = 6: nums[6]=2 == nums[2]=2 → skip
 * i = 7: nums[7]=3 != nums[2]=2 → uniqueIndex=3, nums[3]=3
 * i = 8: nums[8]=3 == nums[3]=3 → skip
 * i = 9: nums[9]=4 != nums[3]=3 → uniqueIndex=4, nums[4]=4
 * 
 * Result: length = 5, array = [0, 1, 2, 3, 4, ...]
 */
public class RemoveDuplicatesFromSortedArray {
    
    /**
     * Removes duplicates from sorted array in-place
     * 
     * @param nums the sorted input array
     * @return the new length after removing duplicates
     */
    public static int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int uniqueIndex = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[uniqueIndex]) {
                uniqueIndex++;
                nums[uniqueIndex] = nums[i];
            }
        }
        
        return uniqueIndex + 1;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Remove Duplicates from Sorted Array Problem:");
        
        // Test 1
        int[] nums1 = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        
        System.out.println("\nTest 1:");
        System.out.println("Input: " + Arrays.toString(nums1));
        int length1 = removeDuplicates(nums1);
        System.out.println("New length: " + length1);
        System.out.println("Modified array (first " + length1 + " elements): " + 
                          Arrays.toString(Arrays.copyOfRange(nums1, 0, length1)));
        System.out.println("Expected length: 5");
        System.out.println("Expected array: [0, 1, 2, 3, 4]");
        
        // Test 2
        int[] nums2 = {1, 1, 2};
        
        System.out.println("\nTest 2:");
        System.out.println("Input: " + Arrays.toString(nums2));
        int length2 = removeDuplicates(nums2);
        System.out.println("New length: " + length2);
        System.out.println("Modified array (first " + length2 + " elements): " + 
                          Arrays.toString(Arrays.copyOfRange(nums2, 0, length2)));
        System.out.println("Expected length: 2");
        System.out.println("Expected array: [1, 2]");
        
        // Test 3: Empty array
        int[] nums3 = {};
        
        System.out.println("\nTest 3:");
        System.out.println("Input: " + Arrays.toString(nums3));
        int length3 = removeDuplicates(nums3);
        System.out.println("New length: " + length3);
        System.out.println("Expected length: 0");
        
        // Test 4: Single element
        int[] nums4 = {5};
        
        System.out.println("\nTest 4:");
        System.out.println("Input: " + Arrays.toString(nums4));
        int length4 = removeDuplicates(nums4);
        System.out.println("New length: " + length4);
        System.out.println("Modified array: " + Arrays.toString(nums4));
        System.out.println("Expected length: 1");
        System.out.println("Expected array: [5]");
    }
}