package com.dsa.tree;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

// Problem: Serialize and Deserialize Binary Tree
// Link: https://leetcode.com/problems/serialize-and-deserialize-binary-tree/
//
// Serialization is the process of converting a data structure or object into a
// sequence of bits so that it can be stored in a file or memory buffer, or
// transmitted across a network connection link to be reconstructed later in the
// same or another computer environment.
//
// Design an algorithm to serialize and deserialize a binary tree. There is no
// restriction on how your serialization/deserialization algorithm should work.
// You just need to ensure that a binary tree can be serialized to a string and
// this string can be deserialized to the original tree structure.
//
// Approach: Preorder traversal with null markers
// - Serialize: Preorder traversal, use "null" for null nodes, separate with ","
// - Deserialize: Use queue of values, build tree recursively
//
// Time Complexity: O(n) - visit each node once
// Space Complexity: O(n) - store serialized string

public class SerializeAndDeserializeBinaryTree {

    // Encodes a tree to a single string.
    public static String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private static void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }
        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    // Decodes your encoded data to tree.
    public static TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserializeHelper(queue);
    }

    private static TreeNode deserializeHelper(Queue<String> queue) {
        String val = queue.poll();
        if (val.equals("null")) return null;

        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }

    public static void main(String[] args) {
        // Test case 1:
        //       1
        //      / \
        //     2   3
        //        / \
        //       4   5
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.right.left = new TreeNode(4);
        root1.right.right = new TreeNode(5);

        String serialized1 = serialize(root1);
        System.out.println("Serialized: " + serialized1);
        TreeNode deserialized1 = deserialize(serialized1);
        System.out.println("Deserialized inorder: " + TreeOperations.inorderTraversal(deserialized1));
        System.out.println("Deserialized preorder: " + TreeOperations.preorderTraversal(deserialized1));
        // Expected serialized: "1,2,null,null,3,4,null,null,5,null,null,"
        // Expected inorder: [2, 1, 4, 3, 5]

        // Test case 2: Empty tree
        String serialized2 = serialize(null);
        System.out.println("\nSerialized (empty): " + serialized2);
        TreeNode deserialized2 = deserialize(serialized2);
        System.out.println("Deserialized (empty): " + deserialized2);
        // Expected: null

        // Test case 3: Single node
        TreeNode root3 = new TreeNode(1);
        String serialized3 = serialize(root3);
        System.out.println("\nSerialized (single): " + serialized3);
        TreeNode deserialized3 = deserialize(serialized3);
        System.out.println("Deserialized inorder: " + TreeOperations.inorderTraversal(deserialized3));
        // Expected: [1]
    }
}
