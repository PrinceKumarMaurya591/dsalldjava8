package com.dsa.linkedlist;

/**
 * Problem: Middle of the Linked List
 * 
 * Given the head of a singly linked list, return the middle node of the linked list.
 * 
 * If there are two middle nodes, return the second middle node.
 * 
 * Example:
 * Input: head = [1,2,3,4,5]
 * Output: [3,4,5]
 * Explanation: The middle node is node 3.
 * 
 * Input: head = [1,2,3,4,5,6]
 * Output: [4,5,6]
 * Explanation: Since there are two middle nodes (3 and 4), we return the second one (4).
 * 
 * Approach: Slow and Fast Pointer (Tortoise and Hare)
 * - Slow pointer moves one step at a time
 * - Fast pointer moves two steps at a time
 * - When fast reaches the end, slow is at the middle
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class MiddleOfLinkedList {

    /**
     * Returns the middle node using slow and fast pointers.
     * When fast reaches the end, slow is at the middle.
     * For even length, returns the second middle node.
     */
    public static ListNode middleNode(ListNode head) {
        if (head == null) {
            return null;
        }

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    /**
     * Returns the middle node using length calculation.
     * First pass: calculate length.
     * Second pass: traverse to middle.
     */
    public static ListNode middleNodeTwoPass(ListNode head) {
        if (head == null) {
            return null;
        }

        // First pass: calculate length
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }

        // Second pass: traverse to middle
        int middle = length / 2;
        current = head;
        for (int i = 0; i < middle; i++) {
            current = current.next;
        }

        return current;
    }

    /**
     * Returns the first middle node for even-length lists.
     * For [1,2,3,4,5,6], returns node 3 instead of 4.
     */
    public static ListNode middleNodeFirst(ListNode head) {
        if (head == null) {
            return null;
        }

        ListNode slow = head;
        ListNode fast = head;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    public static void main(String[] args) {
        // Test case 1: Odd length
        ListNode head1 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("List: ");
        ListNode.printList(head1);
        ListNode middle1 = middleNode(head1);
        System.out.println("Middle node value: " + middle1.val);
        System.out.print("From middle to end: ");
        ListNode.printList(middle1);
        System.out.println("Expected: 3 -> 4 -> 5");
        System.out.println();

        // Test case 2: Even length (returns second middle)
        ListNode head2 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5, 6});
        System.out.print("List: ");
        ListNode.printList(head2);
        ListNode middle2 = middleNode(head2);
        System.out.println("Middle node value: " + middle2.val);
        System.out.print("From middle to end: ");
        ListNode.printList(middle2);
        System.out.println("Expected: 4 -> 5 -> 6");
        System.out.println();

        // Test case 3: Single node
        ListNode head3 = ListNode.fromArray(new int[]{1});
        System.out.print("List: ");
        ListNode.printList(head3);
        ListNode middle3 = middleNode(head3);
        System.out.println("Middle node value: " + (middle3 != null ? middle3.val : "null"));
        System.out.println("Expected: 1");
        System.out.println();

        // Test case 4: Two nodes
        ListNode head4 = ListNode.fromArray(new int[]{1, 2});
        System.out.print("List: ");
        ListNode.printList(head4);
        ListNode middle4 = middleNode(head4);
        System.out.println("Middle node value: " + middle4.val);
        System.out.print("From middle to end: ");
        ListNode.printList(middle4);
        System.out.println("Expected: 2");
        System.out.println();

        // Test case 5: First middle for even length
        ListNode head5 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5, 6});
        System.out.print("List: ");
        ListNode.printList(head5);
        ListNode middle5 = middleNodeFirst(head5);
        System.out.println("First middle node value: " + middle5.val);
        System.out.print("From first middle to end: ");
        ListNode.printList(middle5);
        System.out.println("Expected: 3 -> 4 -> 5 -> 6");
        System.out.println();

        // Test case 6: Empty list
        ListNode head6 = null;
        System.out.print("List: ");
        ListNode.printList(head6);
        ListNode middle6 = middleNode(head6);
        System.out.println("Middle node: " + (middle6 != null ? middle6.val : "null"));
        System.out.println("Expected: null");
    }
}
