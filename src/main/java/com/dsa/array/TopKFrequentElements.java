package com.dsa.array;

import java.util.*;

/**
 * Problem 21: Top K Frequent Elements
 */
public class TopKFrequentElements {
    public static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) freq.put(num, freq.getOrDefault(num, 0) + 1);
        
        List<Integer>[] buckets = new List[nums.length + 1];
        for (int i = 0; i < buckets.length; i++) buckets[i] = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            buckets[entry.getValue()].add(entry.getKey());
        }
        
        int[] result = new int[k];
        int idx = 0;
        for (int i = buckets.length - 1; i >= 0 && idx < k; i--) {
            for (int num : buckets[i]) {
                result[idx++] = num;
                if (idx == k) break;
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
        System.out.println("Top K Frequent Elements:");
        int[] nums = {1,1,1,2,2,3};
        System.out.println("Input: [1,1,1,2,2,3], k=2");
        System.out.println("Output: " + Arrays.toString(topKFrequent(nums, 2)));
        System.out.println("Expected: [1, 2] (order may vary)");
    }
}