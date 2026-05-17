package com.dsa.tree;

// Problem: Diameter of Binary Tree
// Link: https://leetcode.com/problems/diameter-of-binary-tree/
//
// Given the root of a binary tree, return the length of the diameter of the tree.
// The diameter of a binary tree is the length of the longest path between any
// two nodes in a tree. This path may or may not pass through the root.
// The length of a path between two nodes is represented by the number of edges
// between them.
//
// Approach: Recursive DFS with global max
// - For each node, compute height of left and right subtrees
// - The diameter through this node = leftHeight + rightHeight
// - Update global max diameter
// - Return height of current node to parent
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(h) - recursion stack

public class DiameterOfBinaryTree {

    private static int diameter;

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
        System.out.println("Diameter: " + diameterOfBinaryTree(root1));
        // Expected: 3 (path: 4->2->1->3 or 5->2->1->3)

        // Test case 2: Single node
        TreeNode root2 = new TreeNode(1);
        System.out.println("Diameter (single node): " + diameterOfBinaryTree(root2));
        // Expected: 0

        // Test case 3:
        //       1
        //      /
        //     2
        //    /
        //   3
        TreeNode root3 = new TreeNode(1);
        root3.left = new TreeNode(2);
        root3.left.left = new TreeNode(3);
        System.out.println("Diameter (skewed): " + diameterOfBinaryTree(root3));
        // Expected: 2 (path: 3->2->1)
    }

    public static int diameterOfBinaryTree(TreeNode root) {
        diameter = 0;
        height(root);
        return diameter;
    }

    private static int height(TreeNode node) {
        if (node == null) return 0;

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        // Update diameter: path through this node
        diameter = Math.max(diameter, leftHeight + rightHeight);

        // Return height of this node
        return 1 + Math.max(leftHeight, rightHeight);
    }
}
