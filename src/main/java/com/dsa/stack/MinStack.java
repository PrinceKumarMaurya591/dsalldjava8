package com.dsa.stack;

import java.util.*;

/**
 * Min Stack
 * 
 * Design a stack that supports push, pop, top, and retrieving the minimum
 * element in constant time.
 * 
 * Implement the MinStack class:
 * - MinStack() initializes the stack object.
 * - void push(int val) pushes the element val onto the stack.
 * - void pop() removes the element on the top of the stack.
 * - int top() gets the top element of the stack.
 * - int getMin() retrieves the minimum element in the stack.
 * 
 * Approach 1: Two Stacks
 * - Main stack stores all elements
 * - Min stack stores minimum values seen so far
 * - When pushing, if val <= minStack top, push to minStack
 * - When popping, if popped value equals minStack top, pop from minStack
 * 
 * Approach 2: Single Stack with Min Tracking
 * - Store the minimum value seen so far
 * - When pushing a value <= current min, push the old min first, then update
 * - When popping, if popped value equals current min, pop again to get old min
 * 
 * Approach 3: Using a Node that stores min at each level
 * - Each node stores its value and the minimum value at that point
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 */
public class MinStack {

    // =============================================
    // Approach 1: Two Stacks
    // =============================================
    static class MinStackTwoStacks {
        private final Deque<Integer> stack;
        private final Deque<Integer> minStack;

        public MinStackTwoStacks() {
            stack = new ArrayDeque<>();
            minStack = new ArrayDeque<>();
        }

        public void push(int val) {
            stack.push(val);
            // Push to minStack if it's empty or val is new minimum
            if (minStack.isEmpty() || val <= minStack.peek()) {
                minStack.push(val);
            }
        }

        public void pop() {
            if (stack.isEmpty()) return;
            int val = stack.pop();
            // If popped value is current minimum, pop from minStack too
            if (val == minStack.peek()) {
                minStack.pop();
            }
        }

        public int top() {
            return stack.peek();
        }

        public int getMin() {
            return minStack.peek();
        }
    }

    // =============================================
    // Approach 2: Single Stack with Min Tracking
    // =============================================
    static class MinStackSingleStack {
        private final Deque<Integer> stack;
        private int min;

        public MinStackSingleStack() {
            stack = new ArrayDeque<>();
            min = Integer.MAX_VALUE;
        }

        public void push(int val) {
            if (val <= min) {
                // Push old min first, then update min
                stack.push(min);
                min = val;
            }
            stack.push(val);
        }

        public void pop() {
            if (stack.isEmpty()) return;
            int val = stack.pop();
            // If popped value is current min, pop again to get old min
            if (val == min) {
                min = stack.pop();
            }
        }

        public int top() {
            return stack.peek();
        }

        public int getMin() {
            return min;
        }
    }

    // =============================================
    // Approach 3: Node-based Stack
    // =============================================
    static class MinStackNode {
        private class Node {
            int val;
            int min;
            Node next;

            Node(int val, int min) {
                this.val = val;
                this.min = min;
            }
        }

        private Node head;

        public MinStackNode() {
            head = null;
        }

        public void push(int val) {
            if (head == null) {
                head = new Node(val, val);
            } else {
                Node newNode = new Node(val, Math.min(val, head.min));
                newNode.next = head;
                head = newNode;
            }
        }

        public void pop() {
            if (head != null) {
                head = head.next;
            }
        }

        public int top() {
            return head.val;
        }

        public int getMin() {
            return head.min;
        }
    }

    // =============================================
    // Approach 4: Using Difference Encoding
    // =============================================
    static class MinStackDiff {
        private final Deque<Long> stack;
        private long min;

        public MinStackDiff() {
            stack = new ArrayDeque<>();
        }

        public void push(int val) {
            if (stack.isEmpty()) {
                stack.push(0L);
                min = val;
            } else {
                // Store difference between val and current min
                stack.push((long) val - min);
                if (val < min) {
                    min = val;
                }
            }
        }

        public void pop() {
            if (stack.isEmpty()) return;
            long diff = stack.pop();
            if (diff < 0) {
                // If diff is negative, min needs to be restored
                min = min - diff;
            }
        }

        public int top() {
            long diff = stack.peek();
            if (diff < 0) {
                return (int) min;
            }
            return (int) (min + diff);
        }

        public int getMin() {
            return (int) min;
        }
    }

    public static void main(String[] args) {
        System.out.println("Min Stack - All Approaches\n");

        // Test Two Stacks approach
        System.out.println("--- Two Stacks Approach ---");
        MinStackTwoStacks minStack1 = new MinStackTwoStacks();
        minStack1.push(-2);
        minStack1.push(0);
        minStack1.push(-3);
        System.out.println("getMin(): " + minStack1.getMin() + " (expected: -3)");
        minStack1.pop();
        System.out.println("top(): " + minStack1.top() + " (expected: 0)");
        System.out.println("getMin(): " + minStack1.getMin() + " (expected: -2)");

        System.out.println();

        // Test Single Stack approach
        System.out.println("--- Single Stack Approach ---");
        MinStackSingleStack minStack2 = new MinStackSingleStack();
        minStack2.push(5);
        minStack2.push(3);
        minStack2.push(7);
        minStack2.push(2);
        System.out.println("getMin(): " + minStack2.getMin() + " (expected: 2)");
        minStack2.pop();
        System.out.println("getMin(): " + minStack2.getMin() + " (expected: 3)");
        minStack2.pop();
        System.out.println("top(): " + minStack2.top() + " (expected: 3)");
        System.out.println("getMin(): " + minStack2.getMin() + " (expected: 3)");

        System.out.println();

        // Test Node-based approach
        System.out.println("--- Node-based Approach ---");
        MinStackNode minStack3 = new MinStackNode();
        minStack3.push(10);
        minStack3.push(5);
        minStack3.push(15);
        minStack3.push(3);
        System.out.println("getMin(): " + minStack3.getMin() + " (expected: 3)");
        minStack3.pop();
        System.out.println("getMin(): " + minStack3.getMin() + " (expected: 5)");
        minStack3.pop();
        System.out.println("top(): " + minStack3.top() + " (expected: 5)");

        System.out.println();

        // Test Difference Encoding approach
        System.out.println("--- Difference Encoding Approach ---");
        MinStackDiff minStack4 = new MinStackDiff();
        minStack4.push(2);
        minStack4.push(0);
        minStack4.push(3);
        minStack4.push(0);
        System.out.println("getMin(): " + minStack4.getMin() + " (expected: 0)");
        minStack4.pop();
        System.out.println("getMin(): " + minStack4.getMin() + " (expected: 0)");
        minStack4.pop();
        System.out.println("getMin(): " + minStack4.getMin() + " (expected: 0)");
        minStack4.pop();
        System.out.println("getMin(): " + minStack4.getMin() + " (expected: 2)");

        System.out.println();

        // Comprehensive test
        System.out.println("--- Comprehensive Test ---");
        MinStackTwoStacks ms = new MinStackTwoStacks();
        int[][] operations = {
            {0, -10},  // push -10
            {0, 5},    // push 5
            {0, -5},   // push -5
            {1, 0},    // pop
            {2, 0},    // top
            {3, 0},    // getMin
            {0, -15},  // push -15
            {3, 0},    // getMin
            {1, 0},    // pop
            {3, 0},    // getMin
        };
        // 0=push, 1=pop, 2=top, 3=getMin

        for (int[] op : operations) {
            switch (op[0]) {
                case 0 -> {
                    ms.push(op[1]);
                    System.out.println("push(" + op[1] + ")");
                }
                case 1 -> {
                    ms.pop();
                    System.out.println("pop()");
                }
                case 2 -> System.out.println("top() -> " + ms.top());
                case 3 -> System.out.println("getMin() -> " + ms.getMin());
            }
        }
    }
}
