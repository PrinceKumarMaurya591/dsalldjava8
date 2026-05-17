package com.dsa.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Problem: Binary Tree Right Side View
// Link: https://leetcode.com/problems/binary-tree-right-side-view/
//
// Given the root of a binary tree, imagine yourself standing on the right side
// of it, return the values of the nodes you can see ordered from top to bottom.
//
// Approach 1: BFS (Level Order)
// - Traverse level by level
// - Add the last node of each level to result
//
// Approach 2: DFS (Recursive)
// - Traverse right subtree first (Root -> Right -> Left)
// - Add node when depth == result.size() (first node at each depth)
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(n) - queue/result storage

public class BinaryTreeRightSideView {

    public static void main(String[] args) {
        // Test case 1:
        //       1
        //      / \
        //     2   3
        //      \   \
        //       5   4
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.right = new TreeNode(5);
        root1.right.right = new TreeNode(4);
        System.out.println("Right side view (BFS): " + rightSideViewBFS(root1));
        System.out.println("Right side view (DFS): " + rightSideViewDFS(root1));
        // Expected: [1, 3, 4]

        // Test case 2:
        //       1
        //      /
        //     2
        //    /
        //   3
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.left.left = new TreeNode(3);
        System.out.println("Right side view (skewed): " + rightSideViewBFS(root2));
        // Expected: [1, 2, 3]

        // Test case 3: Single node
        TreeNode root3 = new TreeNode(1);
        System.out.println("Right side view (single): " + rightSideViewBFS(root3));
        // Expected: [1]
    }

    // BFS Approach
    public static List<Integer> rightSideViewBFS(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                // If it's the last node in this level, add to result
                if (i == levelSize - 1) {
                    result.add(node.val);
                }

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        return result;
    }

    // DFS Approach
    public static List<Integer> rightSideViewDFS(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        dfs(root, 0, result);
        return result;
    }

    private static void dfs(TreeNode node, int depth, List<Integer> result) {
        if (node == null) return;

        // First time we visit this depth -> add node (since we go right first)
        if (depth == result.size()) {
            result.add(node.val);
        }

        // Traverse right first to get right side view
        dfs(node.right, depth + 1, result);
        dfs(node.left, depth + 1, result);
    }
}
