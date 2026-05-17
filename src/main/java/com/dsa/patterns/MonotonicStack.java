package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Monotonic Stack
 * 
 * Used when: Problems involving next greater/smaller element, 
 * largest rectangle in histogram, daily temperatures, stock span,
 * trapping rain water, sliding window maximum.
 * 
 * Core idea: Maintain a stack that is either strictly increasing or decreasing.
 * When a new element breaks the monotonic property, pop elements and process them.
 * 
 * Key variations:
 * 1. Next Greater Element (NGE)
 * 2. Next Smaller Element
 * 3. Previous Greater Element
 * 4. Daily Temperatures (next warmer day)
 * 5. Largest Rectangle in Histogram
 * 6. Maximal Rectangle
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class MonotonicStack {

    /**
     * Problem: Next Greater Element I
     * Find next greater element for each element in nums1 from nums2.
     * 
     * Approach: Monotonic decreasing stack + HashMap.
     * Time: O(m + n), Space: O(n)
     */
    public static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();

        for (int num : nums2) {
            while (!stack.isEmpty() && stack.peek() < num) {
                map.put(stack.pop(), num);
            }
            stack.push(num);
        }

        // Elements left in stack have no next greater element
        while (!stack.isEmpty()) {
            map.put(stack.pop(), -1);
        }

        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = map.get(nums1[i]);
        }

        return result;
    }

    /**
     * Problem: Next Greater Element II (Circular Array)
     * Find next greater element for each element in a circular array.
     * 
     * Approach: Traverse twice using modulo, monotonic decreasing stack.
     * Time: O(n), Space: O(n)
     */
    public static int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Deque<Integer> stack = new ArrayDeque<>(); // Store indices

        // Traverse twice to handle circular array
        for (int i = 0; i < 2 * n; i++) {
            int idx = i % n;
            while (!stack.isEmpty() && nums[stack.peek()] < nums[idx]) {
                result[stack.pop()] = nums[idx];
            }
            if (i < n) {
                stack.push(idx);
            }
        }

        return result;
    }

    /**
     * Problem: Daily Temperatures
     * Find number of days until a warmer temperature.
     * 
     * Approach: Monotonic decreasing stack of indices.
     * Time: O(n), Space: O(n)
     */
    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // Store indices

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
                int prevIdx = stack.pop();
                result[prevIdx] = i - prevIdx;
            }
            stack.push(i);
        }

        return result;
    }

    /**
     * Problem: Largest Rectangle in Histogram
     * Find largest rectangle that can be formed in a histogram.
     * 
     * Approach: Monotonic increasing stack of indices.
     * When a smaller bar is found, pop and calculate area with popped bar as height.
     * Time: O(n), Space: O(n)
     */
    public static int largestRectangleArea(int[] heights) {
        int n = heights.length;
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;

        for (int i = 0; i <= n; i++) {
            int currentHeight = (i == n) ? 0 : heights[i];

            while (!stack.isEmpty() && heights[stack.peek()] > currentHeight) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }

            stack.push(i);
        }

        return maxArea;
    }

    /**
     * Problem: Maximal Rectangle
     * Find largest rectangle containing only 1's in a binary matrix.
     * 
     * Approach: Treat each row as base of histogram, compute largest rectangle.
     * Time: O(m * n), Space: O(n)
     */
    public static int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0) return 0;

        int m = matrix.length, n = matrix[0].length;
        int[] heights = new int[n];
        int maxArea = 0;

        for (int i = 0; i < m; i++) {
            // Update heights for current row
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == '1') {
                    heights[j]++;
                } else {
                    heights[j] = 0;
                }
            }

            // Calculate largest rectangle in histogram for current heights
            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }

        return maxArea;
    }

    /**
     * Problem: Stock Span
     * Find span of stock price for each day (consecutive days price <= today).
     * 
     * Approach: Monotonic decreasing stack of pairs (price, span).
     * Time: O(n), Space: O(n)
     */
    public static int[] stockSpan(int[] prices) {
        int n = prices.length;
        int[] span = new int[n];
        Deque<int[]> stack = new ArrayDeque<>(); // [price, span]

        for (int i = 0; i < n; i++) {
            int currentSpan = 1;

            while (!stack.isEmpty() && stack.peek()[0] <= prices[i]) {
                currentSpan += stack.pop()[1];
            }

            stack.push(new int[]{prices[i], currentSpan});
            span[i] = currentSpan;
        }

        return span;
    }

    /**
     * Problem: Sum of Subarray Minimums
     * Find sum of minimum values of all subarrays.
     * 
     * Approach: For each element, find how many subarrays it's the minimum of.
     * Use monotonic stack to find previous smaller and next smaller.
     * Time: O(n), Space: O(n)
     */
    public static int sumSubarrayMins(int[] arr) {
        int n = arr.length;
        int MOD = 1_000_000_007;
        long sum = 0;

        int[] prevSmaller = new int[n];
        int[] nextSmaller = new int[n];

        // Find previous smaller element index
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
                stack.pop();
            }
            prevSmaller[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }

        // Find next smaller element index
        stack.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
                stack.pop();
            }
            nextSmaller[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        // Calculate sum: arr[i] * (i - prevSmaller) * (nextSmaller - i)
        for (int i = 0; i < n; i++) {
            long leftCount = i - prevSmaller[i];
            long rightCount = nextSmaller[i] - i;
            sum = (sum + (long) arr[i] * leftCount * rightCount) % MOD;
        }

        return (int) sum;
    }

    /**
     * Problem: Remove Duplicate Letters
     * Remove duplicate letters to get smallest lexicographical subsequence.
     * 
     * Approach: Monotonic increasing stack with frequency tracking.
     * Time: O(n), Space: O(1)
     */
    public static String removeDuplicateLetters(String s) {
        int[] freq = new int[26];
        boolean[] inStack = new boolean[26];
        Deque<Character> stack = new ArrayDeque<>();

        // Count frequencies
        for (char c : s.toCharArray()) {
            freq[c - 'a']++;
        }

        for (char c : s.toCharArray()) {
            freq[c - 'a']--;

            if (inStack[c - 'a']) continue;

            // Pop larger characters that still appear later
            while (!stack.isEmpty() && stack.peek() > c && freq[stack.peek() - 'a'] > 0) {
                inStack[stack.pop() - 'a'] = false;
            }

            stack.push(c);
            inStack[c - 'a'] = true;
        }

        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(stack.pollLast());
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== MONOTONIC STACK PATTERN ===");
        System.out.println();

        // 1. Next Greater Element I
        System.out.println("1. Next Greater Element I:");
        int[] nums1 = {4, 1, 2};
        int[] nums2 = {1, 3, 4, 2};
        System.out.println("   nums1=[4,1,2], nums2=[1,3,4,2]");
        System.out.println("   Output: " + Arrays.toString(nextGreaterElement(nums1, nums2)) + " (expected: [-1,3,-1])");
        System.out.println();

        // 2. Next Greater Element II (Circular)
        System.out.println("2. Next Greater Element II:");
        int[] nums3 = {1, 2, 1};
        System.out.println("   Input: [1,2,1]");
        System.out.println("   Output: " + Arrays.toString(nextGreaterElements(nums3)) + " (expected: [2,-1,2])");
        System.out.println();

        // 3. Daily Temperatures
        System.out.println("3. Daily Temperatures:");
        int[] temps = {73, 74, 75, 71, 69, 72, 76, 73};
        System.out.println("   Input: [73,74,75,71,69,72,76,73]");
        System.out.println("   Output: " + Arrays.toString(dailyTemperatures(temps)) + " (expected: [1,1,4,2,1,1,0,0])");
        System.out.println();

        // 4. Largest Rectangle in Histogram
        System.out.println("4. Largest Rectangle in Histogram:");
        int[] heights = {2, 1, 5, 6, 2, 3};
        System.out.println("   Input: [2,1,5,6,2,3]");
        System.out.println("   Output: " + largestRectangleArea(heights) + " (expected: 10)");
        System.out.println();

        // 5. Maximal Rectangle
        System.out.println("5. Maximal Rectangle:");
        char[][] matrix = {
            {'1', '0', '1', '0', '0'},
            {'1', '0', '1', '1', '1'},
            {'1', '1', '1', '1', '1'},
            {'1', '0', '0', '1', '0'}
        };
        System.out.println("   Matrix: [[1,0,1,0,0],[1,0,1,1,1],[1,1,1,1,1],[1,0,0,1,0]]");
        System.out.println("   Output: " + maximalRectangle(matrix) + " (expected: 6)");
        System.out.println();

        // 6. Stock Span
        System.out.println("6. Stock Span:");
        int[] prices = {100, 80, 60, 70, 60, 75, 85};
        System.out.println("   Input: [100,80,60,70,60,75,85]");
        System.out.println("   Output: " + Arrays.toString(stockSpan(prices)) + " (expected: [1,1,1,2,1,4,6])");
        System.out.println();

        // 7. Sum of Subarray Minimums
        System.out.println("7. Sum of Subarray Minimums:");
        int[] arr = {3, 1, 2, 4};
        System.out.println("   Input: [3,1,2,4]");
        System.out.println("   Output: " + sumSubarrayMins(arr) + " (expected: 17)");
        System.out.println();

        // 8. Remove Duplicate Letters
        System.out.println("8. Remove Duplicate Letters:");
        System.out.println("   Input: \"bcabc\"");
        System.out.println("   Output: \"" + removeDuplicateLetters("bcabc") + "\" (expected: \"abc\")");
        System.out.println("   Input: \"cbacdcbc\"");
        System.out.println("   Output: \"" + removeDuplicateLetters("cbacdcbc") + "\" (expected: \"acdb\")");
    }
}
