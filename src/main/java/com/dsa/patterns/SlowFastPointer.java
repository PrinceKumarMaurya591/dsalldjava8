package com.dsa.patterns;

/**
 * Pattern: Slow and Fast Pointer (Tortoise and Hare)
 * 
 * Used when: Problems involving cycle detection in linked lists/arrays,
 * finding middle element, finding duplicate numbers, detecting palindromes.
 * 
 * Key variations:
 * 1. Cycle detection (Floyd's algorithm)
 * 2. Find middle of linked list
 * 3. Find start of cycle
 * 4. Happy number detection
 * 5. Find duplicate number
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class SlowFastPointer {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /**
     * Problem: Linked List Cycle
     * Determine if a linked list has a cycle.
     * 
     * Approach: Floyd's Cycle Detection
     * Slow moves 1 step, fast moves 2 steps. If they meet, there's a cycle.
     * Time: O(n), Space: O(1)
     */
    public static boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) return false;

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }

        return false;
    }

    /**
     * Problem: Linked List Cycle II
     * Return the node where the cycle begins.
     * 
     * Approach: After detecting cycle, reset slow to head.
     * Move both one step at a time - they meet at cycle start.
     * Time: O(n), Space: O(1)
     */
    public static ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) return null;

        ListNode slow = head;
        ListNode fast = head;
        boolean hasCycle = false;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                hasCycle = true;
                break;
            }
        }

        if (!hasCycle) return null;

        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }

        return slow;
    }

    /**
     * Problem: Middle of the Linked List
     * Return the middle node. For even length, return the second middle.
     * 
     * Approach: Slow moves 1 step, fast moves 2 steps.
     * When fast reaches end, slow is at middle.
     * Time: O(n), Space: O(1)
     */
    public static ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    /**
     * Problem: Find the Duplicate Number
     * Find the duplicate in array of size n+1 with values in [1, n].
     * 
     * Approach: Treat array as linked list (index -> value), use Floyd's algorithm.
     * Time: O(n), Space: O(1)
     */
    public static int findDuplicate(int[] nums) {
        // Phase 1: Find intersection
        int slow = nums[0];
        int fast = nums[0];

        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Phase 2: Find cycle entrance (duplicate)
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }

        return slow;
    }

    /**
     * Problem: Happy Number
     * A number is happy if repeatedly replacing it with sum of squares of its digits
     * eventually reaches 1.
     * 
     * Approach: Treat as cycle detection. If we reach 1, it's happy.
     * If we detect a cycle (not containing 1), it's unhappy.
     * Time: O(log n), Space: O(1)
     */
    public static boolean isHappy(int n) {
        int slow = n;
        int fast = n;

        do {
            slow = sumOfSquares(slow);
            fast = sumOfSquares(sumOfSquares(fast));
        } while (slow != fast);

        return slow == 1;
    }

    private static int sumOfSquares(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }

    /**
     * Problem: Palindrome Linked List
     * Check if a linked list is a palindrome.
     * 
     * Approach: Find middle, reverse second half, compare.
     * Time: O(n), Space: O(1)
     */
    public static boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) return true;

        // Find middle
        ListNode slow = head;
        ListNode fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Reverse second half
        ListNode secondHalf = reverse(slow);
        ListNode firstHalf = head;

        // Compare
        while (secondHalf != null) {
            if (firstHalf.val != secondHalf.val) return false;
            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }

        return true;
    }

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

    /**
     * Problem: Remove Nth Node From End of List
     * 
     * Approach: Move fast n steps ahead, then move both until fast reaches end.
     * Slow will be at the node before the one to remove.
     * Time: O(n), Space: O(1)
     */
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode slow = dummy;
        ListNode fast = dummy;

        // Move fast n+1 steps ahead
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        // Move both until fast reaches end
        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }

        // Remove nth node
        slow.next = slow.next.next;

        return dummy.next;
    }

    // Helper to create linked list from array
    private static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        for (int val : arr) {
            current.next = new ListNode(val);
            current = current.next;
        }
        return dummy.next;
    }

    // Helper to create linked list with cycle
    private static ListNode createCyclicList(int[] arr, int pos) {
        ListNode head = fromArray(arr);
        if (pos < 0) return head;

        ListNode tail = head;
        ListNode cycleNode = null;
        int index = 0;

        while (tail.next != null) {
            if (index == pos) cycleNode = tail;
            tail = tail.next;
            index++;
        }
        if (index == pos) cycleNode = tail;
        if (cycleNode != null) tail.next = cycleNode;

        return head;
    }

    // Helper to print list
    private static void printList(ListNode head) {
        ListNode curr = head;
        while (curr != null) {
            System.out.print(curr.val);
            if (curr.next != null) System.out.print(" -> ");
            curr = curr.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("=== SLOW & FAST POINTER PATTERN ===");
        System.out.println();

        // 1. Linked List Cycle
        System.out.println("1. Linked List Cycle:");
        ListNode head1 = createCyclicList(new int[]{3, 2, 0, -4}, 1);
        System.out.println("   List [3,2,0,-4] with cycle at index 1: " + hasCycle(head1) + " (expected: true)");
        ListNode head1b = fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.println("   List [1,2,3,4,5] without cycle: " + hasCycle(head1b) + " (expected: false)");
        System.out.println();

        // 2. Detect Cycle Start
        System.out.println("2. Detect Cycle Start:");
        ListNode head2 = createCyclicList(new int[]{3, 2, 0, -4}, 1);
        ListNode cycleStart = detectCycle(head2);
        System.out.println("   Cycle starts at node with value: " + (cycleStart != null ? cycleStart.val : "null") + " (expected: 2)");
        System.out.println();

        // 3. Middle of Linked List
        System.out.println("3. Middle of Linked List:");
        ListNode head3 = fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.println("   List [1,2,3,4,5] middle: " + middleNode(head3).val + " (expected: 3)");
        ListNode head3b = fromArray(new int[]{1, 2, 3, 4, 5, 6});
        System.out.println("   List [1,2,3,4,5,6] middle: " + middleNode(head3b).val + " (expected: 4)");
        System.out.println();

        // 4. Find Duplicate Number
        System.out.println("4. Find Duplicate Number:");
        int[] nums4 = {1, 3, 4, 2, 2};
        System.out.println("   Input: [1,3,4,2,2] -> " + findDuplicate(nums4) + " (expected: 2)");
        int[] nums4b = {3, 1, 3, 4, 2};
        System.out.println("   Input: [3,1,3,4,2] -> " + findDuplicate(nums4b) + " (expected: 3)");
        System.out.println();

        // 5. Happy Number
        System.out.println("5. Happy Number:");
        System.out.println("   19 is happy: " + isHappy(19) + " (expected: true)");
        System.out.println("   2 is happy: " + isHappy(2) + " (expected: false)");
        System.out.println();

        // 6. Palindrome Linked List
        System.out.println("6. Palindrome Linked List:");
        ListNode head6 = fromArray(new int[]{1, 2, 3, 2, 1});
        System.out.print("   List: ");
        printList(head6);
        System.out.println("   Is palindrome: " + isPalindrome(head6) + " (expected: true)");
        ListNode head6b = fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("   List: ");
        printList(head6b);
        System.out.println("   Is palindrome: " + isPalindrome(head6b) + " (expected: false)");
        System.out.println();

        // 7. Remove Nth Node From End
        System.out.println("7. Remove Nth Node From End:");
        ListNode head7 = fromArray(new int[]{1, 2, 3, 4, 5});
        System.out.print("   Original: ");
        printList(head7);
        ListNode result7 = removeNthFromEnd(head7, 2);
        System.out.print("   After removing 2nd from end: ");
        printList(result7);
        System.out.println("   Expected: 1 -> 2 -> 3 -> 5");
    }
}
