package com.dsa.stack;

import java.util.*;

/**
 * Minimum Remove to Make Valid Parentheses
 * 
 * Given a string s of '(' , ')' and lowercase English characters, remove the
 * minimum number of parentheses to make the input string valid and return
 * any valid result.
 * 
 * A parentheses string is valid if:
 * - Every open bracket has a corresponding close bracket
 * - Open brackets are closed in the correct order
 * 
 * Approach 1: Stack-based
 * - Use a stack to track indices of '('
 * - When we see ')', if stack is empty, mark it for removal
 * - If stack is not empty, pop the matching '('
 * - After processing, any remaining '(' in stack are unmatched
 * - Build result string excluding marked indices
 * 
 * Approach 2: Two-pass (without stack)
 * - First pass: remove unmatched ')'
 * - Second pass: remove unmatched '(' from the end
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class MinimumRemoveToMakeValidParentheses {

    // =============================================
    // Approach 1: Stack-based
    // =============================================
    public static String minRemoveToMakeValid(String s) {
        Set<Integer> indicesToRemove = new HashSet<>();
        Deque<Integer> stack = new ArrayDeque<>();

        // First pass: identify unmatched parentheses
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                stack.push(i);
            } else if (c == ')') {
                if (stack.isEmpty()) {
                    indicesToRemove.add(i); // Unmatched ')'
                } else {
                    stack.pop(); // Match with a '('
                }
            }
        }

        // Any remaining '(' are unmatched
        while (!stack.isEmpty()) {
            indicesToRemove.add(stack.pop());
        }

        // Build result string
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (!indicesToRemove.contains(i)) {
                result.append(s.charAt(i));
            }
        }

        return result.toString();
    }

    // =============================================
    // Approach 2: Two-pass (without stack)
    // =============================================
    public static String minRemoveToMakeValidTwoPass(String s) {
        StringBuilder sb = new StringBuilder();
        int open = 0;

        // First pass: remove unmatched ')'
        for (char c : s.toCharArray()) {
            if (c == '(') {
                open++;
            } else if (c == ')') {
                if (open == 0) continue; // Skip unmatched ')'
                open--;
            }
            sb.append(c);
        }

        // Second pass: remove unmatched '(' from the end
        StringBuilder result = new StringBuilder();
        int close = 0;
        for (int i = sb.length() - 1; i >= 0; i--) {
            char c = sb.charAt(i);
            if (c == ')') {
                close++;
            } else if (c == '(') {
                if (close == 0) continue; // Skip unmatched '('
                close--;
            }
            result.append(c);
        }

        return result.reverse().toString();
    }

    // =============================================
    // Approach 3: Using StringBuilder as Stack
    // =============================================
    public static String minRemoveToMakeValidStringBuilder(String s) {
        StringBuilder sb = new StringBuilder(s);
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '(') {
                stack.push(i);
            } else if (sb.charAt(i) == ')') {
                if (stack.isEmpty()) {
                    sb.setCharAt(i, '*'); // Mark for removal
                } else {
                    stack.pop();
                }
            }
        }

        // Mark unmatched '('
        while (!stack.isEmpty()) {
            sb.setCharAt(stack.pop(), '*');
        }

        // Build result
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) != '*') {
                result.append(sb.charAt(i));
            }
        }

        return result.toString();
    }

    /**
     * Extension: Return all possible valid strings (minimum removals)
     */
    public static List<String> minRemoveToMakeValidAll(String s) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(s);
        visited.add(s);
        boolean found = false;

        while (!queue.isEmpty() && !found) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                if (isValid(current)) {
                    result.add(current);
                    found = true;
                }
                if (!found) {
                    for (int j = 0; j < current.length(); j++) {
                        if (current.charAt(j) != '(' && current.charAt(j) != ')') {
                            continue;
                        }
                        String next = current.substring(0, j) + current.substring(j + 1);
                        if (!visited.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static boolean isValid(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') {
                if (count == 0) return false;
                count--;
            }
        }
        return count == 0;
    }

    public static void main(String[] args) {
        System.out.println("Minimum Remove to Make Valid Parentheses\n");

        // Test cases
        String[] testCases = {
            "lee(t(c)o)de)",  // "lee(t(c)o)de"
            "a)b(c)d",        // "ab(c)d"
            "))((",           // ""
            "(a(b(c)d)",      // "a(b(c)d)" or "(a(bc)d)"
            "())()(((",       // "()()"
            "abc",            // "abc"
            "",               // ""
            "(((((",          // ""
            "))))",           // ""
            "(())",           // "(())"
        };

        for (String test : testCases) {
            System.out.println("Input:    \"" + test + "\"");
            System.out.println("Stack:    \"" + minRemoveToMakeValid(test) + "\"");
            System.out.println("TwoPass:  \"" + minRemoveToMakeValidTwoPass(test) + "\"");
            System.out.println("SB Stack: \"" + minRemoveToMakeValidStringBuilder(test) + "\"");
            System.out.println();
        }

        // Verify all approaches produce valid results
        System.out.println("--- Verification ---");
        for (String test : testCases) {
            String r1 = minRemoveToMakeValid(test);
            String r2 = minRemoveToMakeValidTwoPass(test);
            String r3 = minRemoveToMakeValidStringBuilder(test);
            
            boolean allValid = isValid(r1) && isValid(r2) && isValid(r3);
            boolean sameLength = r1.length() == r2.length() && r2.length() == r3.length();
            
            System.out.println("Input: \"" + test + "\"");
            System.out.println("  All valid: " + allValid + ", Same length: " + sameLength);
            System.out.println("  Results: \"" + r1 + "\" | \"" + r2 + "\" | \"" + r3 + "\"");
        }

        System.out.println();

        // Show all possible valid strings for a complex case
        System.out.println("--- All Possible Valid Strings ---");
        String complex = "()())()";
        System.out.println("Input: \"" + complex + "\"");
        List<String> allValid = minRemoveToMakeValidAll(complex);
        System.out.println("All valid results (min removals): " + allValid);
    }
}
