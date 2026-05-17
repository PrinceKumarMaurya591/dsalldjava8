package com.dsa.linkedlist;

/**
 * Problem: Merge Two Sorted Lists
 * 
 * You are given the heads of two sorted linked lists list1 and list2.
 * Merge the two lists into one sorted list. The list should be made by
 * splicing together the nodes of the first two lists.
 * 
 * Return the head of the merged linked list.
 * 
 * Example:
 * Input: list1 = [1,2,4], list2 = [1,3,4]
 * Output: [1,1,2,3,4,4]
 * 
 * Approaches:
 * 1. Iterative: Use a dummy head, compare nodes and link the smaller one
 * 2. Recursive: Compare heads, recursively merge the rest
 * 
 * Time Complexity: O(m + n) where m and n are lengths of the two lists
 * Space Complexity: O(1) for iterative, O(m + n) for recursive (call stack)
 */
public class MergeTwoSortedLists {

    /**
     * Iterative approach using a dummy head node.
     * Compares nodes from both lists and links the smaller one.
     * When one list is exhausted, links the remainder of the other.
     */
    public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        // Attach remaining nodes from either list
        if (list1 != null) {
            current.next = list1;
        } else {
            current.next = list2;
        }

        return dummy.next;
    }

    /**
     * Recursive approach.
     * Base case: if either list is null, return the other.
     * Recursive case: compare heads, link the smaller to the merged result of the rest.
     */
    public static ListNode mergeTwoListsRecursive(ListNode list1, ListNode list2) {
        // Base cases
        if (list1 == null) {
            return list2;
        }
        if (list2 == null) {
            return list1;
        }

        // Recursive case
        if (list1.val <= list2.val) {
            list1.next = mergeTwoListsRecursive(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoListsRecursive(list1, list2.next);
            return list2;
        }
    }

    public static void main(String[] args) {
        // Test case 1: Normal merge
        ListNode list1 = ListNode.fromArray(new int[]{1, 2, 4});
        ListNode list2 = ListNode.fromArray(new int[]{1, 3, 4});
        System.out.print("List 1: ");
        ListNode.printList(list1);
        System.out.print("List 2: ");
        ListNode.printList(list2);
        ListNode merged = mergeTwoLists(list1, list2);
        System.out.print("Merged (Iterative): ");
        ListNode.printList(merged);
        System.out.println("Expected: 1 -> 1 -> 2 -> 3 -> 4 -> 4");
        System.out.println();

        // Test case 2: One empty list
        ListNode list3 = ListNode.fromArray(new int[]{});
        ListNode list4 = ListNode.fromArray(new int[]{0});
        System.out.print("List 1: ");
        ListNode.printList(list3);
        System.out.print("List 2: ");
        ListNode.printList(list4);
        merged = mergeTwoLists(list3, list4);
        System.out.print("Merged: ");
        ListNode.printList(merged);
        System.out.println("Expected: 0");
        System.out.println();

        // Test case 3: Both empty
        ListNode list5 = null;
        ListNode list6 = null;
        System.out.print("List 1: ");
        ListNode.printList(list5);
        System.out.print("List 2: ");
        ListNode.printList(list6);
        merged = mergeTwoLists(list5, list6);
        System.out.print("Merged: ");
        ListNode.printList(merged);
        System.out.println("Expected: (empty)");
        System.out.println();

        // Test case 4: Different lengths
        ListNode list7 = ListNode.fromArray(new int[]{1, 3, 5, 7});
        ListNode list8 = ListNode.fromArray(new int[]{2, 4});
        System.out.print("List 1: ");
        ListNode.printList(list7);
        System.out.print("List 2: ");
        ListNode.printList(list8);
        merged = mergeTwoLists(list7, list8);
        System.out.print("Merged: ");
        ListNode.printList(merged);
        System.out.println("Expected: 1 -> 2 -> 3 -> 4 -> 5 -> 7");
        System.out.println();

        // Test case 5: Recursive approach
        ListNode list9 = ListNode.fromArray(new int[]{1, 2, 4});
        ListNode list10 = ListNode.fromArray(new int[]{1, 3, 4});
        System.out.print("List 1: ");
        ListNode.printList(list9);
        System.out.print("List 2: ");
        ListNode.printList(list10);
        merged = mergeTwoListsRecursive(list9, list10);
        System.out.print("Merged (Recursive): ");
        ListNode.printList(merged);
        System.out.println("Expected: 1 -> 1 -> 2 -> 3 -> 4 -> 4");
    }
}
