package com.dsa.array;

/**
 * Problem 20: Valid Anagram
 */
public class ValidAnagram {
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        
        int[] freq = new int[26];
        for (char c : s.toCharArray()) freq[c - 'a']++;
        for (char c : t.toCharArray()) {
            if (--freq[c - 'a'] < 0) return false;
        }
        return true;
    }
    
    public static void main(String[] args) {
        System.out.println("Valid Anagram:");
        System.out.println("Input: \"anagram\", \"nagaram\"");
        System.out.println("Output: " + isAnagram("anagram", "nagaram"));
        System.out.println("Expected: true");
    }
}