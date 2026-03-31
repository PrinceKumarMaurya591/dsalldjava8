package com.dsa.string;

/**
 * Problem: Longest Repeating Character Replacement
 * 
 * Problem Statement:
 * You are given a string s and an integer k. You can choose any character of the string
 * and change it to any other uppercase English character. You can perform this operation
 * at most k times.
 * Return the length of the longest substring containing the same letter after performing
 * the above operations.
 * 
 * Assumptions:
 * - 1 <= s.length <= 10^5
 * - s consists of only uppercase English letters
 * - 0 <= k <= s.length
 * 
 * Optimal Solution: O(n) time, O(1) space using sliding window with frequency array
 * 
 * Algorithm Explanation (Sliding Window):
 * 1. Use two pointers (left and right) to represent the current window
 * 2. Maintain a frequency array of size 26 to count characters in the window
 * 3. Track the maximum frequency of any character in the current window
 * 4. Expand the window by moving the right pointer
 * 5. For each window, calculate: window size - max frequency
 * 6. If (window size - max frequency) > k, we need to shrink the window from left
 * 7. The answer is the maximum window size found
 * 
 * Key Insight:
 * - We don't need to actually replace characters
 * - We just need to find the longest window where (window size - max frequency) <= k
 * - This means we can replace the other characters to match the most frequent character
 * 
 * Dry Run Example:
 * Input: s = "AABABBA", k = 1
 * 
 * left = 0, right = 0, maxFreq = 0, maxLength = 0
 * freq = [0,0,...]
 * 
 * Step 1: right=0, char='A', freq[A]=1, maxFreq=1
 *   window size = 1, 1-1=0 <= k=1 → maxLength=1
 * 
 * Step 2: right=1, char='A', freq[A]=2, maxFreq=2
 *   window size = 2, 2-2=0 <=1 → maxLength=2
 * 
 * Step 3: right=2, char='B', freq[B]=1, maxFreq=2
 *   window size = 3, 3-2=1 <=1 → maxLength=3
 * 
 * Step 4: right=3, char='A', freq[A]=3, maxFreq=3
 *   window size = 4, 4-3=1 <=1 → maxLength=4
 * 
 * Step 5: right=4, char='B', freq[B]=2, maxFreq=3
 *   window size = 5, 5-3=2 >1 → shrink left
 *   left=1, freq[A]=2, maxFreq=3? Actually maxFreq needs recalculation
 *   Continue...
 * 
 * Result: 4 (window "AABA" with one replacement B→A)
 */
public class LongestRepeatingCharacterReplacement {
    
    /**
     * Finds the length of the longest substring with same character after at most k replacements
     * 
     * @param s the input string (uppercase English letters)
     * @param k maximum number of replacements allowed
     * @return length of longest valid substring
     */
    public static int characterReplacement(String s, int k) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        int[] freq = new int[26];  // Frequency of characters in current window
        int left = 0;              // Left pointer of sliding window
        int maxFreq = 0;           // Maximum frequency of any character in current window
        int maxLength = 0;         // Maximum valid window length found
        
        for (int right = 0; right < s.length(); right++) {
            // Add current character to window
            char currentChar = s.charAt(right);
            freq[currentChar - 'A']++;
            
            // Update max frequency in current window
            maxFreq = Math.max(maxFreq, freq[currentChar - 'A']);
            
            // Calculate current window size
            int windowSize = right - left + 1;
            
            // If we need more than k replacements, shrink window from left
            if (windowSize - maxFreq > k) {
                // Remove leftmost character from window
                char leftChar = s.charAt(left);
                freq[leftChar - 'A']--;
                left++;
                
                // Note: We don't update maxFreq here because:
                // 1. It doesn't affect the result (we only care about max length)
                // 2. Updating maxFreq would require O(26) scan each time
                // 3. Even if maxFreq is not accurate, it only makes our condition stricter
                //    which is safe (we might shrink more than needed, but that's okay)
            }
            
            // Update max length
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Alternative implementation with explicit max frequency recalculation
     * More intuitive but slightly less efficient
     */
    public static int characterReplacementWithRecalculation(String s, int k) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        int[] freq = new int[26];
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            freq[s.charAt(right) - 'A']++;
            
            // Recalculate max frequency in current window
            int maxFreq = getMaxFrequency(freq);
            
            // If window is invalid, shrink from left
            while ((right - left + 1) - maxFreq > k) {
                freq[s.charAt(left) - 'A']--;
                left++;
                maxFreq = getMaxFrequency(freq);  // Recalculate after shrinking
            }
            
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Helper method to get maximum frequency from frequency array
     */
    private static int getMaxFrequency(int[] freq) {
        int max = 0;
        for (int count : freq) {
            max = Math.max(max, count);
        }
        return max;
    }
    
    /**
     * Optimized version for better understanding of the algorithm
     */
    public static int characterReplacementOptimized(String s, int k) {
        int[] freq = new int[26];
        int left = 0;
        int maxFreq = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            // Expand window to include s[right]
            int rightCharIndex = s.charAt(right) - 'A';
            freq[rightCharIndex]++;
            
            // Update the maximum frequency in the current window
            maxFreq = Math.max(maxFreq, freq[rightCharIndex]);
            
            // Current window size
            int windowSize = right - left + 1;
            
            // If the window needs more than k replacements, it's invalid
            // Shrink the window from the left
            if (windowSize - maxFreq > k) {
                // Remove leftmost character
                int leftCharIndex = s.charAt(left) - 'A';
                freq[leftCharIndex]--;
                left++;
                
                // Window size decreased, but we don't update maxFreq
                // This is the key optimization: we only need to know if
                // maxFreq could have been larger, but since we're looking
                // for max length, we can keep the old maxFreq
            }
            
            // Update answer
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Example from problem
        String s1 = "AABABBA";
        int k1 = 1;
        int result1 = characterReplacement(s1, k1);
        System.out.println("Longest Repeating Character Replacement Problem:");
        System.out.println("Input: s = \"" + s1 + "\", k = " + k1);
        System.out.println("Output: " + result1);
        System.out.println("Expected: 4");
        System.out.println();
        
        // Test case 2: All same characters
        String s2 = "AAAAA";
        int k2 = 2;
        int result2 = characterReplacementWithRecalculation(s2, k2);
        System.out.println("Input: s = \"" + s2 + "\", k = " + k2);
        System.out.println("Output (with recalculation): " + result2);
        System.out.println("Expected: 5");
        System.out.println();
        
        // Test case 3: Need multiple replacements
        String s3 = "ABAB";
        int k3 = 2;
        int result3 = characterReplacementOptimized(s3, k3);
        System.out.println("Input: s = \"" + s3 + "\", k = " + k3);
        System.out.println("Output (optimized): " + result3);
        System.out.println("Expected: 4");
        System.out.println();
        
        // Test case 4: Complex case
        String s4 = "AABAABB";
        int k4 = 2;
        int result4 = characterReplacement(s4, k4);
        System.out.println("Input: s = \"" + s4 + "\", k = " + k4);
        System.out.println("Output: " + result4);
        System.out.println("Expected: 6 (AABAABB → AAAAAAB with 2 replacements)");
        
        // Dry run visualization
        System.out.println("\nDry run for s = \"AABABBA\", k = 1:");
        System.out.println("Window evolution:");
        String s = "AABABBA";
        int k = 1;
        int[] freq = new int[26];
        int left = 0;
        int maxFreq = 0;
        
        for (int right = 0; right < s.length(); right++) {
            freq[s.charAt(right) - 'A']++;
            maxFreq = Math.max(maxFreq, freq[s.charAt(right) - 'A']);
            
            while ((right - left + 1) - maxFreq > k) {
                freq[s.charAt(left) - 'A']--;
                left++;
                // Recalculate maxFreq for accurate dry run
                maxFreq = getMaxFrequency(freq);
            }
            
            System.out.println("  Window [" + left + "," + right + "]: \"" + 
                             s.substring(left, right + 1) + "\", size=" + (right-left+1) + 
                             ", maxFreq=" + maxFreq);
        }
    }
}