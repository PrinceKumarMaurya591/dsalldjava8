package com.dsa.string;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Problem: Longest Substring Without Repeating Characters
 * 
 * Problem Statement:
 * Given a string s, find the length of the longest substring without repeating characters.
 * 
 * Assumptions:
 * - 0 <= s.length <= 5 * 10^4
 * - s consists of English letters, digits, symbols, and spaces
 * 
 * Optimal Solution: O(n) time, O(min(n, m)) space where m is character set size
 * Using sliding window with HashMap to store last seen indices
 * 
 * Algorithm Explanation (Sliding Window with HashMap):
 * 1. Use two pointers (left and right) to represent the current window
 * 2. Maintain a HashMap to store the last seen index of each character
 * 3. Expand the window by moving the right pointer
 * 4. If the current character is already in the window (last seen index >= left),
 *    move left pointer to (last seen index + 1) to exclude the duplicate
 * 5. Update the last seen index of current character
 * 6. Track the maximum window length
 * 
 * Alternative Approach (Sliding Window with HashSet):
 * 1. Use HashSet to track characters in current window
 * 2. When duplicate found, shrink window from left until duplicate is removed
 * 3. Less efficient than HashMap approach but simpler
 * 
 * Dry Run Example:
 * Input: s = "abcabcbb"
 * 
 * left = 0, right = 0, maxLength = 0
 * map = {}
 * 
 * Step 1: right=0, char='a', not in map → window="a", length=1, maxLength=1
 * Step 2: right=1, char='b', not in map → window="ab", length=2, maxLength=2
 * Step 3: right=2, char='c', not in map → window="abc", length=3, maxLength=3
 * Step 4: right=3, char='a', in map at index 0 >= left=0
 *         → left = map['a'] + 1 = 1, window="bca", length=3, maxLength=3
 * Step 5: right=4, char='b', in map at index 1 >= left=1
 *         → left = map['b'] + 1 = 2, window="cab", length=3, maxLength=3
 * Step 6: right=5, char='c', in map at index 2 >= left=2
 *         → left = map['c'] + 1 = 3, window="abc", length=3, maxLength=3
 * Step 7: right=6, char='b', in map at index 4 >= left=3
 *         → left = map['b'] + 1 = 5, window="cb", length=2, maxLength=3
 * Step 8: right=7, char='b', in map at index 6 >= left=5
 *         → left = map['b'] + 1 = 7, window="b", length=1, maxLength=3
 * 
 * Result: 3
 */
public class LongestSubstringWithoutRepeatingCharacters {
    
    /**
     * Optimal solution using sliding window with HashMap
     * Tracks last seen indices for O(1) duplicate checks
     * 
     * @param s the input string
     * @return length of longest substring without repeating characters
     */
    public static int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        Map<Character, Integer> lastSeen = new HashMap<>();
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            
            // If character is in current window (last seen index >= left)
            if (lastSeen.containsKey(currentChar) && lastSeen.get(currentChar) >= left) {
                // Move left pointer to just after the previous occurrence
                left = lastSeen.get(currentChar) + 1;
            }
            
            // Update last seen index of current character
            lastSeen.put(currentChar, right);
            
            // Update maximum length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Alternative solution using sliding window with HashSet
     * More intuitive but less efficient for some cases
     * 
     * @param s the input string
     * @return length of longest substring without repeating characters
     */
    public static int lengthOfLongestSubstringHashSet(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        Set<Character> window = new HashSet<>();
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            
            // If character is already in window, shrink from left
            while (window.contains(currentChar)) {
                window.remove(s.charAt(left));
                left++;
            }
            
            // Add current character to window
            window.add(currentChar);
            
            // Update maximum length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Optimized solution using integer array for ASCII characters
     * Assumes extended ASCII (256 characters) for O(1) space
     * 
     * @param s the input string
     * @return length of longest substring without repeating characters
     */
    public static int lengthOfLongestSubstringArray(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        // Assuming extended ASCII (256 characters)
        int[] lastIndex = new int[256];
        // Initialize with -1 (not seen)
        for (int i = 0; i < 256; i++) {
            lastIndex[i] = -1;
        }
        
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            int charIndex = (int) currentChar;
            
            // If character was seen in current window (last index >= left)
            if (lastIndex[charIndex] >= left) {
                left = lastIndex[charIndex] + 1;
            }
            
            // Update last seen index
            lastIndex[charIndex] = right;
            
            // Update maximum length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Solution for limited character set (e.g., lowercase letters only)
     * Uses array of size 26 for better space efficiency
     * 
     * @param s the input string (lowercase letters only)
     * @return length of longest substring without repeating characters
     */
    public static int lengthOfLongestSubstringLowercase(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        int[] lastIndex = new int[26];
        // Initialize with -1 (not seen)
        for (int i = 0; i < 26; i++) {
            lastIndex[i] = -1;
        }
        
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            int charIndex = currentChar - 'a';
            
            // If character was seen in current window (last index >= left)
            if (lastIndex[charIndex] >= left) {
                left = lastIndex[charIndex] + 1;
            }
            
            // Update last seen index
            lastIndex[charIndex] = right;
            
            // Update maximum length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Example from problem
        String s1 = "abcabcbb";
        int result1 = lengthOfLongestSubstring(s1);
        System.out.println("Longest Substring Without Repeating Characters Problem:");
        System.out.println("Input: s = \"" + s1 + "\"");
        System.out.println("Output: " + result1);
        System.out.println("Expected: 3");
        System.out.println();
        
        // Test case 2: All same character
        String s2 = "bbbbb";
        int result2 = lengthOfLongestSubstringHashSet(s2);
        System.out.println("Input: s = \"" + s2 + "\"");
        System.out.println("Output (HashSet): " + result2);
        System.out.println("Expected: 1");
        System.out.println();
        
        // Test case 3: Single longest substring
        String s3 = "pwwkew";
        int result3 = lengthOfLongestSubstringArray(s3);
        System.out.println("Input: s = \"" + s3 + "\"");
        System.out.println("Output (Array): " + result3);
        System.out.println("Expected: 3 (\"wke\" or \"kew\")");
        System.out.println();
        
        // Test case 4: Empty string
        String s4 = "";
        int result4 = lengthOfLongestSubstring(s4);
        System.out.println("Input: s = \"" + s4 + "\"");
        System.out.println("Output: " + result4);
        System.out.println("Expected: 0");
        System.out.println();
        
        // Test case 5: Mixed characters
        String s5 = "abc123abc!@#";
        int result5 = lengthOfLongestSubstring(s5);
        System.out.println("Input: s = \"" + s5 + "\"");
        System.out.println("Output: " + result5);
        System.out.println("Expected: 9 (\"abc123!@#\")");
        
        // Dry run visualization for example case
        System.out.println("\nDry run for s = \"abcabcbb\":");
        System.out.println("Window evolution:");
        String s = "abcabcbb";
        Map<Character, Integer> lastSeen = new HashMap<>();
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            
            if (lastSeen.containsKey(currentChar) && lastSeen.get(currentChar) >= left) {
                left = lastSeen.get(currentChar) + 1;
            }
            
            lastSeen.put(currentChar, right);
            int currentLength = right - left + 1;
            maxLength = Math.max(maxLength, currentLength);
            
            System.out.println("  right=" + right + ", char='" + currentChar + 
                             "', left=" + left + ", window=\"" + 
                             s.substring(left, right + 1) + "\", length=" + currentLength);
        }
        System.out.println("Final max length: " + maxLength);
    }
}