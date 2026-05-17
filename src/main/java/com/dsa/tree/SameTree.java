package com.dsa.tree;

// Problem: Same Tree
// Link: https://leetcode.com/problems/same-tree/
//
// Given the roots of two binary trees p and q, write a function to check
// if they are the same or not.
// Two binary trees are considered the same if they are structurally identical,
// and the nodes have the same value.
//
// Approach: Recursive DFS
// - If both nodes are null, they are same
// - If one is null and other is not, they are different
// - If values differ, they are different
// - Recursively check left and right subtrees
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack

public class SameTree {

    public static void main(String[] args) {
        // Test case 1: Same trees
        //     1         1
        //    / \       / \
        //   2   3     2   3
        TreeNode p1 = new TreeNode(1);
        p1.left = new TreeNode(2);
        p1.right = new TreeNode(3);

        TreeNode q1 = new TreeNode(1);
        q1.left = new TreeNode(2);
        q1.right = new TreeNode(3);
        System.out.println("Is same tree (identical): " + isSameTree(p1, q1));
        // Expected: true

        // Test case 2: Different values
        //     1         1
        //    /           \
        //   2             2
        TreeNode p2 = new TreeNode(1);
        p2.left = new TreeNode(2);

        TreeNode q2 = new TreeNode(1);
        q2.right = new TreeNode(2);
        System.out.println("Is same tree (different structure): " + isSameTree(p2, q2));
        // Expected: false

        // Test case 3: Different values
        //     1         1
        //    / \       / \
        //   2   1     1   2
        TreeNode p3 = new TreeNode(1);
        p3.left = new TreeNode(2);
        p3.right = new TreeNode(1);

        TreeNode q3 = new TreeNode(1);
        q3.left = new TreeNode(1);
        q3.right = new TreeNode(2);
        System.out.println("Is same tree (different values): " + isSameTree(p3, q3));
        // Expected: false
    }

    public static boolean isSameTree(TreeNode p, TreeNode q) {
        // Both are null -> same
        if (p == null && q == null) return true;
        // One is null -> different
        if (p == null || q == null) return false;
        // Values differ -> different
        if (p.val != q.val) return false;

        // Recursively check left and right subtrees
        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
}
