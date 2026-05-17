package com.dsa.tree;

// Problem: Maximum Depth of Binary Tree
// Link: https://leetcode.com/problems/maximum-depth-of-binary-tree/
//
// Given the root of a binary tree, return its maximum depth.
// A binary tree's maximum depth is the number of nodes along the longest path
// from the root node down to the farthest leaf node.
//
// Approach 1: Recursive DFS
// - Base case: null node has depth 0
// - Recursively compute max depth of left and right subtrees
// - Return 1 + max(leftDepth, rightDepth)
//
// Approach 2: Iterative BFS (Level Order)
// - Use queue to traverse level by level
// - Count number of levels
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) for recursive, O(n) for iterative

import java.util.LinkedList;
import java.util.Queue;

public class MaximumDepthOfBinaryTree {

    public static void main(String[] args) {
        // Test case 1:
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
        System.out.println("Max depth (recursive): " + maxDepthRecursive(root1));
        System.out.println("Max depth (iterative): " + maxDepthIterative(root1));
        // Expected: 3

        // Test case 2: Single node
        TreeNode root2 = new TreeNode(1);
        System.out.println("Max depth (single node): " + maxDepthRecursive(root2));
        // Expected: 1

        // Test case 3: Empty tree
        System.out.println("Max depth (empty): " + maxDepthRecursive(null));
        // Expected: 0

        // Test case 4: Skewed tree
        //     1
        //    /
        //   2
        //  /
        // 3
        TreeNode root4 = new TreeNode(1);
        root4.left = new TreeNode(2);
        root4.left.left = new TreeNode(3);
        System.out.println("Max depth (skewed): " + maxDepthRecursive(root4));
        // Expected: 3
    }

    // Recursive DFS approach
    public static int maxDepthRecursive(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepthRecursive(root.left), maxDepthRecursive(root.right));
    }

    // Iterative BFS approach
    public static int maxDepthIterative(TreeNode root) {
        if (root == null) return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            depth++;

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return depth;
    }
}
