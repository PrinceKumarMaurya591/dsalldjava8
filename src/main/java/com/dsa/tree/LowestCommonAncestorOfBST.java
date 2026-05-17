package com.dsa.tree;

// Problem: Lowest Common Ancestor of a Binary Search Tree
// Link: https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/
//
// Given a binary search tree (BST), find the lowest common ancestor (LCA) of
// two given nodes in the BST.
//
// The lowest common ancestor is defined between two nodes p and q as the lowest
// node in the tree that has both p and q as descendants (where we allow a node
// to be a descendant of itself).
//
// Approach: Use BST property
// - If both p and q are less than root, LCA is in left subtree
// - If both p and q are greater than root, LCA is in right subtree
// - Otherwise, root is the LCA (one on left, one on right, or root is one of them)
//
// Time Complexity: O(h) where h is height of tree
// Space Complexity: O(1) for iterative, O(h) for recursive

public class LowestCommonAncestorOfBST {

    public static void main(String[] args) {
        // Test case 1:
        //       6
        //      / \
        //     2   8
        //    / \ / \
        //   0  4 7  9
        //     / \
        //    3   5
        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);

        TreeNode p1 = root.left; // 2
        TreeNode q1 = root.right; // 8
        System.out.println("LCA of 2 and 8: " + lowestCommonAncestor(root, p1, q1).val);
        // Expected: 6

        TreeNode p2 = root.left; // 2
        TreeNode q2 = root.left.right; // 4
        System.out.println("LCA of 2 and 4: " + lowestCommonAncestor(root, p2, q2).val);
        // Expected: 2

        TreeNode p3 = root.left.right.left; // 3
        TreeNode q3 = root.left.right.right; // 5
        System.out.println("LCA of 3 and 5: " + lowestCommonAncestor(root, p3, q3).val);
        // Expected: 4
    }

    // Recursive approach
    public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;

        // If both nodes are less than root, LCA is in left subtree
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        // If both nodes are greater than root, LCA is in right subtree
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }
        // Otherwise, root is the LCA
        return root;
    }

    // Iterative approach
    public static TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode current = root;

        while (current != null) {
            if (p.val < current.val && q.val < current.val) {
                current = current.left;
            } else if (p.val > current.val && q.val > current.val) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }
}
