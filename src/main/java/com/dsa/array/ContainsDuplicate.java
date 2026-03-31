package com.dsa.array;

import java.util.HashSet;
import java.util.Set;

/**
 * Problem 3: Contains Duplicate
 * 
 * Problem Statement:
 * Given an integer array nums, return true if any value appears
 * at least twice in the array, false if every element is distinct.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * 
 * Optimal Solution: O(n) time, O(n) space using HashSet
 * 
 * Algorithm Explanation:
 * 1. Create a HashSet to store seen numbers
 * 2. Iterate through each number in the array
 * 3. For each number:
 *    a. Check if it already exists in the set
 *    b. If yes, return true (duplicate found)
 *    c. If no, add it to the set
 * 4. If loop completes without finding duplicates, return false
 * 
 * Alternative Approaches:
 * 1. Sorting: O(n log n) time, O(1) space (if sorting in-place)
 *    - Sort the array, then check adjacent elements
 * 2. Brute Force: O(n²) time, O(1) space
 *    - Compare each element with every other element
 * 
 * Dry Run Example:
 * Input: nums = [1, 2, 3, 1]
 * 
 * Iteration 1: num = 1, set = {} → add 1, set = {1}
 * Iteration 2: num = 2, set = {1} → add 2, set = {1, 2}
 * Iteration 3: num = 3, set = {1, 2} → add 3, set = {1, 2, 3}
 * Iteration 4: num = 1, set = {1, 2, 3} → contains 1 → return true
 * 
 * Result: true
 */
public class ContainsDuplicate {
    
    /**
     * Checks if array contains any duplicate elements using HashSet
     * 
     * @param nums the input array of integers
     * @return true if array contains duplicates, false otherwise
     */
    public static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        
        for (int num : nums) {
            if (seen.contains(num)) {
                return true;
            }
            seen.add(num);
        }
        
        return false;
    }
    
    /**
     * Alternative solution using sorting
     * Time: O(n log n), Space: O(1) if sorting in-place
     */
    public static boolean containsDuplicateSorting(int[] nums) {
        java.util.Arrays.sort(nums);
        
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1]) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Brute force solution (not recommended for large arrays)
     * Time: O(n²), Space: O(1)
     */
    public static boolean containsDuplicateBruteForce(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums1 = {1, 2, 3, 1};
        int[] nums2 = {1, 2, 3, 4};
        int[] nums3 = {1, 1, 1, 3, 3, 4, 3, 2, 4, 2};
        
        System.out.println("Contains Duplicate Problem:");
        
        // Test case 1: Contains duplicate
        System.out.println("\nTest 1 - Contains duplicate:");
        System.out.println("Input: nums = [1, 2, 3, 1]");
        System.out.println("Using HashSet: " + containsDuplicate(nums1));
        System.out.println("Using Sorting: " + containsDuplicateSorting(nums1));
        System.out.println("Expected: true");
        
        // Test case 2: No duplicates
        System.out.println("\nTest 2 - No duplicates:");
        System.out.println("Input: nums = [1, 2, 3, 4]");
        System.out.println("Using HashSet: " + containsDuplicate(nums2));
        System.out.println("Using Sorting: " + containsDuplicateSorting(nums2));
        System.out.println("Expected: false");
        
        // Test case 3: Multiple duplicates
        System.out.println("\nTest 3 - Multiple duplicates:");
        System.out.println("Input: nums = [1, 1, 1, 3, 3, 4, 3, 2, 4, 2]");
        System.out.println("Using HashSet: " + containsDuplicate(nums3));
        System.out.println("Using Sorting: " + containsDuplicateSorting(nums3));
        System.out.println("Expected: true");
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. HashSet Solution: O(n) time, O(n) space");
        System.out.println("2. Sorting Solution: O(n log n) time, O(1) space");
        System.out.println("3. Brute Force: O(n²) time, O(1) space");
    }
}