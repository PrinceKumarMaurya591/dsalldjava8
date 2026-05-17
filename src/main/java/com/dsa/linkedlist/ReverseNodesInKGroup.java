package com.dsa.linkedlist;

/**
 * Problem: Reverse Nodes in k-Group
 * 
 * Given the head of a linked list, reverse the nodes of the list k at a time,
 * and return the modified list.
 * 
 * k is a positive integer and is less than or equal to the length of the linked list.
 * If the number of nodes is not a multiple of k then left-out nodes, in the end,
 * should remain as they are.
 * 
 * You may not alter the values in the list's nodes, only nodes themselves may be changed.
 * 
 * Example:
 * Input: head = [1,2,3,4,5], k = 2
 * Output: [2,1,4,3,5]
 * 
 * Input: head = [1,2,3,4,5], k = 3
 * Output: [3,2,1,4,5]
 * 
 * Approach: Recursive
 * - Check if there are at least k nodes remaining
 * - Reverse the first k nodes
 * - Recursively reverse the remaining nodes in k-groups
 * - Connect the reversed group to the result of the recursive call
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n/k) for recursive call stack
 */
public class ReverseNodesInKGroup {

    /**
     * Reverses nodes in k-group recursively.
     * 1. Check if there are k nodes to reverse
     * 2. Reverse the first k nodes
     * 3. Recursively process the rest
     * 4. Connect and return
     */
    public static ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k <= 1) {
            return head;
        }

        // Check if there are at least k nodes
        ListNode current = head;
        int count = 0;
        while (current != null && count < k) {
            current = current.next;
            count++;
        }

        // If fewer than k nodes remain, return head as-is
        if (count < k) {
            return head;
        }

        // Reverse the first k nodes
        ListNode prev = null;
        ListNode curr = head;
        for (int i = 0; i < k; i++) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        // Recursively reverse the remaining nodes
        // head is now the last node of the reversed group
        head.next = reverseKGroup(curr, k);

        // prev is the new head of the reversed group
        return prev;
    }

    /**
     * Iterative approach using a dummy head.
     * More complex but avoids recursion overhead.
     */
    public static ListNode reverseKGroupIterative(ListNode head, int k) {
        if (head == null || k <= 1) {
            return head;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prevGroupEnd = dummy;

        while (true) {
            // Check if there are at least k nodes remaining
            ListNode kthNode = prevGroupEnd;
            for (int i = 0; i < k && kthNode != null; i++) {
                kthNode = kthNode.next;
            }
            if (kthNode == null) {
                break; // Less than k nodes remaining
            }

            // Reverse k nodes starting from prevGroupEnd.next
            ListNode groupStart = prevGroupEnd.next;
            ListNode nextGroupStart = kthNode.next;

            // Reverse the group
            ListNode prev = kthNode.next;
            ListNode current = groupStart;
            while (current != nextGroupStart) {
                ListNode next = current.next;
                current.next = prev;
                prev = current;
                current = next;
            }

            // Connect the reversed group
            prevGroupEnd.next = kthNode;
            prevGroupEnd = groupStart;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        // Test case 1: k = 2
        ListNode head1 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head1);
        ListNode result1 = reverseKGroup(head1, 2);
        System.out.print("Reversed in groups of 2: ");
        ListNode.printList(result1);
        System.out.println("Expected: 2 -> 1 -> 4 -> 3 -> 5");
        System.out.println();

        // Test case 2: k = 3
        ListNode head2 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head2);
        ListNode result2 = reverseKGroup(head2, 3);
        System.out.print("Reversed in groups of 3: ");
        ListNode.printList(result2);
        System.out.println("Expected: 3 -> 2 -> 1 -> 4 -> 5");
        System.out.println();

        // Test case 3: k = 1 (no change)
        ListNode head3 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        ListNode.printList(head3);
        ListNode result3 = reverseKGroup(head3, 1);
        System.out.print("Reversed in groups of 1: ");
        ListNode.printList(result3);
        System.out.println("Expected: 1 -> 2 -> 3 -> 4 -> 5");
        System.out.println();

        // Test case 4: k equals length
        ListNode head4 = ListNode.fromArray(new int[]{1, 2, 3, 4});
        System.out.print("Original: ");
        ListNode.printList(head4);
        ListNode result4 = reverseKGroup(head4, 4);
        System.out.print("Reversed in groups of 4: ");
        ListNode.printList(result4);
        System.out.println("Expected: 4 -> 3 -> 2 -> 1");
        System.out.println();

        // Test case 5: k larger than length
        ListNode head5 = ListNode.fromArray(new int[]{1, 2, 3});
        System.out.print("Original: ");
        ListNode.printList(head5);
        ListNode result5 = reverseKGroup(head5, 5);
        System.out.print("Reversed in groups of 5: ");
        ListNode.printList(result5);
        System.out.println("Expected: 1 -> 2 -> 3 (no change)");
        System.out.println();

        // Test case 6: Iterative approach
        ListNode head6 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5, 6, 7});
        System.out.print("Original: ");
        ListNode.printList(head6);
        ListNode result6 = reverseKGroupIterative(head6, 3);
        System.out.print("Reversed in groups of 3 (Iterative): ");
        ListNode.printList(result6);
        System.out.println("Expected: 3 -> 2 -> 1 -> 6 -> 5 -> 4 -> 7");
        System.out.println();

        // Test case 7: Single node
        ListNode head7 = ListNode.fromArray(new int[]{1});
        System.out.print("Original: ");
        ListNode.printList(head7);
        ListNode result7 = reverseKGroup(head7, 2);
        System.out.print("Reversed in groups of 2: ");
        ListNode.printList(result7);
        System.out.println("Expected: 1");
    }
}
