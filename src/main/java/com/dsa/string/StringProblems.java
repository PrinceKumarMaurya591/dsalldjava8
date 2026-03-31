package com.dsa.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Comprehensive collection of string problems with optimal solutions.
 * Each problem includes:
 * 1. Problem statement
 * 2. Optimal solution with time and space complexity
 * 3. Algorithm explanation
 * 4. Dry run example
 * 5. Implementation
 * 
 * Timestamps from video: 
 * Longest Common Prefix: 00:06:38
 * Fizz Buzz: 00:15:00
 * Longest Repeating Character Replacement: 00:23:02
 * Longest Substring Without Repeating Characters: 00:44:14
 * Minimum Window Substring: 00:55:29
 * Valid Anagram: 01:18:29
 * Group Anagrams: 01:29:46
 * Valid Parentheses: 01:42:41
 */
public class StringProblems {

    // =========================================================================
    // 1. LONGEST COMMON PREFIX
    // =========================================================================
    
    /**
     * Problem: Write a function to find the longest common prefix string amongst
     * an array of strings. If there is no common prefix, return an empty string "".
     * 
     * Assumptions:
     * - 1 <= strs.length <= 200
     * - 0 <= strs[i].length <= 200
     * - strs[i] consists of only lowercase English letters
     * 
     * Optimal Solution: O(S) time where S is sum of all characters, O(1) space
     * using vertical scanning
     */
    public static String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        
        // Take first string as reference
        String prefix = strs[0];
        
        for (int i = 1; i < strs.length; i++) {
            // Reduce prefix until it matches current string
            while (strs[i].indexOf(prefix) != 0) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }
        
        return prefix;
    }
    
    /**
     * Alternative optimal solution: O(S) time, O(1) space using vertical scanning
     */
    public static String longestCommonPrefixVertical(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        
        // Iterate through characters of first string
        for (int i = 0; i < strs[0].length(); i++) {
            char c = strs[0].charAt(i);
            
            // Compare with other strings
            for (int j = 1; j < strs.length; j++) {
                if (i == strs[j].length() || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        
        return strs[0];
    }
    
    /**
     * Dry Run for Longest Common Prefix:
     * Input: strs = ["flower", "flow", "flight"]
     * 
     * Using first solution:
     *   prefix = "flower"
     *   Compare with "flow": "flow".indexOf("flower") = -1 → prefix = "flowe"
     *   "flow".indexOf("flowe") = -1 → prefix = "flow"
     *   "flow".indexOf("flow") = 0 ✓
     *   Compare with "flight": "flight".indexOf("flow") = -1 → prefix = "flo"
     *   "flight".indexOf("flo") = -1 → prefix = "fl"
     *   "flight".indexOf("fl") = 0 ✓
     * 
     * Result: "fl"
     */

    // =========================================================================
    // 2. FIZZ BUZZ
    // =========================================================================
    
    /**
     * Problem: Given an integer n, return a string array answer (1-indexed) where:
     * - answer[i] == "FizzBuzz" if i is divisible by 3 and 5
     * - answer[i] == "Fizz" if i is divisible by 3
     * - answer[i] == "Buzz" if i is divisible by 5
     * - answer[i] == i (as a string) otherwise
     * 
     * Assumptions:
     * - 1 <= n <= 10^4
     * 
     * Optimal Solution: O(n) time, O(n) space
     */
    public static List<String> fizzBuzz(int n) {
        List<String> result = new ArrayList<>();
        
        for (int i = 1; i <= n; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                result.add("FizzBuzz");
            } else if (i % 3 == 0) {
                result.add("Fizz");
            } else if (i % 5 == 0) {
                result.add("Buzz");
            } else {
                result.add(String.valueOf(i));
            }
        }
        
        return result;
    }
    
    /**
     * Alternative solution without modulo operator (for performance)
     */
    public static List<String> fizzBuzzNoMod(int n) {
        List<String> result = new ArrayList<>();
        
        int fizz = 0;
        int buzz = 0;
        
        for (int i = 1; i <= n; i++) {
            fizz++;
            buzz++;
            
            if (fizz == 3 && buzz == 5) {
                result.add("FizzBuzz");
                fizz = 0;
                buzz = 0;
            } else if (fizz == 3) {
                result.add("Fizz");
                fizz = 0;
            } else if (buzz == 5) {
                result.add("Buzz");
                buzz = 0;
            } else {
                result.add(String.valueOf(i));
            }
        }
        
        return result;
    }
    
    /**
     * Dry Run for Fizz Buzz:
     * Input: n = 15
     * 
     * i = 1: "1"
     * i = 2: "2"
     * i = 3: "Fizz"
     * i = 4: "4"
     * i = 5: "Buzz"
     * i = 6: "Fizz"
     * i = 7: "7"
     * i = 8: "8"
     * i = 9: "Fizz"
     * i = 10: "Buzz"
     * i = 11: "11"
     * i = 12: "Fizz"
     * i = 13: "13"
     * i = 14: "14"
     * i = 15: "FizzBuzz"
     * 
     * Result: ["1","2","Fizz","4","Buzz","Fizz","7","8","Fizz","Buzz","11","Fizz","13","14","FizzBuzz"]
     */

    // =========================================================================
    // 3. LONGEST REPEATING CHARACTER REPLACEMENT
    // =========================================================================
    
    /**
     * Problem: Given a string s and an integer k, you can choose any character
     * of the string and change it to any other uppercase English character at most k times.
     * Return the length of the longest substring containing the same letter after
     * performing the above operations.
     * 
     * Assumptions:
     * - 1 <= s.length <= 10^5
     * - s consists of only uppercase English letters
     * - 0 <= k <= s.length
     * 
     * Optimal Solution: O(n) time, O(1) space using sliding window
     */
    public static int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int maxCount = 0;
        int maxLength = 0;
        int left = 0;
        
        for (int right = 0; right < s.length(); right++) {
            // Increment count of current character
            char c = s.charAt(right);
            count[c - 'A']++;
            
            // Update max count of any character in current window
            maxCount = Math.max(maxCount, count[c - 'A']);
            
            // If window size - maxCount > k, we need to shrink window
            while ((right - left + 1) - maxCount > k) {
                char leftChar = s.charAt(left);
                count[leftChar - 'A']--;
                left++;
            }
            
            // Update max length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Dry Run for Longest Repeating Character Replacement:
     * Input: s = "AABABBA", k = 1
     * 
     * Window expansion:
     *   right=0: count[A]=1, maxCount=1, window=1, maxLength=1
     *   right=1: count[A]=2, maxCount=2, window=2, maxLength=2
     *   right=2: count[B]=1, maxCount=2, window=3-2=1 <= k=1, maxLength=3
     *   right=3: count[A]=3, maxCount=3, window=4-3=1 <= 1, maxLength=4
     *   right=4: count[B]=2, maxCount=3, window=5-3=2 > 1 → shrink
     *     left=0: count[A]=2, left=1
     *     window=4-3=1 <= 1, maxLength=4
     *   right=5: count[B]=3, maxCount=3, window=5-3=2 > 1 → shrink
     *     left=1: count[A]=2, left=2
     *     window=4-3=1 <= 1, maxLength=4
     *   right=6: count[A]=3, maxCount=3, window=5-3=2 > 1 → shrink
     *     left=2: count[B]=2, left=3
     *     window=4-3=1 <= 1, maxLength=4
     * 
     * Result: 4 (window "AABA" or "ABBA" after replacement)
     */

    // =========================================================================
    // 4. LONGEST SUBSTRING WITHOUT REPEATING CHARACTERS
    // =========================================================================
    
    /**
     * Problem: Given a string s, find the length of the longest substring
     * without repeating characters.
     * 
     * Assumptions:
     * - 0 <= s.length <= 5 * 10^4
     * - s consists of English letters, digits, symbols and spaces
     * 
     * Optimal Solution: O(n) time, O(min(m, n)) space where m is charset size
     * using sliding window with HashMap
     */
    public static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> charIndex = new HashMap<>();
        int maxLength = 0;
        int left = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            // If character exists in map and is within current window
            if (charIndex.containsKey(c) && charIndex.get(c) >= left) {
                // Move left pointer to right of previous occurrence
                left = charIndex.get(c) + 1;
            }
            
            // Update character's latest index
            charIndex.put(c, right);
            
            // Update max length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Alternative solution using array for ASCII characters (faster)
     */
    public static int lengthOfLongestSubstringArray(String s) {
        int[] index = new int[128]; // ASCII characters
        Arrays.fill(index, -1);
        int maxLength = 0;
        int left = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            
            // If character exists and is within current window
            if (index[c] >= left) {
                left = index[c] + 1;
            }
            
            // Update character's latest index
            index[c] = right;
            
            // Update max length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Dry Run for Longest Substring Without Repeating Characters:
     * Input: s = "abcabcbb"
     * 
     * right=0 (a): charIndex={a:0}, left=0, maxLength=1
     * right=1 (b): charIndex={a:0,b:1}, left=0, maxLength=2
     * right=2 (c): charIndex={a:0,b:1,c:2}, left=0, maxLength=3
     * right=3 (a): a exists at index 0 >= left=0 → left=1, update a:3, maxLength=3
     * right=4 (b): b exists at index 1 >= left=1 → left=2, update b:4, maxLength=3
     * right=5 (c): c exists at index 2 >= left=2 → left=3, update c:5, maxLength=3
     * right=6 (b): b exists at index 4 >= left=3 → left=5, update b:6, maxLength=3
     * right=7 (b): b exists at index 6 >= left=5 → left=7, update b:7, maxLength=3
     * 
     * Result: 3 ("abc")
     */

    // =========================================================================
    // 5. MINIMUM WINDOW SUBSTRING
    // =========================================================================
    
    /**
     * Problem: Given two strings s and t, return the minimum window substring
     * of s such that every character in t (including duplicates) is included
     * in the window. If there is no such substring, return the empty string "".
     * 
     * Assumptions:
     * - m == s.length, n == t.length
     * - 1 <= m, n <= 10^5
     * - s and t consist of uppercase and lowercase English letters
     * 
     * Optimal Solution: O(m + n) time, O(1) space using sliding window
     */
    public static String minWindow(String s, String t) {
        if (s == null || t == null || s.length() < t.length()) {
            return "";
        }
        
        // Frequency map for characters in t
        int[] tCount = new int[128];
        for (char c : t.toCharArray()) {
            tCount[c]++;
        }
        
        // Sliding window variables
        int left = 0, right = 0;
        int minLeft = 0, minLength = Integer.MAX_VALUE;
        int required = t.length(); // Number of characters needed to match t
        int formed = 0; // Number of characters currently matched
        
        // Frequency map for current window
        int[] windowCount = new int[128];
        
        while (right < s.length()) {
            char c = s.charAt(right);
            windowCount[c]++;
            
            // If this character is needed and we haven't exceeded needed count
            if (tCount[c] > 0 && windowCount[c] <= tCount[c]) {
                formed++;
            }
            
            // Try to shrink window while it's valid
            while (formed == required && left <= right) {
                // Update minimum window
                if (right - left + 1 < minLength) {
                    minLength = right - left + 1;
                    minLeft = left;
                }
                
                // Remove left character from window
                char leftChar = s.charAt(left);
                windowCount[leftChar]--;
                
                // If removing this character breaks the requirement
                if (tCount[leftChar] > 0 && windowCount[leftChar] < tCount[leftChar]) {
                    formed--;
                }
                
                left++;
            }
            
            right++;
        }
        
        return minLength == Integer.MAX_VALUE ? "" : s.substring(minLeft, minLeft + minLength);
    }
    
    /**
     * Dry Run for Minimum Window Substring:
     * Input: s = "ADOBECODEBANC", t = "ABC"
     * 
     * tCount: A=1, B=1, C=1
     * 
     * Expand window:
     *   right=0 (A): windowCount[A]=1, formed=1
     *   right=1 (D): windowCount[D]=1, formed=1
     *   right=2 (O): windowCount[O]=1, formed=1
     *   right=3 (B): windowCount[B]=1, formed=2
     *   right=4 (E): windowCount[E]=1, formed=2
     *   right=5 (C): windowCount[C]=1, formed=3 → window valid
     * 
     * Shrink window:
     *   window "ADOBEC" length=6, minLength=6, minLeft=0
     *   left=0: remove A, windowCount[A]=0, formed=2
     * 
     * Continue expanding:
     *   right=6 (O): windowCount[O]=2, formed=2
     *   right=7 (D): windowCount[D]=2, formed=2
     *   right=8 (E): windowCount[E]=2, formed=2
     *   right=9 (B): windowCount[B]=2, formed=2
     *   right=10 (A): windowCount[A]=1, formed=3 → window valid
     * 
     * Shrink window:
     *   window "CODEBA" length=6, minLength=6 (no update)
     *   left=3: remove C, windowCount[C]=0, formed=2
     * 
     * Continue expanding:
     *   right=11 (N): windowCount[N]=1, formed=2
     *   right=12 (C): windowCount[C]=1, formed=3 → window valid
     * 
     * Shrink window:
     *   window "BANC" length=4, minLength=4, minLeft=9
     * 
     * Result: "BANC"
     */

    // =========================================================================
    // 6. VALID ANAGRAM
    // =========================================================================
    
    /**
     * Problem: Given two strings s and t, return true if t is an anagram of s.
     * An anagram is a word formed by rearranging the letters of another word.
     * 
     * Assumptions:
     * - 1 <= s.length, t.length <= 5 * 10^4
     * - s and t consist of lowercase English letters
     * 
     * Optimal Solution: O(n) time, O(1) space using frequency array (26 letters)
     */
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        int[] frequency = new int[26];
        
        // Count characters in s
        for (char c : s.toCharArray()) {
            frequency[c - 'a']++;
        }
        
        // Subtract characters in t
        for (char c : t.toCharArray()) {
            frequency[c - 'a']--;
            // If count goes negative, t has extra character
            if (frequency[c - 'a'] < 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Alternative solution using sorting: O(n log n) time, O(n) space
     */
    public static boolean isAnagramSort(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        char[] sArray = s.toCharArray();
        char[] tArray = t.toCharArray();
        
        Arrays.sort(sArray);
        Arrays.sort(tArray);
        
        return Arrays.equals(sArray, tArray);
    }
    
    /**
     * Dry Run for Valid Anagram:
     * Input: s = "anagram", t = "nagaram"
     * 
     * Count s: a=3, n=1, g=1, r=1, m=1
     * Subtract t: a=0, n=0, g=0, r=0, m=0
     * All counts zero → true
     * 
     * Result: true
     */

    // =========================================================================
    // 7. GROUP ANAGRAMS
    // =========================================================================
    
    /**
     * Problem: Given an array of strings strs, group the anagrams together.
     * An anagram is a word formed by rearranging the letters of another word.
     * 
     * Assumptions:
     * - 1 <= strs.length <= 10^4
     * - 0 <= strs[i].length <= 100
     * - strs[i] consists of lowercase English letters
     * 
     * Optimal Solution: O(n * m log m) time where n=strs.length, m=avg string length
     * O(n * m) space using HashMap with sorted string as key
     */
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> anagramMap = new HashMap<>();
        
        for (String str : strs) {
            // Create frequency key
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            // Add to map
            anagramMap.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramMap.values());
    }
    
    /**
     * Alternative solution using frequency count as key (better for long strings)
     */
    public static List<List<String>> groupAnagramsFrequency(String[] strs) {
        Map<String, List<String>> anagramMap = new HashMap<>();
        
        for (String str : strs) {
            // Create frequency key like "#1#2#0#..." for 26 letters
            int[] count = new int[26];
            for (char c : str.toCharArray()) {
                count[c - 'a']++;
            }
            
            StringBuilder keyBuilder = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                keyBuilder.append('#');
                keyBuilder.append(count[i]);
            }
            String key = keyBuilder.toString();
            
            // Add to map
            anagramMap.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramMap.values());
    }
    
    /**
     * Dry Run for Group Anagrams:
     * Input: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
     * 
     * Process:
     *   "eat" → sorted "aet" → map["aet"] = ["eat"]
     *   "tea" → sorted "aet" → map["aet"] = ["eat", "tea"]
     *   "tan" → sorted "ant" → map["ant"] = ["tan"]
     *   "ate" → sorted "aet" → map["aet"] = ["eat", "tea", "ate"]
     *   "nat" → sorted "ant" → map["ant"] = ["tan", "nat"]
     *   "bat" → sorted "abt" → map["abt"] = ["bat"]
     * 
     * Result: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
     */

    // =========================================================================
    // 8. VALID PARENTHESES
    // =========================================================================
    
    /**
     * Problem: Given a string s containing just the characters '(', ')', '{', '}', 
     * '[' and ']', determine if the input string is valid.
     * 
     * An input string is valid if:
     * 1. Open brackets must be closed by the same type of brackets.
     * 2. Open brackets must be closed in the correct order.
     * 3. Every close bracket has a corresponding open bracket of the same type.
     * 
     * Assumptions:
     * - 1 <= s.length <= 10^4
     * - s consists of parentheses only '()[]{}'
     * 
     * Optimal Solution: O(n) time, O(n) space using stack
     */
    public static boolean isValidParentheses(String s) {
        Stack<Character> stack = new Stack<>();
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                // Push opening brackets
                stack.push(c);
            } else {
                // Check for closing brackets
                if (stack.isEmpty()) {
                    return false;
                }
                
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                    (c == '}' && top != '{') ||
                    (c == ']' && top != '[')) {
                    return false;
                }
            }
        }
        
        return stack.isEmpty();
    }
    
    /**
     * Alternative solution using array as stack (faster)
     */
    public static boolean isValidParenthesesArray(String s) {
        char[] stack = new char[s.length()];
        int top = -1;
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                // Push opening brackets
                stack[++top] = c;
            } else {
                // Check for closing brackets
                if (top == -1) {
                    return false;
                }
                
                char last = stack[top--];
                if ((c == ')' && last != '(') ||
                    (c == '}' && last != '{') ||
                    (c == ']' && last != '[')) {
                    return false;
                }
            }
        }
        
        return top == -1;
    }
    
    /**
     * Dry Run for Valid Parentheses:
     * Input: s = "()[]{}"
     * 
     * Process:
     *   '(' → push '('
     *   ')' → pop '(' → match ✓
     *   '[' → push '['
     *   ']' → pop '[' → match ✓
     *   '{' → push '{'
     *   '}' → pop '{' → match ✓
     * 
     * Result: true
     * 
     * Input: s = "([)]"
     *   '(' → push '('
     *   '[' → push '['
     *   ')' → pop '[' → mismatch → false
     */

    // =========================================================================
    // MAIN METHOD FOR TESTING
    // =========================================================================
    
    /**
     * Main method to test all string problem solutions
     */
    public static void main(String[] args) {
        System.out.println("Testing String Problems Package");
        System.out.println("===============================");
        
        // Test Longest Common Prefix
        String[] strs1 = {"flower", "flow", "flight"};
        String result1 = longestCommonPrefix(strs1);
        System.out.println("Longest Common Prefix Test: " + result1);
        
        // Test Fizz Buzz
        List<String> result2 = fizzBuzz(15);
        System.out.println("Fizz Buzz Test (first 5): " + result2.subList(0, 5));
        
        // Test Longest Repeating Character Replacement
        int result3 = characterReplacement("AABABBA", 1);
        System.out.println("Longest Repeating Character Replacement Test: " + result3);
        
        // Test Longest Substring Without Repeating Characters
        int result4 = lengthOfLongestSubstring("abcabcbb");
        System.out.println("Longest Substring Without Repeating Characters Test: " + result4);
        
        // Test Minimum Window Substring
        String result5 = minWindow("ADOBECODEBANC", "ABC");
        System.out.println("Minimum Window Substring Test: " + result5);
        
        // Test Valid Anagram
        boolean result6 = isAnagram("anagram", "nagaram");
        System.out.println("Valid Anagram Test: " + result6);
        
        // Test Group Anagrams
        String[] strs2 = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result7 = groupAnagrams(strs2);
        System.out.println("Group Anagrams Test: " + result7.size() + " groups");
        
        // Test Valid Parentheses
        boolean result8 = isValidParentheses("()[]{}");
        System.out.println("Valid Parentheses Test: " + result8);
        
        System.out.println("All tests completed!");
    }
}
