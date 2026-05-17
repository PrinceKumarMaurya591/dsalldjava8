package com.dsa.tree;

// Problem: Symmetric Tree
// Link: https://leetcode.com/problems/symmetric-tree/
//
// Given the root of a binary tree, check whether it is a mirror of itself
// (i.e., symmetric around its center).
//
// Approach: Recursive DFS
// - Compare left subtree of root with right subtree of root
// - Two trees are symmetric if:
//   1. Their root values are equal
//   2. Left subtree of left tree is symmetric to right subtree of right tree
//   3. Right subtree of left tree is symmetric to left subtree of right tree
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack

public class SymmetricTree {

    public static void main(String[] args) {
        // Test case 1: Symmetric tree
        //       1
        //      / \
        //     2   2
        //    / \ / \
        //   3  4 4  3
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(2);
        root1.left.left = new TreeNode(3);
        root1.left.right = new TreeNode(4);
        root1.right.left = new TreeNode(4);
        root1.right.right = new TreeNode(3);
        System.out.println("Is symmetric (symmetric tree): " + isSymmetric(root1));
        // Expected: true

        // Test case 2: Asymmetric tree
        //       1
        //      / \
        //     2   2
        //      \   \
        //       3   3
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(2);
        root2.left.right = new TreeNode(3);
        root2.right.right = new TreeNode(3);
        System.out.println("Is symmetric (asymmetric tree): " + isSymmetric(root2));
        // Expected: false

        // Test case 3: Single node
        TreeNode root3 = new TreeNode(1);
        System.out.println("Is symmetric (single node): " + isSymmetric(root3));
        // Expected: true
    }

    public static boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        return isMirror(root.left, root.right);
    }

    private static boolean isMirror(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        if (left.val != right.val) return false;

        return isMirror(left.left, right.right) && isMirror(left.right, right.left);
    }
}
