package com.dsa.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem: Group Anagrams
 * 
 * Problem Statement:
 * Given an array of strings strs, group the anagrams together. You can return the answer in any order.
 * An anagram is a word or phrase formed by rearranging the letters of a different word or phrase,
 * typically using all the original letters exactly once.
 * 
 * Assumptions:
 * - 1 <= strs.length <= 10^4
 * - 0 <= strs[i].length <= 100
 * - strs[i] consists of lowercase English letters
 * 
 * Optimal Solution: O(n * k log k) time, O(n * k) space where n=strs.length, k=avg string length
 * Using HashMap with sorted string as key
 * 
 * Algorithm Explanation:
 * 1. Create a HashMap where key is the sorted version of a string, value is list of anagrams
 * 2. For each string in the input array:
 *    a. Convert string to char array and sort it
 *    b. Use sorted string as key in HashMap
 *    c. Add original string to the list for that key
 * 3. Return all values from the HashMap as a list of lists
 * 
 * Alternative Approaches:
 * 1. Frequency count as key: Use character frequency array converted to string
 * 2. Prime number product: Assign prime numbers to letters, use product as key
 * 
 * Key Insight:
 * - Anagrams have the same sorted representation
 * - Grouping by sorted string efficiently clusters anagrams
 * 
 * Dry Run Example:
 * Input: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
 * 
 * Step 1: "eat" → sorted "aet" → map["aet"] = ["eat"]
 * Step 2: "tea" → sorted "aet" → map["aet"] = ["eat", "tea"]
 * Step 3: "tan" → sorted "ant" → map["ant"] = ["tan"]
 * Step 4: "ate" → sorted "aet" → map["aet"] = ["eat", "tea", "ate"]
 * Step 5: "nat" → sorted "ant" → map["ant"] = ["tan", "nat"]
 * Step 6: "bat" → sorted "abt" → map["abt"] = ["bat"]
 * 
 * Result: [["eat","tea","ate"], ["tan","nat"], ["bat"]]
 */
public class GroupAnagrams {
    
    /**
     * Standard solution using sorting as key (O(n * k log k) time)
     * 
     * @param strs array of strings to group
     * @return list of grouped anagrams
     */
    public static List<List<String>> groupAnagrams(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        Map<String, List<String>> anagramMap = new HashMap<>();
        
        for (String str : strs) {
            // Convert string to char array and sort
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            String sortedStr = new String(charArray);
            
            // Add to map
            anagramMap.computeIfAbsent(sortedStr, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramMap.values());
    }
    
    /**
     * Optimized solution using frequency count as key (O(n * k) time)
     * Avoids sorting each string, uses character frequency instead
     * 
     * @param strs array of strings to group
     * @return list of grouped anagrams
     */
    public static List<List<String>> groupAnagramsFrequency(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        Map<String, List<String>> anagramMap = new HashMap<>();
        
        for (String str : strs) {
            // Create frequency array for 26 lowercase letters
            int[] frequency = new int[26];
            for (char c : str.toCharArray()) {
                frequency[c - 'a']++;
            }
            
            // Convert frequency array to string key
            StringBuilder keyBuilder = new StringBuilder();
            for (int count : frequency) {
                keyBuilder.append('#').append(count);
            }
            String key = keyBuilder.toString();
            
            // Add to map
            anagramMap.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramMap.values());
    }
    
    /**
     * Solution using prime number product as key (O(n * k) time)
     * Each letter gets a unique prime number, anagrams have same product
     * 
     * @param strs array of strings to group
     * @return list of grouped anagrams
     */
    public static List<List<String>> groupAnagramsPrime(String[] strs) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<>();
        }
        
        // First 26 prime numbers for a-z
        int[] primes = {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
            43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101
        };
        
        Map<Long, List<String>> anagramMap = new HashMap<>();
        
        for (String str : strs) {
            // Calculate product of prime numbers for each character
            long product = 1;
            for (char c : str.toCharArray()) {
                product *= primes[c - 'a'];
            }
            
            // Add to map
            anagramMap.computeIfAbsent(product, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(anagramMap.values());
    }
    
    /**
     * Alternative implementation with character counting
     * Uses array of size 26 and builds key without delimiters
     * 
     * @param strs array of strings to group
     * @return list of grouped anagrams
     */
    public static List<List<String>> groupAnagramsCount(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String str : strs) {
            // Count characters
            int[] count = new int[26];
            for (char c : str.toCharArray()) {
                count[c - 'a']++;
            }
            
            // Build key as "count[0]#count[1]#...#count[25]"
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append(count[i]).append('#');
            }
            String key = sb.toString();
            
            // Group anagrams
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(map.values());
    }
    
    /**
     * Solution for case-insensitive anagrams
     * Converts all strings to lowercase before processing
     * 
     * @param strs array of strings to group
     * @return list of grouped anagrams
     */
    public static List<List<String>> groupAnagramsCaseInsensitive(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        
        for (String str : strs) {
            // Convert to lowercase for case-insensitive comparison
            String lowerStr = str.toLowerCase();
            char[] chars = lowerStr.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        
        return new ArrayList<>(map.values());
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Example from problem
        String[] strs1 = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result1 = groupAnagrams(strs1);
        System.out.println("Group Anagrams Problem:");
        System.out.println("Input: [\"eat\", \"tea\", \"tan\", \"ate\", \"nat\", \"bat\"]");
        System.out.println("Output: " + result1);
        System.out.println("Expected: [[\"eat\",\"tea\",\"ate\"], [\"tan\",\"nat\"], [\"bat\"]]");
        System.out.println();
        
        // Test case 2: Empty array
        String[] strs2 = {};
        List<List<String>> result2 = groupAnagramsFrequency(strs2);
        System.out.println("Input: []");
        System.out.println("Output (Frequency): " + result2);
        System.out.println("Expected: []");
        System.out.println();
        
        // Test case 3: Single string
        String[] strs3 = {"hello"};
        List<List<String>> result3 = groupAnagramsPrime(strs3);
        System.out.println("Input: [\"hello\"]");
        System.out.println("Output (Prime): " + result3);
        System.out.println("Expected: [[\"hello\"]]");
        System.out.println();
        
        // Test case 4: All anagrams
        String[] strs4 = {"abc", "bca", "cab", "acb", "cba", "bac"};
        List<List<String>> result4 = groupAnagramsCount(strs4);
        System.out.println("Input: [\"abc\", \"bca\", \"cab\", \"acb\", \"cba\", \"bac\"]");
        System.out.println("Output (Count): " + result4);
        System.out.println("Expected: [[\"abc\",\"bca\",\"cab\",\"acb\",\"cba\",\"bac\"]]");
        System.out.println();
        
        // Test case 5: Case insensitive
        String[] strs5 = {"Hello", "hello", "Olleh", "World"};
        List<List<String>> result5 = groupAnagramsCaseInsensitive(strs5);
        System.out.println("Input: [\"Hello\", \"hello\", \"Olleh\", \"World\"]");
        System.out.println("Output (Case Insensitive): " + result5);
        System.out.println("Expected: [[\"Hello\",\"hello\",\"Olleh\"], [\"World\"]]");
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. Sorting Key: O(n * k log k) time - Simple, works well for short strings");
        System.out.println("2. Frequency Key: O(n * k) time - Better for long strings, avoids sorting");
        System.out.println("3. Prime Product: O(n * k) time - Mathematical approach, risk of overflow");
        System.out.println("4. Count Key: O(n * k) time - Similar to frequency, uses delimiter");
        
        // Dry run visualization
        System.out.println("\nDry run for strs = [\"eat\", \"tea\", \"tan\", \"ate\", \"nat\", \"bat\"]:");
        System.out.println("Step-by-step map building:");
        
        Map<String, List<String>> demoMap = new HashMap<>();
        String[] demoStrs = {"eat", "tea", "tan", "ate", "nat", "bat"};
        
        for (String str : demoStrs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            
            List<String> group = demoMap.getOrDefault(key, new ArrayList<>());
            group.add(str);
            demoMap.put(key, group);
            
            System.out.println("  Process \"" + str + "\":");
            System.out.println("    Sorted: \"" + key + "\"");
            System.out.println("    Current groups: " + demoMap);
        }
        
        System.out.println("\nFinal groups: " + new ArrayList<>(demoMap.values()));
        
        // Show frequency key approach
        System.out.println("\nFrequency key approach for \"eat\" and \"tea\":");
        String str1 = "eat";
        String str2 = "tea";
        
        int[] freq1 = new int[26];
        int[] freq2 = new int[26];
        
        for (char c : str1.toCharArray()) freq1[c - 'a']++;
        for (char c : str2.toCharArray()) freq2[c - 'a']++;
        
        StringBuilder key1 = new StringBuilder();
        StringBuilder key2 = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            key1.append('#').append(freq1[i]);
            key2.append('#').append(freq2[i]);
        }
        
        System.out.println("  \"eat\" frequency key: " + key1.toString());
        System.out.println("  \"tea\" frequency key: " + key2.toString());
        System.out.println("  Keys equal? " + key1.toString().equals(key2.toString()));
    }
}