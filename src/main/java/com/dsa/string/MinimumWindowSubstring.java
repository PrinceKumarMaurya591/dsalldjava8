package com.dsa.string;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem: Minimum Window Substring
 * 
 * Problem Statement:
 * Given two strings s and t, return the minimum window substring of s such that
 * every character in t (including duplicates) is included in the window.
 * If there is no such substring, return an empty string.
 * 
 * Assumptions:
 * - 1 <= s.length, t.length <= 10^5
 * - s and t consist of uppercase and lowercase English letters
 * 
 * Optimal Solution: O(n + m) time, O(1) space (since fixed character set)
 * Using sliding window with two frequency maps
 * 
 * Algorithm Explanation (Sliding Window):
 * 1. Create frequency map for string t (required characters)
 * 2. Create frequency map for current window in s
 * 3. Use two pointers (left and right) to represent the window
 * 4. Expand window by moving right pointer
 * 5. When window contains all required characters (valid window):
 *    a. Try to shrink from left while keeping window valid
 *    b. Update minimum window found
 * 6. Continue until right pointer reaches end
 * 
 * Key Optimization:
 * - Track "required" count: number of unique characters needed
 * - Track "formed" count: number of unique characters satisfied in current window
 * - A character is "satisfied" when its frequency in window >= frequency in t
 * 
 * Dry Run Example:
 * Input: s = "ADOBECODEBANC", t = "ABC"
 * 
 * t frequency: A=1, B=1, C=1, required=3
 * 
 * Step 1: right=0, char='A', formed=1 (A satisfied)
 * Step 2: right=1, char='D', formed=1
 * Step 3: right=2, char='O', formed=1
 * Step 4: right=3, char='B', formed=2 (B satisfied)
 * Step 5: right=4, char='E', formed=2
 * Step 6: right=5, char='C', formed=3 (C satisfied, all required met)
 *         Window "ADOBEC" length=6, minWindow="ADOBEC"
 *         Shrink left: left=1, window "DOBEC" missing A → stop shrinking
 * Step 7: right=6, char='O', formed=3
 *         Shrink left: left=1, window "DOBECO" missing A
 * Step 8: right=7, char='D', formed=3
 * Step 9: right=8, char='E', formed=3
 * Step 10: right=9, char='B', formed=3
 * Step 11: right=10, char='A', formed=3
 *          Window "ODEBAN" length=6
 *          Shrink left: left=5, window "BAN" missing C
 * Step 12: right=11, char='N', formed=3
 * Step 13: right=12, char='C', formed=3
 *          Window "BANC" length=4, minWindow="BANC"
 *          Shrink left: left=9, window "ANC" missing B → stop
 * 
 * Result: "BANC"
 */
public class MinimumWindowSubstring {
    
    /**
     * Finds the minimum window substring containing all characters of t
     * 
     * @param s the source string
     * @param t the target string
     * @return minimum window substring, or empty string if not found
     */
    public static String minWindow(String s, String t) {
        if (s == null || t == null || s.length() == 0 || t.length() == 0 || s.length() < t.length()) {
            return "";
        }
        
        // Frequency map for characters in t
        Map<Character, Integer> tFreq = new HashMap<>();
        for (char c : t.toCharArray()) {
            tFreq.put(c, tFreq.getOrDefault(c, 0) + 1);
        }
        
        // Frequency map for current window in s
        Map<Character, Integer> windowFreq = new HashMap<>();
        
        // Number of unique characters required
        int required = tFreq.size();
        // Number of unique characters formed in current window
        int formed = 0;
        
        // Sliding window pointers
        int left = 0;
        int right = 0;
        
        // Track minimum window
        int minLength = Integer.MAX_VALUE;
        int minLeft = 0;
        int minRight = 0;
        
        while (right < s.length()) {
            // Add character at right pointer to window
            char rightChar = s.charAt(right);
            windowFreq.put(rightChar, windowFreq.getOrDefault(rightChar, 0) + 1);
            
            // Check if this character completes a requirement
            if (tFreq.containsKey(rightChar) && 
                windowFreq.get(rightChar).intValue() == tFreq.get(rightChar).intValue()) {
                formed++;
            }
            
            // Try to shrink window while it's valid
            while (left <= right && formed == required) {
                char leftChar = s.charAt(left);
                
                // Update minimum window
                int currentLength = right - left + 1;
                if (currentLength < minLength) {
                    minLength = currentLength;
                    minLeft = left;
                    minRight = right;
                }
                
                // Remove left character from window
                windowFreq.put(leftChar, windowFreq.get(leftChar) - 1);
                
                // Check if removing this character breaks a requirement
                if (tFreq.containsKey(leftChar) && 
                    windowFreq.get(leftChar) < tFreq.get(leftChar)) {
                    formed--;
                }
                
                // Move left pointer
                left++;
            }
            
            // Expand window
            right++;
        }
        
        return minLength == Integer.MAX_VALUE ? "" : s.substring(minLeft, minRight + 1);
    }
    
    /**
     * Optimized version using integer arrays for ASCII characters
     * More efficient for fixed character sets
     * 
     * @param s the source string
     * @param t the target string
     * @return minimum window substring, or empty string if not found
     */
    public static String minWindowArray(String s, String t) {
        if (s == null || t == null || s.length() == 0 || t.length() == 0 || s.length() < t.length()) {
            return "";
        }
        
        // Assuming extended ASCII (128 characters)
        int[] tFreq = new int[128];
        int[] windowFreq = new int[128];
        
        // Count characters in t
        for (char c : t.toCharArray()) {
            tFreq[c]++;
        }
        
        // Number of unique characters required (non-zero in tFreq)
        int required = 0;
        for (int count : tFreq) {
            if (count > 0) required++;
        }
        
        int formed = 0;
        int left = 0;
        int right = 0;
        int minLength = Integer.MAX_VALUE;
        int minLeft = 0;
        
        while (right < s.length()) {
            char rightChar = s.charAt(right);
            windowFreq[rightChar]++;
            
            // Check if this character completes a requirement
            if (tFreq[rightChar] > 0 && windowFreq[rightChar] == tFreq[rightChar]) {
                formed++;
            }
            
            // Try to shrink window while it's valid
            while (left <= right && formed == required) {
                char leftChar = s.charAt(left);
                
                // Update minimum window
                int currentLength = right - left + 1;
                if (currentLength < minLength) {
                    minLength = currentLength;
                    minLeft = left;
                }
                
                // Remove left character from window
                windowFreq[leftChar]--;
                
                // Check if removing this character breaks a requirement
                if (tFreq[leftChar] > 0 && windowFreq[leftChar] < tFreq[leftChar]) {
                    formed--;
                }
                
                left++;
            }
            
            right++;
        }
        
        return minLength == Integer.MAX_VALUE ? "" : s.substring(minLeft, minLeft + minLength);
    }
    
    /**
     * Alternative implementation with detailed comments for educational purposes
     */
    public static String minWindowDetailed(String s, String t) {
        if (s.length() < t.length()) return "";
        
        // Map to store frequency of characters in t
        Map<Character, Integer> targetMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }
        
        // Map to store frequency of characters in current window
        Map<Character, Integer> windowMap = new HashMap<>();
        
        // Counters to track progress
        int requiredChars = targetMap.size();  // Number of unique characters needed
        int formedChars = 0;                   // Number of unique characters satisfied
        
        // Window pointers and result trackers
        int left = 0, right = 0;
        int minWindowLength = Integer.MAX_VALUE;
        int minWindowStart = 0;
        
        while (right < s.length()) {
            // STEP 1: Expand window by adding character at right pointer
            char currentChar = s.charAt(right);
            windowMap.put(currentChar, windowMap.getOrDefault(currentChar, 0) + 1);
            
            // Check if adding this character satisfies a requirement from t
            // We need exact match (not just >=) to increment formedChars
            if (targetMap.containsKey(currentChar) && 
                windowMap.get(currentChar).intValue() == targetMap.get(currentChar).intValue()) {
                formedChars++;
            }
            
            // STEP 2: Try to shrink window from left while window is valid
            while (formedChars == requiredChars && left <= right) {
                // Calculate current window size
                int currentWindowSize = right - left + 1;
                
                // Update minimum window if current is smaller
                if (currentWindowSize < minWindowLength) {
                    minWindowLength = currentWindowSize;
                    minWindowStart = left;
                }
                
                // Remove leftmost character from window
                char leftChar = s.charAt(left);
                windowMap.put(leftChar, windowMap.get(leftChar) - 1);
                
                // Check if removing this character breaks a requirement
                if (targetMap.containsKey(leftChar) && 
                    windowMap.get(leftChar) < targetMap.get(leftChar)) {
                    formedChars--;
                }
                
                // Move left pointer
                left++;
            }
            
            // STEP 3: Expand window
            right++;
        }
        
        // Return result
        return minWindowLength == Integer.MAX_VALUE ? "" : 
               s.substring(minWindowStart, minWindowStart + minWindowLength);
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Example from problem
        String s1 = "ADOBECODEBANC";
        String t1 = "ABC";
        String result1 = minWindow(s1, t1);
        System.out.println("Minimum Window Substring Problem:");
        System.out.println("Input: s = \"" + s1 + "\", t = \"" + t1 + "\"");
        System.out.println("Output: \"" + result1 + "\"");
        System.out.println("Expected: \"BANC\"");
        System.out.println();
        
        // Test case 2: t not in s
        String s2 = "a";
        String t2 = "aa";
        String result2 = minWindowArray(s2, t2);
        System.out.println("Input: s = \"" + s2 + "\", t = \"" + t2 + "\"");
        System.out.println("Output (Array): \"" + result2 + "\"");
        System.out.println("Expected: \"\"");
        System.out.println();
        
        // Test case 3: Exact match
        String s3 = "a";
        String t3 = "a";
        String result3 = minWindowDetailed(s3, t3);
        System.out.println("Input: s = \"" + s3 + "\", t = \"" + t3 + "\"");
        System.out.println("Output (Detailed): \"" + result3 + "\"");
        System.out.println("Expected: \"a\"");
        System.out.println();
        
        // Test case 4: Multiple occurrences
        String s4 = "bba";
        String t4 = "ab";
        String result4 = minWindow(s4, t4);
        System.out.println("Input: s = \"" + s4 + "\", t = \"" + t4 + "\"");
        System.out.println("Output: \"" + result4 + "\"");
        System.out.println("Expected: \"ba\"");
        
        // Dry run visualization for example case
        System.out.println("\nDry run for s = \"ADOBECODEBANC\", t = \"ABC\":");
        System.out.println("t frequency: A=1, B=1, C=1");
        System.out.println("Window evolution:");
        
        String s = "ADOBECODEBANC";
        String t = "ABC";
        String result = minWindowDetailed(s, t);
        
        // Simulate key steps
        System.out.println("Key steps:");
        System.out.println("1. Expand to include A, D, O, B, E, C → window \"ADOBEC\" (contains all ABC)");
        System.out.println("2. Shrink from left: remove A → window \"DOBEC\" (missing A)");
        System.out.println("3. Continue expanding...");
        System.out.println("4. Eventually find window \"BANC\" (length 4)");
        System.out.println("5. Final result: \"" + result + "\"");
        
        // Show frequency maps at critical point
        System.out.println("\nFrequency analysis for window \"ADOBEC\":");
        Map<Character, Integer> targetMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }
        System.out.println("Target frequencies: " + targetMap);
        
        Map<Character, Integer> windowMap = new HashMap<>();
        String window = "ADOBEC";
        for (char c : window.toCharArray()) {
            windowMap.put(c, windowMap.getOrDefault(c, 0) + 1);
        }
        System.out.println("Window frequencies: " + windowMap);
        System.out.println("Contains all required? " + 
                          (windowMap.getOrDefault('A', 0) >= 1 &&
                           windowMap.getOrDefault('B', 0) >= 1 &&
                           windowMap.getOrDefault('C', 0) >= 1));
    }
}