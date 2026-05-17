package com.dsa.linkedlist;

/**
 * Problem: Find the Duplicate Number
 * 
 * Given an array of integers nums containing n + 1 integers where each integer
 * is in the range [1, n] inclusive.
 * 
 * There is only one repeated number in nums, return this repeated number.
 * 
 * You must solve the problem without modifying the array nums and using only
 * constant extra space.
 * 
 * Example:
 * Input: nums = [1,3,4,2,2]
 * Output: 2
 * 
 * Input: nums = [3,1,3,4,2]
 * Output: 3
 * 
 * Approach: Floyd's Cycle Detection (Linked List Cycle in Array)
 * - Treat the array as a linked list where index i points to nums[i]
 * - Since there's a duplicate, there must be a cycle
 * - Use Floyd's algorithm to detect the cycle and find its entry point
 * 
 * Constraints:
 * - Cannot modify the array
 * - O(1) extra space
 * - O(n) time complexity
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class FindTheDuplicateNumber {

    /**
     * Uses Floyd's Cycle Detection algorithm.
     * Treat the array as a function f(i) = nums[i].
     * Since values are in [1, n] and there are n+1 elements,
     * there must be a cycle. The entry point of the cycle is the duplicate.
     */
    public static int findDuplicate(int[] nums) {
        // Phase 1: Find the intersection point of slow and fast pointers
        int slow = nums[0];
        int fast = nums[0];

        do {
            slow = nums[slow];       // Move one step
            fast = nums[nums[fast]]; // Move two steps
        } while (slow != fast);

        // Phase 2: Find the entrance to the cycle (the duplicate)
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }

        return slow;
    }

    /**
     * Alternative approach: Binary Search on value range.
     * Count numbers <= mid. If count > mid, duplicate is in [1, mid].
     * Otherwise, duplicate is in [mid+1, n].
     * 
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    public static int findDuplicateBinarySearch(int[] nums) {
        int left = 1;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = 0;

            // Count numbers <= mid
            for (int num : nums) {
                if (num <= mid) {
                    count++;
                }
            }

            // If count > mid, duplicate is in [left, mid]
            if (count > mid) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    /**
     * Alternative approach: Negative marking.
     * Mark visited numbers by negating the value at index nums[i].
     * If a value is already negative, it's the duplicate.
     * 
     * Note: This modifies the array, so it doesn't meet the "no modification" constraint.
     * 
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public static int findDuplicateNegativeMarking(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]);
            if (nums[index] < 0) {
                return index;
            }
            nums[index] = -nums[index];
        }
        return -1;
    }

    public static void main(String[] args) {
        // Test case 1: [1,3,4,2,2]
        int[] nums1 = {1, 3, 4, 2, 2};
        System.out.println("Test 1 - [1,3,4,2,2]:");
        System.out.println("  Floyd's Algorithm: " + findDuplicate(nums1));
        System.out.println("  Binary Search: " + findDuplicateBinarySearch(new int[]{1, 3, 4, 2, 2}));
        System.out.println("  Expected: 2");
        System.out.println();

        // Test case 2: [3,1,3,4,2]
        int[] nums2 = {3, 1, 3, 4, 2};
        System.out.println("Test 2 - [3,1,3,4,2]:");
        System.out.println("  Floyd's Algorithm: " + findDuplicate(nums2));
        System.out.println("  Binary Search: " + findDuplicateBinarySearch(new int[]{3, 1, 3, 4, 2}));
        System.out.println("  Expected: 3");
        System.out.println();

        // Test case 3: [1,1]
        int[] nums3 = {1, 1};
        System.out.println("Test 3 - [1,1]:");
        System.out.println("  Floyd's Algorithm: " + findDuplicate(nums3));
        System.out.println("  Binary Search: " + findDuplicateBinarySearch(new int[]{1, 1}));
        System.out.println("  Expected: 1");
        System.out.println();

        // Test case 4: [1,1,2]
        int[] nums4 = {1, 1, 2};
        System.out.println("Test 4 - [1,1,2]:");
        System.out.println("  Floyd's Algorithm: " + findDuplicate(nums4));
        System.out.println("  Binary Search: " + findDuplicateBinarySearch(new int[]{1, 1, 2}));
        System.out.println("  Expected: 1");
        System.out.println();

        // Test case 5: [2,2,2,2,2]
        int[] nums5 = {2, 2, 2, 2, 2};
        System.out.println("Test 5 - [2,2,2,2,2]:");
        System.out.println("  Floyd's Algorithm: " + findDuplicate(nums5));
        System.out.println("  Binary Search: " + findDuplicateBinarySearch(new int[]{2, 2, 2, 2, 2}));
        System.out.println("  Expected: 2");
        System.out.println();

        // Test case 6: Negative marking (modifies array)
        int[] nums6 = {1, 3, 4, 2, 2};
        System.out.println("Test 6 - Negative Marking:");
        System.out.println("  Result: " + findDuplicateNegativeMarking(nums6));
        System.out.println("  Expected: 2");
    }
}
