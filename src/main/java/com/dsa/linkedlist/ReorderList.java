package com.dsa.linkedlist;

/**
 * Problem: Reorder List
 * 
 * You are given the head of a singly linked-list. The list can be represented as:
 * L0 -> L1 -> ... -> Ln-1 -> Ln
 * 
 * Reorder the list to be in the following form:
 * L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 -> ...
 * 
 * You may not modify the values in the list's nodes. Only nodes themselves may be changed.
 * 
 * Example:
 * Input: head = [1,2,3,4]
 * Output: [1,4,2,3]
 * 
 * Input: head = [1,2,3,4,5]
 * Output: [1,5,2,4,3]
 * 
 * Approach: Three-step process
 * 1. Find the middle of the linked list (slow/fast pointer)
 * 2. Reverse the second half of the list
 * 3. Merge the two halves (interleave first and reversed second)
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class ReorderList {

    /**
     * Reorders the list to L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 -> ...
     * Uses three steps: find middle, reverse second half, merge.
     */
    public static void reorderList(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        // Step 1: Find the middle of the list
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Step 2: Reverse the second half
        ListNode secondHalf = reverse(slow.next);
        slow.next = null; // Split the list into two halves

        // Step 3: Merge the two halves (interleave)
        ListNode firstHalf = head;
        while (secondHalf != null) {
            ListNode nextFirst = firstHalf.next;
            ListNode nextSecond = secondHalf.next;

            firstHalf.next = secondHalf;
            secondHalf.next = nextFirst;

            firstHalf = nextFirst;
            secondHalf = nextSecond;
        }
    }

    /**
     * Helper method to reverse a linked list.
     */
    private static ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        return prev;
    }

    public static void main(String[] args) {
        // Test case 1: Even length
        ListNode head1 = ListNode.fromArray(new int[]{1, 2, 3, 4});
        System.out.print("Original: ");
        ListNode.printList(head1);
        reorderList(head1);
        System.out.print("Reordered: ");
        ListNode.printList(head1);
        System.out.println("Expected: 1 -> 4 -> 2 -> 3");
        System.out.println();

        // Test case 2: Odd length
        ListNode head2 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head2);
        reorderList(head2);
        System.out.print("Reordered: ");
        ListNode.printList(head2);
        System.out.println("Expected: 1 -> 5 -> 2 -> 4 -> 3");
        System.out.println();

        // Test case 3: Single node
        ListNode head3 = ListNode.fromArray(new int[]{1});
        System.out.print("Original: ");
        ListNode.printList(head3);
        reorderList(head3);
        System.out.print("Reordered: ");
        ListNode.printList(head3);
        System.out.println("Expected: 1");
        System.out.println();

        // Test case 4: Two nodes
        ListNode head4 = ListNode.fromArray(new int[]{1, 2});
        System.out.print("Original: ");
        ListNode.printList(head4);
        reorderList(head4);
        System.out.print("Reordered: ");
        ListNode.printList(head4);
        System.out.println("Expected: 1 -> 2");
        System.out.println();

        // Test case 5: Longer list
        ListNode head5 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5, 6});
        System.out.print("Original: ");
        ListNode.printList(head5);
        reorderList(head5);
        System.out.print("Reordered: ");
        ListNode.printList(head5);
        System.out.println("Expected: 1 -> 6 -> 2 -> 5 -> 3 -> 4");
    }
}
