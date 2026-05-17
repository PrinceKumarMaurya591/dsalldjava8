package com.dsa.stack;

import java.util.*;

/**
 * Evaluate Reverse Polish Notation
 * 
 * Evaluate the value of an arithmetic expression in Reverse Polish Notation.
 * Valid operators are +, -, *, and /. Each operand may be an integer or
 * another expression.
 * 
 * Note:
 * - Division between two integers should truncate toward zero.
 * - The RPN expression is always valid.
 * 
 * Approach: Stack-based evaluation
 * - Iterate through tokens
 * - If token is a number, push to stack
 * - If token is an operator, pop two operands, apply operation, push result
 * 
 * Time Complexity: O(n) - single pass through tokens
 * Space Complexity: O(n) - for the stack
 */
public class EvaluateReversePolishNotation {

    /**
     * Evaluate RPN expression
     */
    public static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String token : tokens) {
            switch (token) {
                case "+" -> {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a + b);
                }
                case "-" -> {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a - b);
                }
                case "*" -> {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a * b);
                }
                case "/" -> {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a / b); // Integer division truncates toward zero
                }
                default -> stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }

    /**
     * Alternative: Using if-else and helper method
     */
    public static int evalRPNIfElse(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                int b = stack.pop();
                int a = stack.pop();
                stack.push(applyOperation(a, b, token));
            } else {
                stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || 
               token.equals("*") || token.equals("/");
    }

    private static int applyOperation(int a, int b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> throw new IllegalArgumentException("Invalid operator: " + op);
        };
    }

    /**
     * Alternative: Using array as stack for better performance
     */
    public static int evalRPNArray(String[] tokens) {
        int[] stack = new int[tokens.length];
        int top = -1;

        for (String token : tokens) {
            switch (token) {
                case "+" -> {
                    int b = stack[top--];
                    int a = stack[top--];
                    stack[++top] = a + b;
                }
                case "-" -> {
                    int b = stack[top--];
                    int a = stack[top--];
                    stack[++top] = a - b;
                }
                case "*" -> {
                    int b = stack[top--];
                    int a = stack[top--];
                    stack[++top] = a * b;
                }
                case "/" -> {
                    int b = stack[top--];
                    int a = stack[top--];
                    stack[++top] = a / b;
                }
                default -> stack[++top] = Integer.parseInt(token);
            }
        }

        return stack[top];
    }

    public static void main(String[] args) {
        System.out.println("Evaluate Reverse Polish Notation\n");

        // Test Case 1: Basic arithmetic
        String[] tokens1 = {"2", "1", "+", "3", "*"};
        System.out.println("Tokens: " + Arrays.toString(tokens1));
        System.out.println("Result: " + evalRPN(tokens1));
        System.out.println("Expected: 9 ((2 + 1) * 3)\n");

        // Test Case 2: Division
        String[] tokens2 = {"4", "13", "5", "/", "+"};
        System.out.println("Tokens: " + Arrays.toString(tokens2));
        System.out.println("Result: " + evalRPN(tokens2));
        System.out.println("Expected: 6 (4 + (13 / 5))\n");

        // Test Case 3: Complex expression
        String[] tokens3 = {"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"};
        System.out.println("Tokens: " + Arrays.toString(tokens3));
        System.out.println("Result: " + evalRPN(tokens3));
        System.out.println("Expected: 22\n");

        // Test Case 4: Single number
        String[] tokens4 = {"42"};
        System.out.println("Tokens: " + Arrays.toString(tokens4));
        System.out.println("Result: " + evalRPN(tokens4));
        System.out.println("Expected: 42\n");

        // Test Case 5: Negative numbers
        String[] tokens5 = {"-2", "3", "*"};
        System.out.println("Tokens: " + Arrays.toString(tokens5));
        System.out.println("Result: " + evalRPN(tokens5));
        System.out.println("Expected: -6\n");

        // Test Case 6: Division truncating toward zero
        String[] tokens6 = {"7", "-3", "/"};
        System.out.println("Tokens: " + Arrays.toString(tokens6));
        System.out.println("Result: " + evalRPN(tokens6));
        System.out.println("Expected: -2 (7 / -3 = -2.333... truncates to -2)\n");

        // Test Case 7: All operators
        String[] tokens7 = {"5", "3", "+", "8", "2", "-", "*", "4", "/"};
        System.out.println("Tokens: " + Arrays.toString(tokens7));
        System.out.println("Result: " + evalRPN(tokens7));
        System.out.println("Expected: 12 (((5+3)*(8-2))/4 = 48/4 = 12)\n");

        // Performance comparison - use a simple valid RPN expression repeated
        System.out.println("--- Performance Comparison ---");
        // Build a valid RPN: "1 2 + 3 + 4 + 5 + ..." - always valid
        // Pattern: n numbers followed by (n-1) operators
        int n = 50001; // numbers
        String[] largeTokens = new String[n + (n - 1)]; // 100001 tokens
        for (int i = 0; i < n; i++) {
            largeTokens[i] = String.valueOf(1 + (i % 999));
        }
        for (int i = n; i < largeTokens.length; i++) {
            largeTokens[i] = "+";
        }

        long start = System.nanoTime();
        evalRPN(largeTokens);
        long dequeTime = System.nanoTime() - start;

        start = System.nanoTime();
        evalRPNArray(largeTokens);
        long arrayTime = System.nanoTime() - start;

        System.out.println("Deque Stack: " + dequeTime / 1_000_000.0 + " ms");
        System.out.println("Array Stack: " + arrayTime / 1_000_000.0 + " ms");
    }
}
