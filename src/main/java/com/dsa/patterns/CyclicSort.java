package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Cyclic Sort
 * 
 * Used when: Problems involving numbers in a given range [1, n] or [0, n]
 * where you need to find missing/duplicate/smallest missing positive numbers.
 * 
 * Core idea: Place each number at its correct index (nums[i] should be at index nums[i]-1).
 * 
 * Key variations:
 * 1. Find missing number
 * 2. Find all missing numbers
 * 3. Find duplicate number
 * 4. Find all duplicates
 * 5. Find first missing positive
 * 6. Find corrupt pair (duplicate + missing)
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class CyclicSort {

    /**
     * Problem: Cyclic Sort
     * Sort the array containing numbers from 1 to n.
     * 
     * Approach: Place each number at its correct index.
     * Time: O(n), Space: O(1)
     */
    public static void cyclicSort(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            int correctIndex = nums[i] - 1;
            if (nums[i] != nums[correctIndex]) {
                swap(nums, i, correctIndex);
            } else {
                i++;
            }
        }
    }

    /**
     * Problem: Missing Number
     * Find the missing number in array containing n distinct numbers from [0, n].
     * 
     * Approach: Cyclic sort (place at index = value), find first mismatch.
     * Time: O(n), Space: O(1)
     */
    public static int missingNumber(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            if (nums[i] < nums.length && nums[i] != i) {
                swap(nums, i, nums[i]);
            } else {
                i++;
            }
        }

        // Find first index where value != index
        for (i = 0; i < nums.length; i++) {
            if (nums[i] != i) return i;
        }

        return nums.length;
    }

    /**
     * Problem: Find All Numbers Disappeared in an Array
     * Find all numbers from [1, n] that don't appear in the array.
     * 
     * Approach: Mark visited by negating value at correct index.
     * Time: O(n), Space: O(1) excluding output
     */
    public static List<Integer> findDisappearedNumbers(int[] nums) {
        // Mark visited numbers
        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]) - 1;
            if (nums[index] > 0) {
                nums[index] = -nums[index];
            }
        }

        // Find unmarked indices
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > 0) {
                result.add(i + 1);
            }
        }

        return result;
    }

    /**
     * Problem: Find the Duplicate Number
     * Find the duplicate in array of size n+1 with values in [1, n].
     * 
     * Approach: Cyclic sort - when trying to place, if target already has correct value, it's duplicate.
     * Time: O(n), Space: O(1)
     */
    public static int findDuplicate(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            if (nums[i] != i + 1) {
                int correctIndex = nums[i] - 1;
                if (nums[i] != nums[correctIndex]) {
                    swap(nums, i, correctIndex);
                } else {
                    return nums[i]; // Duplicate found
                }
            } else {
                i++;
            }
        }

        return -1;
    }

    /**
     * Problem: Find All Duplicates in an Array
     * Find all numbers that appear twice in array of size n with values in [1, n].
     * 
     * Approach: Mark visited by negating value at correct index.
     * If already negative, it's a duplicate.
     * Time: O(n), Space: O(1) excluding output
     */
    public static List<Integer> findDuplicates(int[] nums) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]) - 1;
            if (nums[index] < 0) {
                result.add(Math.abs(nums[i]));
            } else {
                nums[index] = -nums[index];
            }
        }

        return result;
    }

    /**
     * Problem: First Missing Positive
     * Find smallest positive integer not in array.
     * 
     * Approach: Cyclic sort ignoring numbers outside [1, n].
     * Time: O(n), Space: O(1)
     */
    public static int firstMissingPositive(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            if (nums[i] > 0 && nums[i] <= nums.length && nums[i] != nums[nums[i] - 1]) {
                swap(nums, i, nums[i] - 1);
            } else {
                i++;
            }
        }

        for (i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) return i + 1;
        }

        return nums.length + 1;
    }

    /**
     * Problem: Find Corrupt Pair
     * Array has one duplicate and one missing number. Find both.
     * 
     * Approach: Cyclic sort, then find mismatch.
     * Time: O(n), Space: O(1)
     */
    public static int[] findCorruptPair(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            int correctIndex = nums[i] - 1;
            if (nums[i] != nums[correctIndex]) {
                swap(nums, i, correctIndex);
            } else {
                i++;
            }
        }

        for (i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                return new int[]{nums[i], i + 1}; // {duplicate, missing}
            }
        }

        return new int[]{-1, -1};
    }

    /**
     * Problem: Set Mismatch
     * Same as corrupt pair - find duplicate and missing.
     * 
     * Time: O(n), Space: O(1)
     */
    public static int[] findErrorNums(int[] nums) {
        return findCorruptPair(nums);
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        System.out.println("=== CYCLIC SORT PATTERN ===");
        System.out.println();

        // 1. Cyclic Sort
        System.out.println("1. Cyclic Sort:");
        int[] nums1 = {3, 1, 5, 4, 2};
        System.out.print("   Input: [3,1,5,4,2] -> ");
        cyclicSort(nums1);
        System.out.println(Arrays.toString(nums1) + " (expected: [1,2,3,4,5])");
        System.out.println();

        // 2. Missing Number
        System.out.println("2. Missing Number:");
        int[] nums2 = {3, 0, 1};
        System.out.println("   Input: [3,0,1] -> " + missingNumber(nums2) + " (expected: 2)");
        int[] nums2b = {9, 6, 4, 2, 3, 5, 7, 0, 1};
        System.out.println("   Input: [9,6,4,2,3,5,7,0,1] -> " + missingNumber(nums2b) + " (expected: 8)");
        System.out.println();

        // 3. Find All Numbers Disappeared
        System.out.println("3. Find All Numbers Disappeared:");
        int[] nums3 = {4, 3, 2, 7, 8, 2, 3, 1};
        System.out.println("   Input: [4,3,2,7,8,2,3,1] -> " + findDisappearedNumbers(nums3) + " (expected: [5,6])");
        System.out.println();

        // 4. Find Duplicate Number
        System.out.println("4. Find Duplicate Number:");
        int[] nums4 = {1, 3, 4, 2, 2};
        System.out.println("   Input: [1,3,4,2,2] -> " + findDuplicate(nums4) + " (expected: 2)");
        System.out.println();

        // 5. Find All Duplicates
        System.out.println("5. Find All Duplicates:");
        int[] nums5 = {4, 3, 2, 7, 8, 2, 3, 1};
        System.out.println("   Input: [4,3,2,7,8,2,3,1] -> " + findDuplicates(nums5) + " (expected: [2,3])");
        System.out.println();

        // 6. First Missing Positive
        System.out.println("6. First Missing Positive:");
        int[] nums6 = {1, 2, 0};
        System.out.println("   Input: [1,2,0] -> " + firstMissingPositive(nums6) + " (expected: 3)");
        int[] nums6b = {3, 4, -1, 1};
        System.out.println("   Input: [3,4,-1,1] -> " + firstMissingPositive(nums6b) + " (expected: 2)");
        int[] nums6c = {7, 8, 9, 11, 12};
        System.out.println("   Input: [7,8,9,11,12] -> " + firstMissingPositive(nums6c) + " (expected: 1)");
        System.out.println();

        // 7. Find Corrupt Pair
        System.out.println("7. Find Corrupt Pair:");
        int[] nums7 = {3, 1, 2, 5, 2};
        System.out.println("   Input: [3,1,2,5,2] -> " + Arrays.toString(findCorruptPair(nums7)) + " (expected: [2,4])");
        System.out.println();

        // 8. Set Mismatch
        System.out.println("8. Set Mismatch:");
        int[] nums8 = {1, 2, 2, 4};
        System.out.println("   Input: [1,2,2,4] -> " + Arrays.toString(findErrorNums(nums8)) + " (expected: [2,3])");
    }
}
