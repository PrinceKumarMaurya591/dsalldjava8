package com.dsa.tree;

// Problem: Balanced Binary Tree
// Link: https://leetcode.com/problems/balanced-binary-tree/
//
// Given a binary tree, determine if it is height-balanced.
// A height-balanced binary tree is defined as:
//   a binary tree in which the left and right subtrees of every node
//   differ in height by no more than 1.
//
// Approach: Recursive DFS
// - Compute height of left and right subtrees
// - If at any node the difference > 1, tree is unbalanced
// - Return -1 to indicate unbalanced, otherwise return height
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack, h = height of tree

public class BalancedBinaryTree {

    public static void main(String[] args) {
        // Test case 1: Balanced tree
        //       3
        //      / \
        //     9  20
        //        / \
        //       15  7
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(9);
        root1.right = new TreeNode(20);
        root1.right.left = new TreeNode(15);
        root1.right.right = new TreeNode(7);
        System.out.println("Is balanced (3,9,20,15,7): " + isBalanced(root1));
        // Expected: true

        // Test case 2: Unbalanced tree
        //       1
        //      / \
        //     2   2
        //    / \
        //   3   3
        //  / \
        // 4   4
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(2);
        root2.left.left = new TreeNode(3);
        root2.left.right = new TreeNode(3);
        root2.left.left.left = new TreeNode(4);
        root2.left.left.right = new TreeNode(4);
        System.out.println("Is balanced (1,2,2,3,3,4,4): " + isBalanced(root2));
        // Expected: false

        // Test case 3: Empty tree
        System.out.println("Is balanced (empty): " + isBalanced(null));
        // Expected: true
    }

    public static boolean isBalanced(TreeNode root) {
        return checkHeight(root) != -1;
    }

    private static int checkHeight(TreeNode node) {
        if (node == null) return 0;

        int leftHeight = checkHeight(node.left);
        if (leftHeight == -1) return -1;

        int rightHeight = checkHeight(node.right);
        if (rightHeight == -1) return -1;

        if (Math.abs(leftHeight - rightHeight) > 1) return -1;

        return 1 + Math.max(leftHeight, rightHeight);
    }
}
