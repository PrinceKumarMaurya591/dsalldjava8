package com.dsa.dp;

import java.util.Arrays;

// Problem: Race Car
// Link: https://leetcode.com/problems/race-car/
//
// Your car starts at position 0 and speed +1 on an infinite number line.
// The car can accelerate (A): position += speed, speed *= 2
// or reverse (R): speed = (speed > 0 ? -1 : 1)
// Return the minimum number of instructions needed to reach the target position.
//
// Approach: DP with memoization
// For a given target, the optimal strategy involves:
// 1. Drive past the target, then come back.
// 2. Or drive partway, reverse, drive a bit, reverse again, then reach target.
//
// Time Complexity: O(target * log target)
// Space Complexity: O(target)

public class RaceCar {

    public static void main(String[] args) {
        System.out.println("Race Car (target=3): " + racecar(3));  // Expected: 2 (AARA)
        System.out.println("Race Car (target=6): " + racecar(6));  // Expected: 5
    }

    public static int racecar(int target) {
        int[] dp = new int[target + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int t = 1; t <= target; t++) {
            // Try going past the target
            for (int k = 1; (1 << k) - 1 < 2 * t; k++) {
                int forwardDist = (1 << k) - 1; // Distance covered by k consecutive As

                if (forwardDist == t) {
                    dp[t] = k; // Exactly reached with k As
                } else if (forwardDist > t) {
                    // Went past target, need to come back
                    // k As + 1 R + racecar(forwardDist - t)
                    dp[t] = Math.min(dp[t], k + 1 + dp[forwardDist - t]);
                } else {
                    // Didn't reach target, need to reverse and go back a bit
                    for (int m = 0; m < k; m++) {
                        int backDist = (1 << m) - 1; // Distance covered by m As after reversing
                        // k As + 1 R + m As + 1 R + racecar(t - forwardDist + backDist)
                        dp[t] = Math.min(dp[t], k + 1 + m + 1 + dp[t - forwardDist + backDist]);
                    }
                }
            }
        }

        return dp[target];
    }
}
