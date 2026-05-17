package com.dsa.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

// Problem: Alien Dictionary
// Link: https://leetcode.com/problems/alien-dictionary/ (Premium)
//       https://www.lintcode.com/problem/892/
//
// There is a new alien language that uses the English alphabet. However, the order
// among the letters is unknown to you. You are given a list of strings words from
// the alien language's dictionary, where the strings are sorted lexicographically
// by the rules of this new language.
//
// Return a string of the unique letters in the new alien language sorted in
// lexicographically increasing order by the new language's rules. If there is
// no solution, return "". If there are multiple solutions, return any of them.
//
// Approach: Topological Sort (Kahn's Algorithm - BFS)
// 1. Compare adjacent words to find character ordering relationships.
// 2. Build a graph (adjacency list) and indegree array.
// 3. Perform topological sort using BFS.
//
// Time Complexity: O(C) where C is total length of all words
// Space Complexity: O(1) since only 26 lowercase letters

public class AlienDictionary {

    public static void main(String[] args) {
        // Test case 1
        String[] words1 = {"wrt", "wrf", "er", "ett", "rftt"};
        System.out.println("Alien Order: " + alienOrder(words1));
        // Expected: "wertf"

        // Test case 2
        String[] words2 = {"z", "x"};
        System.out.println("Alien Order: " + alienOrder(words2));
        // Expected: "zx"

        // Test case 3: Invalid (cycle)
        String[] words3 = {"z", "x", "z"};
        System.out.println("Alien Order: " + alienOrder(words3));
        // Expected: ""
    }

    public static String alienOrder(String[] words) {
        if (words == null || words.length == 0) return "";

        // Build graph
        Map<Character, List<Character>> adj = new HashMap<>();
        Map<Character, Integer> indegree = new HashMap<>();

        // Initialize all characters
        for (String word : words) {
            for (char c : word.toCharArray()) {
                adj.putIfAbsent(c, new ArrayList<>());
                indegree.putIfAbsent(c, 0);
            }
        }

        // Find ordering by comparing adjacent words
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            // Check for invalid case: word2 is a prefix of word1
            if (word1.length() > word2.length() && word1.startsWith(word2)) {
                return "";
            }

            int minLen = Math.min(word1.length(), word2.length());
            for (int j = 0; j < minLen; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);

                if (c1 != c2) {
                    adj.get(c1).add(c2);
                    indegree.put(c2, indegree.get(c2) + 1);
                    break; // Only the first differing character matters
                }
            }
        }

        // Topological Sort (BFS - Kahn's Algorithm)
        Queue<Character> queue = new LinkedList<>();
        for (char c : indegree.keySet()) {
            if (indegree.get(c) == 0) {
                queue.offer(c);
            }
        }

        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            result.append(c);

            for (char neighbor : adj.get(c)) {
                indegree.put(neighbor, indegree.get(neighbor) - 1);
                if (indegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // If we couldn't process all characters, there's a cycle
        if (result.length() != indegree.size()) {
            return "";
        }

        return result.toString();
    }
}
