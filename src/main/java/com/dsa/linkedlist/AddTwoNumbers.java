package com.dsa.linkedlist;

/**
 * Problem: Add Two Numbers
 * 
 * You are given two non-empty linked lists representing two non-negative integers.
 * The digits are stored in reverse order, and each of their nodes contains a single digit.
 * Add the two numbers and return the sum as a linked list.
 * 
 * You may assume the two numbers do not contain any leading zero, except the number 0 itself.
 * 
 * Example:
 * Input: l1 = [2,4,3], l2 = [5,6,4]
 * Output: [7,0,8]
 * Explanation: 342 + 465 = 807.
 * 
 * Time Complexity: O(max(m, n)) where m and n are lengths of the two lists
 * Space Complexity: O(max(m, n)) for the result list
 */
public class AddTwoNumbers {

    /**
     * Adds two numbers represented as linked lists.
     * Uses a dummy head for simplified result construction.
     * Handles carry propagation and remaining digits.
     */
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;

            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            carry = sum / 10;
            current.next = new ListNode(sum % 10);
            current = current.next;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        // Test case 1: 342 + 465 = 807
        ListNode l1 = ListNode.fromArray(new int[]{2, 4, 3});
        ListNode l2 = ListNode.fromArray(new int[]{5, 6, 4});
        System.out.print("Input: l1 = ");
        ListNode.printList(l1);
        System.out.print("Input: l2 = ");
        ListNode.printList(l2);
        ListNode result = addTwoNumbers(l1, l2);
        System.out.print("Result: ");
        ListNode.printList(result);
        System.out.println("Expected: 7 -> 0 -> 8 (342 + 465 = 807)");
        System.out.println();

        // Test case 2: 0 + 0 = 0
        ListNode l3 = ListNode.fromArray(new int[]{0});
        ListNode l4 = ListNode.fromArray(new int[]{0});
        System.out.print("Input: l1 = ");
        ListNode.printList(l3);
        System.out.print("Input: l2 = ");
        ListNode.printList(l4);
        result = addTwoNumbers(l3, l4);
        System.out.print("Result: ");
        ListNode.printList(result);
        System.out.println("Expected: 0");
        System.out.println();

        // Test case 3: 9999999 + 9999 = 10009998
        ListNode l5 = ListNode.fromArray(new int[]{9, 9, 9, 9, 9, 9, 9});
        ListNode l6 = ListNode.fromArray(new int[]{9, 9, 9, 9});
        System.out.print("Input: l1 = ");
        ListNode.printList(l5);
        System.out.print("Input: l2 = ");
        ListNode.printList(l6);
        result = addTwoNumbers(l5, l6);
        System.out.print("Result: ");
        ListNode.printList(result);
        System.out.println("Expected: 8 -> 9 -> 9 -> 9 -> 0 -> 0 -> 0 -> 1");
        System.out.println();

        // Test case 4: Different lengths
        ListNode l7 = ListNode.fromArray(new int[]{1, 8});
        ListNode l8 = ListNode.fromArray(new int[]{0});
        System.out.print("Input: l1 = ");
        ListNode.printList(l7);
        System.out.print("Input: l2 = ");
        ListNode.printList(l8);
        result = addTwoNumbers(l7, l8);
        System.out.print("Result: ");
        ListNode.printList(result);
        System.out.println("Expected: 1 -> 8 (81 + 0 = 81)");
    }
}
