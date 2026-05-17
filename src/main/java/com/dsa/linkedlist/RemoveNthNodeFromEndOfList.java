package com.dsa.linkedlist;

/**
 * Problem: Remove Nth Node From End of List
 * 
 * Given the head of a linked list, remove the nth node from the end of the list
 * and return its head.
 * 
 * Example:
 * Input: head = [1,2,3,4,5], n = 2
 * Output: [1,2,3,5]
 * 
 * Approach: Two-pointer technique
 * - Use a dummy head to handle edge cases (removing the first node)
 * - Move fast pointer n steps ahead
 * - Move both slow and fast pointers until fast reaches the end
 * - Slow pointer will be at the node just before the one to remove
 * - Remove the nth node by updating slow.next
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class RemoveNthNodeFromEndOfList {

    /**
     * Removes the nth node from the end of the list.
     * Uses a dummy head to simplify edge cases.
     * Fast pointer advances n steps first, then both move together.
     */
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode slow = dummy;
        ListNode fast = dummy;

        // Move fast pointer n steps ahead
        for (int i = 0; i <= n; i++) {
            if (fast == null) {
                return head; // n is larger than list size
            }
            fast = fast.next;
        }

        // Move both pointers until fast reaches the end
        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }

        // Remove the nth node from end
        slow.next = slow.next.next;

        return dummy.next;
    }

    /**
     * Alternative implementation using length calculation.
     * First pass: calculate length of the list.
     * Second pass: remove (length - n)th node.
     */
    public static ListNode removeNthFromEndTwoPass(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        // First pass: calculate length
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }

        // Second pass: find the node before the one to remove
        int target = length - n;
        current = dummy;
        for (int i = 0; i < target; i++) {
            current = current.next;
        }

        // Remove the node
        current.next = current.next.next;

        return dummy.next;
    }

    public static void main(String[] args) {
        // Test case 1: Remove 2nd from end of [1,2,3,4,5]
        ListNode head1 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head1);
        ListNode result1 = removeNthFromEnd(head1, 2);
        System.out.print("After removing 2nd from end: ");
        ListNode.printList(result1);
        System.out.println("Expected: 1 -> 2 -> 3 -> 5");
        System.out.println();

        // Test case 2: Remove first node
        ListNode head2 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head2);
        ListNode result2 = removeNthFromEnd(head2, 5);
        System.out.print("After removing 5th from end (first node): ");
        ListNode.printList(result2);
        System.out.println("Expected: 2 -> 3 -> 4 -> 5");
        System.out.println();

        // Test case 3: Remove last node
        ListNode head3 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head3);
        ListNode result3 = removeNthFromEnd(head3, 1);
        System.out.print("After removing 1st from end (last node): ");
        ListNode.printList(result3);
        System.out.println("Expected: 1 -> 2 -> 3 -> 4");
        System.out.println();

        // Test case 4: Single node
        ListNode head4 = ListNode.fromArray(new int[]{1});
        System.out.print("Original: ");
        ListNode.printList(head4);
        ListNode result4 = removeNthFromEnd(head4, 1);
        System.out.print("After removing only node: ");
        ListNode.printList(result4);
        System.out.println("Expected: (empty)");
        System.out.println();

        // Test case 5: Two nodes, remove first
        ListNode head5 = ListNode.fromArray(new int[]{1, 2});
        System.out.print("Original: ");
        ListNode.printList(head5);
        ListNode result5 = removeNthFromEnd(head5, 2);
        System.out.print("After removing 2nd from end: ");
        ListNode.printList(result5);
        System.out.println("Expected: 2");
        System.out.println();

        // Test case 6: Two-pass approach
        ListNode head6 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head6);
        ListNode result6 = removeNthFromEndTwoPass(head6, 2);
        System.out.print("After removing 2nd from end (Two-pass): ");
        ListNode.printList(result6);
        System.out.println("Expected: 1 -> 2 -> 3 -> 5");
    }
}
