package com.dsa.array;

/**
 * Problem 18: Trapping Rain Water
 * 
 * Problem Statement:
 * Given n non-negative integers representing an elevation map where
 * each bar has width 1, compute how much water it can trap after raining.
 * 
 * Assumptions:
 * - Each bar has width 1
 * - n >= 0
 * - Heights are non-negative integers
 * - Water can be trapped between bars
 * 
 * Optimal Solution: O(n) time, O(1) space using two pointers
 * 
 * Algorithm Explanation:
 * 1. Initialize two pointers: left at start, right at end
 * 2. Initialize leftMax = 0, rightMax = 0, water = 0
 * 3. While left < right:
 *    - If height[left] < height[right]:
 *      - If height[left] >= leftMax: update leftMax
 *      - Else: water += leftMax - height[left]
 *      - Move left pointer right
 *    - Else:
 *      - If height[right] >= rightMax: update rightMax
 *      - Else: water += rightMax - height[right]
 *      - Move right pointer left
 * 4. Return water
 * 
 * Why this works:
 * - At each position, water trapped depends on min(leftMax, rightMax)
 * - We always process the side with smaller height
 * - This ensures we know the limiting height for water trapping
 * 
 * Dry Run Example:
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
public class TrappingRainWater {
    
    /**
     * Calculates the amount of trapped rain water
     * 
     * @param height the array of bar heights
     * @return the total amount of trapped water
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
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Trapping Rain Water Problem:");
        
        // Test 1
        int[] height1 = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        
        System.out.println("\nTest 1:");
        System.out.println("Input: [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]");
        int result1 = trap(height1);
        System.out.println("Output: " + result1);
        System.out.println("Expected: 6");
        
        // Test 2
        int[] height2 = {4, 2, 0, 3, 2, 5};
        
        System.out.println("\nTest 2:");
        System.out.println("Input: [4, 2, 0, 3, 2, 5]");
        int result2 = trap(height2);
        System.out.println("Output: " + result2);
        System.out.println("Expected: 9");
        
        // Test 3: No trapping possible
        int[] height3 = {1, 2, 3, 4, 5};
        
        System.out.println("\nTest 3:");
        System.out.println("Input: [1, 2, 3, 4, 5]");
        int result3 = trap(height3);
        System.out.println("Output: " + result3);
        System.out.println("Expected: 0");
        
        // Test 4: Empty array
        int[] height4 = {};
        
        System.out.println("\nTest 4:");
        System.out.println("Input: []");
        int result4 = trap(height4);
        System.out.println("Output: " + result4);
        System.out.println("Expected: 0");
        
        // Test 5: Single element
        int[] height5 = {5};
        
        System.out.println("\nTest 5:");
        System.out.println("Input: [5]");
        int result5 = trap(height5);
        System.out.println("Output: " + result5);
        System.out.println("Expected: 0");
    }
}