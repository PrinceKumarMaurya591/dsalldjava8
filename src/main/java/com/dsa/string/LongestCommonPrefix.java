package com.dsa.string;

/**
 * Problem: Longest Common Prefix
 * 
 * Problem Statement:
 * Write a function to find the longest common prefix string amongst an array of strings.
 * If there is no common prefix, return an empty string "".
 * 
 * Assumptions:
 * - 1 <= strs.length <= 200
 * - 0 <= strs[i].length <= 200
 * - strs[i] consists of only lowercase English letters
 * 
 * Optimal Solution: O(S) time, O(1) space where S is the sum of all characters in all strings
 * 
 * Algorithm Explanation:
 * 1. Take the first string as the initial prefix
 * 2. Iterate through the remaining strings
 * 3. For each string, check how many characters match with the current prefix
 * 4. Update the prefix to the common part
 * 5. If prefix becomes empty at any point, return empty string
 * 6. Continue until all strings are processed
 * 
 * Alternative Approach (Vertical Scanning):
 * 1. Compare characters at the same index across all strings
 * 2. Stop when characters don't match or a string ends
 * 
 * Dry Run Example:
 * Input: strs = ["flower", "flow", "flight"]
 * 
 * Step 1: prefix = "flower"
 * Step 2: Compare with "flow" → common = "flow"
 * Step 3: Compare "flow" with "flight" → common = "fl"
 * 
 * Result: "fl"
 */
public class LongestCommonPrefix {
    
    /**
     * Finds the longest common prefix among an array of strings
     * 
     * @param strs the array of strings
     * @return the longest common prefix string
     */
    public static String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        
        // Start with the first string as the prefix
        String prefix = strs[0];
        
        // Compare with each string in the array
        for (int i = 1; i < strs.length; i++) {
            // While the current string doesn't start with the prefix
            while (strs[i].indexOf(prefix) != 0) {
                // Shorten the prefix by one character
                prefix = prefix.substring(0, prefix.length() - 1);
                
                // If prefix becomes empty, return empty string
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }
        
        return prefix;
    }
    
    /**
     * Alternative implementation using vertical scanning
     * More efficient for cases where strings have different lengths
     */
    public static String longestCommonPrefixVertical(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        
        // Take the first string as reference
        String first = strs[0];
        
        // Check each character position
        for (int i = 0; i < first.length(); i++) {
            char c = first.charAt(i);
            
            // Check this character in all other strings
            for (int j = 1; j < strs.length; j++) {
                // If this string is shorter or character doesn't match
                if (i >= strs[j].length() || strs[j].charAt(i) != c) {
                    return first.substring(0, i);
                }
            }
        }
        
        return first;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Common prefix exists
        String[] strs1 = {"flower", "flow", "flight"};
        String result1 = longestCommonPrefix(strs1);
        System.out.println("Longest Common Prefix Problem:");
        System.out.println("Input: [\"flower\", \"flow\", \"flight\"]");
        System.out.println("Output: \"" + result1 + "\"");
        System.out.println("Expected: \"fl\"");
        System.out.println();
        
        // Test case 2: No common prefix
        String[] strs2 = {"dog", "racecar", "car"};
        String result2 = longestCommonPrefix(strs2);
        System.out.println("Input: [\"dog\", \"racecar\", \"car\"]");
        System.out.println("Output: \"" + result2 + "\"");
        System.out.println("Expected: \"\"");
        System.out.println();
        
        // Test case 3: All strings identical
        String[] strs3 = {"test", "test", "test"};
        String result3 = longestCommonPrefixVertical(strs3);
        System.out.println("Input: [\"test\", \"test\", \"test\"]");
        System.out.println("Output (Vertical): \"" + result3 + "\"");
        System.out.println("Expected: \"test\"");
    }
}