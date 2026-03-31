package com.dsa.array;

/**
 * Problem 14: Verifying an Alien Dictionary
 * 
 * Problem Statement:
 * Given a sequence of words written in an alien language and the order
 * of its alphabet, return true if the words are sorted lexicographically according
 * to the alien alphabet.
 * 
 * Assumptions:
 * - All characters are lowercase English letters
 * - The order string contains all 26 letters exactly once
 * - Words are non-empty
 * - Comparison is based on alien dictionary order, not English alphabetical order
 * 
 * Optimal Solution: O(N * M) time where N=words, M=avg word length, O(1) space
 * 
 * Algorithm Explanation:
 * 1. Create a mapping from alien character to its position in the order
 * 2. Compare each adjacent pair of words (words[i] and words[i+1])
 * 3. For each pair, compare characters until a difference is found:
 *    - If characters are different, check their order in alien alphabet
 *    - If order is incorrect (c1 > c2 in alien order), return false
 * 4. If no difference found in common prefix, check lengths:
 *    - If word1 is longer than word2, return false (shorter word should come first)
 * 5. If all pairs are valid, return true
 * 
 * Dry Run Example:
 * Input: words = ["hello","leetcode"], order = "hlabcdefgijkmnopqrstuvwxyz"
 * 
 * Create orderMap: h→0, l→1, a→2, ...
 * Compare "hello" and "leetcode":
 *   Compare h(0) and l(1): 0 < 1 → words are sorted
 * 
 * Result: true
 */
public class VerifyingAnAlienDictionary {
    
    /**
     * Checks if words are sorted according to alien dictionary order
     * 
     * @param words the array of words to check
     * @param order the alien alphabet order
     * @return true if words are sorted, false otherwise
     */
    public static boolean isAlienSorted(String[] words, String order) {
        // Create mapping from alien character to position
        int[] orderMap = new int[26];
        for (int i = 0; i < order.length(); i++) {
            orderMap[order.charAt(i) - 'a'] = i;
        }
        
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            
            // Compare characters until a difference is found
            int minLength = Math.min(word1.length(), word2.length());
            boolean foundDifference = false;
            
            for (int j = 0; j < minLength; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);
                
                if (c1 != c2) {
                    if (orderMap[c1 - 'a'] > orderMap[c2 - 'a']) {
                        return false;
                    }
                    foundDifference = true;
                    break;
                }
            }
            
            // If no difference found in common prefix, check lengths
            if (!foundDifference && word1.length() > word2.length()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Verifying an Alien Dictionary Problem:");
        
        // Test 1: Valid order
        String[] words1 = {"hello", "leetcode"};
        String order1 = "hlabcdefgijkmnopqrstuvwxyz";
        
        System.out.println("\nTest 1:");
        System.out.println("Words: [\"hello\", \"leetcode\"]");
        System.out.println("Order: \"" + order1 + "\"");
        boolean result1 = isAlienSorted(words1, order1);
        System.out.println("Output: " + result1);
        System.out.println("Expected: true");
        System.out.println("Explanation: 'h' comes before 'l' in alien order");
        
        // Test 2: Invalid order
        String[] words2 = {"word", "world", "row"};
        String order2 = "worldabcefghijkmnpqstuvxyz";
        
        System.out.println("\nTest 2:");
        System.out.println("Words: [\"word\", \"world\", \"row\"]");
        System.out.println("Order: \"" + order2 + "\"");
        boolean result2 = isAlienSorted(words2, order2);
        System.out.println("Output: " + result2);
        System.out.println("Expected: false");
        System.out.println("Explanation: 'word' should come before 'world' but 'd' > 'l' in alien order");
        
        // Test 3: Same prefix, different lengths
        String[] words3 = {"apple", "app"};
        String order3 = "abcdefghijklmnopqrstuvwxyz";
        
        System.out.println("\nTest 3:");
        System.out.println("Words: [\"apple\", \"app\"]");
        System.out.println("Order: \"" + order3 + "\"");
        boolean result3 = isAlienSorted(words3, order3);
        System.out.println("Output: " + result3);
        System.out.println("Expected: false");
        System.out.println("Explanation: Shorter word 'app' should come before longer word 'apple'");
        
        // Test 4: Single word (always sorted)
        String[] words4 = {"hello"};
        String order4 = "abcdefghijklmnopqrstuvwxyz";
        
        System.out.println("\nTest 4:");
        System.out.println("Words: [\"hello\"]");
        System.out.println("Order: \"" + order4 + "\"");
        boolean result4 = isAlienSorted(words4, order4);
        System.out.println("Output: " + result4);
        System.out.println("Expected: true");
        System.out.println("Explanation: Single word is always sorted");
        
        // Test 5: Multiple words with same characters
        String[] words5 = {"kuvp", "q"};
        String order5 = "ngxlkthsjuoqcpavbfdermiywz";
        
        System.out.println("\nTest 5:");
        System.out.println("Words: [\"kuvp\", \"q\"]");
        System.out.println("Order: \"" + order5 + "\"");
        boolean result5 = isAlienSorted(words5, order5);
        System.out.println("Output: " + result5);
        System.out.println("Expected: true");
        System.out.println("Explanation: All characters in 'kuvp' come before 'q' in alien order");
    }
}