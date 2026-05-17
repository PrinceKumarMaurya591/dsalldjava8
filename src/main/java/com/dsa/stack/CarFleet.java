package com.dsa.stack;

import java.util.*;

/**
 * Car Fleet
 * 
 * There are n cars going to the same destination along a one-lane road.
 * The destination is 'target' miles away.
 * 
 * You are given two integer arrays:
 * - position[i]: position of the ith car
 * - speed[i]: speed of the ith car (miles per hour)
 * 
 * A car can never pass another car ahead of it, but it can catch up to it
 * and drive bumper to bumper at the same speed. The faster car will slow
 * down to match the slower car's speed. A car fleet is a non-empty set of
 * cars driving at the same position and same speed.
 * 
 * A single car is also a car fleet.
 * 
 * Return the number of car fleets that will arrive at the destination.
 * 
 * Approach: Monotonic Stack
 * 1. Create pairs of (position, speed) and sort by position (descending)
 * 2. Calculate time to reach destination for each car: (target - pos) / speed
 * 3. Use a stack to track fleets:
 *    - If a car behind takes less time than the car ahead, it will catch up
 *    - If it takes more time, it forms a new fleet
 * 
 * Time Complexity: O(n log n) - sorting
 * Space Complexity: O(n) - for the stack
 */
public class CarFleet {

    /**
     * Calculate number of car fleets using stack
     */
    public static int carFleet(int target, int[] position, int[] speed) {
        int n = position.length;
        if (n == 0) return 0;

        // Create array of car indices and sort by position (descending)
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (a, b) -> position[b] - position[a]);

        // Stack to store times of fleets
        Deque<Double> stack = new ArrayDeque<>();

        for (int idx : indices) {
            double time = (double) (target - position[idx]) / speed[idx];
            
            // If current car takes longer than the fleet ahead, it forms a new fleet
            if (stack.isEmpty() || time > stack.peek()) {
                stack.push(time);
            }
            // If time <= stack.peek(), it catches up to the fleet ahead
        }

        return stack.size();
    }

    /**
     * Alternative: Without stack, just count fleets
     */
    public static int carFleetCount(int target, int[] position, int[] speed) {
        int n = position.length;
        if (n == 0) return 0;

        // Create cars array and sort by position
        int[][] cars = new int[n][2];
        for (int i = 0; i < n; i++) {
            cars[i][0] = position[i];
            cars[i][1] = speed[i];
        }
        Arrays.sort(cars, (a, b) -> b[0] - a[0]); // Sort by position descending

        int fleets = 0;
        double maxTime = 0;

        for (int[] car : cars) {
            double time = (double) (target - car[0]) / car[1];
            if (time > maxTime) {
                maxTime = time;
                fleets++;
            }
        }

        return fleets;
    }

    /**
     * Alternative: Using array as stack
     */
    public static int carFleetArray(int target, int[] position, int[] speed) {
        int n = position.length;
        if (n == 0) return 0;

        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (a, b) -> position[b] - position[a]);

        double[] stack = new double[n];
        int top = -1;

        for (int idx : indices) {
            double time = (double) (target - position[idx]) / speed[idx];
            if (top == -1 || time > stack[top]) {
                stack[++top] = time;
            }
        }

        return top + 1;
    }

    public static void main(String[] args) {
        System.out.println("Car Fleet\n");

        // Test Case 1
        int target1 = 12;
        int[] pos1 = {10, 8, 0, 5, 3};
        int[] speed1 = {2, 4, 1, 1, 3};
        System.out.println("Target: " + target1);
        System.out.println("Positions: " + Arrays.toString(pos1));
        System.out.println("Speeds: " + Arrays.toString(speed1));
        System.out.println("Fleets: " + carFleet(target1, pos1, speed1));
        System.out.println("Expected: 3\n");
        // Explanation:
        // Car 0 (pos=10, speed=2): time = (12-10)/2 = 1.0
        // Car 1 (pos=8, speed=4):  time = (12-8)/4 = 1.0 -> catches car 0
        // Car 2 (pos=0, speed=1):  time = (12-0)/1 = 12.0
        // Car 3 (pos=5, speed=1):  time = (12-5)/1 = 7.0
        // Car 4 (pos=3, speed=3):  time = (12-3)/3 = 3.0 -> catches car 3
        // Fleets: [car0,car1], [car4,car3], [car2] = 3

        // Test Case 2
        int target2 = 10;
        int[] pos2 = {3};
        int[] speed2 = {3};
        System.out.println("Target: " + target2);
        System.out.println("Positions: " + Arrays.toString(pos2));
        System.out.println("Speeds: " + Arrays.toString(speed2));
        System.out.println("Fleets: " + carFleet(target2, pos2, speed2));
        System.out.println("Expected: 1\n");

        // Test Case 3
        int target3 = 100;
        int[] pos3 = {0, 2, 4};
        int[] speed3 = {4, 2, 1};
        System.out.println("Target: " + target3);
        System.out.println("Positions: " + Arrays.toString(pos3));
        System.out.println("Speeds: " + Arrays.toString(speed3));
        System.out.println("Fleets: " + carFleet(target3, pos3, speed3));
        System.out.println("Expected: 1\n");
        // All cars catch up to the slowest car ahead

        // Test Case 4 - No catching up
        int target4 = 10;
        int[] pos4 = {0, 1, 2};
        int[] speed4 = {1, 2, 3};
        System.out.println("Target: " + target4);
        System.out.println("Positions: " + Arrays.toString(pos4));
        System.out.println("Speeds: " + Arrays.toString(speed4));
        System.out.println("Fleets: " + carFleet(target4, pos4, speed4));
        System.out.println("Expected: 3\n");
        // Each car is slower than the one ahead, so no catching up

        // Test Case 5 - Empty
        int target5 = 10;
        int[] pos5 = {};
        int[] speed5 = {};
        System.out.println("Target: " + target5);
        System.out.println("Positions: " + Arrays.toString(pos5));
        System.out.println("Speeds: " + Arrays.toString(speed5));
        System.out.println("Fleets: " + carFleet(target5, pos5, speed5));
        System.out.println("Expected: 0\n");

        // Test Case 6 - All same speed
        int target6 = 20;
        int[] pos6 = {5, 10, 15};
        int[] speed6 = {2, 2, 2};
        System.out.println("Target: " + target6);
        System.out.println("Positions: " + Arrays.toString(pos6));
        System.out.println("Speeds: " + Arrays.toString(speed6));
        System.out.println("Fleets: " + carFleet(target6, pos6, speed6));
        System.out.println("Expected: 3\n");
        // Same speed, different positions - no catching up

        // Test Case 7 - Complex scenario
        int target7 = 12;
        int[] pos7 = {10, 8, 6, 4, 2, 0};
        int[] speed7 = {1, 2, 3, 4, 5, 6};
        System.out.println("Target: " + target7);
        System.out.println("Positions: " + Arrays.toString(pos7));
        System.out.println("Speeds: " + Arrays.toString(speed7));
        System.out.println("Fleets: " + carFleet(target7, pos7, speed7));
        System.out.println("Expected: 1 (all arrive at same time: 2.0)\n");

        // Verify all approaches match
        System.out.println("--- Verification ---");
        int[][] testPositions = {pos1, pos2, pos3, pos4, pos5, pos6, pos7};
        int[][] testSpeeds = {speed1, speed2, speed3, speed4, speed5, speed6, speed7};
        int[] testTargets = {target1, target2, target3, target4, target5, target6, target7};

        for (int i = 0; i < testPositions.length; i++) {
            int r1 = carFleet(testTargets[i], testPositions[i], testSpeeds[i]);
            int r2 = carFleetCount(testTargets[i], testPositions[i], testSpeeds[i]);
            int r3 = carFleetArray(testTargets[i], testPositions[i], testSpeeds[i]);
            System.out.println("Test " + (i + 1) + ": " + r1 + " | " + r2 + " | " + r3 + 
                             " (all match: " + (r1 == r2 && r2 == r3) + ")");
        }
    }
}
