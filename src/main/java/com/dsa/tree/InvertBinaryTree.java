package com.dsa.tree;

// Problem: Invert/Flip Binary Tree
// Link: https://leetcode.com/problems/invert-binary-tree/
//
// Given the root of a binary tree, invert the tree, and return its root.
// Inverting a tree means swapping the left and right children of every node.
//
// Approach: Recursive DFS
// - Swap left and right children of current node
// - Recursively invert left and right subtrees
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack

public class InvertBinaryTree {

    public static void main(String[] args) {
        // Test case 1:
        //       4                   4
        //      / \                 / \
        //     2   7       ->      7   2
        //    / \ / \             / \ / \
        //   1  3 6  9           9  6 3  1
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);
        root1.right.left = new TreeNode(6);
        root1.right.right = new TreeNode(9);

        System.out.println("Original tree inorder: " + TreeOperations.inorderTraversal(root1));
        TreeNode inverted1 = invertTree(root1);
        System.out.println("Inverted tree inorder: " + TreeOperations.inorderTraversal(inverted1));
        // Expected original inorder: [1, 2, 3, 4, 6, 7, 9]
        // Expected inverted inorder: [9, 7, 6, 4, 3, 2, 1]

        // Test case 2: Single node
        TreeNode root2 = new TreeNode(1);
        System.out.println("Inverted single node: " + invertTree(root2).val);
        // Expected: 1

        // Test case 3: Empty tree
        System.out.println("Inverted empty tree: " + invertTree(null));
        // Expected: null
    }

    public static TreeNode invertTree(TreeNode root) {
        if (root == null) return null;

        // Swap left and right children
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        // Recursively invert subtrees
        invertTree(root.left);
        invertTree(root.right);

        return root;
    }
}
