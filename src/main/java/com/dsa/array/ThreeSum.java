package com.dsa.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Problem 11: 3 Sum
 * 
 * Problem Statement:
 * Given an integer array nums, return all triplets [nums[i], nums[j], nums[k]]
 * such that i != j != k and nums[i] + nums[j] + nums[k] == 0.
 * Solution set must not contain duplicate triplets.
 * 
 * Assumptions:
 * - The solution set must not contain duplicate triplets
 * - Triple elements can be in any order
 * - Array may contain negative numbers
 * 
 * Optimal Solution: O(n²) time, O(1) space (excluding output) using sorting + two pointers
 * 
 * Algorithm Explanation:
 * 1. Sort the array to enable two-pointer technique and skip duplicates
 * 2. Iterate through the array with index i as the first element
 * 3. Skip duplicates for the first element (if nums[i] == nums[i-1], continue)
 * 4. For each i, use two pointers (left = i+1, right = n-1) to find pairs that sum to -nums[i]
 * 5. While left < right:
 *    - Calculate sum = nums[left] + nums[right]
 *    - If sum == target (-nums[i]): add triplet to result
 *      - Skip duplicates for left and right pointers
 *      - Move both pointers inward
 *    - If sum < target: move left pointer right (need larger sum)
 *    - If sum > target: move right pointer left (need smaller sum)
 * 
 * Dry Run Example:
 * Input: nums = [-1, 0, 1, 2, -1, -4]
 * Sorted: [-4, -1, -1, 0, 1, 2]
 * 
 * i = 0: nums[0] = -4, target = 4
 *   left = 1, right = 5: -1 + 2 = 1 < 4 → left = 2
 *   left = 2, right = 5: -1 + 2 = 1 < 4 → left = 3
 *   left = 3, right = 5: 0 + 2 = 2 < 4 → left = 4
 *   left = 4, right = 5: 1 + 2 = 3 < 4 → left = 5 → break
 * 
 * i = 1: nums[1] = -1, target = 1
 *   left = 2, right = 5: -1 + 2 = 1 == 1 → add [-1, -1, 2]
 *   Skip duplicates: left=2→3, right=5→4
 *   left = 3, right = 4: 0 + 1 = 1 == 1 → add [-1, 0, 1]
 *   Skip duplicates: left=3→4, right=4→3 → break
 * 
 * i = 2: nums[2] = -1 (duplicate of i=1) → skip
 * i = 3: nums[3] = 0, target = 0
 *   left = 4, right = 5: 1 + 2 = 3 > 0 → right = 4 → break
 * 
 * Result: [[-1, -1, 2], [-1, 0, 1]]
 */
public class ThreeSum {
    
    /**
     * Finds all unique triplets that sum to zero
     * 
     * @param nums the input array
     * @return a list of all unique triplets that sum to zero
     */
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        
        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates for the first element
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            
            int left = i + 1;
            int right = nums.length - 1;
            int target = -nums[i];
            
            while (left < right) {
                int sum = nums[left] + nums[right];
                
                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    
                    // Skip duplicates for left pointer
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    // Skip duplicates for right pointer
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }
                    
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums1 = {-1, 0, 1, 2, -1, -4};
        int[] nums2 = {0, 0, 0};
        int[] nums3 = {0, 1, 1};
        
        System.out.println("3 Sum Problem:");
        
        System.out.println("\nTest 1:");
        System.out.println("Input: [-1, 0, 1, 2, -1, -4]");
        List<List<Integer>> result1 = threeSum(nums1);
        System.out.println("Output: " + result1);
        System.out.println("Expected: [[-1, -1, 2], [-1, 0, 1]]");
        
        System.out.println("\nTest 2:");
        System.out.println("Input: [0, 0, 0]");
        List<List<Integer>> result2 = threeSum(nums2);
        System.out.println("Output: " + result2);
        System.out.println("Expected: [[0, 0, 0]]");
        
        System.out.println("\nTest 3:");
        System.out.println("Input: [0, 1, 1]");
        List<List<Integer>> result3 = threeSum(nums3);
        System.out.println("Output: " + result3);
        System.out.println("Expected: []");
    }
}