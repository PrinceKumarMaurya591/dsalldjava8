package com.dsa.linkedlist;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem: Copy List with Random Pointer
 * 
 * A linked list of length n is given such that each node contains an additional
 * random pointer, which could point to any node in the list or null.
 * 
 * Construct a deep copy of the list. The deep copy should consist of exactly n
 * brand new nodes, where each new node has its value set to the value of its
 * corresponding original node. Both the next and random pointers of the new nodes
 * should point to new nodes in the copied list such that the pointers in the
 * original list and copied list represent the same list state.
 * 
 * None of the pointers in the new list should point to nodes in the original list.
 * 
 * Example:
 * Input: head = [[7,null],[13,0],[11,4],[10,2],[1,0]]
 * Output: [[7,null],[13,0],[11,4],[10,2],[1,0]]
 * 
 * Approaches:
 * 1. HashMap: Store mapping from original node to copied node
 * 2. Interweaving: Insert copied nodes between original nodes, then separate
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n) for HashMap, O(1) for interweaving
 */
public class CopyListWithRandomPointer {

    static class Node {
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    /**
     * Approach 1: HashMap
     * First pass: create all new nodes and store mapping
     * Second pass: set next and random pointers using the map
     */
    public static Node copyRandomList(Node head) {
        if (head == null) {
            return null;
        }

        // First pass: create all new nodes and store mapping
        Map<Node, Node> map = new HashMap<>();
        Node current = head;
        while (current != null) {
            map.put(current, new Node(current.val));
            current = current.next;
        }

        // Second pass: set next and random pointers
        current = head;
        while (current != null) {
            Node copy = map.get(current);
            copy.next = map.get(current.next);
            copy.random = map.get(current.random);
            current = current.next;
        }

        return map.get(head);
    }

    /**
     * Approach 2: Interweaving (O(1) extra space)
     * Step 1: Insert copied nodes between original nodes
     * Step 2: Set random pointers for copied nodes
     * Step 3: Separate the interwoven list into original and copy
     */
    public static Node copyRandomListInterweave(Node head) {
        if (head == null) {
            return null;
        }

        // Step 1: Insert copied nodes between original nodes
        // Original: A -> B -> C
        // After: A -> A' -> B -> B' -> C -> C'
        Node current = head;
        while (current != null) {
            Node copy = new Node(current.val);
            copy.next = current.next;
            current.next = copy;
            current = copy.next;
        }

        // Step 2: Set random pointers for copied nodes
        current = head;
        while (current != null) {
            if (current.random != null) {
                current.next.random = current.random.next;
            }
            current = current.next.next;
        }

        // Step 3: Separate the interwoven list
        Node dummy = new Node(0);
        Node copyCurrent = dummy;
        current = head;

        while (current != null) {
            copyCurrent.next = current.next;
            copyCurrent = copyCurrent.next;
            current.next = current.next.next;
            current = current.next;
        }

        return dummy.next;
    }

    /**
     * Helper method to create a linked list with random pointers from a 2D array.
     * Each element is [val, randomIndex] where randomIndex is the index of the
     * node the random pointer should point to, or -1 for null.
     */
    public static Node createListWithRandom(int[][] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        Node[] nodes = new Node[values.length];
        for (int i = 0; i < values.length; i++) {
            nodes[i] = new Node(values[i][0]);
        }

        for (int i = 0; i < values.length; i++) {
            if (i < values.length - 1) {
                nodes[i].next = nodes[i + 1];
            }
            int randomIndex = values[i][1];
            if (randomIndex >= 0 && randomIndex < values.length) {
                nodes[i].random = nodes[randomIndex];
            }
        }

        return nodes[0];
    }

    /**
     * Helper method to print the list with random pointers.
     */
    public static void printListWithRandom(Node head) {
        Node current = head;
        while (current != null) {
            String randomVal = (current.random != null) ? String.valueOf(current.random.val) : "null";
            System.out.print("[" + current.val + "," + randomVal + "]");
            if (current.next != null) {
                System.out.print(" -> ");
            }
            current = current.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Test case 1: [[7,null],[13,0],[11,4],[10,2],[1,0]]
        Node head1 = createListWithRandom(new int[][]{
            {7, -1}, {13, 0}, {11, 4}, {10, 2}, {1, 0}
        });
        System.out.println("Test 1 - Original:");
        System.out.print("  ");
        printListWithRandom(head1);
        Node copy1 = copyRandomList(head1);
        System.out.print("  Copy (HashMap): ");
        printListWithRandom(copy1);
        System.out.println("  Expected: [7,null] -> [13,7] -> [11,1] -> [10,11] -> [1,7]");
        System.out.println("  Is deep copy: " + (head1 != copy1));
        System.out.println();

        // Test case 2: Single node with null random
        Node head2 = createListWithRandom(new int[][]{{1, -1}});
        System.out.println("Test 2 - Single node:");
        System.out.print("  Original: ");
        printListWithRandom(head2);
        Node copy2 = copyRandomList(head2);
        System.out.print("  Copy: ");
        printListWithRandom(copy2);
        System.out.println("  Expected: [1,null]");
        System.out.println();

        // Test case 3: Two nodes pointing to each other
        Node head3 = createListWithRandom(new int[][]{{1, 1}, {2, 0}});
        System.out.println("Test 3 - Mutual random pointers:");
        System.out.print("  Original: ");
        printListWithRandom(head3);
        Node copy3 = copyRandomList(head3);
        System.out.print("  Copy: ");
        printListWithRandom(copy3);
        System.out.println("  Expected: [1,2] -> [2,1]");
        System.out.println();

        // Test case 4: Interweaving approach
        Node head4 = createListWithRandom(new int[][]{
            {7, -1}, {13, 0}, {11, 4}, {10, 2}, {1, 0}
        });
        System.out.println("Test 4 - Interweaving approach:");
        System.out.print("  Original: ");
        printListWithRandom(head4);
        Node copy4 = copyRandomListInterweave(head4);
        System.out.print("  Copy: ");
        printListWithRandom(copy4);
        System.out.println("  Expected: [7,null] -> [13,7] -> [11,1] -> [10,11] -> [1,7]");
        System.out.println("  Is deep copy: " + (head4 != copy4));
        System.out.println();

        // Test case 5: Null list
        Node head5 = null;
        System.out.println("Test 5 - Null list:");
        Node copy5 = copyRandomList(head5);
        System.out.println("  Copy: " + (copy5 == null ? "null" : "not null"));
        System.out.println("  Expected: null");
    }
}
