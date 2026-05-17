package com.dsa.graph;

// Problem: Find the Celebrity
// Link: https://leetcode.com/problems/find-the-celebrity/ (Premium)
//       https://www.lintcode.com/problem/645/
//
// Suppose you are at a party with n people labeled from 0 to n - 1. Among them,
// there may be one celebrity. The celebrity is defined as:
// - Everyone knows the celebrity.
// - The celebrity knows no one.
//
// You are given a helper function knows(a, b) which returns true if a knows b.
// Return the label of the celebrity. If there is no celebrity, return -1.
//
// Approach: Two-pass elimination
// 1. First pass: Find a candidate. If candidate knows i, then candidate can't be
//    celebrity (since celebrity knows no one), so set i as candidate.
// 2. Second pass: Verify the candidate by checking:
//    - Candidate knows no one (candidate knows i → false for all i)
//    - Everyone knows candidate (i knows candidate → true for all i != candidate)
//
// Time Complexity: O(n)
// Space Complexity: O(1)

public class FindTheCelebrity {

    // Mock knows function for testing
    // In the actual problem, this would be provided by the API
    private static int[][] adjacency;

    private static boolean knows(int a, int b) {
        return adjacency[a][b] == 1;
    }

    public static void main(String[] args) {
        // Test case 1: Celebrity is 1
        // 0 knows 1, 2 knows 1, 1 knows no one
        adjacency = new int[][]{
            {0, 1, 0},
            {0, 0, 0},
            {0, 1, 0}
        };
        System.out.println("Celebrity: " + findCelebrity(3));
        // Expected: 1

        // Test case 2: No celebrity (0 knows 1, 1 knows 0)
        adjacency = new int[][]{
            {0, 1, 0},
            {1, 0, 0},
            {0, 1, 0}
        };
        System.out.println("Celebrity: " + findCelebrity(3));
        // Expected: -1
    }

    public static int findCelebrity(int n) {
        // First pass: find candidate
        int candidate = 0;
        for (int i = 1; i < n; i++) {
            if (knows(candidate, i)) {
                // Candidate knows i, so candidate can't be celebrity
                candidate = i;
            }
        }

        // Second pass: verify candidate
        for (int i = 0; i < n; i++) {
            if (i == candidate) continue;

            // Candidate should not know anyone
            if (knows(candidate, i)) return -1;

            // Everyone should know candidate
            if (!knows(i, candidate)) return -1;
        }

        return candidate;
    }
}
