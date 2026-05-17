package com.dsa.tree;

// Problem: Kth Smallest Element in a BST
// Link: https://leetcode.com/problems/kth-smallest-element-in-a-bst/
//
// Given the root of a binary search tree, and an integer k, return the kth
// smallest value (1-indexed) of all the values of the nodes in the tree.
//
// Approach 1: Inorder Traversal (Recursive)
// - Inorder traversal of BST gives sorted order
// - Track count and stop when count == k
//
// Approach 2: Iterative Inorder Traversal using Stack
// - Simulate inorder traversal iteratively
// - Stop when we've processed k elements
//
// Time Complexity: O(n) worst case, O(h + k) average
// Space Complexity: O(h) - stack/recursion

import java.util.Stack;

public class KthSmallestElementInBST {

    private static int count;
    private static int result;

    public static void main(String[] args) {
        // Test case 1:
        //       3
        //      / \
        //     1   4
        //      \
        //       2
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(4);
        root1.left.right = new TreeNode(2);
        System.out.println("1st smallest: " + kthSmallestRecursive(root1, 1));
        System.out.println("2nd smallest: " + kthSmallestIterative(root1, 2));
        System.out.println("3rd smallest: " + kthSmallestRecursive(root1, 3));
        // Expected: 1, 2, 3

        // Test case 2:
        //       5
        //      / \
        //     3   6
        //    / \
        //   2   4
        //  /
        // 1
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(3);
        root2.right = new TreeNode(6);
        root2.left.left = new TreeNode(2);
        root2.left.right = new TreeNode(4);
        root2.left.left.left = new TreeNode(1);
        System.out.println("3rd smallest: " + kthSmallestRecursive(root2, 3));
        // Expected: 3
    }

    // Recursive approach
    public static int kthSmallestRecursive(TreeNode root, int k) {
        count = 0;
        result = 0;
        inorder(root, k);
        return result;
    }

    private static void inorder(TreeNode node, int k) {
        if (node == null) return;

        inorder(node.left, k);
        count++;
        if (count == k) {
            result = node.val;
            return;
        }
        inorder(node.right, k);
    }

    // Iterative approach using stack
    public static int kthSmallestIterative(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        int count = 0;

        while (current != null || !stack.isEmpty()) {
            // Go to leftmost node
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            count++;
            if (count == k) return current.val;

            current = current.right;
        }

        return -1; // k is larger than number of nodes
    }
}
