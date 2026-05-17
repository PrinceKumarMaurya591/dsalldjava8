package com.dsa.tree;

// Problem: Implement Trie (Prefix Tree)
// Link: https://leetcode.com/problems/implement-trie-prefix-tree/
//
// A trie (pronounced as "try") or prefix tree is a tree data structure used to
// efficiently store and retrieve keys in a dataset of strings. There are various
// applications of this data structure, such as autocomplete and spellchecker.
//
// Implement the Trie class:
// - Trie() Initializes the trie object.
// - void insert(String word) Inserts the string word into the trie.
// - boolean search(String word) Returns true if the string word is in the trie.
// - boolean startsWith(String prefix) Returns true if there is a previously
//   inserted string word that has the prefix.
//
// Time Complexity:
// - insert: O(L) where L = word length
// - search: O(L)
// - startsWith: O(L)
// Space Complexity: O(N * L) where N = number of words, L = average length

public class ImplementTrie {

    // Trie node class
    static class TrieNode {
        TrieNode[] children;
        boolean isEnd;

        TrieNode() {
            children = new TrieNode[26]; // For lowercase English letters
            isEnd = false;
        }
    }

    private final TrieNode root;

    public ImplementTrie() {
        root = new TrieNode();
    }

    // Inserts a word into the trie
    public void insert(String word) {
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

    // Returns true if the word is in the trie
    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isEnd;
    }

    // Returns true if there is any word in the trie that starts with the given prefix
    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    // Helper method to search for a prefix
    private TrieNode searchPrefix(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            int index = ch - 'a';
            if (current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        return current;
    }

    public static void main(String[] args) {
        ImplementTrie trie = new ImplementTrie();

        trie.insert("apple");
        System.out.println("Search 'apple': " + trie.search("apple"));   // true
        System.out.println("Search 'app': " + trie.search("app"));       // false
        System.out.println("StartsWith 'app': " + trie.startsWith("app")); // true

        trie.insert("app");
        System.out.println("Search 'app': " + trie.search("app"));       // true

        System.out.println("Search 'ap': " + trie.search("ap"));         // false
        System.out.println("StartsWith 'ap': " + trie.startsWith("ap")); // true

        System.out.println("Search 'apple': " + trie.search("apple"));   // true
        System.out.println("Search 'apples': " + trie.search("apples")); // false
    }
}
