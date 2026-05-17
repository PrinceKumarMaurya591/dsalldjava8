package com.dsa.stack;

import java.util.*;

/**
 * Valid Parentheses
 * 
 * Given a string s containing just the characters '(', ')', '{', '}', '[' and ']',
 * determine if the input string is valid.
 * 
 * A string is valid if:
 * 1. Open brackets must be closed by the same type of brackets.
 * 2. Open brackets must be closed in the correct order.
 * 3. Every close bracket has a corresponding open bracket of the same type.
 * 
 * Approach: Stack-based
 * - Use a stack to track opening brackets
 * - When we see a closing bracket, check if it matches the top of stack
 * - If stack is empty or doesn't match, return false
 * - At the end, stack should be empty
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class ValidParentheses {

    // =============================================
    // Approach 1: Stack with HashMap
    // =============================================
    public static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> matching = new HashMap<>();
        matching.put(')', '(');
        matching.put('}', '{');
        matching.put(']', '[');

        for (char c : s.toCharArray()) {
            if (matching.containsKey(c)) {
                // Closing bracket
                char top = stack.isEmpty() ? '#' : stack.pop();
                if (top != matching.get(c)) {
                    return false;
                }
            } else {
                // Opening bracket
                stack.push(c);
            }
        }

        return stack.isEmpty();
    }

    // =============================================
    // Approach 2: Stack with switch-case
    // =============================================
    public static boolean isValidSwitch(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            switch (c) {
                case '(':
                case '{':
                case '[':
                    stack.push(c);
                    break;
                case ')':
                    if (stack.isEmpty() || stack.pop() != '(') return false;
                    break;
                case '}':
                    if (stack.isEmpty() || stack.pop() != '{') return false;
                    break;
                case ']':
                    if (stack.isEmpty() || stack.pop() != '[') return false;
                    break;
            }
        }

        return stack.isEmpty();
    }

    // =============================================
    // Approach 3: Push expected closing bracket
    // =============================================
    public static boolean isValidPushExpected(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(')');
            } else if (c == '{') {
                stack.push('}');
            } else if (c == '[') {
                stack.push(']');
            } else {
                // Closing bracket - should match expected
                if (stack.isEmpty() || stack.pop() != c) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    // =============================================
    // Approach 4: Replace pairs (not efficient, just for fun)
    // =============================================
    public static boolean isValidReplace(String s) {
        while (s.contains("()") || s.contains("{}") || s.contains("[]")) {
            s = s.replace("()", "");
            s = s.replace("{}", "");
            s = s.replace("[]", "");
        }
        return s.isEmpty();
    }

    /**
     * Extension: Find longest valid parentheses substring length
     */
    public static int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1);
        int maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }

        return maxLen;
    }

    /**
     * Extension: Return the positions of invalid parentheses
     */
    public static List<Integer> findInvalidPositions(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        Set<Integer> invalid = new HashSet<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '{' || c == '[') {
                stack.push(i);
            } else if (c == ')' || c == '}' || c == ']') {
                if (stack.isEmpty()) {
                    invalid.add(i);
                } else {
                    int openPos = stack.pop();
                    char open = s.charAt(openPos);
                    if ((c == ')' && open != '(') ||
                        (c == '}' && open != '{') ||
                        (c == ']' && open != '[')) {
                        invalid.add(openPos);
                        invalid.add(i);
                    }
                }
            }
        }

        invalid.addAll(stack);
        List<Integer> sorted = new ArrayList<>(invalid);
        Collections.sort(sorted);
        return sorted;
    }

    public static void main(String[] args) {
        System.out.println("Valid Parentheses\n");

        // Test cases
        String[][] testCases = {
            {"()", "true"},
            {"()[]{}", "true"},
            {"(]", "false"},
            {"([)]", "false"},
            {"{[]}", "true"},
            {"", "true"},
            {"(", "false"},
            {"]", "false"},
            {"((()))", "true"},
            {"(()", "false"},
            {")(", "false"},
        };

        System.out.println("--- Basic Tests ---");
        for (String[] test : testCases) {
            String input = test[0];
            String expected = test[1];
            boolean r1 = isValid(input);
            boolean r2 = isValidSwitch(input);
            boolean r3 = isValidPushExpected(input);
            boolean r4 = isValidReplace(input);
            
            System.out.println("Input: \"" + input + "\"");
            System.out.println("  HashMap:  " + r1 + " (expected: " + expected + ")");
            System.out.println("  Switch:   " + r2 + " (expected: " + expected + ")");
            System.out.println("  Expected: " + r3 + " (expected: " + expected + ")");
            System.out.println("  Replace:  " + r4 + " (expected: " + expected + ")");
            System.out.println();
        }

        // Performance comparison
        System.out.println("--- Performance Comparison ---");
        String largeInput = "(" + "()".repeat(5000) + ")";
        
        long start = System.nanoTime();
        isValid(largeInput);
        System.out.println("HashMap:  " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        isValidSwitch(largeInput);
        System.out.println("Switch:   " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        isValidPushExpected(largeInput);
        System.out.println("Expected: " + (System.nanoTime() - start) / 1_000_000.0 + " ms");

        System.out.println();

        // Extension: Find invalid positions
        System.out.println("--- Find Invalid Positions ---");
        String[] invalidTests = {
            "())",           // position 2 is invalid
            "((())",         // position 0 is invalid
            "({)}",          // positions 1, 2, 3 are invalid
            "([{}])",        // all valid
        };
        for (String test : invalidTests) {
            System.out.println("Input: \"" + test + "\"");
            System.out.println("  Valid: " + isValid(test));
            System.out.println("  Invalid positions: " + findInvalidPositions(test));
        }
    }
}
