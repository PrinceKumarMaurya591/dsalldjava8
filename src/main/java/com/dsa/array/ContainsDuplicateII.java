package com.dsa.array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Problem 4: Contains Duplicate II
 * 
 * Problem Statement:
 * Given an integer array nums and an integer k, return true if
 * there are two distinct indices i and j such that nums[i] == nums[j]
 * and abs(i - j) <= k.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * - 0 <= k <= 10^5
 * 
 * Optimal Solution: O(n) time, O(min(n, k)) space using sliding window with HashSet
 * 
 * Algorithm Explanation (Sliding Window):
 * 1. Create a HashSet to maintain a window of size k
 * 2. Iterate through the array with index i
 * 3. For each element:
 *    a. If i > k, remove the element that's out of window range (nums[i - k - 1])
 *    b. Check if current element exists in the window
 *    c. If yes, return true (duplicate within k distance found)
 *    d. If no, add current element to the window
 * 4. If loop completes, return false
 * 
 * Alternative Approach: HashMap
 * - Store number -> last seen index mapping
 * - When encountering a number, check if it was seen within k distance
 * 
 * Dry Run Example:
 * Input: nums = [1, 2, 3, 1], k = 3
 * 
 * i = 0: window = {} → add 1, window = {1}
 * i = 1: window = {1} → add 2, window = {1, 2}
 * i = 2: window = {1, 2} → add 3, window = {1, 2, 3}
 * i = 3: window = {1, 2, 3} (i=3 > k=3? no removal)
 *        window contains 1 → return true
 * 
 * Result: true
 */
public class ContainsDuplicateII {
    
    /**
     * Checks for duplicates within k distance using sliding window
     * 
     * @param nums the input array of integers
     * @param k the maximum distance between duplicates
     * @return true if duplicate exists within k distance, false otherwise
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
     * Alternative solution using HashMap
     * Time: O(n), Space: O(n)
     */
    public static boolean containsNearbyDuplicateHashMap(int[] nums, int k) {
        Map<Integer, Integer> indexMap = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            
            // Check if we've seen this number before
            if (indexMap.containsKey(num)) {
                int lastIndex = indexMap.get(num);
                // Check if within k distance
                if (i - lastIndex <= k) {
                    return true;
                }
            }
            
            // Update the last seen index
            indexMap.put(num, i);
        }
        
        return false;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] nums1 = {1, 2, 3, 1};
        int k1 = 3;
        
        int[] nums2 = {1, 0, 1, 1};
        int k2 = 1;
        
        int[] nums3 = {1, 2, 3, 1, 2, 3};
        int k3 = 2;
        
        System.out.println("Contains Duplicate II Problem:");
        
        // Test case 1: Duplicate within k distance
        System.out.println("\nTest 1 - Duplicate within k distance:");
        System.out.println("Input: nums = [1, 2, 3, 1], k = 3");
        System.out.println("Sliding Window: " + containsNearbyDuplicate(nums1, k1));
        System.out.println("HashMap: " + containsNearbyDuplicateHashMap(nums1, k1));
        System.out.println("Expected: true");
        
        // Test case 2: Duplicate with k=1
        System.out.println("\nTest 2 - Duplicate with k=1:");
        System.out.println("Input: nums = [1, 0, 1, 1], k = 1");
        System.out.println("Sliding Window: " + containsNearbyDuplicate(nums2, k2));
        System.out.println("HashMap: " + containsNearbyDuplicateHashMap(nums2, k2));
        System.out.println("Expected: true");
        
        // Test case 3: No duplicate within k distance
        System.out.println("\nTest 3 - No duplicate within k distance:");
        System.out.println("Input: nums = [1, 2, 3, 1, 2, 3], k = 2");
        System.out.println("Sliding Window: " + containsNearbyDuplicate(nums3, k3));
        System.out.println("HashMap: " + containsNearbyDuplicateHashMap(nums3, k3));
        System.out.println("Expected: false");
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. Sliding Window: O(n) time, O(min(n, k)) space");
        System.out.println("2. HashMap: O(n) time, O(n) space");
        System.out.println("Note: Sliding window is more space efficient when k is small");
    }
}