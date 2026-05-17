package com.dsa.tree;

// Problem: Binary Tree Maximum Path Sum
// Link: https://leetcode.com/problems/binary-tree-maximum-path-sum/
//
// A path in a binary tree is a sequence of nodes where each pair of adjacent
// nodes has an edge connecting them. A node can only appear in the sequence
// at most once. Note that the path does not need to pass through the root.
//
// The path sum of a path is the sum of the node's values in the path.
// Given the root of a binary tree, return the maximum path sum of any non-empty path.
//
// Approach: Recursive DFS with global max
// - For each node, compute max gain from left and right subtrees
// - Max path through current node = node.val + leftGain + rightGain
// - Update global max with max path through current node
// - Return max gain from current node to parent (can only take one branch)
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack

public class BinaryTreeMaximumPathSum {

    private static int maxSum;

    public static void main(String[] args) {
        // Test case 1:
        //       1
        //      / \
        //     2   3
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        System.out.println("Max path sum: " + maxPathSum(root1));
        // Expected: 6 (path: 2 -> 1 -> 3)

        // Test case 2:
        //     -10
        //     /  \
        //    9   20
        //       /  \
        //      15   7
        TreeNode root2 = new TreeNode(-10);
        root2.left = new TreeNode(9);
        root2.right = new TreeNode(20);
        root2.right.left = new TreeNode(15);
        root2.right.right = new TreeNode(7);
        System.out.println("Max path sum (2): " + maxPathSum(root2));
        // Expected: 42 (path: 15 -> 20 -> 7)

        // Test case 3: Single negative node
        TreeNode root3 = new TreeNode(-3);
        System.out.println("Max path sum (single negative): " + maxPathSum(root3));
        // Expected: -3

        // Test case 4:
        //       2
        //      / \
        //    -1  -1
        TreeNode root4 = new TreeNode(2);
        root4.left = new TreeNode(-1);
        root4.right = new TreeNode(-1);
        System.out.println("Max path sum (2): " + maxPathSum(root4));
        // Expected: 2
    }

    public static int maxPathSum(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        maxGain(root);
        return maxSum;
    }

    private static int maxGain(TreeNode node) {
        if (node == null) return 0;

        // Max gain from left and right subtrees (0 if negative)
        int leftGain = Math.max(maxGain(node.left), 0);
        int rightGain = Math.max(maxGain(node.right), 0);

        // Max path through current node
        int currentPathSum = node.val + leftGain + rightGain;

        // Update global max
        maxSum = Math.max(maxSum, currentPathSum);

        // Return max gain to parent (can only take one branch)
        return node.val + Math.max(leftGain, rightGain);
    }
}
