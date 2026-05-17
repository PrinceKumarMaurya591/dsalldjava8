package com.dsa.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Problem: Binary Tree Level Order Traversal
// Link: https://leetcode.com/problems/binary-tree-level-order-traversal/
//
// Given the root of a binary tree, return the level order traversal of its
// nodes' values (i.e., from left to right, level by level).
//
// Approach: BFS using Queue
// - Use a queue to process nodes level by level
// - For each level, process all nodes currently in queue
// - Add children to queue for next level
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(n) - queue storage

public class BinaryTreeLevelOrderTraversal {

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
        System.out.println("Level order: " + levelOrder(root1));
        // Expected: [[3], [9, 20], [15, 7]]

        // Test case 2: Single node
        TreeNode root2 = new TreeNode(1);
        System.out.println("Level order (single): " + levelOrder(root2));
        // Expected: [[1]]

        // Test case 3: Empty tree
        System.out.println("Level order (empty): " + levelOrder(null));
        // Expected: []
    }

    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(currentLevel);
        }
        return result;
    }
}
