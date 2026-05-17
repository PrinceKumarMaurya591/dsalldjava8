package com.dsa.stack;

import java.util.*;

/**
 * Largest Rectangle in Histogram
 * 
 * Given an array of integers heights representing the histogram's bar height
 * where the width of each bar is 1, return the area of the largest rectangle
 * in the histogram.
 * 
 * Approach 1: Monotonic Stack (Optimal)
 * - Maintain a stack of indices with increasing heights
 * - When we find a bar shorter than the top of stack, we calculate area
 *   using the popped bar as the shortest bar
 * - Width = current index - stack.peek() - 1 (or just i if stack empty)
 * - Height = heights[popped]
 * 
 * Approach 2: Divide and Conquer
 * - Find the minimum height bar
 * - Max area is max of:
 *   - Area using min height * full width
 *   - Max area in left part
 *   - Max area in right part
 * 
 * Approach 3: Brute Force (for comparison)
 * - For each bar, expand left and right while height >= current bar
 * 
 * Time Complexity: O(n) for stack approach
 * Space Complexity: O(n)
 */
public class LargestRectangleInHistogram {

    // =============================================
    // Approach 1: Monotonic Stack (Optimal)
    // =============================================
    public static int largestRectangleArea(int[] heights) {
        int n = heights.length;
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;

        for (int i = 0; i <= n; i++) {
            int currentHeight = (i == n) ? 0 : heights[i];

            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }

            stack.push(i);
        }

        return maxArea;
    }

    // =============================================
    // Approach 2: Monotonic Stack with Arrays
    // =============================================
    public static int largestRectangleAreaArray(int[] heights) {
        int n = heights.length;
        int[] stack = new int[n + 1];
        int top = -1;
        int maxArea = 0;

        for (int i = 0; i <= n; i++) {
            int currentHeight = (i == n) ? 0 : heights[i];

            while (top >= 0 && currentHeight < heights[stack[top]]) {
                int height = heights[stack[top--]];
                int width = top < 0 ? i : i - stack[top] - 1;
                maxArea = Math.max(maxArea, height * width);
            }

            stack[++top] = i;
        }

        return maxArea;
    }

    // =============================================
    // Approach 3: Two-pass (Left/Right boundaries)
    // =============================================
    public static int largestRectangleAreaBoundaries(int[] heights) {
        int n = heights.length;
        if (n == 0) return 0;

        int[] leftBoundary = new int[n];
        int[] rightBoundary = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        // Find left boundary (nearest smaller to left)
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
                stack.pop();
            }
            leftBoundary[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }

        stack.clear();

        // Find right boundary (nearest smaller to right)
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[i]) {
                stack.pop();
            }
            rightBoundary[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        // Calculate max area
        int maxArea = 0;
        for (int i = 0; i < n; i++) {
            int width = rightBoundary[i] - leftBoundary[i] - 1;
            maxArea = Math.max(maxArea, heights[i] * width);
        }

        return maxArea;
    }

    // =============================================
    // Approach 4: Divide and Conquer
    // =============================================
    public static int largestRectangleAreaDC(int[] heights) {
        return divideAndConquer(heights, 0, heights.length - 1);
    }

    private static int divideAndConquer(int[] heights, int left, int right) {
        if (left > right) return 0;
        if (left == right) return heights[left];

        int minIndex = left;
        for (int i = left; i <= right; i++) {
            if (heights[i] < heights[minIndex]) {
                minIndex = i;
            }
        }

        int areaWithMin = heights[minIndex] * (right - left + 1);
        int areaLeft = divideAndConquer(heights, left, minIndex - 1);
        int areaRight = divideAndConquer(heights, minIndex + 1, right);

        return Math.max(areaWithMin, Math.max(areaLeft, areaRight));
    }

    // =============================================
    // Approach 5: Brute Force (for comparison)
    // =============================================
    public static int largestRectangleAreaBruteForce(int[] heights) {
        int maxArea = 0;
        int n = heights.length;

        for (int i = 0; i < n; i++) {
            int minHeight = heights[i];
            for (int j = i; j < n; j++) {
                minHeight = Math.min(minHeight, heights[j]);
                maxArea = Math.max(maxArea, minHeight * (j - i + 1));
            }
        }

        return maxArea;
    }

    /**
     * Extension: Maximal Rectangle (from LeetCode 85)
     * Given a 2D binary matrix filled with 0's and 1's, find the largest
     * rectangle containing only 1's and return its area.
     */
    public static int maximalRectangle(char[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        int[] heights = new int[cols];
        int maxArea = 0;

        for (int i = 0; i < rows; i++) {
            // Update heights
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == '1') {
                    heights[j]++;
                } else {
                    heights[j] = 0;
                }
            }
            // Calculate max area for current histogram
            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }

        return maxArea;
    }

    public static void main(String[] args) {
        System.out.println("Largest Rectangle in Histogram\n");

        // Test cases
        int[][] testCases = {
            {2, 1, 5, 6, 2, 3},  // Expected: 10
            {2, 4},               // Expected: 4
            {1, 2, 3, 4, 5},     // Expected: 9
            {5, 4, 3, 2, 1},     // Expected: 9
            {1},                  // Expected: 1
            {},                   // Expected: 0
            {1, 1, 1, 1},        // Expected: 4
            {2, 1, 2},           // Expected: 3
            {6, 7, 5, 2, 4, 5, 9, 3}, // Expected: 16
        };

        int[] expected = {10, 4, 9, 9, 1, 0, 4, 3, 16};

        System.out.println("--- Test Cases ---");
        for (int i = 0; i < testCases.length; i++) {
            int r1 = largestRectangleArea(testCases[i]);
            int r2 = largestRectangleAreaArray(testCases[i]);
            int r3 = largestRectangleAreaBoundaries(testCases[i]);
            int r4 = largestRectangleAreaDC(testCases[i]);
            int r5 = largestRectangleAreaBruteForce(testCases[i]);

            System.out.println("Heights: " + Arrays.toString(testCases[i]));
            System.out.println("  Stack:       " + r1 + " (expected: " + expected[i] + ")");
            System.out.println("  Array:       " + r2);
            System.out.println("  Boundaries:  " + r3);
            System.out.println("  D&C:         " + r4);
            System.out.println("  Brute Force: " + r5);
            System.out.println();
        }

        // Maximal Rectangle extension
        System.out.println("--- Maximal Rectangle (Extension) ---");
        char[][] matrix = {
            {'1', '0', '1', '0', '0'},
            {'1', '0', '1', '1', '1'},
            {'1', '1', '1', '1', '1'},
            {'1', '0', '0', '1', '0'}
        };
        System.out.println("Matrix:");
        for (char[] row : matrix) {
            System.out.println("  " + Arrays.toString(row));
        }
        System.out.println("Maximal Rectangle Area: " + maximalRectangle(matrix));
        System.out.println("Expected: 6");

        System.out.println();

        // Performance comparison
        System.out.println("--- Performance Comparison ---");
        Random rand = new Random(42);
        int[] largeHeights = new int[10000];
        for (int i = 0; i < largeHeights.length; i++) {
            largeHeights[i] = rand.nextInt(100);
        }

        long start = System.nanoTime();
        largestRectangleArea(largeHeights);
        System.out.println("Stack:       " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        largestRectangleAreaArray(largeHeights);
        System.out.println("Array:       " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        largestRectangleAreaBoundaries(largeHeights);
        System.out.println("Boundaries:  " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        largestRectangleAreaDC(largeHeights);
        System.out.println("D&C:         " + (System.nanoTime() - start) / 1_000_000.0 + " ms");
    }
}
