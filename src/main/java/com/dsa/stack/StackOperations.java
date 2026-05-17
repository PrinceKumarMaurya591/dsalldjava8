package com.dsa.stack;

import java.util.*;

/**
 * Stack Operations - Basics, Implementation, Real World Use Cases
 * 
 * Stack is a LIFO (Last In, First Out) data structure.
 * 
 * Real World Applications:
 * - Undo/Redo in text editors
 * - Browser back/forward navigation
 * - Call stack in programming languages
 * - Expression evaluation (infix/postfix/prefix)
 * - Syntax parsing (parentheses matching)
 * - Backtracking algorithms (DFS, maze solving)
 * - Memory management (stack vs heap)
 * 
 * Basic Operations:
 * - push(element): Add element to top - O(1)
 * - pop(): Remove and return top element - O(1)
 * - peek(): View top element without removing - O(1)
 * - isEmpty(): Check if stack is empty - O(1)
 * - size(): Get number of elements - O(1)
 * - search(element): Find element position - O(n)
 */
public class StackOperations {

    // =============================================
    // Array-based Stack Implementation
    // =============================================
    static class ArrayStack<T> {
        private static final int DEFAULT_CAPACITY = 10;
        private Object[] elements;
        private int top;
        private int capacity;

        public ArrayStack() {
            this.capacity = DEFAULT_CAPACITY;
            this.elements = new Object[capacity];
            this.top = -1;
        }

        public ArrayStack(int capacity) {
            this.capacity = capacity;
            this.elements = new Object[capacity];
            this.top = -1;
        }

        public void push(T item) {
            if (isFull()) {
                resize();
            }
            elements[++top] = item;
        }

        @SuppressWarnings("unchecked")
        public T pop() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            T item = (T) elements[top];
            elements[top--] = null; // Avoid memory leak
            return item;
        }

        @SuppressWarnings("unchecked")
        public T peek() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            return (T) elements[top];
        }

        public boolean isEmpty() {
            return top == -1;
        }

        public boolean isFull() {
            return top == capacity - 1;
        }

        public int size() {
            return top + 1;
        }

        private void resize() {
            capacity *= 2;
            elements = Arrays.copyOf(elements, capacity);
        }

        public void clear() {
            while (!isEmpty()) {
                pop();
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = top; i >= 0; i--) {
                sb.append(elements[i]);
                if (i > 0) sb.append(", ");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // =============================================
    // Linked List-based Stack Implementation
    // =============================================
    static class LinkedListStack<T> {
        private class Node {
            T data;
            Node next;

            Node(T data) {
                this.data = data;
            }
        }

        private Node top;
        private int size;

        public LinkedListStack() {
            this.top = null;
            this.size = 0;
        }

        public void push(T item) {
            Node newNode = new Node(item);
            newNode.next = top;
            top = newNode;
            size++;
        }

        public T pop() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            T item = top.data;
            top = top.next;
            size--;
            return item;
        }

        public T peek() {
            if (isEmpty()) {
                throw new EmptyStackException();
            }
            return top.data;
        }

        public boolean isEmpty() {
            return top == null;
        }

        public int size() {
            return size;
        }

        public void clear() {
            top = null;
            size = 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            Node current = top;
            while (current != null) {
                sb.append(current.data);
                if (current.next != null) sb.append(", ");
                current = current.next;
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // =============================================
    // Stack Use Cases
    // =============================================

    /**
     * Use Case 1: Undo/Redo functionality
     * Simulates text editor undo/redo operations
     */
    static class UndoRedoManager {
        private final LinkedListStack<String> undoStack = new LinkedListStack<>();
        private final LinkedListStack<String> redoStack = new LinkedListStack<>();

        public void performAction(String action) {
            undoStack.push(action);
            redoStack.clear(); // Clear redo on new action
            System.out.println("Performed: " + action);
        }

        public String undo() {
            if (undoStack.isEmpty()) {
                throw new EmptyStackException();
            }
            String action = undoStack.pop();
            redoStack.push(action);
            System.out.println("Undo: " + action);
            return action;
        }

        public String redo() {
            if (redoStack.isEmpty()) {
                throw new EmptyStackException();
            }
            String action = redoStack.pop();
            undoStack.push(action);
            System.out.println("Redo: " + action);
            return action;
        }
    }

    /**
     * Use Case 2: Browser Navigation (Back/Forward)
     */
    static class BrowserHistory {
        private final LinkedListStack<String> backStack = new LinkedListStack<>();
        private final LinkedListStack<String> forwardStack = new LinkedListStack<>();
        private String currentPage;

        public BrowserHistory(String homepage) {
            this.currentPage = homepage;
            System.out.println("Homepage: " + homepage);
        }

        public void visit(String url) {
            backStack.push(currentPage);
            currentPage = url;
            forwardStack.clear(); // Clear forward on new visit
            System.out.println("Visited: " + url);
        }

        public String back() {
            if (backStack.isEmpty()) {
                System.out.println("No previous page");
                return currentPage;
            }
            forwardStack.push(currentPage);
            currentPage = backStack.pop();
            System.out.println("Back to: " + currentPage);
            return currentPage;
        }

        public String forward() {
            if (forwardStack.isEmpty()) {
                System.out.println("No forward page");
                return currentPage;
            }
            backStack.push(currentPage);
            currentPage = forwardStack.pop();
            System.out.println("Forward to: " + currentPage);
            return currentPage;
        }
    }

    /**
     * Use Case 3: Balanced Parentheses Check
     */
    public static boolean isValidParentheses(String s) {
        LinkedListStack<Character> stack = new LinkedListStack<>();
        Map<Character, Character> map = Map.of(')', '(', '}', '{', ']', '[');

        for (char c : s.toCharArray()) {
            if (map.containsValue(c)) {
                stack.push(c);
            } else if (map.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != map.get(c)) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /**
     * Use Case 4: Infix to Postfix Conversion
     */
    public static String infixToPostfix(String expression) {
        StringBuilder result = new StringBuilder();
        LinkedListStack<Character> stack = new LinkedListStack<>();
        Map<Character, Integer> precedence = Map.of(
            '+', 1, '-', 1, '*', 2, '/', 2, '^', 3
        );

        for (char c : expression.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                result.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    result.append(stack.pop());
                }
                stack.pop(); // Remove '('
            } else { // Operator
                while (!stack.isEmpty() && stack.peek() != '(' &&
                       precedence.getOrDefault(stack.peek(), 0) >= precedence.getOrDefault(c, 0)) {
                    result.append(stack.pop());
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }

        return result.toString();
    }

    /**
     * Use Case 5: Evaluate Postfix Expression
     */
    public static int evaluatePostfix(String expression) {
        LinkedListStack<Integer> stack = new LinkedListStack<>();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c)) {
                stack.push(c - '0');
            } else {
                int b = stack.pop();
                int a = stack.pop();
                switch (c) {
                    case '+': stack.push(a + b); break;
                    case '-': stack.push(a - b); break;
                    case '*': stack.push(a * b); break;
                    case '/': stack.push(a / b); break;
                }
            }
        }
        return stack.pop();
    }

    /**
     * Use Case 6: Sort a Stack (using recursion)
     */
    public static void sortStack(LinkedListStack<Integer> stack) {
        if (!stack.isEmpty()) {
            int temp = stack.pop();
            sortStack(stack);
            sortedInsert(stack, temp);
        }
    }

    private static void sortedInsert(LinkedListStack<Integer> stack, int element) {
        if (stack.isEmpty() || element > stack.peek()) {
            stack.push(element);
        } else {
            int temp = stack.pop();
            sortedInsert(stack, element);
            stack.push(temp);
        }
    }

    /**
     * Use Case 7: Reverse a Stack (using recursion)
     */
    public static void reverseStack(LinkedListStack<Integer> stack) {
        if (!stack.isEmpty()) {
            int temp = stack.pop();
            reverseStack(stack);
            insertAtBottom(stack, temp);
        }
    }

    private static void insertAtBottom(LinkedListStack<Integer> stack, int element) {
        if (stack.isEmpty()) {
            stack.push(element);
        } else {
            int temp = stack.pop();
            insertAtBottom(stack, element);
            stack.push(temp);
        }
    }

    /**
     * Use Case 8: Next Greater Element
     */
    public static int[] nextGreaterElement(int[] nums) {
        int[] result = new int[nums.length];
        Arrays.fill(result, -1);
        LinkedListStack<Integer> stack = new LinkedListStack<>();

        for (int i = 0; i < nums.length; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }
        return result;
    }

    /**
     * Use Case 9: Stock Span Problem
     */
    public static int[] stockSpan(int[] prices) {
        int[] span = new int[prices.length];
        LinkedListStack<Integer> stack = new LinkedListStack<>();
        stack.push(0);
        span[0] = 1;

        for (int i = 1; i < prices.length; i++) {
            while (!stack.isEmpty() && prices[stack.peek()] <= prices[i]) {
                stack.pop();
            }
            span[i] = stack.isEmpty() ? i + 1 : i - stack.peek();
            stack.push(i);
        }
        return span;
    }

    /**
     * Use Case 10: Min Stack (O(1) getMin)
     */
    static class MinStack {
        private final LinkedListStack<Integer> stack = new LinkedListStack<>();
        private final LinkedListStack<Integer> minStack = new LinkedListStack<>();

        public void push(int val) {
            stack.push(val);
            if (minStack.isEmpty() || val <= minStack.peek()) {
                minStack.push(val);
            }
        }

        public void pop() {
            if (stack.pop().equals(minStack.peek())) {
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

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("STACK OPERATIONS DEMONSTRATION");
        System.out.println("=================================\n");

        // 1. Array-based Stack
        System.out.println("--- Array-based Stack ---");
        ArrayStack<Integer> arrayStack = new ArrayStack<>(3);
        arrayStack.push(10);
        arrayStack.push(20);
        arrayStack.push(30);
        System.out.println("Stack: " + arrayStack);
        System.out.println("Peek: " + arrayStack.peek());
        System.out.println("Pop: " + arrayStack.pop());
        System.out.println("After pop: " + arrayStack);
        System.out.println("Size: " + arrayStack.size());
        System.out.println("Is Empty: " + arrayStack.isEmpty());

        // Auto-resize
        arrayStack.push(40);
        arrayStack.push(50);
        System.out.println("After push 40, 50 (auto-resize): " + arrayStack);

        System.out.println();

        // 2. Linked List-based Stack
        System.out.println("--- Linked List-based Stack ---");
        LinkedListStack<String> linkedStack = new LinkedListStack<>();
        linkedStack.push("Apple");
        linkedStack.push("Banana");
        linkedStack.push("Cherry");
        System.out.println("Stack: " + linkedStack);
        System.out.println("Peek: " + linkedStack.peek());
        System.out.println("Pop: " + linkedStack.pop());
        System.out.println("After pop: " + linkedStack);
        System.out.println("Size: " + linkedStack.size());

        System.out.println();

        // 3. Undo/Redo Manager
        System.out.println("--- Undo/Redo Manager ---");
        UndoRedoManager editor = new UndoRedoManager();
        editor.performAction("Type 'Hello'");
        editor.performAction("Type ' World'");
        editor.performAction("Bold text");
        editor.undo();
        editor.undo();
        editor.redo();

        System.out.println();

        // 4. Browser History
        System.out.println("--- Browser History ---");
        BrowserHistory browser = new BrowserHistory("google.com");
        browser.visit("youtube.com");
        browser.visit("github.com");
        browser.visit("stackoverflow.com");
        browser.back();
        browser.back();
        browser.forward();
        browser.visit("reddit.com");

        System.out.println();

        // 5. Balanced Parentheses
        System.out.println("--- Balanced Parentheses ---");
        String[] tests = {"()", "()[]{}", "(]", "([)]", "{[]}"};
        for (String test : tests) {
            System.out.println(test + " -> " + isValidParentheses(test));
        }

        System.out.println();

        // 6. Infix to Postfix
        System.out.println("--- Infix to Postfix ---");
        String infix = "A+B*C-D/E";
        System.out.println("Infix: " + infix);
        System.out.println("Postfix: " + infixToPostfix(infix));

        System.out.println();

        // 7. Evaluate Postfix
        System.out.println("--- Evaluate Postfix ---");
        String postfix = "23*54*+9-";
        System.out.println("Postfix: " + postfix);
        System.out.println("Result: " + evaluatePostfix(postfix));

        System.out.println();

        // 8. Sort Stack
        System.out.println("--- Sort Stack ---");
        LinkedListStack<Integer> unsorted = new LinkedListStack<>();
        unsorted.push(34);
        unsorted.push(3);
        unsorted.push(31);
        unsorted.push(98);
        unsorted.push(92);
        unsorted.push(23);
        System.out.println("Original: " + unsorted);
        sortStack(unsorted);
        System.out.println("Sorted: " + unsorted);

        System.out.println();

        // 9. Reverse Stack
        System.out.println("--- Reverse Stack ---");
        LinkedListStack<Integer> toReverse = new LinkedListStack<>();
        toReverse.push(1);
        toReverse.push(2);
        toReverse.push(3);
        toReverse.push(4);
        toReverse.push(5);
        System.out.println("Original: " + toReverse);
        reverseStack(toReverse);
        System.out.println("Reversed: " + toReverse);

        System.out.println();

        // 10. Next Greater Element
        System.out.println("--- Next Greater Element ---");
        int[] nums = {4, 5, 2, 25, 7, 8};
        int[] nge = nextGreaterElement(nums);
        System.out.println("Array: " + Arrays.toString(nums));
        System.out.println("NGE:   " + Arrays.toString(nge));

        System.out.println();

        // 11. Stock Span
        System.out.println("--- Stock Span ---");
        int[] prices = {100, 80, 60, 70, 60, 75, 85};
        int[] span = stockSpan(prices);
        System.out.println("Prices: " + Arrays.toString(prices));
        System.out.println("Span:   " + Arrays.toString(span));

        System.out.println();

        // 12. Min Stack
        System.out.println("--- Min Stack ---");
        MinStack minStack = new MinStack();
        minStack.push(5);
        minStack.push(3);
        minStack.push(7);
        minStack.push(2);
        System.out.println("Min: " + minStack.getMin());
        minStack.pop();
        System.out.println("After pop, Min: " + minStack.getMin());
        minStack.pop();
        System.out.println("After pop, Min: " + minStack.getMin());
    }
}
