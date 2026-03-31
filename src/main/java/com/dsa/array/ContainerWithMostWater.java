package com.dsa.array;

/**
 * Problem 13: Container With Most Water
 * 
 * Problem Statement:
 * Given n non-negative integers representing an elevation map where
 * each bar has width 1, find the maximum amount of water that can be trapped.
 * 
 * Assumptions:
 * - Each bar has width 1
 * - n >= 2
 * - Heights are non-negative integers
 * - Water can be trapped between two bars
 * 
 * Optimal Solution: O(n) time, O(1) space using two pointers
 * 
 * Algorithm Explanation:
 * 1. Initialize two pointers: left at start, right at end
 * 2. Initialize maxArea = 0
 * 3. While left < right:
 *    - Calculate current area = min(height[left], height[right]) * (right - left)
 *    - Update maxArea = max(maxArea, current area)
 *    - Move the pointer with smaller height:
 *      - If height[left] < height[right]: move left pointer right
 *      - Otherwise: move right pointer left
 * 4. Return maxArea
 * 
 * Why moving the smaller height pointer works:
 * - The area is limited by the smaller height
 * - Moving the larger height pointer won't increase the area (height won't increase, width decreases)
 * - Moving the smaller height pointer might find a taller bar, potentially increasing area
 * 
 * Dry Run Example:
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
public class ContainerWithMostWater {
    
    /**
     * Calculates the maximum amount of water that can be trapped
     * 
     * @param height the array of bar heights
     * @return the maximum area of water that can be trapped
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
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Container With Most Water Problem:");
        
        // Test 1
        int[] height1 = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        
        System.out.println("\nTest 1:");
        System.out.println("Input: [1, 8, 6, 2, 5, 4, 8, 3, 7]");
        int result1 = maxArea(height1);
        System.out.println("Output: " + result1);
        System.out.println("Expected: 49");
        System.out.println("Explanation: The vertical lines are at positions 1 and 8 (0-indexed)");
        System.out.println("             with heights 8 and 7, width = 7, area = min(8,7)*7 = 49");
        
        // Test 2
        int[] height2 = {1, 1};
        
        System.out.println("\nTest 2:");
        System.out.println("Input: [1, 1]");
        int result2 = maxArea(height2);
        System.out.println("Output: " + result2);
        System.out.println("Expected: 1");
        System.out.println("Explanation: Two bars of height 1, width = 1, area = 1*1 = 1");
        
        // Test 3
        int[] height3 = {4, 3, 2, 1, 4};
        
        System.out.println("\nTest 3:");
        System.out.println("Input: [4, 3, 2, 1, 4]");
        int result3 = maxArea(height3);
        System.out.println("Output: " + result3);
        System.out.println("Expected: 16");
        System.out.println("Explanation: First and last bars (height 4), width = 4, area = 4*4 = 16");
        
        // Test 4
        int[] height4 = {1, 2, 1};
        
        System.out.println("\nTest 4:");
        System.out.println("Input: [1, 2, 1]");
        int result4 = maxArea(height4);
        System.out.println("Output: " + result4);
        System.out.println("Expected: 2");
        System.out.println("Explanation: Bars at positions 0 and 2 (height 1), width = 2, area = 1*2 = 2");
    }
}