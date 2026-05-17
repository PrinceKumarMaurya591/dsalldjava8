package com.dsa.tree;

// Problem: Validate Binary Search Tree
// Link: https://leetcode.com/problems/validate-binary-search-tree/
//
// Given the root of a binary tree, determine if it is a valid binary search tree (BST).
//
// A valid BST is defined as follows:
// - The left subtree of a node contains only nodes with values less than the node's value
// - The right subtree of a node contains only nodes with values greater than the node's value
// - Both the left and right subtrees must also be binary search trees
//
// Approach 1: Recursive with min/max bounds
// - Pass down valid range (min, max) for each node
// - Left child must be in range (min, root.val)
// - Right child must be in range (root.val, max)
//
// Approach 2: Inorder Traversal
// - Inorder traversal of BST should be strictly increasing
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack

public class ValidateBinarySearchTree {

    public static void main(String[] args) {
        // Test case 1: Valid BST
        //       2
        //      / \
        //     1   3
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);
        System.out.println("Is valid BST (valid): " + isValidBST(root1));
        // Expected: true

        // Test case 2: Invalid BST
        //       5
        //      / \
        //     1   4
        //        / \
        //       3   6
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.right.left = new TreeNode(3);
        root2.right.right = new TreeNode(6);
        System.out.println("Is valid BST (invalid): " + isValidBST(root2));
        // Expected: false

        // Test case 3: Invalid BST (equal values not allowed)
        //       2
        //      / \
        //     2   2
        TreeNode root3 = new TreeNode(2);
        root3.left = new TreeNode(2);
        root3.right = new TreeNode(2);
        System.out.println("Is valid BST (equal values): " + isValidBST(root3));
        // Expected: false

        // Test case 4: Valid BST with larger values
        //       10
        //      /  \
        //     5   15
        //        /  \
        //       12  20
        TreeNode root4 = new TreeNode(10);
        root4.left = new TreeNode(5);
        root4.right = new TreeNode(15);
        root4.right.left = new TreeNode(12);
        root4.right.right = new TreeNode(20);
        System.out.println("Is valid BST (larger valid): " + isValidBST(root4));
        // Expected: true
    }

    public static boolean isValidBST(TreeNode root) {
        return isValidBSTHelper(root, null, null);
    }

    private static boolean isValidBSTHelper(TreeNode node, Integer min, Integer max) {
        if (node == null) return true;

        // Check if current node violates min/max constraints
        if (min != null && node.val <= min) return false;
        if (max != null && node.val >= max) return false;

        // Recursively check left and right subtrees with updated bounds
        return isValidBSTHelper(node.left, min, node.val)
            && isValidBSTHelper(node.right, node.val, max);
    }
}
