package com.dsa.linkedlist;

import java.util.PriorityQueue;

/**
 * Problem: Merge K Sorted Lists
 * 
 * You are given an array of k linked-lists lists, each linked-list is sorted in ascending order.
 * Merge all the linked-lists into one sorted linked-list and return it.
 * 
 * Example:
 * Input: lists = [[1,4,5],[1,3,4],[2,6]]
 * Output: [1,1,2,3,4,4,5,6]
 * 
 * Approaches:
 * 1. Min-Heap (Priority Queue): Insert all heads into a min-heap, repeatedly extract min
 * 2. Divide and Conquer: Pairwise merge lists recursively
 * 3. Sequential Merge: Merge first two, then merge result with third, etc.
 * 
 * Time Complexity: O(N log k) where N is total nodes and k is number of lists
 * Space Complexity: O(k) for the heap
 */
public class MergeKSortedLists {

    /**
     * Approach 1: Min-Heap (Priority Queue)
     * Insert the head of each list into a min-heap ordered by node value.
     * Repeatedly extract the minimum node, add it to result, and push its next.
     */
    public static ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        // Min-heap ordered by node value
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>(
            (a, b) -> a.val - b.val
        );

        // Add all non-null heads to the heap
        for (ListNode head : lists) {
            if (head != null) {
                minHeap.offer(head);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (!minHeap.isEmpty()) {
            ListNode node = minHeap.poll();
            current.next = node;
            current = current.next;

            if (node.next != null) {
                minHeap.offer(node.next);
            }
        }

        return dummy.next;
    }

    /**
     * Approach 2: Divide and Conquer
     * Pairwise merge lists, reducing k by half each iteration.
     * More efficient than sequential merge: O(N log k) time, O(1) extra space.
     */
    public static ListNode mergeKListsDivideAndConquer(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }

        int interval = 1;
        while (interval < lists.length) {
            for (int i = 0; i + interval < lists.length; i += interval * 2) {
                lists[i] = mergeTwoLists(lists[i], lists[i + interval]);
            }
            interval *= 2;
        }

        return lists[0];
    }

    /**
     * Helper method to merge two sorted lists.
     */
    private static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }

        if (l1 != null) {
            current.next = l1;
        } else {
            current.next = l2;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        // Test case 1: Normal case
        ListNode[] lists1 = new ListNode[]{
            ListNode.fromArray(new int[]{1, 4, 5}),
            ListNode.fromArray(new int[]{1, 3, 4}),
            ListNode.fromArray(new int[]{2, 6})
        };
        System.out.println("Test 1 - Merge [[1,4,5],[1,3,4],[2,6]]:");
        for (int i = 0; i < lists1.length; i++) {
            System.out.print("  List " + (i + 1) + ": ");
            ListNode.printList(lists1[i]);
        }
        ListNode result1 = mergeKLists(lists1);
        System.out.print("Result (Heap): ");
        ListNode.printList(result1);
        System.out.println("Expected: 1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6");
        System.out.println();

        // Test case 2: Empty array
        ListNode[] lists2 = new ListNode[]{};
        System.out.println("Test 2 - Empty array:");
        ListNode result2 = mergeKLists(lists2);
        System.out.print("Result: ");
        ListNode.printList(result2);
        System.out.println("Expected: (empty)");
        System.out.println();

        // Test case 3: Array with null lists
        ListNode[] lists3 = new ListNode[]{null, ListNode.fromArray(new int[]{1}), null};
        System.out.println("Test 3 - Array with nulls:");
        ListNode result3 = mergeKLists(lists3);
        System.out.print("Result: ");
        ListNode.printList(result3);
        System.out.println("Expected: 1");
        System.out.println();

        // Test case 4: Single list
        ListNode[] lists4 = new ListNode[]{
            ListNode.fromArray(new int[]{1, 2, 3})
        };
        System.out.println("Test 4 - Single list:");
        ListNode result4 = mergeKLists(lists4);
        System.out.print("Result: ");
        ListNode.printList(result4);
        System.out.println("Expected: 1 -> 2 -> 3");
        System.out.println();

        // Test case 5: Divide and Conquer approach
        ListNode[] lists5 = new ListNode[]{
            ListNode.fromArray(new int[]{1, 4, 5}),
            ListNode.fromArray(new int[]{1, 3, 4}),
            ListNode.fromArray(new int[]{2, 6})
        };
        System.out.println("Test 5 - Divide and Conquer:");
        ListNode result5 = mergeKListsDivideAndConquer(lists5);
        System.out.print("Result: ");
        ListNode.printList(result5);
        System.out.println("Expected: 1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6");
    }
}
