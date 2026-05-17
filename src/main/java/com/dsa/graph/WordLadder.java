package com.dsa.graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

// Problem: Word Ladder
// Link: https://leetcode.com/problems/word-ladder/
//
// Given two words, beginWord and endWord, and a dictionary wordList, return the
// number of words in the shortest transformation sequence from beginWord to endWord,
// or 0 if no such sequence exists.
//
// Each transformation must change exactly one letter, and each transformed word
// must exist in the wordList.
//
// Approach: BFS (shortest path in unweighted graph)
// 1. Treat each word as a node, and an edge exists if two words differ by one letter.
// 2. Use BFS from beginWord to find the shortest path to endWord.
// 3. For each word, try changing each character to 'a'-'z' and check if it exists
//    in the word set.
//
// Time Complexity: O(M^2 * N) where M = word length, N = number of words
// Space Complexity: O(M * N)

public class WordLadder {

    public static void main(String[] args) {
        // Test case 1
        String beginWord1 = "hit";
        String endWord1 = "cog";
        List<String> wordList1 = Arrays.asList("hot", "dot", "dog", "lot", "log", "cog");
        System.out.println("Word Ladder length: " + ladderLength(beginWord1, endWord1, wordList1));
        // Expected: 5 (hit -> hot -> dot -> dog -> cog)

        // Test case 2: No path
        String beginWord2 = "hit";
        String endWord2 = "cog";
        List<String> wordList2 = Arrays.asList("hot", "dot", "dog", "lot", "log");
        System.out.println("Word Ladder length: " + ladderLength(beginWord2, endWord2, wordList2));
        // Expected: 0
    }

    public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);

        // If endWord is not in the dictionary, no transformation is possible
        if (!wordSet.contains(endWord)) return 0;

        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);

        Set<String> visited = new HashSet<>();
        visited.add(beginWord);

        int level = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String current = queue.poll();

                // Try all possible one-letter transformations
                char[] chars = current.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char originalChar = chars[j];

                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == originalChar) continue;

                        chars[j] = c;
                        String newWord = new String(chars);

                        if (newWord.equals(endWord)) {
                            return level + 1;
                        }

                        if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                            visited.add(newWord);
                            queue.offer(newWord);
                        }
                    }

                    chars[j] = originalChar; // Restore
                }
            }

            level++;
        }

        return 0; // No transformation sequence found
    }
}
