package com.dsa.tree;

// Problem: Count Good Nodes In Binary Tree
// Link: https://leetcode.com/problems/count-good-nodes-in-binary-tree/
//
// Given a binary tree root, a node X in the tree is named "good" if in the path
// from root to X there are no nodes with a value greater than X.
// Return the number of good nodes in the binary tree.
//
// Approach: Recursive DFS with max value tracking
// - Traverse the tree keeping track of the maximum value seen so far on the path
// - If current node's value >= maxSoFar, it's a good node (increment count)
// - Update maxSoFar = max(maxSoFar, node.val)
// - Recursively process left and right children
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack

public class CountGoodNodesInBinaryTree {

    public static void main(String[] args) {
        // Test case 1:
        //       3
        //      / \
        //     1   4
        //    /   / \
        //   3   1   5
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(4);
        root1.left.left = new TreeNode(3);
        root1.right.left = new TreeNode(1);
        root1.right.right = new TreeNode(5);
        System.out.println("Good nodes count: " + goodNodes(root1));
        // Expected: 4 (3, 4, 5, and the rightmost 3)

        // Test case 2:
        //       3
        //      /
        //     3
        //    / \
        //   4   2
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(3);
        root2.left.left = new TreeNode(4);
        root2.left.right = new TreeNode(2);
        System.out.println("Good nodes count (2): " + goodNodes(root2));
        // Expected: 3 (3, 3, 4)

        // Test case 3: Single node
        TreeNode root3 = new TreeNode(1);
        System.out.println("Good nodes count (single): " + goodNodes(root3));
        // Expected: 1
    }

    public static int goodNodes(TreeNode root) {
        return countGoodNodes(root, Integer.MIN_VALUE);
    }

    private static int countGoodNodes(TreeNode node, int maxSoFar) {
        if (node == null) return 0;

        int count = 0;
        if (node.val >= maxSoFar) {
            count = 1;
        }

        int newMax = Math.max(maxSoFar, node.val);
        count += countGoodNodes(node.left, newMax);
        count += countGoodNodes(node.right, newMax);

        return count;
    }
}
