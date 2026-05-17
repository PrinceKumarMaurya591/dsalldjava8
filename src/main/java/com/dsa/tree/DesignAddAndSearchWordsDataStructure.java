package com.dsa.tree;

// Problem: Design Add And Search Words Data Structure
// Link: https://leetcode.com/problems/design-add-and-search-words-data-structure/
//
// Design a data structure that supports adding new words and finding if a string
// matches any previously added string.
//
// Implement the WordDictionary class:
// - WordDictionary() Initializes the object.
// - void addWord(word) Adds word to the data structure.
// - bool search(word) Returns true if there is any string in the data structure
//   that matches word. word may contain dots '.' where dots can be matched with
//   any letter.
//
// Approach: Trie with DFS for wildcard search
// - Use a Trie to store words
// - For search with '.', use DFS/backtracking to try all 26 children
//
// Time Complexity:
// - addWord: O(L) where L = word length
// - search: O(26^L) worst case for all dots, O(L) for normal words
// Space Complexity: O(N * L) where N = number of words

public class DesignAddAndSearchWordsDataStructure {

    // Trie node class
    static class TrieNode {
        TrieNode[] children;
        boolean isEnd;

        TrieNode() {
            children = new TrieNode[26];
            isEnd = false;
        }
    }

    private final TrieNode root;

    public DesignAddAndSearchWordsDataStructure() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            int index = ch - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEnd = true;
    }

    public boolean search(String word) {
        return searchInNode(word, 0, root);
    }

    private boolean searchInNode(String word, int index, TrieNode node) {
        if (node == null) return false;
        if (index == word.length()) return node.isEnd;

        char ch = word.charAt(index);

        if (ch == '.') {
            // Try all 26 possible children
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    if (searchInNode(word, index + 1, node.children[i])) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            int childIndex = ch - 'a';
            return searchInNode(word, index + 1, node.children[childIndex]);
        }
    }

    public static void main(String[] args) {
        DesignAddAndSearchWordsDataStructure wordDictionary = new DesignAddAndSearchWordsDataStructure();

        wordDictionary.addWord("bad");
        wordDictionary.addWord("dad");
        wordDictionary.addWord("mad");

        System.out.println("Search 'pad': " + wordDictionary.search("pad")); // false
        System.out.println("Search 'bad': " + wordDictionary.search("bad")); // true
        System.out.println("Search '.ad': " + wordDictionary.search(".ad")); // true
        System.out.println("Search 'b..': " + wordDictionary.search("b..")); // true
        System.out.println("Search '...': " + wordDictionary.search("...")); // true
        System.out.println("Search 'b.': " + wordDictionary.search("b."));   // false
        System.out.println("Search 'ma.': " + wordDictionary.search("ma.")); // true
    }
}
