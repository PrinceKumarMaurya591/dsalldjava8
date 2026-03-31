package com.dsa.array;

/**
 * Problem 2: Best Time to Buy and Sell Stock
 * 
 * Problem Statement:
 * Given an array prices where prices[i] is the price on day i,
 * maximize profit by choosing a single day to buy and a different day in the
 * future to sell. Return maximum profit.
 * 
 * Constraints:
 * - You must buy before you sell
 * - If no profit can be made, return 0
 * 
 * Optimal Solution: O(n) time, O(1) space using single pass
 * 
 * Algorithm Explanation (One Pass):
 * 1. Initialize minPrice to Integer.MAX_VALUE and maxProfit to 0
 * 2. Iterate through each price in the array
 * 3. Update minPrice to be the minimum of current minPrice and current price
 * 4. Calculate potential profit as current price - minPrice
 * 5. Update maxProfit to be the maximum of current maxProfit and potential profit
 * 6. Return maxProfit
 * 
 * Intuition:
 * - We track the minimum price seen so far
 * - For each day, we calculate the profit if we sold at current price (bought at min price)
 * - We keep track of the maximum profit encountered
 * 
 * Dry Run Example:
 * Input: prices = [7, 1, 5, 3, 6, 4]
 * 
 * Day 1: price = 7, minPrice = 7, profit = 0, maxProfit = 0
 * Day 2: price = 1, minPrice = 1, profit = 0, maxProfit = 0
 * Day 3: price = 5, minPrice = 1, profit = 4, maxProfit = 4
 * Day 4: price = 3, minPrice = 1, profit = 2, maxProfit = 4
 * Day 5: price = 6, minPrice = 1, profit = 5, maxProfit = 5
 * Day 6: price = 4, minPrice = 1, profit = 3, maxProfit = 5
 * 
 * Result: 5 (buy at 1, sell at 6)
 */
public class BestTimeToBuySellStock {
    
    /**
     * Calculates the maximum profit from buying and selling stock
     * 
     * @param prices array of stock prices for each day
     * @return maximum profit that can be achieved
     */
    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }
        
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        
        for (int price : prices) {
            // Update minimum price seen so far
            if (price < minPrice) {
                minPrice = price;
            }
            // Calculate potential profit and update maxProfit
            else if (price - minPrice > maxProfit) {
                maxProfit = price - minPrice;
            }
        }
        
        return maxProfit;
    }
    
    /**
     * Alternative solution using Kadane's algorithm approach
     * This approach works by finding maximum subarray sum of price differences
     */
    public static int maxProfitKadane(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }
        
        int maxCur = 0;
        int maxSoFar = 0;
        
        for (int i = 1; i < prices.length; i++) {
            // Calculate daily profit/loss
            int dailyChange = prices[i] - prices[i - 1];
            // Kadane's algorithm: max ending here
            maxCur = Math.max(0, maxCur + dailyChange);
            // Update max so far
            maxSoFar = Math.max(maxSoFar, maxCur);
        }
        
        return maxSoFar;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        int[] prices = {7, 1, 5, 3, 6, 4};
        int result = maxProfit(prices);
        
        System.out.println("Best Time to Buy and Sell Stock Problem:");
        System.out.println("Input: prices = [7, 1, 5, 3, 6, 4]");
        System.out.println("Output: " + result);
        System.out.println("Expected: 5");
        System.out.println("Explanation: Buy on day 2 (price=1) and sell on day 5 (price=6), profit = 6-1 = 5");
        
        // Test with decreasing prices
        int[] prices2 = {7, 6, 4, 3, 1};
        int result2 = maxProfit(prices2);
        System.out.println("\nTest with decreasing prices:");
        System.out.println("Input: prices = [7, 6, 4, 3, 1]");
        System.out.println("Output: " + result2);
        System.out.println("Expected: 0 (no transaction)");
        
        // Test Kadane's approach
        System.out.println("\nUsing Kadane's algorithm approach:");
        System.out.println("Result: " + maxProfitKadane(prices));
    }
}