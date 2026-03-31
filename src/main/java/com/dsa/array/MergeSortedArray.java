package com.dsa.array;

import java.util.Arrays;

/**
 * Problem 12: Merge Sorted Array
 * 
 * Problem Statement:
 * Given two sorted integer arrays nums1 and nums2, merge nums2 into nums1
 * as one sorted array. nums1 has enough space to hold additional elements from nums2.
 * 
 * Assumptions:
 * - nums1 has length m + n where m is number of elements in nums1, n is number of elements in nums2
 * - nums1 has enough space (extra zeros) to hold all elements from nums2
 * - Both arrays are sorted in non-decreasing order
 * - Merge should be done in-place in nums1
 * 
 * Optimal Solution: O(m + n) time, O(1) space using three pointers from the end
 * 
 * Algorithm Explanation:
 * 1. Initialize three pointers:
 *    - i = m - 1 (last element in nums1's initial part)
 *    - j = n - 1 (last element in nums2)
 *    - k = m + n - 1 (last position in nums1)
 * 2. While both i and j are >= 0:
 *    - Compare nums1[i] and nums2[j]
 *    - Place larger element at nums1[k]
 *    - Decrement pointers accordingly
 * 3. If there are remaining elements in nums2, copy them to nums1
 * 4. If there are remaining elements in nums1, they're already in correct position
 * 
 * Dry Run Example:
 * Input: nums1 = [1, 2, 3, 0, 0, 0], m = 3, nums2 = [2, 5, 6], n = 3
 * 
 * Initial: i = 2, j = 2, k = 5
 * Iteration 1: nums1[2]=3 > nums2[2]=6? false → nums1[5]=6, j=1, k=4
 * Iteration 2: nums1[2]=3 > nums2[1]=5? false → nums1[4]=5, j=0, k=3
 * Iteration 3: nums1[2]=3 > nums2[0]=2? true → nums1[3]=3, i=1, k=2
 * Iteration 4: nums1[1]=2 > nums2[0]=2? false → nums1[2]=2, j=-1, k=1
 * Copy remaining from nums1: nums1[1]=2, nums1[0]=1
 * 
 * Result: nums1 = [1, 2, 2, 3, 5, 6]
 */
public class MergeSortedArray {
    
    /**
     * Merges two sorted arrays into nums1 in-place
     * 
     * @param nums1 the first sorted array with extra space
     * @param m the number of elements in nums1
     * @param nums2 the second sorted array
     * @param n the number of elements in nums2
     */
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1;      // Last element in nums1's initial part
        int j = n - 1;      // Last element in nums2
        int k = m + n - 1;  // Last position in nums1
        
        while (i >= 0 && j >= 0) {
            if (nums1[i] > nums2[j]) {
                nums1[k] = nums1[i];
                i--;
            } else {
                nums1[k] = nums2[j];
                j--;
            }
            k--;
        }
        
        // If there are remaining elements in nums2, copy them
        while (j >= 0) {
            nums1[k] = nums2[j];
            j--;
            k--;
        }
        // If there are remaining elements in nums1, they're already in place
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Merge Sorted Array Problem:");
        
        // Test 1
        int[] nums1 = {1, 2, 3, 0, 0, 0};
        int m1 = 3;
        int[] nums2 = {2, 5, 6};
        int n1 = 3;
        
        System.out.println("\nTest 1:");
        System.out.println("Before merge:");
        System.out.println("nums1: " + Arrays.toString(nums1) + " (m = " + m1 + ")");
        System.out.println("nums2: " + Arrays.toString(nums2) + " (n = " + n1 + ")");
        
        merge(nums1, m1, nums2, n1);
        
        System.out.println("After merge:");
        System.out.println("nums1: " + Arrays.toString(nums1));
        System.out.println("Expected: [1, 2, 2, 3, 5, 6]");
        
        // Test 2
        int[] nums3 = {4, 5, 6, 0, 0, 0};
        int m2 = 3;
        int[] nums4 = {1, 2, 3};
        int n2 = 3;
        
        System.out.println("\nTest 2:");
        System.out.println("Before merge:");
        System.out.println("nums1: " + Arrays.toString(nums3) + " (m = " + m2 + ")");
        System.out.println("nums2: " + Arrays.toString(nums4) + " (n = " + n2 + ")");
        
        merge(nums3, m2, nums4, n2);
        
        System.out.println("After merge:");
        System.out.println("nums1: " + Arrays.toString(nums3));
        System.out.println("Expected: [1, 2, 3, 4, 5, 6]");
        
        // Test 3
        int[] nums5 = {1, 0};
        int m3 = 1;
        int[] nums6 = {2};
        int n3 = 1;
        
        System.out.println("\nTest 3:");
        System.out.println("Before merge:");
        System.out.println("nums1: " + Arrays.toString(nums5) + " (m = " + m3 + ")");
        System.out.println("nums2: " + Arrays.toString(nums6) + " (n = " + n3 + ")");
        
        merge(nums5, m3, nums6, n3);
        
        System.out.println("After merge:");
        System.out.println("nums1: " + Arrays.toString(nums5));
        System.out.println("Expected: [1, 2]");
    }
}