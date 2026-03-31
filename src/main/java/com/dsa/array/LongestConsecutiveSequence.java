package com.dsa.array;

import java.util.*;

/**
 * Problem 25: Longest Consecutive Sequence
 */
public class LongestConsecutiveSequence {
    public static int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        
        Set<Integer> set = new HashSet<>();
        for (int num : nums) set.add(num);
        
        int longest = 0;
        for (int num : set) {
            if (!set.contains(num - 1)) {
                int current = num;
                int streak = 1;
                while (set.contains(current + 1)) {
                    current++;
                    streak++;
                }
                longest = Math.max(longest, streak);
            }
        }
        return longest;
    }
    
    public static void main(String[] args) {
        System.out.println("Longest Consecutive Sequence:");
        int[] nums = {100, 4, 200, 1, 3, 2};
        System.out.println("Input: " + Arrays.toString(nums));
        System.out.println("Output: " + longestConsecutive(nums));
        System.out.println("Expected: 4");
    }
}