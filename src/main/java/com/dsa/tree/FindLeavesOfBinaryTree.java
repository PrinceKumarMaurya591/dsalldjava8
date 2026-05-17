package com.dsa.tree;

import java.util.ArrayList;
import java.util.List;

// Problem: Find Leaves of Binary Tree
// Link: https://leetcode.com/problems/find-leaves-of-binary-tree/
//
// Given the root of a binary tree, collect a tree's nodes as if you were doing this:
// - Collect all the leaf nodes
// - Remove all the leaf nodes
// - Repeat until the tree is empty
//
// Approach: Recursive DFS with height-based grouping
// - Compute the height of each node (leaf = height 0)
// - Group nodes by their height
// - Nodes with same height are collected together
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(n) - store result

public class FindLeavesOfBinaryTree {

    public static void main(String[] args) {
        // Test case 1:
        //       1
        //      / \
        //     2   3
        //    / \
        //   4   5
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);
        System.out.println("Find leaves: " + findLeaves(root1));
        // Expected: [[4, 5, 3], [2], [1]]

        // Test case 2:
        //       1
        //      /
        //     2
        //    /
        //   3
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.left.left = new TreeNode(3);
        System.out.println("Find leaves (skewed): " + findLeaves(root2));
        // Expected: [[3], [2], [1]]
    }

    public static List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        getHeight(root, result);
        return result;
    }

    private static int getHeight(TreeNode node, List<List<Integer>> result) {
        if (node == null) return -1;

        int leftHeight = getHeight(node.left, result);
        int rightHeight = getHeight(node.right, result);
        int currentHeight = 1 + Math.max(leftHeight, rightHeight);

        // Ensure we have a list for this height level
        if (currentHeight == result.size()) {
            result.add(new ArrayList<>());
        }

        result.get(currentHeight).add(node.val);
        return currentHeight;
    }
}
