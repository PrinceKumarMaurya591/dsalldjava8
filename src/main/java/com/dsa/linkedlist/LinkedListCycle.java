package com.dsa.linkedlist;

/**
 * Problem: Linked List Cycle (Detect Cycle in a Linked List)
 * 
 * Given head, the head of a linked list, determine if the linked list has a cycle in it.
 * 
 * There is a cycle in a linked list if there is some node in the list that can be reached
 * again by continuously following the next pointer. Internally, pos is used to denote the
 * index of the node that tail's next pointer is connected to.
 * 
 * Return true if there is a cycle in the linked list. Otherwise, return false.
 * 
 * Approach: Floyd's Cycle Detection (Tortoise and Hare)
 * - Use two pointers: slow moves one step, fast moves two steps
 * - If they meet, there is a cycle
 * - If fast reaches null, there is no cycle
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class LinkedListCycle {

    /**
     * Detects if a linked list has a cycle using Floyd's Cycle Detection algorithm.
     * Slow pointer moves 1 step, fast pointer moves 2 steps.
     * If they meet, a cycle exists.
     */
    public static boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true; // Cycle detected
            }
        }

        return false; // No cycle
    }

    /**
     * Returns the node where the cycle begins, or null if no cycle.
     * After detecting cycle with Floyd's algorithm:
     * - Reset slow to head
     * - Move both pointers one step at a time
     * - They meet at the start of the cycle
     */
    public static ListNode detectCycleStart(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }

        ListNode slow = head;
        ListNode fast = head;
        boolean hasCycle = false;

        // Phase 1: Detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                hasCycle = true;
                break;
            }
        }

        if (!hasCycle) {
            return null;
        }

        // Phase 2: Find cycle start
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }

        return slow;
    }

    /**
     * Helper method to create a linked list with a cycle.
     * The tail's next points to the node at the given position (0-indexed).
     * If pos is -1, no cycle is created.
     */
    public static ListNode createCyclicList(int[] values, int pos) {
        if (values == null || values.length == 0) {
            return null;
        }

        ListNode head = ListNode.fromArray(values);
        if (pos < 0) {
            return head;
        }

        // Find tail and the node at position pos
        ListNode tail = head;
        ListNode cycleNode = null;
        int index = 0;

        while (tail.next != null) {
            if (index == pos) {
                cycleNode = tail;
            }
            tail = tail.next;
            index++;
        }
        // Check last node
        if (index == pos) {
            cycleNode = tail;
        }

        // Create cycle
        if (cycleNode != null) {
            tail.next = cycleNode;
        }

        return head;
    }

    public static void main(String[] args) {
        // Test case 1: List with cycle
        ListNode head1 = createCyclicList(new int[]{3, 2, 0, -4}, 1);
        System.out.println("Test 1 - List [3,2,0,-4] with cycle at index 1:");
        System.out.println("Has cycle: " + hasCycle(head1));
        ListNode cycleStart1 = detectCycleStart(head1);
        System.out.println("Cycle starts at node with value: " + (cycleStart1 != null ? cycleStart1.val : "null"));
        System.out.println("Expected: hasCycle=true, cycleStart=2");
        System.out.println();

        // Test case 2: List with cycle at head
        ListNode head2 = createCyclicList(new int[]{1, 2}, 0);
        System.out.println("Test 2 - List [1,2] with cycle at index 0:");
        System.out.println("Has cycle: " + hasCycle(head2));
        ListNode cycleStart2 = detectCycleStart(head2);
        System.out.println("Cycle starts at node with value: " + (cycleStart2 != null ? cycleStart2.val : "null"));
        System.out.println("Expected: hasCycle=true, cycleStart=1");
        System.out.println();

        // Test case 3: List without cycle
        ListNode head3 = ListNode.fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.println("Test 3 - List [1,2,3,4,5] without cycle:");
        System.out.println("Has cycle: " + hasCycle(head3));
        ListNode cycleStart3 = detectCycleStart(head3);
        System.out.println("Cycle starts at node with value: " + (cycleStart3 != null ? cycleStart3.val : "null"));
        System.out.println("Expected: hasCycle=false, cycleStart=null");
        System.out.println();

        // Test case 4: Single node without cycle
        ListNode head4 = ListNode.fromArray(new int[]{1});
        System.out.println("Test 4 - Single node [1] without cycle:");
        System.out.println("Has cycle: " + hasCycle(head4));
        System.out.println("Expected: hasCycle=false");
        System.out.println();

        // Test case 5: Single node with cycle
        ListNode head5 = createCyclicList(new int[]{1}, 0);
        System.out.println("Test 5 - Single node [1] with cycle to itself:");
        System.out.println("Has cycle: " + hasCycle(head5));
        ListNode cycleStart5 = detectCycleStart(head5);
        System.out.println("Cycle starts at node with value: " + (cycleStart5 != null ? cycleStart5.val : "null"));
        System.out.println("Expected: hasCycle=true, cycleStart=1");
        System.out.println();

        // Test case 6: Empty list
        ListNode head6 = null;
        System.out.println("Test 6 - Empty list:");
        System.out.println("Has cycle: " + hasCycle(head6));
        System.out.println("Expected: hasCycle=false");
    }
}
