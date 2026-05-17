package com.dsa.patterns;

/**
 * Pattern: Kadane's Algorithm
 * 
 * Used when: Problems involving maximum/minimum subarray sum, maximum product subarray,
 * maximum sum circular subarray, longest subarray with positive sum.
 * 
 * Core idea: Maintain a running sum (current subarray). If it becomes negative,
 * reset to 0 (start new subarray). Track the maximum seen so far.
 * 
 * Key variations:
 * 1. Maximum subarray sum (standard)
 * 2. Maximum product subarray (track both max and min)
 * 3. Maximum sum circular subarray
 * 4. Maximum length subarray with positive sum
 * 5. Maximum sum with at most k deletions
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class KadanesAlgorithm {

    /**
     * Problem: Maximum Subarray (Kadane's Algorithm - Classic)
     * Find contiguous subarray with the largest sum.
     * 
     * Approach: Maintain currentSum and maxSum.
     * If currentSum becomes negative, reset to 0.
     * Time: O(n), Space: O(1)
     */
    public static int maxSubArray(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        int currentSum = 0;

        for (int num : nums) {
            currentSum += num;
            maxSum = Math.max(maxSum, currentSum);

            // Reset if negative (start new subarray)
            if (currentSum < 0) {
                currentSum = 0;
            }
        }

        return maxSum;
    }

    /**
     * Problem: Maximum Subarray with Indices
     * Return the subarray with maximum sum (not just the sum).
     * 
     * Time: O(n), Space: O(1) excluding output
     */
    public static int[] maxSubArrayWithIndices(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        int currentSum = 0;
        int start = 0, end = 0, tempStart = 0;

        for (int i = 0; i < nums.length; i++) {
            currentSum += nums[i];

            if (currentSum > maxSum) {
                maxSum = currentSum;
                start = tempStart;
                end = i;
            }

            if (currentSum < 0) {
                currentSum = 0;
                tempStart = i + 1;
            }
        }

        int[] result = new int[end - start + 1];
        for (int i = start; i <= end; i++) {
            result[i - start] = nums[i];
        }
        return result;
    }

    /**
     * Problem: Maximum Product Subarray
     * Find contiguous subarray with the largest product.
     * 
     * Approach: Track both max and min at each position (since negative * negative = positive).
     * Time: O(n), Space: O(1)
     */
    public static int maxProduct(int[] nums) {
        int maxProduct = nums[0];
        int currentMax = nums[0];
        int currentMin = nums[0];

        for (int i = 1; i < nums.length; i++) {
            int num = nums[i];

            // If num is negative, max and min swap
            if (num < 0) {
                int temp = currentMax;
                currentMax = currentMin;
                currentMin = temp;
            }

            currentMax = Math.max(num, currentMax * num);
            currentMin = Math.min(num, currentMin * num);

            maxProduct = Math.max(maxProduct, currentMax);
        }

        return maxProduct;
    }

    /**
     * Problem: Maximum Sum Circular Subarray
     * Find maximum subarray sum in a circular array.
     * 
     * Approach: Max subarray = max(standard max, total - min subarray)
     * Edge case: if all numbers are negative, return the maximum element.
     * Time: O(n), Space: O(1)
     */
    public static int maxSubarraySumCircular(int[] nums) {
        int total = 0;
        int maxSum = Integer.MIN_VALUE;
        int currentMax = 0;
        int minSum = Integer.MAX_VALUE;
        int currentMin = 0;

        for (int num : nums) {
            total += num;

            // Standard Kadane for max
            currentMax = Math.max(num, currentMax + num);
            maxSum = Math.max(maxSum, currentMax);

            // Kadane for min
            currentMin = Math.min(num, currentMin + num);
            minSum = Math.min(minSum, currentMin);
        }

        // If all numbers are negative, return the maximum element
        if (maxSum < 0) {
            return maxSum;
        }

        // Max circular = max(standard max, total - min subarray)
        return Math.max(maxSum, total - minSum);
    }

    /**
     * Problem: Maximum Length of Subarray With Positive Product
     * Find longest subarray where product of all elements is positive.
     * 
     * Approach: Track first negative position and count of negatives.
     * Time: O(n), Space: O(1)
     */
    public static int getMaxLen(int[] nums) {
        int maxLen = 0;
        int firstNegative = -1;
        int negativeCount = 0;
        int start = -1;

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 0) {
                // Reset on zero
                start = i;
                firstNegative = -1;
                negativeCount = 0;
            } else if (nums[i] < 0) {
                negativeCount++;
                if (firstNegative == -1) {
                    firstNegative = i;
                }
            }

            if (negativeCount % 2 == 0) {
                // Even negatives -> positive product
                maxLen = Math.max(maxLen, i - start);
            } else {
                // Odd negatives -> exclude first negative
                maxLen = Math.max(maxLen, i - firstNegative);
            }
        }

        return maxLen;
    }

    /**
     * Problem: Best Time to Buy and Sell Stock
     * Find maximum profit from a single transaction.
     * 
     * Approach: Track minimum price seen so far, maximize profit.
     * Time: O(n), Space: O(1)
     */
    public static int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            } else {
                maxProfit = Math.max(maxProfit, price - minPrice);
            }
        }

        return maxProfit;
    }

    /**
     * Problem: Maximum Sum of Two Non-Overlapping Subarrays
     * Find maximum sum of two non-overlapping subarrays of lengths firstLen and secondLen.
     * 
     * Approach: Two passes of Kadane-like sliding window.
     * Time: O(n), Space: O(n)
     */
    public static int maxSumTwoNoOverlap(int[] nums, int firstLen, int secondLen) {
        return Math.max(
            helperMaxSum(nums, firstLen, secondLen),
            helperMaxSum(nums, secondLen, firstLen)
        );
    }

    private static int helperMaxSum(int[] nums, int len1, int len2) {
        int n = nums.length;

        // Calculate prefix sums
        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        int maxSum = 0;
        int maxFirst = 0;

        // First subarray of length len1, then second of length len2
        for (int i = len1 + len2; i <= n; i++) {
            maxFirst = Math.max(maxFirst, prefixSum[i - len2] - prefixSum[i - len2 - len1]);
            maxSum = Math.max(maxSum, maxFirst + (prefixSum[i] - prefixSum[i - len2]));
        }

        return maxSum;
    }

    /**
     * Problem: Maximum Alternating Subsequence Sum
     * Find max sum of alternating subsequence (nums[i1] - nums[i2] + nums[i3] - nums[i4] + ...).
     * 
     * Approach: Track even and odd sums.
     * Time: O(n), Space: O(1)
     */
    public static long maxAlternatingSum(int[] nums) {
        long even = 0; // Sum ending with even index (positive)
        long odd = Long.MIN_VALUE; // Sum ending with odd index (negative)

        for (int num : nums) {
            long newEven = Math.max(even, Math.max(odd + num, num));
            long newOdd = Math.max(odd, even - num);
            even = newEven;
            odd = newOdd;
        }

        return even;
    }

    public static void main(String[] args) {
        System.out.println("=== KADANE'S ALGORITHM PATTERN ===");
        System.out.println();

        // 1. Maximum Subarray
        System.out.println("1. Maximum Subarray:");
        int[] nums1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("   Input: [-2,1,-3,4,-1,2,1,-5,4]");
        System.out.println("   Max sum: " + maxSubArray(nums1) + " (expected: 6)");
        int[] subarray = maxSubArrayWithIndices(nums1);
        System.out.print("   Subarray: ");
        for (int x : subarray) System.out.print(x + " ");
        System.out.println("(expected: 4 -1 2 1)");
        System.out.println();

        // 2. Maximum Product Subarray
        System.out.println("2. Maximum Product Subarray:");
        int[] nums2 = {2, 3, -2, 4};
        System.out.println("   Input: [2,3,-2,4] -> " + maxProduct(nums2) + " (expected: 6)");
        int[] nums2b = {-2, 0, -1};
        System.out.println("   Input: [-2,0,-1] -> " + maxProduct(nums2b) + " (expected: 0)");
        int[] nums2c = {-2, 3, -4};
        System.out.println("   Input: [-2,3,-4] -> " + maxProduct(nums2c) + " (expected: 24)");
        System.out.println();

        // 3. Maximum Sum Circular Subarray
        System.out.println("3. Maximum Sum Circular Subarray:");
        int[] nums3 = {1, -2, 3, -2};
        System.out.println("   Input: [1,-2,3,-2] -> " + maxSubarraySumCircular(nums3) + " (expected: 3)");
        int[] nums3b = {5, -3, 5};
        System.out.println("   Input: [5,-3,5] -> " + maxSubarraySumCircular(nums3b) + " (expected: 10)");
        int[] nums3c = {-3, -2, -3};
        System.out.println("   Input: [-3,-2,-3] -> " + maxSubarraySumCircular(nums3c) + " (expected: -2)");
        System.out.println();

        // 4. Maximum Length of Subarray With Positive Product
        System.out.println("4. Maximum Length of Subarray With Positive Product:");
        int[] nums4 = {1, -2, -3, 4};
        System.out.println("   Input: [1,-2,-3,4] -> " + getMaxLen(nums4) + " (expected: 4)");
        int[] nums4b = {0, 1, -2, -3, -4};
        System.out.println("   Input: [0,1,-2,-3,-4] -> " + getMaxLen(nums4b) + " (expected: 3)");
        System.out.println();

        // 5. Best Time to Buy and Sell Stock
        System.out.println("5. Best Time to Buy and Sell Stock:");
        int[] nums5 = {7, 1, 5, 3, 6, 4};
        System.out.println("   Input: [7,1,5,3,6,4] -> " + maxProfit(nums5) + " (expected: 5)");
        int[] nums5b = {7, 6, 4, 3, 1};
        System.out.println("   Input: [7,6,4,3,1] -> " + maxProfit(nums5b) + " (expected: 0)");
        System.out.println();

        // 6. Maximum Sum of Two Non-Overlapping Subarrays
        System.out.println("6. Maximum Sum of Two Non-Overlapping Subarrays:");
        int[] nums6 = {0, 6, 5, 2, 2, 5, 0, 9, 8, 1};
        System.out.println("   Input: [0,6,5,2,2,5,0,9,8,1], firstLen=3, secondLen=2");
        System.out.println("   Output: " + maxSumTwoNoOverlap(nums6, 3, 2) + " (expected: 29)");
        System.out.println();

        // 7. Maximum Alternating Subsequence Sum
        System.out.println("7. Maximum Alternating Subsequence Sum:");
        int[] nums7 = {4, 2, 5, 3};
        System.out.println("   Input: [4,2,5,3] -> " + maxAlternatingSum(nums7) + " (expected: 7)");
        int[] nums7b = {5, 6, 7, 8};
        System.out.println("   Input: [5,6,7,8] -> " + maxAlternatingSum(nums7b) + " (expected: 8)");
    }
}
