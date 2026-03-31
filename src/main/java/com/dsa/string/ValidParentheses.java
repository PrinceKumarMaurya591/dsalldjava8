package com.dsa.string;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Problem: Valid Parentheses
 * 
 * Problem Statement:
 * Given a string s containing just the characters '(', ')', '{', '}', '[' and ']',
 * determine if the input string is valid.
 * 
 * A string is valid if:
 * 1. Open brackets must be closed by the same type of brackets.
 * 2. Open brackets must be closed in the correct order.
 * 3. Every close bracket has a corresponding open bracket of the same type.
 * 
 * Assumptions:
 * - 1 <= s.length <= 10^4
 * - s consists of parentheses only '()[]{}'
 * 
 * Optimal Solution: O(n) time, O(n) space using stack
 * 
 * Algorithm Explanation:
 * 1. Create a stack to track opening brackets
 * 2. Create a mapping of closing brackets to their corresponding opening brackets
 * 3. Iterate through each character in the string:
 *    a. If it's an opening bracket ('(', '{', '['), push it onto the stack
 *    b. If it's a closing bracket (')', '}', ']'):
 *       - If stack is empty, return false (no matching opening bracket)
 *       - Pop from stack and check if it matches the expected opening bracket
 *       - If not matching, return false
 * 4. After processing all characters, check if stack is empty
 *    (all opening brackets were properly closed)
 * 
 * Key Insight:
 * - Last opened bracket must be first closed (LIFO - stack property)
 * - Stack naturally handles nested parentheses
 * 
 * Dry Run Example:
 * Input: s = "()[]{}"
 * 
 * Step 1: '(' → push '(' → stack = ['(']
 * Step 2: ')' → pop '(' → matches ')' → stack = []
 * Step 3: '[' → push '[' → stack = ['[']
 * Step 4: ']' → pop '[' → matches ']' → stack = []
 * Step 5: '{' → push '{' → stack = ['{']
 * Step 6: '}' → pop '{' → matches '}' → stack = []
 * 
 * Stack empty → true
 * 
 * Result: true
 */
public class ValidParentheses {
    
    /**
     * Standard solution using Stack class (O(n) time, O(n) space)
     * 
     * @param s string containing only parentheses
     * @return true if parentheses are valid
     */
    public static boolean isValid(String s) {
        if (s == null || s.length() % 2 != 0) {
            return false;  // Odd length cannot be valid
        }
        
        Stack<Character> stack = new Stack<>();
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                // Opening bracket - push to stack
                stack.push(c);
            } else {
                // Closing bracket - check if stack is empty
                if (stack.isEmpty()) {
                    return false;
                }
                
                // Pop and check if it matches
                char top = stack.pop();
                if (!isMatchingPair(top, c)) {
                    return false;
                }
            }
        }
        
        // All brackets should be matched
        return stack.isEmpty();
    }
    
    /**
     * Helper method to check if two brackets form a valid pair
     */
    private static boolean isMatchingPair(char open, char close) {
        return (open == '(' && close == ')') ||
               (open == '{' && close == '}') ||
               (open == '[' && close == ']');
    }
    
    /**
     * Optimized solution using HashMap and ArrayDeque (faster than Stack)
     * 
     * @param s string containing only parentheses
     * @return true if parentheses are valid
     */
    public static boolean isValidOptimized(String s) {
        if (s == null || s.length() % 2 != 0) {
            return false;
        }
        
        // Map closing brackets to their corresponding opening brackets
        Map<Character, Character> bracketMap = new HashMap<>();
        bracketMap.put(')', '(');
        bracketMap.put('}', '{');
        bracketMap.put(']', '[');
        
        // Use ArrayDeque instead of Stack for better performance
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char c : s.toCharArray()) {
            if (bracketMap.containsValue(c)) {
                // Opening bracket
                stack.push(c);
            } else if (bracketMap.containsKey(c)) {
                // Closing bracket
                if (stack.isEmpty() || stack.pop() != bracketMap.get(c)) {
                    return false;
                }
            } else {
                // Invalid character
                return false;
            }
        }
        
        return stack.isEmpty();
    }
    
    /**
     * Solution using only array (no Stack class)
     * More memory efficient for fixed character set
     * 
     * @param s string containing only parentheses
     * @return true if parentheses are valid
     */
    public static boolean isValidArray(String s) {
        if (s == null || s.length() % 2 != 0) {
            return false;
        }
        
        // Use array as stack with pointer
        char[] stack = new char[s.length()];
        int top = -1;  // Stack pointer
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                // Push opening bracket
                stack[++top] = c;
            } else {
                // Closing bracket - check if stack is empty
                if (top == -1) {
                    return false;
                }
                
                // Check if top matches closing bracket
                char open = stack[top--];
                if (!((open == '(' && c == ')') ||
                      (open == '{' && c == '}') ||
                      (open == '[' && c == ']'))) {
                    return false;
                }
            }
        }
        
        // Stack should be empty
        return top == -1;
    }
    
    /**
     * Solution for extended bracket types
     * Supports additional bracket types like <>, "", ''
     * 
     * @param s string containing brackets
     * @return true if brackets are valid
     */
    public static boolean isValidExtended(String s) {
        if (s == null || s.length() % 2 != 0) {
            return false;
        }
        
        Map<Character, Character> bracketMap = new HashMap<>();
        bracketMap.put(')', '(');
        bracketMap.put('}', '{');
        bracketMap.put(']', '[');
        bracketMap.put('>', '<');
        bracketMap.put('"', '"');
        bracketMap.put('\'', '\'');
        
        Deque<Character> stack = new ArrayDeque<>();
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            
            if (bracketMap.containsValue(c) && 
                !(c == '"' || c == '\'')) {
                // Opening bracket (except quotes which are both opening and closing)
                stack.push(c);
            } else if (bracketMap.containsKey(c)) {
                if (c == '"' || c == '\'') {
                    // Quotes - if stack has matching quote, pop it
                    if (!stack.isEmpty() && stack.peek() == c) {
                        stack.pop();
                    } else {
                        stack.push(c);
                    }
                } else {
                    // Regular closing bracket
                    if (stack.isEmpty() || stack.pop() != bracketMap.get(c)) {
                        return false;
                    }
                }
            } else {
                return false;  // Invalid character
            }
        }
        
        return stack.isEmpty();
    }
    
    /**
     * Solution with early termination for invalid patterns
     * More efficient for strings that become invalid early
     * 
     * @param s string containing only parentheses
     * @return true if parentheses are valid
     */
    public static boolean isValidEarlyExit(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        
        if (s.length() % 2 != 0) {
            return false;
        }
        
        // Quick checks for common invalid patterns
        char first = s.charAt(0);
        char last = s.charAt(s.length() - 1);
        if (first == ')' || first == '}' || first == ']' ||
            last == '(' || last == '{' || last == '[') {
            return false;
        }
        
        // Count of each type (optional optimization)
        int roundCount = 0, curlyCount = 0, squareCount = 0;
        
        Deque<Character> stack = new ArrayDeque<>();
        
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(':
                    stack.push(c);
                    roundCount++;
                    break;
                case '{':
                    stack.push(c);
                    curlyCount++;
                    break;
                case '[':
                    stack.push(c);
                    squareCount++;
                    break;
                case ')':
                    if (stack.isEmpty() || stack.pop() != '(') {
                        return false;
                    }
                    roundCount--;
                    break;
                case '}':
                    if (stack.isEmpty() || stack.pop() != '{') {
                        return false;
                    }
                    curlyCount--;
                    break;
                case ']':
                    if (stack.isEmpty() || stack.pop() != '[') {
                        return false;
                    }
                    squareCount--;
                    break;
                default:
                    return false;
            }
            
            // Early exit if counts go negative
            if (roundCount < 0 || curlyCount < 0 || squareCount < 0) {
                return false;
            }
        }
        
        // Final count check
        return roundCount == 0 && curlyCount == 0 && squareCount == 0 && stack.isEmpty();
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Test case 1: Valid simple parentheses
        String s1 = "()";
        boolean result1 = isValid(s1);
        System.out.println("Valid Parentheses Problem:");
        System.out.println("Input: s = \"" + s1 + "\"");
        System.out.println("Output: " + result1);
        System.out.println("Expected: true");
        System.out.println();
        
        // Test case 2: Valid multiple types
        String s2 = "()[]{}";
        boolean result2 = isValidOptimized(s2);
        System.out.println("Input: s = \"" + s2 + "\"");
        System.out.println("Output (Optimized): " + result2);
        System.out.println("Expected: true");
        System.out.println();
        
        // Test case 3: Invalid - mismatched
        String s3 = "(]";
        boolean result3 = isValidArray(s3);
        System.out.println("Input: s = \"" + s3 + "\"");
        System.out.println("Output (Array): " + result3);
        System.out.println("Expected: false");
        System.out.println();
        
        // Test case 4: Valid nested
        String s4 = "([{}])";
        boolean result4 = isValidExtended(s4);
        System.out.println("Input: s = \"" + s4 + "\"");
        System.out.println("Output (Extended): " + result4);
        System.out.println("Expected: true");
        System.out.println();
        
        // Test case 5: Invalid - odd length
        String s5 = "([]";
        boolean result5 = isValidEarlyExit(s5);
        System.out.println("Input: s = \"" + s5 + "\"");
        System.out.println("Output (Early Exit): " + result5);
        System.out.println("Expected: false");
        System.out.println();
        
        // Test case 6: Complex valid case
        String s6 = "{[]()}";
        boolean result6 = isValid(s6);
        System.out.println("Input: s = \"" + s6 + "\"");
        System.out.println("Output: " + result6);
        System.out.println("Expected: true");
        
        // Performance comparison
        System.out.println("\nPerformance Analysis:");
        System.out.println("1. Stack Class: O(n) time, O(n) space - Simple, uses Java Stack");
        System.out.println("2. ArrayDeque: O(n) time, O(n) space - Faster than Stack");
        System.out.println("3. Array: O(n) time, O(n) space - Most memory efficient");
        System.out.println("4. Early Exit: O(n) time, O(n) space - Optimized for early failures");
        
        // Dry run visualization
        System.out.println("\nDry run for s = \"([{}])\":");
        System.out.println("Step-by-step stack operations:");
        
        String s = "([{}])";
        Deque<Character> stack = new ArrayDeque<>();
        boolean valid = true;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            System.out.print("  Character '" + c + "': ");
            
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
                System.out.println("push -> stack = " + stack);
            } else {
                if (stack.isEmpty()) {
                    System.out.println("ERROR: stack empty, no matching opening bracket");
                    valid = false;
                    break;
                }
                
                char top = stack.pop();
                boolean matches = (top == '(' && c == ')') ||
                                 (top == '[' && c == ']') ||
                                 (top == '{' && c == '}');
                
                if (!matches) {
                    System.out.println("ERROR: '" + top + "' doesn't match '" + c + "'");
                    valid = false;
                    break;
                }
                
                System.out.println("pop '" + top + "' matches '" + c + "' -> stack = " + stack);
            }
        }
        
        if (valid && !stack.isEmpty()) {
            System.out.println("ERROR: stack not empty after processing: " + stack);
            valid = false;
        }
        
        System.out.println("Final result: " + (valid ? "true (valid)" : "false (invalid)"));
        
        // Show common invalid patterns
        System.out.println("\nCommon invalid patterns:");
        String[] invalidExamples = {
            ")(",  // Starts with closing bracket
            "({)}",  // Interleaved brackets
            "(((",   // Only opening brackets
            "}}}",   // Only closing brackets
            "([)]",  // Wrong nesting order
        };
        
        for (String example : invalidExamples) {
            System.out.println("  \"" + example + "\" -> " + isValid(example));
        }
    }
}