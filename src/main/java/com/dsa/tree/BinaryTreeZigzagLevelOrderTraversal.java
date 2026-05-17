package com.dsa.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Problem: Binary Tree Zigzag Level Order Traversal
// Link: https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/
//
// Given the root of a binary tree, return the zigzag level order traversal of
// its nodes' values. (i.e., from left to right, then right to left for the next
// level and alternate between).
//
// Approach: BFS with level-based direction toggle
// - Use queue for level order traversal
// - For even levels (0-indexed), add left to right
// - For odd levels, add right to left (prepend)
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(n) - queue storage

public class BinaryTreeZigzagLevelOrderTraversal {

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
        System.out.println("Zigzag level order: " + zigzagLevelOrder(root1));
        // Expected: [[3], [20, 9], [15, 7]]

        // Test case 2: Single node
        TreeNode root2 = new TreeNode(1);
        System.out.println("Zigzag (single): " + zigzagLevelOrder(root2));
        // Expected: [[1]]

        // Test case 3:
        //       1
        //      / \
        //     2   3
        //    /     \
        //   4       5
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(2);
        root3.right = new TreeNode(3);
        root3.left.left = new TreeNode(4);
        root3.right.right = new TreeNode(5);
        System.out.println("Zigzag (asymmetric): " + zigzagLevelOrder(root3));
        // Expected: [[1], [3, 2], [4, 5]]
    }

    public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            LinkedList<Integer> currentLevel = new LinkedList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                if (leftToRight) {
                    currentLevel.addLast(node.val);
                } else {
                    currentLevel.addFirst(node.val);
                }

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(currentLevel);
            leftToRight = !leftToRight;
        }
        return result;
    }
}
