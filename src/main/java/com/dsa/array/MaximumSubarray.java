package com.dsa.array;

/**
 * Problem 6: Maximum Subarray (Kadane's Algorithm)
 * 
 * Problem Statement:
 * Given an integer array nums, find the contiguous subarray
 * (containing at least one number) which has the largest sum and return its sum.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Optimal Solution: O(n) time, O(1) space using Kadane's Algorithm
 * 
 * Algorithm Explanation (Kadane's Algorithm):
 * 1. Initialize maxSoFar and maxEndingHere to nums[0]
 * 2. Iterate through array starting from index 1
 * 3. For each element:
 *    - maxEndingHere = max(nums[i], maxEndingHere + nums[i])
 *      (either start new subarray or extend previous one)
 *    - maxSoFar = max(maxSoFar, maxEndingHere)
 * 4. Return maxSoFar
 * 
 * Alternative Approaches:
 * 1. Divide and Conquer: O(n log n) time, O(log n) space
 * 2. Brute Force: O(n²) time, O(1) space
 * 
 * Dry Run Example:
 * Input: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
 * 
 * i = 0: maxEndingHere = -2, maxSoFar = -2
 * i = 1: maxEndingHere = max(1, -2+1=-1) = 1, maxSoFar = max(-2, 1) = 1
 * i = 2: maxEndingHere = max(-3, 1-3=-2) = -2, maxSoFar = max(1, -2) = 1
 * i = 3: maxEndingHere = max(4, -2+4=2) = 4, maxSoFar = max(1, 4) = 4
 * i = 4: maxEndingHere = max(-1, 4-1=3) = 3, maxSoFar = max(4, 3) = 4
 * i = 5: maxEndingHere = max(2, 3+2=5) = 5, maxSoFar = max(4, 5) = 5
 * i = 6: maxEndingHere = max(1, 5+1=6) = 6, maxSoFar = max(5, 6) = 6
 * i = 7: maxEndingHere = max(-5, 6-5=1) = 1, maxSoFar = max(6, 1) = 6
 * i = 8: maxEndingHere = max(4, 1+4=5) = 5, maxSoFar = max(6, 5) = 6
 * 
 * Result: 6 (subarray [4, -1, 2, 1])
 */
public class MaximumSubarray {
    
    /**
     * Finds maximum subarray sum using Kadane's Algorithm
     * 
     * @param nums the input array of integers
     * @return maximum sum of any contiguous subarray
     */
    public static int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            // Either extend the previous subarray or start new subarray
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        
        return maxSoFar;
    }
    
    /**
     * Kadane's algorithm that also returns the subarray indices
     * 
     * @param nums the input array
     * @return array containing [maxSum, startIndex, endIndex]
     */
    public static int[] maxSubArrayWithIndices(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new int[]{0, -1, -1};
        }
        
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];
        int start = 0, end = 0;
        int tempStart = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > maxEndingHere + nums[i]) {
                maxEndingHere = nums[i];
                tempStart = i;
            } else {
                maxEndingHere = maxEndingHere + nums[i];
            }
            
            if (maxEndingHere > maxSoFar) {
                maxSoFar = maxEndingHere;
                start = tempStart;
                end = i;
            }
        }
        
        return new int[]{maxSoFar, start, end};
    }
    
    /**
     * Divide and conquer solution
     * Time: O(n log n), Space: O(log n) for recursion stack
     */
    public static int maxSubArrayDivideConquer(int[] nums) {
        return maxSubArrayHelper(nums, 0, nums.length - 1);
    }
    
    private static int maxSubArrayHelper(int[] nums, int left, int right) {
        if (left == right) {
            return nums[left];
        }
        
        int mid = left + (right - left) / 2;
        
        int leftMax = maxSubArrayHelper(nums, left, mid);
        int rightMax = maxSubArrayHelper(nums, mid + 1, right);
        int crossMax = maxCrossingSum(nums, left, mid, right);
        
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }
    
    private static int maxCrossingSum(int[] nums, int left, int mid, int right) {
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        
        // Left of mid
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            if (sum > leftSum) {
                leftSum = sum;
            }
        }
        
        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        
        // Right of mid
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            if (sum > rightSum) {
                rightSum = sum;
            }
        }
        
        return leftSum + rightSum;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int[] nums2 = {1};
        int[] nums3 = {5, 4, -1, 7, 8};
        
        System.out.println("Maximum Subarray Problem (Kadane's Algorithm):");
        
        // Test case 1
        System.out.println("\nTest 1:");
        System.out.println("Input: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]");
        int result1 = maxSubArray(nums1);
        System.out.println("Maximum sum: " + result1);
        System.out.println("Expected: 6");
        
        int[] indices1 = maxSubArrayWithIndices(nums1);
        System.out.println("Subarray indices: [" + indices1[1] + ", " + indices1[2] + "]");
        System.out.println("Subarray: [4, -1, 2, 1]");
        
        // Test case 2
        System.out.println("\nTest 2:");
        System.out.println("Input: nums = [1]");
        int result2 = maxSubArray(nums2);
        System.out.println("Maximum sum: " + result2);
        System.out.println("Expected: 1");
        
        // Test case 3
        System.out.println("\nTest 3:");
        System.out.println("Input: nums = [5, 4, -1, 7, 8]");
        int result3 = maxSubArray(nums3);
        System.out.println("Maximum sum: " + result3);
        System.out.println("Expected: 23");
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. Kadane's Algorithm: O(n) time, O(1) space");
        System.out.println("2. Divide and Conquer: O(n log n) time, O(log n) space");
        System.out.println("3. Brute Force: O(n²) time, O(1) space");
    }
}