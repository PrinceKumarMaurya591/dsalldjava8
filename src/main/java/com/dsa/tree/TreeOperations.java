package com.dsa.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Core Tree Operations and Traversals
//
// Tree Traversal types:
// 1. Inorder Traversal (Left -> Root -> Right)
// 2. Preorder Traversal (Root -> Left -> Right)
// 3. Postorder Traversal (Left -> Right -> Root)
// 4. Level Order Traversal (BFS - level by level)
//
// Tree Operations:
// - Insert, Search, Delete
// - Find Min/Max, Height, Size
//
// Tree vs Graph:
// - Tree is a connected acyclic graph with N nodes and N-1 edges
// - Tree has a root, no cycles, hierarchical structure
// - Graph can have cycles, multiple paths, any node can be starting point
//
// Benefits of Trees:
// - Fast search, insert, delete operations (O(log n) for balanced BST)
// - Hierarchical data representation
// - Natural ordering of data
// - Efficient range queries
//
// Limitations:
// - Degenerates to linked list in worst case (unbalanced)
// - Complex balancing algorithms needed
// - Not as fast as hash tables for simple lookups

public class TreeOperations {

    // ==================== TRAVERSALS ====================

    // Inorder Traversal: Left -> Root -> Right
    // Time: O(n), Space: O(h) where h is height of tree
    public static List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private static void inorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        inorderHelper(node.left, result);
        result.add(node.val);
        inorderHelper(node.right, result);
    }

    // Preorder Traversal: Root -> Left -> Right
    // Time: O(n), Space: O(h)
    public static List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorderHelper(root, result);
        return result;
    }

    private static void preorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        result.add(node.val);
        preorderHelper(node.left, result);
        preorderHelper(node.right, result);
    }

    // Postorder Traversal: Left -> Right -> Root
    // Time: O(n), Space: O(h)
    public static List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }

    private static void postorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        postorderHelper(node.left, result);
        postorderHelper(node.right, result);
        result.add(node.val);
    }

    // Level Order Traversal (BFS)
    // Time: O(n), Space: O(n)
    public static List<List<Integer>> levelOrderTraversal(TreeNode root) {
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

    // ==================== TREE OPERATIONS ====================

    // Insert a node in BST
    // Time: O(h), Space: O(h)
    public static TreeNode insert(TreeNode root, int val) {
        if (root == null) return new TreeNode(val);

        if (val < root.val) {
            root.left = insert(root.left, val);
        } else if (val > root.val) {
            root.right = insert(root.right, val);
        }
        return root;
    }

    // Search for a value in BST
    // Time: O(h), Space: O(h)
    public static boolean search(TreeNode root, int val) {
        if (root == null) return false;
        if (root.val == val) return true;
        return val < root.val ? search(root.left, val) : search(root.right, val);
    }

    // Find minimum value in BST
    // Time: O(h), Space: O(1)
    public static int findMin(TreeNode root) {
        if (root == null) throw new IllegalArgumentException("Tree is empty");
        TreeNode current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.val;
    }

    // Find maximum value in BST
    // Time: O(h), Space: O(1)
    public static int findMax(TreeNode root) {
        if (root == null) throw new IllegalArgumentException("Tree is empty");
        TreeNode current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.val;
    }

    // Find height of tree
    // Time: O(n), Space: O(h)
    public static int height(TreeNode root) {
        if (root == null) return -1; // Height of empty tree is -1
        return 1 + Math.max(height(root.left), height(root.right));
    }

    // Find size (number of nodes) of tree
    // Time: O(n), Space: O(h)
    public static int size(TreeNode root) {
        if (root == null) return 0;
        return 1 + size(root.left) + size(root.right);
    }

    // Delete a node from BST
    // Time: O(h), Space: O(h)
    public static TreeNode delete(TreeNode root, int val) {
        if (root == null) return null;

        if (val < root.val) {
            root.left = delete(root.left, val);
        } else if (val > root.val) {
            root.right = delete(root.right, val);
        } else {
            // Node to be deleted found
            // Case 1: Leaf node
            if (root.left == null && root.right == null) {
                return null;
            }
            // Case 2: One child
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;
            // Case 3: Two children - find inorder successor
            TreeNode successor = findMinNode(root.right);
            root.val = successor.val;
            root.right = delete(root.right, successor.val);
        }
        return root;
    }

    private static TreeNode findMinNode(TreeNode root) {
        TreeNode current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // ==================== MAIN METHOD ====================

    public static void main(String[] args) {
        // Build a sample tree:
        //       4
        //      / \
        //     2   6
        //    / \ / \
        //   1  3 5  7

        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(5);
        root.right.right = new TreeNode(7);

        System.out.println("Tree Traversals:");
        System.out.println("Inorder: " + inorderTraversal(root));
        System.out.println("Preorder: " + preorderTraversal(root));
        System.out.println("Postorder: " + postorderTraversal(root));
        System.out.println("Level Order: " + levelOrderTraversal(root));

        System.out.println("\nTree Operations:");
        System.out.println("Height: " + height(root));
        System.out.println("Size: " + size(root));
        System.out.println("Min: " + findMin(root));
        System.out.println("Max: " + findMax(root));
        System.out.println("Search 5: " + search(root, 5));
        System.out.println("Search 8: " + search(root, 8));

        System.out.println("\nInsert 8:");
        root = insert(root, 8);
        System.out.println("Inorder after insert: " + inorderTraversal(root));

        System.out.println("\nDelete 6:");
        root = delete(root, 6);
        System.out.println("Inorder after delete: " + inorderTraversal(root));
    }
}
