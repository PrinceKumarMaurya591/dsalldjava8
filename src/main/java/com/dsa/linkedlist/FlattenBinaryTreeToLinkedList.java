package com.dsa.linkedlist;

/**
 * Problem: Flatten Binary Tree to Linked List
 * 
 * Given the root of a binary tree, flatten the tree into a "linked list":
 * 
 * - The "linked list" should use the same TreeNode class where the right child
 *   pointer points to the next node in the list and the left child pointer is always null.
 * - The "linked list" should be in the same order as a pre-order traversal of the binary tree.
 * 
 * Example:
 * Input: root = [1,2,5,3,4,null,6]
 * Output: [1,null,2,null,3,null,4,null,5,null,6]
 * 
 * Explanation: The pre-order traversal is [1,2,3,4,5,6].
 * After flattening, the tree becomes:
 * 1 -> 2 -> 3 -> 4 -> 5 -> 6
 * (all left pointers are null, right pointers form the list)
 * 
 * Approaches:
 * 1. Recursive: Flatten left and right subtrees, then rearrange pointers
 * 2. Morris Traversal-like: Iterative using rightmost node of left subtree
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(h) for recursive (call stack), O(1) for iterative
 */
public class FlattenBinaryTreeToLinkedList {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Recursive approach.
     * Flatten left and right subtrees, then:
     * 1. Save right subtree
     * 2. Move left subtree to right
     * 3. Traverse to end of new right subtree
     * 4. Attach saved right subtree
     */
    public static void flatten(TreeNode root) {
        if (root == null) {
            return;
        }

        // Flatten left and right subtrees
        flatten(root.left);
        flatten(root.right);

        // Save the right subtree
        TreeNode rightSubtree = root.right;

        // Move left subtree to right
        root.right = root.left;
        root.left = null;

        // Traverse to the end of the new right subtree
        TreeNode current = root;
        while (current.right != null) {
            current = current.right;
        }

        // Attach the saved right subtree
        current.right = rightSubtree;
    }

    /**
     * Iterative approach using Morris-like traversal.
     * For each node, find the rightmost node in its left subtree,
     * then rearrange pointers.
     */
    public static void flattenIterative(TreeNode root) {
        TreeNode current = root;

        while (current != null) {
            if (current.left != null) {
                // Find the rightmost node in the left subtree
                TreeNode rightmost = current.left;
                while (rightmost.right != null) {
                    rightmost = rightmost.right;
                }

                // Rearrange pointers
                rightmost.right = current.right;
                current.right = current.left;
                current.left = null;
            }
            current = current.right;
        }
    }

    /**
     * Helper method to print the flattened tree as a linked list.
     */
    public static void printFlattened(TreeNode root) {
        TreeNode current = root;
        while (current != null) {
            System.out.print(current.val);
            if (current.right != null) {
                System.out.print(" -> ");
            }
            current = current.right;
        }
        System.out.println();
    }

    /**
     * Helper method to build a binary tree from an array (level order).
     * null values represent missing nodes.
     */
    public static TreeNode buildTree(Integer[] values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return null;
        }

        TreeNode[] nodes = new TreeNode[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                nodes[i] = new TreeNode(values[i]);
            }
        }

        for (int i = 0; i < values.length; i++) {
            if (nodes[i] != null) {
                int leftIdx = 2 * i + 1;
                int rightIdx = 2 * i + 2;
                if (leftIdx < values.length) {
                    nodes[i].left = nodes[leftIdx];
                }
                if (rightIdx < values.length) {
                    nodes[i].right = nodes[rightIdx];
                }
            }
        }

        return nodes[0];
    }

    public static void main(String[] args) {
        // Test case 1: Standard tree
        TreeNode root1 = buildTree(new Integer[]{1, 2, 5, 3, 4, null, 6});
        System.out.println("Test 1 - Tree [1,2,5,3,4,null,6]:");
        flatten(root1);
        System.out.print("Flattened (Recursive): ");
        printFlattened(root1);
        System.out.println("Expected: 1 -> 2 -> 3 -> 4 -> 5 -> 6");
        System.out.println();

        // Test case 2: Single node
        TreeNode root2 = buildTree(new Integer[]{1});
        System.out.println("Test 2 - Single node:");
        flatten(root2);
        System.out.print("Flattened: ");
        printFlattened(root2);
        System.out.println("Expected: 1");
        System.out.println();

        // Test case 3: Left-skewed tree
        TreeNode root3 = buildTree(new Integer[]{1, 2, null, 3});
        System.out.println("Test 3 - Left-skewed tree:");
        flatten(root3);
        System.out.print("Flattened: ");
        printFlattened(root3);
        System.out.println("Expected: 1 -> 2 -> 3");
        System.out.println();

        // Test case 4: Right-skewed tree
        TreeNode root4 = buildTree(new Integer[]{1, null, 2, null, null, null, 3});
        System.out.println("Test 4 - Right-skewed tree:");
        flatten(root4);
        System.out.print("Flattened: ");
        printFlattened(root4);
        System.out.println("Expected: 1 -> 2 -> 3");
        System.out.println();

        // Test case 5: Iterative approach
        TreeNode root5 = buildTree(new Integer[]{1, 2, 5, 3, 4, null, 6});
        System.out.println("Test 5 - Iterative approach:");
        flattenIterative(root5);
        System.out.print("Flattened: ");
        printFlattened(root5);
        System.out.println("Expected: 1 -> 2 -> 3 -> 4 -> 5 -> 6");
        System.out.println();

        // Test case 6: Empty tree
        TreeNode root6 = null;
        System.out.println("Test 6 - Empty tree:");
        flatten(root6);
        System.out.print("Flattened: ");
        printFlattened(root6);
        System.out.println("Expected: (empty)");
    }
}
