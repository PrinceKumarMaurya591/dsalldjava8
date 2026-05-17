package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Sliding Window
 * 
 * Used when: Problems involving subarrays/substrings, contiguous sequences,
 * fixed or variable window size, often with "maximum/minimum" or "contains" conditions.
 * 
 * Key variations:
 * 1. Fixed window size
 * 2. Variable window size (expand/shrink)
 * 3. Window with auxiliary data structure (map, set, deque)
 * 
 * Time Complexity: O(n) typically
 * Space Complexity: O(k) where k is window size or distinct characters
 */
public class SlidingWindow {

    /**
     * Problem: Maximum Average Subarray I
     * Find a contiguous subarray of size k with maximum average.
     * 
     * Approach: Fixed window size
     * Time: O(n), Space: O(1)
     */
    public static double findMaxAverage(int[] nums, int k) {
        // Calculate sum of first window
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }

        int maxSum = windowSum;

        // Slide the window
        for (int i = k; i < nums.length; i++) {
            windowSum += nums[i] - nums[i - k];
            maxSum = Math.max(maxSum, windowSum);
        }

        return (double) maxSum / k;
    }

    /**
     * Problem: Maximum Sum Subarray of Size K (Fixed Window)
     * Time: O(n), Space: O(1)
     */
    public static int maxSumSubarray(int[] nums, int k) {
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }

        int maxSum = windowSum;
        for (int i = k; i < nums.length; i++) {
            windowSum += nums[i] - nums[i - k];
            maxSum = Math.max(maxSum, windowSum);
        }

        return maxSum;
    }

    /**
     * Problem: Longest Substring Without Repeating Characters
     * Find the length of the longest substring without repeating characters.
     * 
     * Approach: Variable window with HashSet
     * Time: O(n), Space: O(min(m, n)) where m is charset size
     */
    public static int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            // Shrink window until no duplicate
            while (set.contains(s.charAt(right))) {
                set.remove(s.charAt(left));
                left++;
            }
            set.add(s.charAt(right));
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * Problem: Longest Repeating Character Replacement
     * You can replace any character k times. Find longest substring with same char.
     * 
     * Approach: Variable window with frequency map
     * Time: O(n), Space: O(26) = O(1)
     */
    public static int characterReplacement(String s, int k) {
        int[] freq = new int[26];
        int left = 0, maxFreq = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            freq[s.charAt(right) - 'A']++;
            maxFreq = Math.max(maxFreq, freq[s.charAt(right) - 'A']);

            // If remaining characters > k, shrink window
            while ((right - left + 1) - maxFreq > k) {
                freq[s.charAt(left) - 'A']--;
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * Problem: Minimum Window Substring
     * Find minimum window in s that contains all chars of t.
     * 
     * Approach: Variable window with frequency map and counter
     * Time: O(n), Space: O(m) where m is charset size
     */
    public static String minWindow(String s, String t) {
        if (s.length() < t.length()) return "";

        Map<Character, Integer> targetFreq = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetFreq.put(c, targetFreq.getOrDefault(c, 0) + 1);
        }

        int left = 0, matched = 0;
        int minLen = Integer.MAX_VALUE, minStart = 0;
        Map<Character, Integer> windowFreq = new HashMap<>();

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            windowFreq.put(c, windowFreq.getOrDefault(c, 0) + 1);

            if (targetFreq.containsKey(c) && 
                windowFreq.get(c).intValue() == targetFreq.get(c).intValue()) {
                matched++;
            }

            // Shrink window while all chars matched
            while (matched == targetFreq.size()) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                char leftChar = s.charAt(left);
                windowFreq.put(leftChar, windowFreq.get(leftChar) - 1);
                if (targetFreq.containsKey(leftChar) && 
                    windowFreq.get(leftChar) < targetFreq.get(leftChar)) {
                    matched--;
                }
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    /**
     * Problem: Max Consecutive Ones III
     * Find longest subarray with at most k zeros that can be flipped to 1.
     * 
     * Approach: Variable window counting zeros
     * Time: O(n), Space: O(1)
     */
    public static int longestOnes(int[] nums, int k) {
        int left = 0, zeroCount = 0, maxLen = 0;

        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                zeroCount++;
            }

            while (zeroCount > k) {
                if (nums[left] == 0) {
                    zeroCount--;
                }
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * Problem: Fruit Into Baskets
     * Find longest subarray with at most 2 distinct numbers.
     * 
     * Approach: Variable window with frequency map
     * Time: O(n), Space: O(1) (at most 3 entries)
     */
    public static int totalFruit(int[] fruits) {
        Map<Integer, Integer> basket = new HashMap<>();
        int left = 0, maxFruits = 0;

        for (int right = 0; right < fruits.length; right++) {
            basket.put(fruits[right], basket.getOrDefault(fruits[right], 0) + 1);

            while (basket.size() > 2) {
                basket.put(fruits[left], basket.get(fruits[left]) - 1);
                if (basket.get(fruits[left]) == 0) {
                    basket.remove(fruits[left]);
                }
                left++;
            }

            maxFruits = Math.max(maxFruits, right - left + 1);
        }

        return maxFruits;
    }

    /**
     * Problem: Permutation in String
     * Check if s2 contains a permutation of s1.
     * 
     * Approach: Fixed window with frequency comparison
     * Time: O(n), Space: O(1)
     */
    public static boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;

        int[] s1Freq = new int[26];
        int[] s2Freq = new int[26];

        for (char c : s1.toCharArray()) {
            s1Freq[c - 'a']++;
        }

        // First window
        for (int i = 0; i < s1.length(); i++) {
            s2Freq[s2.charAt(i) - 'a']++;
        }

        if (Arrays.equals(s1Freq, s2Freq)) return true;

        // Slide window
        for (int i = s1.length(); i < s2.length(); i++) {
            s2Freq[s2.charAt(i) - 'a']++;
            s2Freq[s2.charAt(i - s1.length()) - 'a']--;
            if (Arrays.equals(s1Freq, s2Freq)) return true;
        }

        return false;
    }

    /**
     * Problem: Find All Anagrams in a String
     * Find all start indices of p's anagrams in s.
     * 
     * Approach: Fixed window with frequency comparison
     * Time: O(n), Space: O(1)
     */
    public static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) return result;

        int[] pFreq = new int[26];
        int[] sFreq = new int[26];

        for (char c : p.toCharArray()) {
            pFreq[c - 'a']++;
        }

        for (int i = 0; i < s.length(); i++) {
            sFreq[s.charAt(i) - 'a']++;

            // Remove leftmost char when window exceeds p length
            if (i >= p.length()) {
                sFreq[s.charAt(i - p.length()) - 'a']--;
            }

            if (Arrays.equals(pFreq, sFreq)) {
                result.add(i - p.length() + 1);
            }
        }

        return result;
    }

    /**
     * Problem: Sliding Window Maximum
     * Return max element in every sliding window of size k.
     * 
     * Approach: Deque (monotonic queue)
     * Time: O(n), Space: O(k)
     */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) return new int[0];

        int[] result = new int[nums.length - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // Stores indices

        for (int i = 0; i < nums.length; i++) {
            // Remove indices outside current window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove smaller elements from back (maintain decreasing order)
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i);

            // Add to result when window is complete
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== SLIDING WINDOW PATTERN ===");
        System.out.println();

        // 1. Maximum Average Subarray
        System.out.println("1. Maximum Average Subarray I:");
        int[] nums1 = {1, 12, -5, -6, 50, 3};
        System.out.println("   Input: nums=[1,12,-5,-6,50,3], k=4");
        System.out.println("   Output: " + findMaxAverage(nums1, 4) + " (expected: 12.75)");
        System.out.println();

        // 2. Maximum Sum Subarray of Size K
        System.out.println("2. Maximum Sum Subarray of Size K:");
        int[] nums2 = {2, 1, 5, 1, 3, 2};
        System.out.println("   Input: nums=[2,1,5,1,3,2], k=3");
        System.out.println("   Output: " + maxSumSubarray(nums2, 3) + " (expected: 9)");
        System.out.println();

        // 3. Longest Substring Without Repeating Characters
        System.out.println("3. Longest Substring Without Repeating Characters:");
        System.out.println("   Input: \"abcabcbb\"");
        System.out.println("   Output: " + lengthOfLongestSubstring("abcabcbb") + " (expected: 3)");
        System.out.println("   Input: \"bbbbb\"");
        System.out.println("   Output: " + lengthOfLongestSubstring("bbbbb") + " (expected: 1)");
        System.out.println("   Input: \"pwwkew\"");
        System.out.println("   Output: " + lengthOfLongestSubstring("pwwkew") + " (expected: 3)");
        System.out.println();

        // 4. Longest Repeating Character Replacement
        System.out.println("4. Longest Repeating Character Replacement:");
        System.out.println("   Input: s=\"ABAB\", k=2");
        System.out.println("   Output: " + characterReplacement("ABAB", 2) + " (expected: 4)");
        System.out.println("   Input: s=\"AABABBA\", k=1");
        System.out.println("   Output: " + characterReplacement("AABABBA", 1) + " (expected: 4)");
        System.out.println();

        // 5. Minimum Window Substring
        System.out.println("5. Minimum Window Substring:");
        System.out.println("   Input: s=\"ADOBECODEBANC\", t=\"ABC\"");
        System.out.println("   Output: \"" + minWindow("ADOBECODEBANC", "ABC") + "\" (expected: \"BANC\")");
        System.out.println();

        // 6. Max Consecutive Ones III
        System.out.println("6. Max Consecutive Ones III:");
        int[] nums3 = {1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0};
        System.out.println("   Input: nums=[1,1,1,0,0,0,1,1,1,1,0], k=2");
        System.out.println("   Output: " + longestOnes(nums3, 2) + " (expected: 6)");
        System.out.println();

        // 7. Fruit Into Baskets
        System.out.println("7. Fruit Into Baskets:");
        int[] fruits = {1, 2, 1, 2, 3};
        System.out.println("   Input: [1,2,1,2,3]");
        System.out.println("   Output: " + totalFruit(fruits) + " (expected: 4)");
        System.out.println();

        // 8. Permutation in String
        System.out.println("8. Permutation in String:");
        System.out.println("   Input: s1=\"ab\", s2=\"eidbaooo\"");
        System.out.println("   Output: " + checkInclusion("ab", "eidbaooo") + " (expected: true)");
        System.out.println("   Input: s1=\"ab\", s2=\"eidboaoo\"");
        System.out.println("   Output: " + checkInclusion("ab", "eidboaoo") + " (expected: false)");
        System.out.println();

        // 9. Find All Anagrams in a String
        System.out.println("9. Find All Anagrams in a String:");
        System.out.println("   Input: s=\"cbaebabacd\", p=\"abc\"");
        System.out.println("   Output: " + findAnagrams("cbaebabacd", "abc") + " (expected: [0, 6])");
        System.out.println();

        // 10. Sliding Window Maximum
        System.out.println("10. Sliding Window Maximum:");
        int[] nums4 = {1, 3, -1, -3, 5, 3, 6, 7};
        System.out.println("    Input: nums=[1,3,-1,-3,5,3,6,7], k=3");
        int[] result = maxSlidingWindow(nums4, 3);
        System.out.println("    Output: " + Arrays.toString(result) + " (expected: [3,3,5,5,6,7])");
    }
}
