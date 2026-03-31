package com.dsa.string;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Problem: Valid Anagram
 * 
 * Problem Statement:
 * Given two strings s and t, return true if t is an anagram of s, and false otherwise.
 * An anagram is a word or phrase formed by rearranging the letters of a different word or phrase,
 * typically using all the original letters exactly once.
 * 
 * Assumptions:
 * - 1 <= s.length, t.length <= 5 * 10^4
 * - s and t consist of lowercase English letters
 * 
 * Optimal Solution: O(n) time, O(1) space using frequency array (26 letters)
 * 
 * Algorithm Explanation:
 * 1. If strings have different lengths, they cannot be anagrams
 * 2. Create frequency array of size 26 (for lowercase English letters)
 * 3. Count characters in string s (increment frequency)
 * 4. Count characters in string t (decrement frequency)
 * 5. If any frequency is non-zero, strings are not anagrams
 * 6. Alternative approaches: sorting, HashMap
 * 
 * Key Insight:
 * - Anagrams have same character frequencies
 * - Sorting both strings and comparing is O(n log n)
 * - Frequency array is O(n) with O(1) space for fixed alphabet
 * 
 * Dry Run Example:
 * Input: s = "anagram", t = "nagaram"
 * 
 * Step 1: Lengths equal (7 = 7)
 * Step 2: Initialize freq[26] = [0,0,...]
 * Step 3: Count s:
 *   a→3, n→1, g→1, r→1, m→1
 * Step 4: Count t (decrement):
 *   n→0, a→2, g→0, a→1, r→0, a→0, m→0
 * Step 5: All frequencies zero → true
 * 
 * Result: true
 */
public class ValidAnagram {
    
    /**
     * Optimal solution using frequency array (O(n) time, O(1) space)
     * Assumes strings contain only lowercase English letters
     * 
     * @param s first string
     * @param t second string
     * @return true if t is an anagram of s
     */
    public static boolean isAnagram(String s, String t) {
        // Different lengths cannot be anagrams
        if (s.length() != t.length()) {
            return false;
        }
        
        // Frequency array for 26 lowercase letters
        int[] frequency = new int[26];
        
        // Count characters in s
        for (int i = 0; i < s.length(); i++) {
            frequency[s.charAt(i) - 'a']++;
        }
        
        // Subtract characters in t
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            frequency[c - 'a']--;
            
            // Early exit if frequency becomes negative
            if (frequency[c - 'a'] < 0) {
                return false;
            }
        }
        
        // Check all frequencies are zero
        for (int count : frequency) {
            if (count != 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Alternative solution using sorting (O(n log n) time, O(n) space)
     * Works for any characters, not just lowercase letters
     * 
     * @param s first string
     * @param t second string
     * @return true if t is an anagram of s
     */
    public static boolean isAnagramSorting(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        // Convert strings to char arrays and sort
        char[] sArray = s.toCharArray();
        char[] tArray = t.toCharArray();
        
        Arrays.sort(sArray);
        Arrays.sort(tArray);
        
        // Compare sorted arrays
        return Arrays.equals(sArray, tArray);
    }
    
    /**
     * Solution using HashMap (O(n) time, O(n) space)
     * Works for any characters, including Unicode
     * 
     * @param s first string
     * @param t second string
     * @return true if t is an anagram of s
     */
    public static boolean isAnagramHashMap(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        Map<Character, Integer> frequencyMap = new HashMap<>();
        
        // Count characters in s
        for (char c : s.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        
        // Subtract characters in t
        for (char c : t.toCharArray()) {
            if (!frequencyMap.containsKey(c)) {
                return false;  // Character not in s
            }
            
            int count = frequencyMap.get(c);
            if (count == 1) {
                frequencyMap.remove(c);
            } else {
                frequencyMap.put(c, count - 1);
            }
        }
        
        // HashMap should be empty if all characters matched
        return frequencyMap.isEmpty();
    }
    
    /**
     * Optimized frequency array solution with early exit
     * More efficient for large strings with early mismatches
     * 
     * @param s first string
     * @param t second string
     * @return true if t is an anagram of s
     */
    public static boolean isAnagramOptimized(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        int[] freq = new int[26];
        int countNonZero = 0;
        
        // Process first string
        for (char c : s.toCharArray()) {
            int index = c - 'a';
            if (freq[index] == 0) {
                countNonZero++;
            }
            freq[index]++;
        }
        
        // Process second string
        for (char c : t.toCharArray()) {
            int index = c - 'a';
            if (freq[index] == 0) {
                return false;  // Character not in s
            }
            
            freq[index]--;
            if (freq[index] == 0) {
                countNonZero--;
            }
        }
        
        return countNonZero == 0;
    }
    
    /**
     * Solution for extended character set (ASCII)
     * Uses array of size 128 for all ASCII characters
     * 
     * @param s first string
     * @param t second string
     * @return true if t is an anagram of s
     */
    public static boolean isAnagramASCII(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        int[] freq = new int[128];  // ASCII characters
        
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i)]++;
            freq[t.charAt(i)]--;
        }
        
        for (int count : freq) {
            if (count != 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Valid anagram
        String s1 = "anagram";
        String t1 = "nagaram";
        boolean result1 = isAnagram(s1, t1);
        System.out.println("Valid Anagram Problem:");
        System.out.println("Input: s = \"" + s1 + "\", t = \"" + t1 + "\"");
        System.out.println("Output: " + result1);
        System.out.println("Expected: true");
        System.out.println();
        
        // Test case 2: Not an anagram
        String s2 = "rat";
        String t2 = "car";
        boolean result2 = isAnagramSorting(s2, t2);
        System.out.println("Input: s = \"" + s2 + "\", t = \"" + t2 + "\"");
        System.out.println("Output (Sorting): " + result2);
        System.out.println("Expected: false");
        System.out.println();
        
        // Test case 3: Different lengths
        String s3 = "abc";
        String t3 = "abcd";
        boolean result3 = isAnagramHashMap(s3, t3);
        System.out.println("Input: s = \"" + s3 + "\", t = \"" + t3 + "\"");
        System.out.println("Output (HashMap): " + result3);
        System.out.println("Expected: false");
        System.out.println();
        
        // Test case 4: Empty strings
        String s4 = "";
        String t4 = "";
        boolean result4 = isAnagramOptimized(s4, t4);
        System.out.println("Input: s = \"" + s4 + "\", t = \"" + t4 + "\"");
        System.out.println("Output (Optimized): " + result4);
        System.out.println("Expected: true");
        System.out.println();
        
        // Test case 5: Case sensitivity (not handled by basic version)
        String s5 = "Hello";
        String t5 = "hello";
        boolean result5 = isAnagramASCII(s5, t5);
        System.out.println("Input: s = \"" + s5 + "\", t = \"" + t5 + "\"");
        System.out.println("Output (ASCII): " + result5);
        System.out.println("Expected: false (case sensitive)");
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. Frequency Array (26): O(n) time, O(1) space - Best for lowercase letters");
        System.out.println("2. Sorting: O(n log n) time, O(n) space - Works for any characters");
        System.out.println("3. HashMap: O(n) time, O(n) space - Works for Unicode");
        System.out.println("4. ASCII Array (128): O(n) time, O(1) space - Best for ASCII");
        
        // Dry run visualization
        System.out.println("\nDry run for s = \"anagram\", t = \"nagaram\":");
        System.out.println("Step 1: Check lengths (7 == 7) ✓");
        System.out.println("Step 2: Initialize frequency array[26] = all zeros");
        
        int[] freq = new int[26];
        String s = "anagram";
        String t = "nagaram";
        
        System.out.println("Step 3: Count characters in s:");
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            freq[idx]++;
            System.out.println("  '" + c + "' -> freq[" + idx + "] = " + freq[idx]);
        }
        
        System.out.println("Step 4: Subtract characters in t:");
        for (char c : t.toCharArray()) {
            int idx = c - 'a';
            freq[idx]--;
            System.out.println("  '" + c + "' -> freq[" + idx + "] = " + freq[idx]);
        }
        
        System.out.println("Step 5: Check all frequencies are zero:");
        boolean allZero = true;
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] != 0) {
                System.out.println("  freq[" + i + "] = " + freq[i] + " ≠ 0");
                allZero = false;
            }
        }
        System.out.println("Result: " + (allZero ? "true (anagram)" : "false (not anagram)"));
    }
}