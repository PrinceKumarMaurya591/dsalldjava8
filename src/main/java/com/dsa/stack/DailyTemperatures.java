package com.dsa.stack;

import java.util.*;

/**
 * Daily Temperatures
 * 
 * Given an array of integers temperatures representing daily temperatures,
 * return an array answer such that answer[i] is the number of days you have
 * to wait after the ith day to get a warmer temperature. If there is no
 * future day with a warmer temperature, answer[i] = 0.
 * 
 * Approach: Monotonic Decreasing Stack
 * - Iterate through temperatures
 * - Maintain a stack of indices with decreasing temperatures
 * - When we find a warmer temperature, pop from stack and calculate days
 * 
 * Time Complexity: O(n) - each element pushed/popped at most once
 * Space Complexity: O(n) - for the stack
 */
public class DailyTemperatures {

    /**
     * Find number of days until warmer temperature using monotonic stack
     */
    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // stores indices

        for (int i = 0; i < n; i++) {
            // While current temp is warmer than temp at top of stack
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prevIndex = stack.pop();
                result[prevIndex] = i - prevIndex;
            }
            stack.push(i);
        }
        // Remaining indices in stack have no warmer day -> result stays 0

        return result;
    }

    /**
     * Alternative: Array-based stack for better performance
     */
    public static int[] dailyTemperaturesArray(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        int[] stack = new int[n]; // array as stack
        int top = -1;

        for (int i = 0; i < n; i++) {
            while (top >= 0 && temperatures[i] > temperatures[stack[top]]) {
                int prevIndex = stack[top--];
                result[prevIndex] = i - prevIndex;
            }
            stack[++top] = i;
        }

        return result;
    }

    /**
     * Brute Force (for comparison)
     * Time: O(n²), Space: O(1)
     */
    public static int[] dailyTemperaturesBruteForce(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (temperatures[j] > temperatures[i]) {
                    result[i] = j - i;
                    break;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println("Daily Temperatures\n");

        // Test Case 1
        int[] temps1 = {73, 74, 75, 71, 69, 72, 76, 73};
        System.out.println("Temperatures: " + Arrays.toString(temps1));
        System.out.println("Result (Stack): " + Arrays.toString(dailyTemperatures(temps1)));
        System.out.println("Result (Array): " + Arrays.toString(dailyTemperaturesArray(temps1)));
        System.out.println("Expected:       [1, 1, 4, 2, 1, 1, 0, 0]\n");

        // Test Case 2
        int[] temps2 = {30, 40, 50, 60};
        System.out.println("Temperatures: " + Arrays.toString(temps2));
        System.out.println("Result: " + Arrays.toString(dailyTemperatures(temps2)));
        System.out.println("Expected: [1, 1, 1, 0]\n");

        // Test Case 3
        int[] temps3 = {30, 60, 90};
        System.out.println("Temperatures: " + Arrays.toString(temps3));
        System.out.println("Result: " + Arrays.toString(dailyTemperatures(temps3)));
        System.out.println("Expected: [1, 1, 0]\n");

        // Test Case 4 - Decreasing temperatures
        int[] temps4 = {90, 80, 70, 60};
        System.out.println("Temperatures: " + Arrays.toString(temps4));
        System.out.println("Result: " + Arrays.toString(dailyTemperatures(temps4)));
        System.out.println("Expected: [0, 0, 0, 0]\n");

        // Test Case 5 - Single day
        int[] temps5 = {75};
        System.out.println("Temperatures: " + Arrays.toString(temps5));
        System.out.println("Result: " + Arrays.toString(dailyTemperatures(temps5)));
        System.out.println("Expected: [0]\n");

        // Performance comparison
        System.out.println("--- Performance Comparison ---");
        int[] largeTemps = new int[10000];
        Random rand = new Random(42);
        for (int i = 0; i < largeTemps.length; i++) {
            largeTemps[i] = 30 + rand.nextInt(40);
        }

        long start = System.nanoTime();
        dailyTemperatures(largeTemps);
        long stackTime = System.nanoTime() - start;

        start = System.nanoTime();
        dailyTemperaturesArray(largeTemps);
        long arrayTime = System.nanoTime() - start;

        start = System.nanoTime();
        dailyTemperaturesBruteForce(largeTemps);
        long bruteTime = System.nanoTime() - start;

        System.out.println("Stack (Deque):  " + stackTime / 1_000_000.0 + " ms");
        System.out.println("Stack (Array):  " + arrayTime / 1_000_000.0 + " ms");
        System.out.println("Brute Force:    " + bruteTime / 1_000_000.0 + " ms");
    }
}
