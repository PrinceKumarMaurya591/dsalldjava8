package com.dsa.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Problem: Word Search II
// Link: https://leetcode.com/problems/word-search-ii/
//
// Given an m x n board of characters and a list of strings words, return all
// words on the board.
//
// Each word must be constructed from letters of sequentially adjacent cells,
// where adjacent cells are horizontally or vertically neighboring. The same
// letter cell may not be used more than once in a word.
//
// Approach: Trie + Backtracking (DFS)
// - Build a Trie from the list of words
// - For each cell on the board, perform DFS to find words
// - Use Trie to prune search paths that don't match any prefix
// - Mark visited cells to avoid reusing the same cell
//
// Time Complexity: O(M * N * 4^L) where M,N = board dimensions, L = max word length
// Space Complexity: O(K * L) where K = number of words, L = average length

public class WordSearchII {

    // Trie node class
    static class TrieNode {
        TrieNode[] children;
        String word; // Store word at end node for O(1) retrieval

        TrieNode() {
            children = new TrieNode[26];
            word = null;
        }
    }

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static void main(String[] args) {
        // Test case 1:
        // board = [['o','a','a','n'],
        //          ['e','t','a','e'],
        //          ['i','h','k','r'],
        //          ['i','f','l','v']]
        // words = ['oath','pea','eat','rain']
        // Output: ['eat','oath']
        char[][] board1 = {
                {'o', 'a', 'a', 'n'},
                {'e', 't', 'a', 'e'},
                {'i', 'h', 'k', 'r'},
                {'i', 'f', 'l', 'v'}
        };
        String[] words1 = {"oath", "pea", "eat", "rain"};
        System.out.println("Found words: " + findWords(board1, words1));
        // Expected: [oath, eat] (order may vary)

        // Test case 2:
        // board = [['a','b'],
        //          ['c','d']]
        // words = ['abcb']
        // Output: []
        char[][] board2 = {{'a', 'b'}, {'c', 'd'}};
        String[] words2 = {"abcb"};
        System.out.println("Found words (2): " + findWords(board2, words2));
        // Expected: []

        // Test case 3: Single character board
        char[][] board3 = {{'a'}};
        String[] words3 = {"a"};
        System.out.println("Found words (3): " + findWords(board3, words3));
        // Expected: [a]
    }

    public static List<String> findWords(char[][] board, String[] words) {
        // Build Trie
        TrieNode root = buildTrie(words);

        Set<String> result = new HashSet<>();
        int m = board.length;
        int n = board[0].length;

        // Search from each cell
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(board, i, j, root, result);
            }
        }

        return new ArrayList<>(result);
    }

    private static TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String word : words) {
            TrieNode current = root;
            for (char ch : word.toCharArray()) {
                int index = ch - 'a';
                if (current.children[index] == null) {
                    current.children[index] = new TrieNode();
                }
                current = current.children[index];
            }
            current.word = word;
        }
        return root;
    }

    private static void dfs(char[][] board, int i, int j, TrieNode node, Set<String> result) {
        // Boundary checks
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return;
        if (board[i][j] == '#') return; // Already visited

        char ch = board[i][j];
        int index = ch - 'a';
        if (node.children[index] == null) return; // No matching prefix

        TrieNode next = node.children[index];
        if (next.word != null) {
            result.add(next.word); // Found a word
            // Don't return - there might be longer words with same prefix
        }

        // Mark as visited
        board[i][j] = '#';

        // Explore all 4 directions
        for (int[] dir : DIRECTIONS) {
            dfs(board, i + dir[0], j + dir[1], next, result);
        }

        // Backtrack - restore original character
        board[i][j] = ch;
    }
}
