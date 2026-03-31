package com.dsa.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class StringProblemsTest {

    // =========================================================================
    // 1. LONGEST COMMON PREFIX TEST
    // =========================================================================
    @Test
    public void testLongestCommonPrefix() {
        String[] strs1 = {"flower", "flow", "flight"};
        assertEquals("fl", StringProblems.longestCommonPrefix(strs1));
        
        String[] strs2 = {"dog", "racecar", "car"};
        assertEquals("", StringProblems.longestCommonPrefix(strs2));
        
        String[] strs3 = {"interspecies", "interstellar", "interstate"};
        assertEquals("inters", StringProblems.longestCommonPrefix(strs3));
        
        // Test vertical scanning method
        assertEquals("fl", StringProblems.longestCommonPrefixVertical(strs1));
        assertEquals("", StringProblems.longestCommonPrefixVertical(strs2));
        assertEquals("inters", StringProblems.longestCommonPrefixVertical(strs3));
    }

    // =========================================================================
    // 2. FIZZ BUZZ TEST
    // =========================================================================
    @Test
    public void testFizzBuzz() {
        List<String> result1 = StringProblems.fizzBuzz(15);
        List<String> expected1 = Arrays.asList(
            "1", "2", "Fizz", "4", "Buzz", "Fizz", "7", "8", "Fizz", "Buzz",
            "11", "Fizz", "13", "14", "FizzBuzz"
        );
        assertEquals(expected1, result1);
        
        List<String> result2 = StringProblems.fizzBuzz(5);
        List<String> expected2 = Arrays.asList("1", "2", "Fizz", "4", "Buzz");
        assertEquals(expected2, result2);
        
        // Test no-modulo version
        List<String> result3 = StringProblems.fizzBuzzNoMod(15);
        assertEquals(expected1, result3);
    }

    // =========================================================================
    // 3. LONGEST REPEATING CHARACTER REPLACEMENT TEST
    // =========================================================================
    @Test
    public void testCharacterReplacement() {
        assertEquals(4, StringProblems.characterReplacement("AABABBA", 1));
        assertEquals(4, StringProblems.characterReplacement("ABAB", 2));
        assertEquals(5, StringProblems.characterReplacement("AABA", 0));
        assertEquals(7, StringProblems.characterReplacement("AAAAAAA", 0));
        
        // Edge cases
        assertEquals(0, StringProblems.characterReplacement("", 5));
        assertEquals(1, StringProblems.characterReplacement("A", 0));
        assertEquals(2, StringProblems.characterReplacement("AB", 1));
    }

    // =========================================================================
    // 4. LONGEST SUBSTRING WITHOUT REPEATING CHARACTERS TEST
    // =========================================================================
    @Test
    public void testLengthOfLongestSubstring() {
        assertEquals(3, StringProblems.lengthOfLongestSubstring("abcabcbb"));
        assertEquals(1, StringProblems.lengthOfLongestSubstring("bbbbb"));
        assertEquals(3, StringProblems.lengthOfLongestSubstring("pwwkew"));
        assertEquals(0, StringProblems.lengthOfLongestSubstring(""));
        assertEquals(1, StringProblems.lengthOfLongestSubstring(" "));
        assertEquals(2, StringProblems.lengthOfLongestSubstring("au"));
        assertEquals(3, StringProblems.lengthOfLongestSubstring("dvdf"));
        
        // Test array version
        assertEquals(3, StringProblems.lengthOfLongestSubstringArray("abcabcbb"));
        assertEquals(1, StringProblems.lengthOfLongestSubstringArray("bbbbb"));
        assertEquals(3, StringProblems.lengthOfLongestSubstringArray("pwwkew"));
    }

    // =========================================================================
    // 5. MINIMUM WINDOW SUBSTRING TEST
    // =========================================================================
    @Test
    public void testMinWindow() {
        assertEquals("BANC", StringProblems.minWindow("ADOBECODEBANC", "ABC"));
        assertEquals("a", StringProblems.minWindow("a", "a"));
        assertEquals("", StringProblems.minWindow("a", "aa"));
        assertEquals("", StringProblems.minWindow("", "ABC"));
        assertEquals("", StringProblems.minWindow("ADOBECODEBANC", ""));
        
        // Additional test cases
        assertEquals("aa", StringProblems.minWindow("aa", "aa"));
        assertEquals("baca", StringProblems.minWindow("acbbaca", "aba"));
    }

    // =========================================================================
    // 6. VALID ANAGRAM TEST
    // =========================================================================
    @Test
    public void testIsAnagram() {
        assertTrue(StringProblems.isAnagram("anagram", "nagaram"));
        assertFalse(StringProblems.isAnagram("rat", "car"));
        assertTrue(StringProblems.isAnagram("", ""));
        assertFalse(StringProblems.isAnagram("a", "b"));
        assertTrue(StringProblems.isAnagram("listen", "silent"));
        
        // Test sorting version
        assertTrue(StringProblems.isAnagramSort("anagram", "nagaram"));
        assertFalse(StringProblems.isAnagramSort("rat", "car"));
        assertTrue(StringProblems.isAnagramSort("", ""));
    }

    // =========================================================================
    // 7. GROUP ANAGRAMS TEST
    // =========================================================================
    @Test
    public void testGroupAnagrams() {
        String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result = StringProblems.groupAnagrams(strs);
        
        // Check that we have 3 groups
        assertEquals(3, result.size());
        
        // Check each group
        boolean foundEatGroup = false;
        boolean foundTanGroup = false;
        boolean foundBatGroup = false;
        
        for (List<String> group : result) {
            if (group.contains("eat")) {
                foundEatGroup = true;
                assertEquals(3, group.size());
                assertTrue(group.containsAll(Arrays.asList("eat", "tea", "ate")));
            } else if (group.contains("tan")) {
                foundTanGroup = true;
                assertEquals(2, group.size());
                assertTrue(group.containsAll(Arrays.asList("tan", "nat")));
            } else if (group.contains("bat")) {
                foundBatGroup = true;
                assertEquals(1, group.size());
                assertTrue(group.contains("bat"));
            }
        }
        
        assertTrue(foundEatGroup && foundTanGroup && foundBatGroup);
        
        // Test frequency version
        List<List<String>> result2 = StringProblems.groupAnagramsFrequency(strs);
        assertEquals(3, result2.size());
    }

    // =========================================================================
    // 8. VALID PARENTHESES TEST
    // =========================================================================
    @Test
    public void testIsValidParentheses() {
        assertTrue(StringProblems.isValidParentheses("()"));
        assertTrue(StringProblems.isValidParentheses("()[]{}"));
        assertFalse(StringProblems.isValidParentheses("(]"));
        assertFalse(StringProblems.isValidParentheses("([)]"));
        assertTrue(StringProblems.isValidParentheses("{[]}"));
        assertFalse(StringProblems.isValidParentheses("]"));
        assertFalse(StringProblems.isValidParentheses("("));
        assertTrue(StringProblems.isValidParentheses(""));
        
        // Test array version
        assertTrue(StringProblems.isValidParenthesesArray("()"));
        assertTrue(StringProblems.isValidParenthesesArray("()[]{}"));
        assertFalse(StringProblems.isValidParenthesesArray("(]"));
        assertFalse(StringProblems.isValidParenthesesArray("([)]"));
        assertTrue(StringProblems.isValidParenthesesArray("{[]}"));
    }

    // =========================================================================
    // COMPREHENSIVE TEST RUN
    // =========================================================================
    @Test
    public void testAllProblems() {
        System.out.println("Running comprehensive tests for all 8 string problems...");
        
        // Run all individual tests
        testLongestCommonPrefix();
        testFizzBuzz();
        testCharacterReplacement();
        testLengthOfLongestSubstring();
        testMinWindow();
        testIsAnagram();
        testGroupAnagrams();
        testIsValidParentheses();
        
        System.out.println("All 8 string problem tests passed!");
    }
    
    // =========================================================================
    // ADDITIONAL EDGE CASE TESTS
    // =========================================================================
    @Test
    public void testEdgeCases() {
        // Test empty arrays and strings
        assertEquals("", StringProblems.longestCommonPrefix(new String[]{}));
        assertEquals("", StringProblems.longestCommonPrefix(new String[]{""}));
        assertEquals("a", StringProblems.longestCommonPrefix(new String[]{"a"}));
        
        // Test Fizz Buzz with 0
        List<String> result = StringProblems.fizzBuzz(0);
        assertTrue(result.isEmpty());
        
        // Test character replacement with empty string
        assertEquals(0, StringProblems.characterReplacement("", 5));
        
        // Test min window with empty target
        assertEquals("", StringProblems.minWindow("abc", ""));
        
        // Test anagram with different lengths
        assertFalse(StringProblems.isAnagram("abc", "ab"));
        
        // Test parentheses with single character
        assertFalse(StringProblems.isValidParentheses("]"));
        assertFalse(StringProblems.isValidParentheses("["));
    }
    
    // =========================================================================
    // PERFORMANCE TESTS (basic sanity checks)
    // =========================================================================
    @Test
    public void testPerformanceScenarios() {
        // Test long string without repeating characters
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longString.append((char)('a' + (i % 26)));
        }
        assertEquals(26, StringProblems.lengthOfLongestSubstring(longString.toString()));
        
        // Test Fizz Buzz with large n
        List<String> largeFizzBuzz = StringProblems.fizzBuzz(1000);
        assertEquals(1000, largeFizzBuzz.size());
        assertEquals("FizzBuzz", largeFizzBuzz.get(14)); // 15th element (0-indexed 14)
        
        // Test group anagrams with many strings
        String[] manyAnagrams = new String[100];
        for (int i = 0; i < 100; i++) {
            manyAnagrams[i] = "listen"; // All the same anagram
        }
        List<List<String>> grouped = StringProblems.groupAnagrams(manyAnagrams);
        assertEquals(1, grouped.size());
        assertEquals(100, grouped.get(0).size());
    }
}