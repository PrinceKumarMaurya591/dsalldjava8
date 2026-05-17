package com.dsa.tree;

// Problem: Subtree of Another Tree
// Link: https://leetcode.com/problems/subtree-of-another-tree/
//
// Given the roots of two binary trees root and subRoot, return true if there is
// a subtree of root with the same structure and node values as subRoot, and
// false otherwise.
// A subtree of a binary tree is a tree that consists of a node in root and all
// of its descendants.
//
// Approach: Recursive DFS
// - For each node in root, check if the tree rooted at that node is same as subRoot
// - Use isSameTree helper from SameTree problem
//
// Time Complexity: O(m * n) where m = nodes in root, n = nodes in subRoot
// Space Complexity: O(h) - recursion stack

public class SubtreeOfAnotherTree {

    public static void main(String[] args) {
        // Test case 1: Subtree exists
        //       3                   4
        //      / \                 / \
        //     4   5               1   2
        //    / \
        //   1   2
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(4);
        root1.right = new TreeNode(5);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(2);

        TreeNode subRoot1 = new TreeNode(4);
        subRoot1.left = new TreeNode(1);
        subRoot1.right = new TreeNode(2);
        System.out.println("Is subtree (exists): " + isSubtree(root1, subRoot1));
        // Expected: true

        // Test case 2: Subtree does not exist
        //       3                   4
        //      / \                 / \
        //     4   5               1   3
        //    / \
        //   1   2
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(4);
        root2.right = new TreeNode(5);
        root2.left.left = new TreeNode(1);
        root2.left.right = new TreeNode(2);

        TreeNode subRoot2 = new TreeNode(4);
        subRoot2.left = new TreeNode(1);
        subRoot2.right = new TreeNode(3);
        System.out.println("Is subtree (not exists): " + isSubtree(root2, subRoot2));
        // Expected: false

        // Test case 3: Same tree
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(2);

        TreeNode subRoot3 = new TreeNode(1);
        subRoot3.left = new TreeNode(2);
        System.out.println("Is subtree (same tree): " + isSubtree(root3, subRoot3));
        // Expected: true
    }

    public static boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (root == null) return false;

        // Check if current node's tree matches subRoot
        if (isSameTree(root, subRoot)) return true;

        // Recursively check left and right subtrees
        return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    private static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        if (p.val != q.val) return false;
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
}
