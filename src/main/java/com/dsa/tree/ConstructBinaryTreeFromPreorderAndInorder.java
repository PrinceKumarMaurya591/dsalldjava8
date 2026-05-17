package com.dsa.tree;

import java.util.HashMap;
import java.util.Map;

// Problem: Construct Binary Tree from Preorder and Inorder Traversal
// Link: https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/
//
// Given two integer arrays preorder and inorder where preorder is the preorder
// traversal of a binary tree and inorder is the inorder traversal of the same
// tree, construct and return the binary tree.
//
// Approach: Recursive with HashMap
// - First element of preorder is always the root
// - Find root in inorder to determine left and right subtrees
// - Left subtree: elements left of root in inorder
// - Right subtree: elements right of root in inorder
// - Use HashMap for O(1) lookup of root index in inorder
//
// Time Complexity: O(n) - each node processed once
// Space Complexity: O(n) - HashMap and recursion stack

public class ConstructBinaryTreeFromPreorderAndInorder {

    private static int preorderIndex;
    private static Map<Integer, Integer> inorderMap;

    public static void main(String[] args) {
        // Test case 1:
        // preorder = [3, 9, 20, 15, 7]
        // inorder = [9, 3, 15, 20, 7]
        // Tree:
        //       3
        //      / \
        //     9  20
        //        / \
        //       15  7
        int[] preorder1 = {3, 9, 20, 15, 7};
        int[] inorder1 = {9, 3, 15, 20, 7};
        TreeNode root1 = buildTree(preorder1, inorder1);
        System.out.println("Constructed tree inorder: " + TreeOperations.inorderTraversal(root1));
        System.out.println("Constructed tree preorder: " + TreeOperations.preorderTraversal(root1));
        // Expected inorder: [9, 3, 15, 20, 7]
        // Expected preorder: [3, 9, 20, 15, 7]

        // Test case 2:
        // preorder = [-1], inorder = [-1]
        int[] preorder2 = {-1};
        int[] inorder2 = {-1};
        TreeNode root2 = buildTree(preorder2, inorder2);
        System.out.println("Constructed tree (single): " + TreeOperations.inorderTraversal(root2));
        // Expected: [-1]
    }

    public static TreeNode buildTree(int[] preorder, int[] inorder) {
        preorderIndex = 0;
        inorderMap = new HashMap<>();

        // Build a map of value -> index for inorder array
        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i], i);
        }

        return buildTreeHelper(preorder, 0, inorder.length - 1);
    }

    private static TreeNode buildTreeHelper(int[] preorder, int left, int right) {
        if (left > right) return null;

        // Current root value from preorder
        int rootVal = preorder[preorderIndex++];
        TreeNode root = new TreeNode(rootVal);

        // Root divides inorder into left and right subtrees
        int inorderIndex = inorderMap.get(rootVal);

        // Build left subtree first (preorder: Root -> Left -> Right)
        root.left = buildTreeHelper(preorder, left, inorderIndex - 1);
        root.right = buildTreeHelper(preorder, inorderIndex + 1, right);

        return root;
    }
}
