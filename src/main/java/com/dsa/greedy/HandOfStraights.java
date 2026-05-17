package com.dsa.greedy;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

// Problem: Hand of Straights
// Link: https://leetcode.com/problems/hand-of-straights/
//
// Alice has a hand of cards, given as an array of integers.
// She wants to rearrange the cards into groups so that each group is of size
// groupSize, and consists of groupSize consecutive cards.
// Return true if she can rearrange the cards, otherwise false.
//
// Approach: Greedy with Min-Heap (or TreeMap)
// - Count frequencies of each card value
// - Use a min-heap to process cards in ascending order
// - For each card, try to form a group of consecutive cards
// - Decrease frequencies as we use cards
//
// Time Complexity: O(n log n) - heap operations
// Space Complexity: O(n)

public class HandOfStraights {

    public static void main(String[] args) {
        System.out.println("=== Hand of Straights ===");
        int[] hand1 = {1, 2, 3, 6, 2, 3, 4, 7, 8};
        System.out.println("Can form groups of 3: " + isNStraightHand(hand1, 3));
        // Expected: true (groups: [1,2,3], [2,3,4], [6,7,8])

        int[] hand2 = {1, 2, 3, 4, 5};
        System.out.println("Can form groups of 4: " + isNStraightHand(hand2, 4));
        // Expected: false

        int[] hand3 = {2, 1};
        System.out.println("Can form groups of 2: " + isNStraightHand(hand3, 2));
        // Expected: true (group: [1,2])

        int[] hand4 = {8, 10, 12};
        System.out.println("Can form groups of 3: " + isNStraightHand(hand4, 3));
        // Expected: false
    }

    public static boolean isNStraightHand(int[] hand, int groupSize) {
        if (hand.length % groupSize != 0) return false;

        // Count frequencies
        Map<Integer, Integer> freq = new HashMap<>();
        for (int card : hand) {
            freq.put(card, freq.getOrDefault(card, 0) + 1);
        }

        // Min-heap of unique card values
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(freq.keySet());

        while (!minHeap.isEmpty()) {
            int first = minHeap.peek();

            // Try to form a group starting from 'first'
            for (int i = 0; i < groupSize; i++) {
                int card = first + i;

                if (!freq.containsKey(card)) return false;

                int count = freq.get(card);
                if (count == 1) {
                    freq.remove(card);
                    minHeap.poll(); // Remove from heap when count reaches 0
                } else {
                    freq.put(card, count - 1);
                }
            }
        }

        return true;
    }
}
