package com.dsa.dp;

import java.util.Arrays;

// Problem: Coin Change
// Link: https://leetcode.com/problems/coin-change/
//
// You are given an integer array coins representing coins of different denominations
// and an integer amount representing a total amount of money.
// Return the fewest number of coins that you need to make up that amount.
// If that amount cannot be made up by any combination of the coins, return -1.
//
// Approach: DP (bottom-up)
// dp[i] = minimum coins needed to make amount i
// dp[i] = min(dp[i], dp[i - coin] + 1) for each coin
//
// Time Complexity: O(amount * n) where n = number of coins
// Space Complexity: O(amount)

public class CoinChange {

    public static void main(String[] args) {
        int[] coins1 = {1, 2, 5};
        System.out.println("Coin Change: " + coinChange(coins1, 11)); // Expected: 3 (5+5+1)

        int[] coins2 = {2};
        System.out.println("Coin Change: " + coinChange(coins2, 3));  // Expected: -1

        int[] coins3 = {1};
        System.out.println("Coin Change: " + coinChange(coins3, 0));  // Expected: 0
    }

    public static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // Fill with a value larger than any possible answer
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }
}
