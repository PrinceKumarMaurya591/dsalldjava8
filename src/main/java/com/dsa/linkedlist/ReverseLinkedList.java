package com.dsa.linkedlist;

/**
 * Problem: Reverse a Linked List
 * 
 * Given the head of a singly linked list, reverse the list, and return the reversed list.
 * 
 * Example:
 * Input: head = [1,2,3,4,5]
 * Output: [5,4,3,2,1]
 * 
 * Approaches:
 * 1. Iterative: Use three pointers (prev, current, next) to reverse links in-place
 * 2. Recursive: Reverse the rest of the list, then fix the head
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1) for iterative, O(n) for recursive (call stack)
 */
public class ReverseLinkedList {

    /**
     * Iterative approach using three pointers.
     * Maintains prev, current, and next pointers to reverse links one by one.
     */
    public static ListNode reverseIterative(ListNode head) {
        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode next = current.next; // Save next node
            current.next = prev;          // Reverse the link
            prev = current;               // Move prev forward
            current = next;               // Move current forward
        }

        return prev; // New head
    }

    /**
     * Recursive approach.
     * Base case: empty list or single node
     * Recursive case: reverse rest, then make head the last node
     */
    public static ListNode reverseRecursive(ListNode head) {
        // Base case: empty list or single node
        if (head == null || head.next == null) {
            return head;
        }

        // Reverse the rest of the list
        ListNode newHead = reverseRecursive(head.next);

        // Make the current head the last node
        head.next.next = head;
        head.next = null;

        return newHead;
    }

    public static void main(String[] args) {
        // Test case 1: Normal list
        ListNode head1 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head1);
        ListNode reversed1 = reverseIterative(head1);
        System.out.print("Reversed (Iterative): ");
        ListNode.printList(reversed1);
        System.out.println("Expected: 5 -> 4 -> 3 -> 2 -> 1");
        System.out.println();

        // Test case 2: Single node
        ListNode head2 = ListNode.fromArray(new int[]{1});
        System.out.print("Original: ");
        ListNode.printList(head2);
        ListNode reversed2 = reverseIterative(head2);
        System.out.print("Reversed: ");
        ListNode.printList(reversed2);
        System.out.println("Expected: 1");
        System.out.println();

        // Test case 3: Empty list
        ListNode head3 = null;
        System.out.print("Original: ");
        ListNode.printList(head3);
        ListNode reversed3 = reverseIterative(head3);
        System.out.print("Reversed: ");
        ListNode.printList(reversed3);
        System.out.println("Expected: (empty)");
        System.out.println();

        // Test case 4: Two nodes
        ListNode head4 = ListNode.fromArray(new int[]{1, 2});
        System.out.print("Original: ");
        ListNode.printList(head4);
        ListNode reversed4 = reverseIterative(head4);
        System.out.print("Reversed: ");
        ListNode.printList(reversed4);
        System.out.println("Expected: 2 -> 1");
        System.out.println();

        // Test case 5: Recursive approach
        ListNode head5 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head5);
        ListNode reversed5 = reverseRecursive(head5);
        System.out.print("Reversed (Recursive): ");
        ListNode.printList(reversed5);
        System.out.println("Expected: 5 -> 4 -> 3 -> 2 -> 1");
    }
}
