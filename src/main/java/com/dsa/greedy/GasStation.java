package com.dsa.greedy;

// Problem: Gas Station
// Link: https://leetcode.com/problems/gas-station/
//
// There are n gas stations along a circular route. You are given two integer
// arrays gas and cost where:
// - gas[i] is the amount of gas at station i
// - cost[i] is the amount of gas needed to travel from station i to i+1
//
// You begin the journey with an empty tank at one of the gas stations.
// Return the starting gas station's index if you can travel around the circuit
// once in the clockwise direction, otherwise return -1.
//
// Approach: Greedy
// - If total gas < total cost, impossible -> return -1
// - Track current tank balance. If balance < 0 at any station, reset start to
//   next station and reset balance to 0
// - The key insight: if you can't reach station j from i, you can't reach it
//   from any station between i and j either
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class GasStation {

    public static void main(String[] args) {
        System.out.println("=== Gas Station ===");
        int[] gas1 = {1, 2, 3, 4, 5};
        int[] cost1 = {3, 4, 5, 1, 2};
        System.out.println("Start station: " + canCompleteCircuit(gas1, cost1));
        // Expected: 3

        int[] gas2 = {2, 3, 4};
        int[] cost2 = {3, 4, 3};
        System.out.println("Start station: " + canCompleteCircuit(gas2, cost2));
        // Expected: -1

        int[] gas3 = {5, 1, 2, 3, 4};
        int[] cost3 = {4, 4, 1, 5, 1};
        System.out.println("Start station: " + canCompleteCircuit(gas3, cost3));
        // Expected: 4
    }

    public static int canCompleteCircuit(int[] gas, int[] cost) {
        int totalGas = 0;
        int totalCost = 0;
        int currentTank = 0;
        int startStation = 0;

        for (int i = 0; i < gas.length; i++) {
            totalGas += gas[i];
            totalCost += cost[i];
            currentTank += gas[i] - cost[i];

            // If we can't reach the next station from current start
            if (currentTank < 0) {
                startStation = i + 1; // Try next station as start
                currentTank = 0; // Reset tank
            }
        }

        // If total gas < total cost, impossible
        return totalGas >= totalCost ? startStation : -1;
    }
}
