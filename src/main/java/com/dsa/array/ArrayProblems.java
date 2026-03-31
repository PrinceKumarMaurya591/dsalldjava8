package com.dsa.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Comprehensive collection of array problems with optimal solutions.
 * Each problem includes:
 * 1. Problem statement
 * 2. Optimal solution with time and space complexity
 * 3. Algorithm explanation
 * 4. Dry run example
 * 5. Implementation
 */
public class ArrayProblems {

    // =========================================================================
    // 1. TWO SUM
    // =========================================================================
    
    /**
     * Problem: Given an array of integers nums and an integer target,
     * return indices of the two numbers such that they add up to target.
     * 
     * Assumptions:
     * - Each input has exactly one solution
     * - Cannot use the same element twice
     * - Return answer in any order
     * 
     * Optimal Solution: O(n) time, O(n) space using HashMap
     */
    public static int[] twoSum(int[] nums, int target) {
        // HashMap to store number -> index mapping
        Map<Integer, Integer> numMap = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            // Check if complement exists in map
            if (numMap.containsKey(complement)) {
                return new int[] {numMap.get(complement), i};
            }
            
            // Store current number with its index
            numMap.put(nums[i], i);
        }
        
        // According to problem constraints, this should never be reached
        return new int[] {-1, -1};
    }
    
    /**
     * Dry Run for Two Sum:
     * Input: nums = [2, 7, 11, 15], target = 9
     * 
     * Iteration 1: i = 0, nums[0] = 2
     *   complement = 9 - 2 = 7
     *   map doesn't contain 7 → store (2, 0)
     * 
     * Iteration 2: i = 1, nums[1] = 7
     *   complement = 9 - 7 = 2
     *   map contains 2 at index 0 → return [0, 1]
     * 
     * Result: [0, 1]
     */

    // =========================================================================
    // 2. BEST TIME TO BUY AND SELL STOCK
    // =========================================================================
    
    /**
     * Problem: Given an array prices where prices[i] is the price on day i,
     * maximize profit by choosing a single day to buy and a different day in the
     * future to sell. Return maximum profit.
     * 
     * Optimal Solution: O(n) time, O(1) space using single pass
     */
    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }
        
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        
        for (int price : prices) {
            // Update minimum price seen so far
            if (price < minPrice) {
                minPrice = price;
            }
            // Calculate potential profit and update maxProfit
            else if (price - minPrice > maxProfit) {
                maxProfit = price - minPrice;
            }
        }
        
        return maxProfit;
    }
    
    /**
     * Dry Run for Best Time to Buy and Sell Stock:
     * Input: prices = [7, 1, 5, 3, 6, 4]
     * 
     * Day 1: price = 7, minPrice = 7, profit = 0
     * Day 2: price = 1, minPrice = 1, profit = 0
     * Day 3: price = 5, minPrice = 1, profit = 4
     * Day 4: price = 3, minPrice = 1, profit = 4
     * Day 5: price = 6, minPrice = 1, profit = 5
     * Day 6: price = 4, minPrice = 1, profit = 5
     * 
     * Result: 5 (buy at 1, sell at 6)
     */

    // =========================================================================
    // 3. CONTAINS DUPLICATE
    // =========================================================================
    
    /**
     * Problem: Given an integer array nums, return true if any value appears
     * at least twice in the array, false if every element is distinct.
     * 
     * Optimal Solution: O(n) time, O(n) space using HashSet
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
     * Dry Run for Contains Duplicate:
     * Input: nums = [1, 2, 3, 1]
     * 
     * Iteration 1: num = 1, set = {1}
     * Iteration 2: num = 2, set = {1, 2}
     * Iteration 3: num = 3, set = {1, 2, 3}
     * Iteration 4: num = 1, set contains 1 → return true
     * 
     * Result: true
     */

    // =========================================================================
    // 4. CONTAINS DUPLICATE II
    // =========================================================================
    
    /**
     * Problem: Given an integer array nums and an integer k, return true if
     * there are two distinct indices i and j such that nums[i] == nums[j]
     * and abs(i - j) <= k.
     * 
     * Optimal Solution: O(n) time, O(min(n, k)) space using sliding window with HashSet
     */
    public static boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> window = new HashSet<>();
        
        for (int i = 0; i < nums.length; i++) {
            // Remove element that's out of window range
            if (i > k) {
                window.remove(nums[i - k - 1]);
            }
            
            // If current element exists in window, we found a duplicate within k distance
            if (window.contains(nums[i])) {
                return true;
            }
            
            window.add(nums[i]);
        }
        
        return false;
    }
    
    /**
     * Dry Run for Contains Duplicate II:
     * Input: nums = [1, 2, 3, 1], k = 3
     * 
     * i = 0: window = {1}
     * i = 1: window = {1, 2}
     * i = 2: window = {1, 2, 3}
     * i = 3: window = {1, 2, 3} (remove nums[0] if i > 3, but i=3 so no removal)
     *        window contains 1 → return true
     * 
     * Result: true
     */

    // =========================================================================
    // 5. PRODUCT OF ARRAY EXCEPT SELF
    // =========================================================================
    
    /**
     * Problem: Given an integer array nums, return an array answer such that
     * answer[i] is equal to the product of all elements of nums except nums[i].
     * Must run in O(n) time and without using division operation.
     * 
     * Optimal Solution: O(n) time, O(1) space (excluding output array)
     * using prefix and suffix products
     */
    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        
        // Calculate prefix products
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }
        
        // Calculate suffix products and combine with prefix
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] = result[i] * suffix;
            suffix *= nums[i];
        }
        
        return result;
    }
    
    /**
     * Dry Run for Product of Array Except Self:
     * Input: nums = [1, 2, 3, 4]
     * 
     * Prefix pass:
     * result[0] = 1
     * result[1] = 1 * 1 = 1
     * result[2] = 1 * 2 = 2
     * result[3] = 2 * 3 = 6
     * result = [1, 1, 2, 6]
     * 
     * Suffix pass (right to left):
     * i = 3: result[3] = 6 * 1 = 6, suffix = 1 * 4 = 4
     * i = 2: result[2] = 2 * 4 = 8, suffix = 4 * 3 = 12
     * i = 1: result[1] = 1 * 12 = 12, suffix = 12 * 2 = 24
     * i = 0: result[0] = 1 * 24 = 24, suffix = 24 * 1 = 24
     * 
     * Result: [24, 12, 8, 6]
     */

    // =========================================================================
    // 6. MAXIMUM SUBARRAY (KADANE'S ALGORITHM)
    // =========================================================================
    
    /**
     * Problem: Given an integer array nums, find the contiguous subarray
     * (containing at least one number) which has the largest sum and return its sum.
     * 
     * Optimal Solution: O(n) time, O(1) space using Kadane's Algorithm
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
     * Dry Run for Maximum Subarray:
     * Input: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
     * 
     * i = 0: maxEndingHere = -2, maxSoFar = -2
     * i = 1: maxEndingHere = max(1, -2+1= -1) = 1, maxSoFar = max(-2, 1) = 1
     * i = 2: maxEndingHere = max(-3, 1-3= -2) = -2, maxSoFar = max(1, -2) = 1
     * i = 3: maxEndingHere = max(4, -2+4= 2) = 4, maxSoFar = max(1, 4) = 4
     * i = 4: maxEndingHere = max(-1, 4-1= 3) = 3, maxSoFar = max(4, 3) = 4
     * i = 5: maxEndingHere = max(2, 3+2= 5) = 5, maxSoFar = max(4, 5) = 5
     * i = 6: maxEndingHere = max(1, 5+1= 6) = 6, maxSoFar = max(5, 6) = 6
     * i = 7: maxEndingHere = max(-5, 6-5= 1) = 1, maxSoFar = max(6, 1) = 6
     * i = 8: maxEndingHere = max(4, 1+4= 5) = 5, maxSoFar = max(6, 5) = 6
     * 
     * Result: 6 (subarray [4, -1, 2, 1])
     */

    // =========================================================================
    // 7. MAXIMUM PRODUCT SUBARRAY
    // =========================================================================
    
    /**
     * Problem: Given an integer array nums, find the contiguous subarray
     * that has the largest product and return the product.
     * 
     * Optimal Solution: O(n) time, O(1) space tracking min and max products
     */
    public static int maxProduct(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int maxSoFar = nums[0];
        int minSoFar = nums[0];
        int result = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            int current = nums[i];
            
            // Store previous max before updating
            int tempMax = Math.max(current, Math.max(maxSoFar * current, minSoFar * current));
            minSoFar = Math.min(current, Math.min(maxSoFar * current, minSoFar * current));
            
            maxSoFar = tempMax;
            result = Math.max(result, maxSoFar);
        }
        
        return result;
    }
    
    /**
     * Dry Run for Maximum Product Subarray:
     * Input: nums = [2, 3, -2, 4]
     * 
     * i = 0: max = 2, min = 2, result = 2
     * i = 1: current = 3
     *   tempMax = max(3, max(2*3=6, 2*3=6)) = 6
     *   min = min(3, min(2*3=6, 2*3=6)) = 3
     *   max = 6, result = max(2, 6) = 6
     * i = 2: current = -2
     *   tempMax = max(-2, max(6*-2=-12, 3*-2=-6)) = -2
     *   min = min(-2, min(6*-2=-12, 3*-2=-6)) = -12
     *   max = -2, result = max(6, -2) = 6
     * i = 3: current = 4
     *   tempMax = max(4, max(-2*4=-8, -12*4=-48)) = 4
     *   min = min(4, min(-2*4=-8, -12*4=-48)) = -48
     *   max = 4, result = max(6, 4) = 6
     * 
     * Result: 6 (subarray [2, 3])
     */

    // =========================================================================
    // 8. FIND MINIMUM IN ROTATED SORTED ARRAY
    // =========================================================================
    
    /**
     * Problem: Given a sorted rotated array of unique elements, return the minimum element.
     * Array was sorted in ascending order and then rotated.
     * 
     * Optimal Solution: O(log n) time, O(1) space using modified binary search
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
     * Dry Run for Find Minimum in Rotated Sorted Array:
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

    // =========================================================================
    // 9. SEARCH IN ROTATED SORTED ARRAY
    // =========================================================================
    
    /**
     * Problem: Given a sorted rotated array of distinct integers and a target value,
     * return the index of target if it is in the array, otherwise return -1.
     * 
     * Optimal Solution: O(log n) time, O(1) space using modified binary search
     */
    public static int searchInRotatedSortedArray(int[] nums, int target) {
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
     * Dry Run for Search in Rotated Sorted Array:
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

    // =========================================================================
    // 10. TWO SUM II (INPUT ARRAY IS SORTED)
    // =========================================================================
    
    /**
     * Problem: Given a 1-indexed array of integers numbers that is already sorted
     * in non-decreasing order, find two numbers such that they add up to a specific
     * target number. Return the indices of the two numbers (1-indexed).
     * 
     * Optimal Solution: O(n) time, O(1) space using two pointers
     */
    public static int[] twoSumII(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;
        
        while (left < right) {
            int sum = numbers[left] + numbers[right];
            
            if (sum == target) {
                // Convert to 1-indexed
                return new int[] {left + 1, right + 1};
            } else if (sum < target) {
                // Need larger sum, move left pointer right
                left++;
            } else {
                // Need smaller sum, move right pointer left
                right--;
            }
        }
        
        return new int[] {-1, -1};
    }
    
    /**
     * Dry Run for Two Sum II:
     * Input: numbers = [2, 7, 11, 15], target = 9
     * 
     * Initial: left = 0, right = 3
     * Iteration 1: sum = 2 + 15 = 17 > 9 → right = 2
     * Iteration 2: sum = 2 + 11 = 13 > 9 → right = 1
     * Iteration 3: sum = 2 + 7 = 9 == 9 → return [1, 2] (1-indexed)
     * 
     * Result: [1, 2]
     */

    // =========================================================================
    // 11. 3 SUM
    // =========================================================================
    
    /**
     * Problem: Given an integer array nums, return all triplets [nums[i], nums[j], nums[k]]
     * such that i != j != k and nums[i] + nums[j] + nums[k] == 0.
     * Solution set must not contain duplicate triplets.
     * 
     * Optimal Solution: O(n²) time, O(1) space (excluding output) using sorting + two pointers
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
     * Dry Run for 3 Sum:
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

    // =========================================================================
    // 12. MERGE SORTED ARRAY
    // =========================================================================
    
    /**
     * Problem: Given two sorted integer arrays nums1 and nums2, merge nums2 into nums1
     * as one sorted array. nums1 has enough space to hold additional elements from nums2.
     * 
     * Optimal Solution: O(m + n) time, O(1) space using three pointers from the end
     */
    public static void mergeSortedArray(int[] nums1, int m, int[] nums2, int n) {
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
     * Dry Run for Merge Sorted Array:
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

    // =========================================================================
    // 13. CONTAINER WITH MOST WATER
    // =========================================================================
    
    /**
     * Problem: Given n non-negative integers representing an elevation map where
     * each bar has width 1, find the maximum amount of water that can be trapped.
     * 
     * Optimal Solution: O(n) time, O(1) space using two pointers
     */
    public static int maxArea(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int maxArea = 0;
        
        while (left < right) {
            // Calculate current area
            int currentArea = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, currentArea);
            
            // Move the pointer with smaller height
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        
        return maxArea;
    }
    
    /**
     * Dry Run for Container With Most Water:
     * Input: height = [1, 8, 6, 2, 5, 4, 8, 3, 7]
     * 
     * Initial: left = 0, right = 8, maxArea = 0
     * Iteration 1: area = min(1,7)*8 = 8, maxArea=8, left=1
     * Iteration 2: area = min(8,7)*7 = 49, maxArea=49, right=7
     * Iteration 3: area = min(8,3)*6 = 18, maxArea=49, right=6
     * Iteration 4: area = min(8,8)*5 = 40, maxArea=49, left=2 (or right=5)
     * ... continues
     * 
     * Result: 49
     */

    // =========================================================================
    // 14. VERIFYING AN ALIEN DICTIONARY
    // =========================================================================
    
    /**
     * Problem: Given a sequence of words written in an alien language and the order
     * of its alphabet, return true if the words are sorted lexicographically according
     * to the alien alphabet.
     * 
     * Optimal Solution: O(N * M) time where N=words, M=avg word length, O(1) space
     */
    public static boolean isAlienSorted(String[] words, String order) {
        // Create mapping from alien character to position
        int[] orderMap = new int[26];
        for (int i = 0; i < order.length(); i++) {
            orderMap[order.charAt(i) - 'a'] = i;
        }
        
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            
            // Compare characters until a difference is found
            int minLength = Math.min(word1.length(), word2.length());
            boolean foundDifference = false;
            
            for (int j = 0; j < minLength; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);
                
                if (c1 != c2) {
                    if (orderMap[c1 - 'a'] > orderMap[c2 - 'a']) {
                        return false;
                    }
                    foundDifference = true;
                    break;
                }
            }
            
            // If no difference found in common prefix, check lengths
            if (!foundDifference && word1.length() > word2.length()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Dry Run for Verifying an Alien Dictionary:
     * Input: words = ["hello","leetcode"], order = "hlabcdefgijkmnopqrstuvwxyz"
     * 
     * Create orderMap: h→0, l→1, a→2, ...
     * Compare "hello" and "leetcode":
     *   Compare h(0) and l(1): 0 < 1 → words are sorted
     * 
     * Result: true
     */

    // =========================================================================
    // 15. NEXT PERMUTATION
    // =========================================================================
    
    /**
     * Problem: Given an array of integers, rearrange numbers into the lexicographically
     * next greater permutation. If not possible, rearrange to lowest possible order.
     * 
     * Optimal Solution: O(n) time, O(1) space
     */
    public static void nextPermutation(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return;
        }
        
        // Step 1: Find first decreasing element from right
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }
        
        // Step 2: If found, find element just larger than nums[i] from right
        if (i >= 0) {
            int j = nums.length - 1;
            while (j >= 0 && nums[j] <= nums[i]) {
                j--;
            }
            // Swap nums[i] and nums[j]
            swap(nums, i, j);
        }
        
        // Step 3: Reverse the suffix starting from i+1
        reverse(nums, i + 1, nums.length - 1);
    }
    
    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
    
    private static void reverse(int[] nums, int start, int end) {
        while (start < end) {
            swap(nums, start, end);
            start++;
            end--;
        }
    }
    
    /**
     * Dry Run for Next Permutation:
     * Input: nums = [1, 2, 3]
     * 
     * Step 1: i = 1 (nums[1]=2 < nums[2]=3)
     * Step 2: j = 2 (nums[2]=3 > nums[1]=2)
     *   Swap: nums = [1, 3, 2]
     * Step 3: Reverse suffix from index 2: already reversed
     * 
     * Result: [1, 3, 2]
     */

    // =========================================================================
    // 16. REMOVE DUPLICATES FROM SORTED ARRAY
    // =========================================================================
    
    /**
     * Problem: Given a sorted array nums, remove duplicates in-place such that each
     * element appears only once and return the new length.
     * 
     * Optimal Solution: O(n) time, O(1) space using two pointers
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
     * Dry Run for Remove Duplicates from Sorted Array:
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

    // =========================================================================
    // 17. FIND FIRST AND LAST POSITION OF ELEMENT IN SORTED ARRAY
    // =========================================================================
    
    /**
     * Problem: Given an array of integers nums sorted in non-decreasing order,
     * find the starting and ending position of a given target value.
     * Return [-1, -1] if target is not found.
     * 
     * Optimal Solution: O(log n) time, O(1) space using binary search
     */
    public static int[] searchRange(int[] nums, int target) {
        int[] result = new int[] {-1, -1};
        
        // Find first occurrence
        result[0] = findBound(nums, target, true);
        
        // If first occurrence not found, target doesn't exist
        if (result[0] == -1) {
            return result;
        }
        
        // Find last occurrence
        result[1] = findBound(nums, target, false);
        
        return result;
    }
    
    private static int findBound(int[] nums, int target, boolean isFirst) {
        int left = 0;
        int right = nums.length - 1;
        int bound = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                bound = mid;
                if (isFirst) {
                    // Search left for first occurrence
                    right = mid - 1;
                } else {
                    // Search right for last occurrence
                    left = mid + 1;
                }
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return bound;
    }
    
    /**
     * Dry Run for Find First and Last Position:
     * Input: nums = [5, 7, 7, 8, 8, 10], target = 8
     * 
     * Find first occurrence:
     *   mid = 2, nums[2]=7 < 8 → left=3
     *   mid = 4, nums[4]=8 == 8 → bound=4, right=3
     *   mid = 3, nums[3]=8 == 8 → bound=3, right=2 → break
     *   First occurrence = 3
     * 
     * Find last occurrence:
     *   mid = 2, nums[2]=7 < 8 → left=3
     *   mid = 4, nums[4]=8 == 8 → bound=4, left=5
     *   mid = 5, nums[5]=10 > 8 → right=4 → break
     *   Last occurrence = 4
     * 
     * Result: [3, 4]
     */

    // =========================================================================
    // 18. TRAPPING RAIN WATER
    // =========================================================================
    
    /**
     * Problem: Given n non-negative integers representing an elevation map where
     * each bar has width 1, compute how much water it can trap after raining.
     * 
     * Optimal Solution: O(n) time, O(1) space using two pointers
     */
    public static int trap(int[] height) {
        if (height == null || height.length < 3) {
            return 0;
        }
        
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;
        
        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }
        
        return water;
    }
    
    /**
     * Dry Run for Trapping Rain Water:
     * Input: height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]
     * 
     * left=0, right=11: height[0]=0 < height[11]=1
     *   height[0]=0 < leftMax=0 → leftMax=0, left=1
     * left=1, right=11: height[1]=1 < height[11]=1
     *   height[1]=1 >= leftMax=0 → leftMax=1, left=2
     * left=2, right=11: height[2]=0 < height[11]=1
     *   height[2]=0 < leftMax=1 → water+=1, left=3
     * ... continues
     * 
     * Result: 6
     */

    // =========================================================================
    // 19. MEDIAN OF TWO SORTED ARRAYS
    // =========================================================================
    
    /**
     * Problem: Given two sorted arrays nums1 and nums2 of size m and n respectively,
     * return the median of the two sorted arrays.
     * 
     * Optimal Solution: O(log(min(m, n))) time, O(1) space using binary search
     */
    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // Ensure nums1 is the smaller array for binary search
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }
        
        int m = nums1.length;
        int n = nums2.length;
        int total = m + n;
        int half = (total + 1) / 2;
        
        int left = 0;
        int right = m;
        
        while (left <= right) {
            int partition1 = (left + right) / 2;
            int partition2 = half - partition1;
            
            int maxLeft1 = (partition1 == 0) ? Integer.MIN_VALUE : nums1[partition1 - 1];
            int minRight1 = (partition1 == m) ? Integer.MAX_VALUE : nums1[partition1];
            
            int maxLeft2 = (partition2 == 0) ? Integer.MIN_VALUE : nums2[partition2 - 1];
            int minRight2 = (partition2 == n) ? Integer.MAX_VALUE : nums2[partition2];
            
            // Check if partition is correct
            if (maxLeft1 <= minRight2 && maxLeft2 <= minRight1) {
                // Found correct partition
                if (total % 2 == 0) {
                    // Even total length: average of two middle elements
                    return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
                } else {
                    // Odd total length: middle element
                    return Math.max(maxLeft1, maxLeft2);
                }
            } else if (maxLeft1 > minRight2) {
                // Too far right in nums1, move left
                right = partition1 - 1;
            } else {
                // Too far left in nums1, move right
                left = partition1 + 1;
            }
        }
        
        throw new IllegalArgumentException("Input arrays are not sorted");
    }
    
    /**
     * Dry Run for Median of Two Sorted Arrays:
     * Input: nums1 = [1, 3], nums2 = [2]
     * 
     * m=2, n=1, total=3, half=2
     * left=0, right=2
     * partition1=1, partition2=1
     * maxLeft1=1, minRight1=3, maxLeft2=2, minRight2=∞
     * Check: 1≤∞ && 2≤3 → true
     * total odd → return max(1,2)=2.0
     * 
     * Result: 2.0
     */

    // =========================================================================
    // 20. VALID ANAGRAM
    // =========================================================================
    
    /**
     * Problem: Given two strings s and t, return true if t is an anagram of s.
     * An anagram is a word formed by rearranging the letters of another word.
     * 
     * Optimal Solution: O(n) time, O(1) space using frequency array (since only 26 letters)
     */
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        int[] frequency = new int[26];
        
        // Count characters in s
        for (char c : s.toCharArray()) {
            frequency[c - 'a']++;
        }
        
        // Subtract characters in t
        for (char c : t.toCharArray()) {
            frequency[c - 'a']--;
            // If count goes negative, t has extra character
            if (frequency[c - 'a'] < 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Dry Run for Valid Anagram:
     * Input: s = "anagram", t = "nagaram"
     * 
     * Count s: a=3, n=1, g=1, r=1, m=1
     * Subtract t: a=0, n=0, g=0, r=0, m=0
     * All counts zero → true
     * 
     * Result: true
     */

    // =========================================================================
    // 21. TOP K FREQUENT ELEMENTS
    // =========================================================================
    
    /**
     * Problem: Given an integer array nums and an integer k, return the k most
     * frequent elements. You may return the answer in any order.
     * 
     * Optimal Solution: O(n) time, O(n) space using bucket sort
     */
    public static int[] topKFrequent(int[] nums, int k) {
        // Step 1: Count frequencies
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int num : nums) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        
        // Step 2: Create buckets where index = frequency
        List<Integer>[] buckets = new List[nums.length + 1];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
        }
        
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            int num = entry.getKey();
            int frequency = entry.getValue();
            buckets[frequency].add(num);
        }
        
        // Step 3: Collect top k frequent elements
        int[] result = new int[k];
        int index = 0;
        
        for (int i = buckets.length - 1; i >= 0 && index < k; i--) {
            for (int num : buckets[i]) {
                result[index++] = num;
                if (index == k) {
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Dry Run for Top K Frequent Elements:
     * Input: nums = [1, 1, 1, 2, 2, 3], k = 2
     * 
     * Frequency map: {1=3, 2=2, 3=1}
     * Buckets:
     *   index 1: [3]
     *   index 2: [2]
     *   index 3: [1]
     * Collect from highest frequency: [1, 2]
     * 
     * Result: [1, 2]
     */

    // =========================================================================
    // 22. GROUP ANAGRAMS
    // =========================================================================
    
    /**
     * Problem: Given an array of strings strs, group the anagrams together.
     * An anagram is a word formed by rearranging the letters of another word.
     * 
     * Optimal Solution: O(n * m) time where n=strs.length, m=avg string length, O(n) space
     */
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> anagramMap = new HashMap<>();
        
        for (String str : strs) {
            // Create frequency key
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            // Add to map
            anagramMap.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramMap.values());
    }
    
    /**
     * Dry Run for Group Anagrams:
     * Input: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
     * 
     * Process:
     *   "eat" → sorted "aet" → map["aet"] = ["eat"]
     *   "tea" → sorted "aet" → map["aet"] = ["eat", "tea"]
     *   "tan" → sorted "ant" → map["ant"] = ["tan"]
     *   "ate" → sorted "aet" → map["aet"] = ["eat", "tea", "ate"]
     *   "nat" → sorted "ant" → map["ant"] = ["tan", "nat"]
     *   "bat" → sorted "abt" → map["abt"] = ["bat"]
     * 
     * Result: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
     */

    // =========================================================================
    // 23. VALID SUDOKU
    // =========================================================================
    
    /**
     * Problem: Determine if a 9x9 Sudoku board is valid according to Sudoku rules.
     * Only the filled cells need to be validated.
     * 
     * Optimal Solution: O(1) time (81 cells), O(1) space using sets
     */
    public static boolean isValidSudoku(char[][] board) {
        Set<String> seen = new HashSet<>();
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                char num = board[i][j];
                if (num != '.') {
                    // Check row, column, and sub-box
                    String rowKey = num + " in row " + i;
                    String colKey = num + " in col " + j;
                    String boxKey = num + " in box " + (i / 3) + "-" + (j / 3);
                    
                    if (!seen.add(rowKey) || !seen.add(colKey) || !seen.add(boxKey)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Dry Run for Valid Sudoku:
     * Input: Valid Sudoku board
     *
     * Check each cell:
     *   Cell (0,0)='5': add "5 in row 0", "5 in col 0", "5 in box 0-0"
     *   Cell (0,1)='3': add "3 in row 0", "3 in col 1", "3 in box 0-0"
     *   ... continue
     * If all checks pass → return true
     * 
     * Result: true
     */

    // =========================================================================
    // 24. ENCODE AND DECODE STRINGS
    // =========================================================================
    
    /**
     * Problem: Design an algorithm to encode a list of strings to a single string
     * and decode the string back to the original list of strings.
     * 
     * Optimal Solution: Use length prefix with delimiter
     */
    public static String encode(List<String> strs) {
        StringBuilder encoded = new StringBuilder();
        for (String str : strs) {
            // Format: length + "#" + string
            encoded.append(str.length()).append("#").append(str);
        }
        return encoded.toString();
    }
    
    public static List<String> decode(String s) {
        List<String> decoded = new ArrayList<>();
        int i = 0;
        
        while (i < s.length()) {
            // Find delimiter
            int delimiterIndex = s.indexOf("#", i);
            // Parse length
            int length = Integer.parseInt(s.substring(i, delimiterIndex));
            // Extract string
            String str = s.substring(delimiterIndex + 1, delimiterIndex + 1 + length);
            decoded.add(str);
            // Move to next encoded string
            i = delimiterIndex + 1 + length;
        }
        
        return decoded;
    }
    
    /**
     * Dry Run for Encode and Decode Strings:
     * Input: strs = ["hello", "world", "code"]
     * 
     * Encode:
     *   "hello" → "5#hello"
     *   "world" → "5#world"
     *   "code" → "4#code"
     *   Result: "5#hello5#world4#code"
     * 
     * Decode:
     *   Parse "5#hello" → length=5, string="hello"
     *   Parse "5#world" → length=5, string="world"
     *   Parse "4#code" → length=4, string="code"
     * 
     * Result: ["hello", "world", "code"]
     */

    // =========================================================================
    // 25. LONGEST CONSECUTIVE SEQUENCE
    // =========================================================================
    
    /**
     * Problem: Given an unsorted array of integers nums, return the length of the
     * longest consecutive elements sequence. Must run in O(n) time.
     * 
     * Optimal Solution: O(n) time, O(n) space using HashSet
     */
    public static int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }
        
        int longestStreak = 0;
        
        for (int num : numSet) {
            // Only start counting from the beginning of a sequence
            if (!numSet.contains(num - 1)) {
                int currentNum = num;
                int currentStreak = 1;
                
                // Count consecutive numbers
                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    currentStreak++;
                }
                
                longestStreak = Math.max(longestStreak, currentStreak);
            }
        }
        
        return longestStreak;
    }
    
    /**
     * Dry Run for Longest Consecutive Sequence:
     * Input: nums = [100, 4, 200, 1, 3, 2]
     * 
     * Create set: {100, 4, 200, 1, 3, 2}
     * Check each number:
     *   100: 99 not in set → count streak: 100 only → streak=1
     *   4: 3 in set → skip
     *   200: 199 not in set → count streak: 200 only → streak=1
     *   1: 0 not in set → count streak: 1,2,3,4 → streak=4
     *   3: 2 in set → skip
     *   2: 1 in set → skip
     * 
     * Result: 4 (sequence [1, 2, 3, 4])
     */

    // =========================================================================
    // MAIN METHOD FOR TESTING
    // =========================================================================
    
    /**
     * Main method to test all array problem solutions
     */
    public static void main(String[] args) {
        System.out.println("Testing Array Problems Package");
        System.out.println("==============================");
        
        // Test Two Sum
        int[] nums1 = {2, 7, 11, 15};
        int[] result1 = twoSum(nums1, 9);
        System.out.println("Two Sum Test: " + Arrays.toString(result1));
        
        // Test Contains Duplicate
        int[] nums2 = {1, 2, 3, 1};
        boolean result2 = containsDuplicate(nums2);
        System.out.println("Contains Duplicate Test: " + result2);
        
        // Test Maximum Subarray
        int[] nums3 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int result3 = maxSubArray(nums3);
        System.out.println("Maximum Subarray Test: " + result3);
        
        // Test Product of Array Except Self
        int[] nums4 = {1, 2, 3, 4};
        int[] result4 = productExceptSelf(nums4);
        System.out.println("Product Except Self Test: " + Arrays.toString(result4));
        
        // Test Valid Anagram
        boolean result5 = isAnagram("anagram", "nagaram");
        System.out.println("Valid Anagram Test: " + result5);
        
        System.out.println("All tests completed!");
    }
}
