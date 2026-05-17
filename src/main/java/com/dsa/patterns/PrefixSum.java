package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Prefix Sum
 * 
 * Used when: Problems involving subarray sums, range sum queries,
 * finding subarrays with target sum, balance in arrays.
 * 
 * Core idea: Precompute cumulative sums so that subarray sum can be
 * computed in O(1): sum[i..j] = prefix[j] - prefix[i-1].
 * Often combined with HashMap for O(n) solutions.
 * 
 * Key variations:
 * 1. Range sum query (immutable)
 * 2. Subarray sum equals K
 * 3. Subarray sum divisible by K
 * 4. Contiguous array (equal 0s and 1s)
 * 5. Find pivot index
 * 6. Product of array except self
 * 
 * Time Complexity: O(n) typically
 * Space Complexity: O(n) for prefix array, O(1) for running sum
 */
public class PrefixSum {

    /**
     * Problem: Range Sum Query - Immutable
     * Calculate sum of elements between indices left and right inclusive.
     */
    static class NumArray {
        private int[] prefixSum;

        public NumArray(int[] nums) {
            prefixSum = new int[nums.length + 1];
            for (int i = 0; i < nums.length; i++) {
                prefixSum[i + 1] = prefixSum[i] + nums[i];
            }
        }

        public int sumRange(int left, int right) {
            return prefixSum[right + 1] - prefixSum[left];
        }
    }

    /**
     * Problem: Range Sum Query 2D - Immutable
     * Calculate sum of elements in a 2D rectangle.
     */
    static class NumMatrix {
        private int[][] prefixSum;

        public NumMatrix(int[][] matrix) {
            int m = matrix.length, n = matrix[0].length;
            prefixSum = new int[m + 1][n + 1];

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    prefixSum[i][j] = matrix[i - 1][j - 1] 
                        + prefixSum[i - 1][j] 
                        + prefixSum[i][j - 1] 
                        - prefixSum[i - 1][j - 1];
                }
            }
        }

        public int sumRegion(int row1, int col1, int row2, int col2) {
            return prefixSum[row2 + 1][col2 + 1] 
                 - prefixSum[row1][col2 + 1] 
                 - prefixSum[row2 + 1][col1] 
                 + prefixSum[row1][col1];
        }
    }

    /**
     * Problem: Subarray Sum Equals K
     * Find number of subarrays whose sum equals k.
     * 
     * Approach: HashMap of prefix sum frequencies.
     * Time: O(n), Space: O(n)
     */
    public static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixSumFreq = new HashMap<>();
        prefixSumFreq.put(0, 1); // Empty subarray

        int count = 0;
        int runningSum = 0;

        for (int num : nums) {
            runningSum += num;

            // If runningSum - k exists, subarrays ending here sum to k
            count += prefixSumFreq.getOrDefault(runningSum - k, 0);

            prefixSumFreq.put(runningSum, prefixSumFreq.getOrDefault(runningSum, 0) + 1);
        }

        return count;
    }

    /**
     * Problem: Subarray Sums Divisible by K
     * Find number of subarrays whose sum is divisible by k.
     * 
     * Approach: HashMap of remainder frequencies.
     * Time: O(n), Space: O(k)
     */
    public static int subarraysDivByK(int[] nums, int k) {
        Map<Integer, Integer> remainderFreq = new HashMap<>();
        remainderFreq.put(0, 1);

        int count = 0;
        int runningSum = 0;

        for (int num : nums) {
            runningSum += num;
            int remainder = ((runningSum % k) + k) % k; // Handle negative

            count += remainderFreq.getOrDefault(remainder, 0);
            remainderFreq.put(remainder, remainderFreq.getOrDefault(remainder, 0) + 1);
        }

        return count;
    }

    /**
     * Problem: Contiguous Array
     * Find max length of subarray with equal number of 0s and 1s.
     * 
     * Approach: Treat 0 as -1, find longest subarray with sum 0.
     * Time: O(n), Space: O(n)
     */
    public static int findMaxLength(int[] nums) {
        Map<Integer, Integer> firstOccurrence = new HashMap<>();
        firstOccurrence.put(0, -1);

        int maxLen = 0;
        int runningSum = 0;

        for (int i = 0; i < nums.length; i++) {
            runningSum += (nums[i] == 0) ? -1 : 1;

            if (firstOccurrence.containsKey(runningSum)) {
                maxLen = Math.max(maxLen, i - firstOccurrence.get(runningSum));
            } else {
                firstOccurrence.put(runningSum, i);
            }
        }

        return maxLen;
    }

    /**
     * Problem: Find Pivot Index
     * Find index where sum of left elements equals sum of right elements.
     * 
     * Approach: Calculate total sum, then track left sum.
     * Time: O(n), Space: O(1)
     */
    public static int pivotIndex(int[] nums) {
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        int leftSum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (leftSum == totalSum - leftSum - nums[i]) {
                return i;
            }
            leftSum += nums[i];
        }

        return -1;
    }

    /**
     * Problem: Product of Array Except Self
     * Return array where result[i] = product of all elements except nums[i].
     * 
     * Approach: Left pass and right pass.
     * Time: O(n), Space: O(1) excluding output
     */
    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // Left pass: result[i] = product of elements to the left
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }

        // Right pass: multiply by product of elements to the right
        int rightProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= rightProduct;
            rightProduct *= nums[i];
        }

        return result;
    }

    /**
     * Problem: Minimum Size Subarray Sum
     * Find minimal length of contiguous subarray with sum >= target.
     * 
     * Approach: Sliding window with prefix sum concept.
     * Time: O(n), Space: O(1)
     */
    public static int minSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0;
        int minLen = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];

            while (sum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left];
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    /**
     * Problem: Maximum Size Subarray Sum Equals K
     * Find max length of subarray with sum = k.
     * 
     * Time: O(n), Space: O(n)
     */
    public static int maxSubArrayLen(int[] nums, int k) {
        Map<Integer, Integer> firstOccurrence = new HashMap<>();
        firstOccurrence.put(0, -1);

        int maxLen = 0;
        int runningSum = 0;

        for (int i = 0; i < nums.length; i++) {
            runningSum += nums[i];

            if (firstOccurrence.containsKey(runningSum - k)) {
                maxLen = Math.max(maxLen, i - firstOccurrence.get(runningSum - k));
            }

            // Store first occurrence only
            if (!firstOccurrence.containsKey(runningSum)) {
                firstOccurrence.put(runningSum, i);
            }
        }

        return maxLen;
    }

    public static void main(String[] args) {
        System.out.println("=== PREFIX SUM PATTERN ===");
        System.out.println();

        // 1. Range Sum Query
        System.out.println("1. Range Sum Query:");
        NumArray na = new NumArray(new int[]{-2, 0, 3, -5, 2, -1});
        System.out.println("   sumRange(0,2): " + na.sumRange(0, 2) + " (expected: 1)");
        System.out.println("   sumRange(2,5): " + na.sumRange(2, 5) + " (expected: -1)");
        System.out.println("   sumRange(0,5): " + na.sumRange(0, 5) + " (expected: -3)");
        System.out.println();

        // 2. Range Sum Query 2D
        System.out.println("2. Range Sum Query 2D:");
        int[][] matrix = {
            {3, 0, 1, 4, 2},
            {5, 6, 3, 2, 1},
            {1, 2, 0, 1, 5},
            {4, 1, 0, 1, 7},
            {1, 0, 3, 0, 5}
        };
        NumMatrix nm = new NumMatrix(matrix);
        System.out.println("   sumRegion(2,1,4,3): " + nm.sumRegion(2, 1, 4, 3) + " (expected: 8)");
        System.out.println("   sumRegion(1,1,2,2): " + nm.sumRegion(1, 1, 2, 2) + " (expected: 11)");
        System.out.println("   sumRegion(0,0,0,0): " + nm.sumRegion(0, 0, 0, 0) + " (expected: 3)");
        System.out.println();

        // 3. Subarray Sum Equals K
        System.out.println("3. Subarray Sum Equals K:");
        int[] nums3 = {1, 1, 1};
        System.out.println("   Input: [1,1,1], k=2");
        System.out.println("   Output: " + subarraySum(nums3, 2) + " (expected: 2)");
        System.out.println();

        // 4. Subarray Sums Divisible by K
        System.out.println("4. Subarray Sums Divisible by K:");
        int[] nums4 = {4, 5, 0, -2, -3, 1};
        System.out.println("   Input: [4,5,0,-2,-3,1], k=5");
        System.out.println("   Output: " + subarraysDivByK(nums4, 5) + " (expected: 7)");
        System.out.println();

        // 5. Contiguous Array
        System.out.println("5. Contiguous Array:");
        int[] nums5 = {0, 1, 0};
        System.out.println("   Input: [0,1,0]");
        System.out.println("   Output: " + findMaxLength(nums5) + " (expected: 2)");
        System.out.println();

        // 6. Find Pivot Index
        System.out.println("6. Find Pivot Index:");
        int[] nums6 = {1, 7, 3, 6, 5, 6};
        System.out.println("   Input: [1,7,3,6,5,6]");
        System.out.println("   Output: " + pivotIndex(nums6) + " (expected: 3)");
        System.out.println();

        // 7. Product of Array Except Self
        System.out.println("7. Product of Array Except Self:");
        int[] nums7 = {1, 2, 3, 4};
        System.out.println("   Input: [1,2,3,4]");
        System.out.println("   Output: " + Arrays.toString(productExceptSelf(nums7)) + " (expected: [24,12,8,6])");
        System.out.println();

        // 8. Minimum Size Subarray Sum
        System.out.println("8. Minimum Size Subarray Sum:");
        int[] nums8 = {2, 3, 1, 2, 4, 3};
        System.out.println("   Input: [2,3,1,2,4,3], target=7");
        System.out.println("   Output: " + minSubArrayLen(7, nums8) + " (expected: 2)");
        System.out.println();

        // 9. Maximum Size Subarray Sum Equals K
        System.out.println("9. Maximum Size Subarray Sum Equals K:");
        int[] nums9 = {1, -1, 5, -2, 3};
        System.out.println("   Input: [1,-1,5,-2,3], k=3");
        System.out.println("   Output: " + maxSubArrayLen(nums9, 3) + " (expected: 4)");
    }
}
