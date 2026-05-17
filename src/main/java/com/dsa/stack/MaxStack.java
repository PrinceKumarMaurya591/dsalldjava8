package com.dsa.stack;

import java.util.*;

/**
 * Max Stack
 * 
 * Design a max stack data structure that supports the stack operations and
 * supports finding the maximum element.
 * 
 * Implement the MaxStack class:
 * - MaxStack() initializes the stack object.
 * - void push(int x) pushes element x onto the stack.
 * - int pop() removes the element on top of the stack and returns it.
 * - int top() gets the element on the top of the stack without removing it.
 * - int peekMax() retrieves the maximum element in the stack without removing it.
 * - int popMax() retrieves and removes the maximum element in the stack.
 *   If there is more than one maximum element, remove the topmost one.
 * 
 * Approach 1: Two Stacks (push/pop O(1), popMax O(n))
 * - Main stack stores all elements
 * - Max stack stores maximum values seen so far
 * - popMax() requires temporary stack to pop until max found
 * 
 * Approach 2: Double Linked List + TreeMap (all O(log n))
 * - Double linked list maintains stack order
 * - TreeMap maps values to list of nodes for O(log n) max operations
 * 
 * Approach 3: Stack of Nodes (each node tracks max at that level)
 * - Each node stores value and current max
 * - popMax() still requires O(n) to find and remove
 * 
 * Time Complexity:
 * - push: O(1) for Approach 1, O(log n) for Approach 2
 * - pop: O(1) for Approach 1, O(log n) for Approach 2
 * - top: O(1)
 * - peekMax: O(1) for Approach 1, O(log n) for Approach 2
 * - popMax: O(n) for Approach 1, O(log n) for Approach 2
 */
public class MaxStack {

    // =============================================
    // Approach 1: Two Stacks (Simple)
    // =============================================
    static class MaxStackTwoStacks {
        private final Deque<Integer> stack;
        private final Deque<Integer> maxStack;

        public MaxStackTwoStacks() {
            stack = new ArrayDeque<>();
            maxStack = new ArrayDeque<>();
        }

        public void push(int x) {
            stack.push(x);
            if (maxStack.isEmpty() || x >= maxStack.peek()) {
                maxStack.push(x);
            }
        }

        public int pop() {
            if (stack.isEmpty()) {
                throw new EmptyStackException();
            }
            int val = stack.pop();
            if (val == maxStack.peek()) {
                maxStack.pop();
            }
            return val;
        }

        public int top() {
            return stack.peek();
        }

        public int peekMax() {
            return maxStack.peek();
        }

        public int popMax() {
            if (stack.isEmpty()) {
                throw new EmptyStackException();
            }
            int max = maxStack.pop();
            Deque<Integer> temp = new ArrayDeque<>();

            // Pop from stack until we find the max
            while (!stack.isEmpty() && stack.peek() != max) {
                temp.push(stack.pop());
            }
            stack.pop(); // Remove the max

            // Restore elements back to stack
            while (!temp.isEmpty()) {
                int val = temp.pop();
                stack.push(val);
                // Update maxStack
                if (maxStack.isEmpty() || val >= maxStack.peek()) {
                    maxStack.push(val);
                }
            }

            return max;
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public int size() {
            return stack.size();
        }
    }

    // =============================================
    // Approach 2: Double Linked List + TreeMap
    // =============================================
    static class MaxStackLinkedList {
        private class Node {
            int val;
            Node prev;
            Node next;

            Node(int val) {
                this.val = val;
            }
        }

        private final Node head; // Dummy head
        private final Node tail; // Dummy tail
        private final TreeMap<Integer, List<Node>> map;

        public MaxStackLinkedList() {
            head = new Node(0);
            tail = new Node(0);
            head.next = tail;
            tail.prev = head;
            map = new TreeMap<>();
        }

        public void push(int x) {
            Node node = new Node(x);
            
            // Add to end of linked list (top of stack)
            Node last = tail.prev;
            last.next = node;
            node.prev = last;
            node.next = tail;
            tail.prev = node;

            // Add to TreeMap
            map.computeIfAbsent(x, k -> new ArrayList<>()).add(node);
        }

        public int pop() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }

            Node node = tail.prev;
            remove(node);
            
            List<Node> nodes = map.get(node.val);
            nodes.remove(nodes.size() - 1);
            if (nodes.isEmpty()) {
                map.remove(node.val);
            }

            return node.val;
        }

        public int top() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            return tail.prev.val;
        }

        public int peekMax() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            return map.lastKey();
        }

        public int popMax() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }

            int max = map.lastKey();
            List<Node> nodes = map.get(max);
            Node node = nodes.remove(nodes.size() - 1);
            if (nodes.isEmpty()) {
                map.remove(max);
            }

            remove(node);
            return max;
        }

        private void remove(Node node) {
            Node prev = node.prev;
            Node next = node.next;
            prev.next = next;
            next.prev = prev;
        }

        public boolean isEmpty() {
            return head.next == tail;
        }
    }

    // =============================================
    // Approach 3: Stack with Max Tracking per Node
    // =============================================
    static class MaxStackNode {
        private class Node {
            int val;
            int max;
            Node next;

            Node(int val, int max) {
                this.val = val;
                this.max = max;
            }
        }

        private Node top;

        public MaxStackNode() {
            top = null;
        }

        public void push(int x) {
            if (top == null) {
                top = new Node(x, x);
            } else {
                Node newNode = new Node(x, Math.max(x, top.max));
                newNode.next = top;
                top = newNode;
            }
        }

        public int pop() {
            if (top == null) {
                throw new EmptyStackException();
            }
            int val = top.val;
            top = top.next;
            return val;
        }

        public int top() {
            return top.val;
        }

        public int peekMax() {
            return top.max;
        }

        public int popMax() {
            if (top == null) {
                throw new EmptyStackException();
            }

            int max = top.max;
            Deque<Integer> temp = new ArrayDeque<>();

            while (top != null && top.val != max) {
                temp.push(pop());
            }
            
            if (top != null) {
                pop(); // Remove the max node
            }

            // Restore elements
            while (!temp.isEmpty()) {
                push(temp.pop());
            }

            return max;
        }

        public boolean isEmpty() {
            return top == null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Max Stack - All Approaches\n");

        // Test Two Stacks approach
        System.out.println("--- Two Stacks Approach ---");
        MaxStackTwoStacks maxStack1 = new MaxStackTwoStacks();
        maxStack1.push(5);
        maxStack1.push(1);
        maxStack1.push(5);
        System.out.println("After push(5), push(1), push(5)");
        System.out.println("top(): " + maxStack1.top() + " (expected: 5)");
        System.out.println("peekMax(): " + maxStack1.peekMax() + " (expected: 5)");
        System.out.println("popMax(): " + maxStack1.popMax() + " (expected: 5)");
        System.out.println("top(): " + maxStack1.top() + " (expected: 1)");
        System.out.println("peekMax(): " + maxStack1.peekMax() + " (expected: 5)");
        System.out.println("pop(): " + maxStack1.pop() + " (expected: 1)");
        System.out.println("top(): " + maxStack1.top() + " (expected: 5)");

        System.out.println();

        // Test Linked List approach
        System.out.println("--- Linked List + TreeMap Approach ---");
        MaxStackLinkedList maxStack2 = new MaxStackLinkedList();
        maxStack2.push(5);
        maxStack2.push(1);
        maxStack2.push(5);
        System.out.println("After push(5), push(1), push(5)");
        System.out.println("top(): " + maxStack2.top() + " (expected: 5)");
        System.out.println("peekMax(): " + maxStack2.peekMax() + " (expected: 5)");
        System.out.println("popMax(): " + maxStack2.popMax() + " (expected: 5)");
        System.out.println("top(): " + maxStack2.top() + " (expected: 1)");
        System.out.println("peekMax(): " + maxStack2.peekMax() + " (expected: 5)");
        System.out.println("pop(): " + maxStack2.pop() + " (expected: 1)");
        System.out.println("top(): " + maxStack2.top() + " (expected: 5)");

        System.out.println();

        // Test Node approach
        System.out.println("--- Node-based Approach ---");
        MaxStackNode maxStack3 = new MaxStackNode();
        maxStack3.push(5);
        maxStack3.push(1);
        maxStack3.push(5);
        System.out.println("After push(5), push(1), push(5)");
        System.out.println("top(): " + maxStack3.top() + " (expected: 5)");
        System.out.println("peekMax(): " + maxStack3.peekMax() + " (expected: 5)");
        System.out.println("popMax(): " + maxStack3.popMax() + " (expected: 5)");
        System.out.println("top(): " + maxStack3.top() + " (expected: 1)");
        System.out.println("peekMax(): " + maxStack3.peekMax() + " (expected: 5)");
        System.out.println("pop(): " + maxStack3.pop() + " (expected: 1)");
        System.out.println("top(): " + maxStack3.top() + " (expected: 5)");

        System.out.println();

        // Comprehensive test
        System.out.println("--- Comprehensive Test ---");
        MaxStackTwoStacks ms = new MaxStackTwoStacks();
        int[][] operations = {
            {0, 5},    // push(5)
            {0, 3},    // push(3)
            {0, 8},    // push(8)
            {0, 1},    // push(1)
            {0, 6},    // push(6)
            {4, 0},    // peekMax -> 8
            {5, 0},    // popMax -> 8
            {4, 0},    // peekMax -> 6
            {1, 0},    // pop -> 6
            {2, 0},    // top -> 1
            {4, 0},    // peekMax -> 5
            {5, 0},    // popMax -> 5
            {1, 0},    // pop -> 1
            {1, 0},    // pop -> 3
        };
        // 0=push, 1=pop, 2=top, 4=peekMax, 5=popMax

        System.out.println("Operations:");
        for (int[] op : operations) {
            switch (op[0]) {
                case 0 -> {
                    ms.push(op[1]);
                    System.out.println("  push(" + op[1] + ")");
                }
                case 1 -> System.out.println("  pop() -> " + ms.pop());
                case 2 -> System.out.println("  top() -> " + ms.top());
                case 4 -> System.out.println("  peekMax() -> " + ms.peekMax());
                case 5 -> System.out.println("  popMax() -> " + ms.popMax());
            }
        }

        System.out.println();

        // Test with duplicate max values
        System.out.println("--- Duplicate Max Values ---");
        MaxStackTwoStacks dupStack = new MaxStackTwoStacks();
        dupStack.push(5);
        dupStack.push(5);
        dupStack.push(5);
        System.out.println("After push(5), push(5), push(5)");
        System.out.println("popMax(): " + dupStack.popMax() + " (expected: 5)");
        System.out.println("popMax(): " + dupStack.popMax() + " (expected: 5)");
        System.out.println("peekMax(): " + dupStack.peekMax() + " (expected: 5)");
        System.out.println("pop(): " + dupStack.pop() + " (expected: 5)");
        System.out.println("isEmpty(): " + dupStack.isEmpty() + " (expected: true)");
    }
}
