package com.dsa.stack;

import java.util.*;

/**
 * Generate Parentheses
 * 
 * Given n pairs of parentheses, write a function to generate all combinations
 * of well-formed parentheses.
 * 
 * Example: n = 3
 * Output: ["((()))","(()())","(())()","()(())","()()()"]
 * 
 * Approach 1: Backtracking (DFS)
 * - Track open and close counts
 * - Add '(' if open < n
 * - Add ')' if close < open
 * - Base case: when length == 2*n, add to result
 * 
 * Approach 2: Stack-based iterative approach
 * - Use a stack to simulate the backtracking process
 * - Each stack element contains: current string, open count, close count
 * 
 * Approach 3: Closure Number method
 * - For each possible split, generate left and right parts
 * 
 * Time Complexity: O(4^n / sqrt(n)) - Catalan number
 * Space Complexity: O(n) - recursion depth
 */
public class GenerateParentheses {

    // =============================================
    // Approach 1: Backtracking (DFS)
    // =============================================
    public static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private static void backtrack(List<String> result, StringBuilder current,
                                  int open, int close, int max) {
        if (current.length() == max * 2) {
            result.add(current.toString());
            return;
        }

        if (open < max) {
            current.append('(');
            backtrack(result, current, open + 1, close, max);
            current.deleteCharAt(current.length() - 1);
        }

        if (close < open) {
            current.append(')');
            backtrack(result, current, open, close + 1, max);
            current.deleteCharAt(current.length() - 1);
        }
    }

    // =============================================
    // Approach 2: Stack-based Iterative
    // =============================================
    static class State {
        String str;
        int open;
        int close;

        State(String str, int open, int close) {
            this.str = str;
            this.open = open;
            this.close = close;
        }
    }

    public static List<String> generateParenthesisIterative(int n) {
        List<String> result = new ArrayList<>();
        Deque<State> stack = new ArrayDeque<>();
        stack.push(new State("", 0, 0));

        while (!stack.isEmpty()) {
            State current = stack.pop();

            if (current.str.length() == n * 2) {
                result.add(current.str);
                continue;
            }

            if (current.close < current.open) {
                stack.push(new State(current.str + ")", current.open, current.close + 1));
            }

            if (current.open < n) {
                stack.push(new State(current.str + "(", current.open + 1, current.close));
            }
        }

        return result;
    }

    // =============================================
    // Approach 3: Closure Number
    // =============================================
    public static List<String> generateParenthesisClosure(int n) {
        List<String> result = new ArrayList<>();
        if (n == 0) {
            result.add("");
        } else {
            for (int i = 0; i < n; i++) {
                for (String left : generateParenthesisClosure(i)) {
                    for (String right : generateParenthesisClosure(n - 1 - i)) {
                        result.add("(" + left + ")" + right);
                    }
                }
            }
        }
        return result;
    }

    // =============================================
    // Approach 4: Dynamic Programming
    // =============================================
    public static List<String> generateParenthesisDP(int n) {
        List<List<String>> dp = new ArrayList<>();
        dp.add(List.of("")); // dp[0] = [""]

        for (int i = 1; i <= n; i++) {
            List<String> current = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                for (String left : dp.get(j)) {
                    for (String right : dp.get(i - 1 - j)) {
                        current.add("(" + left + ")" + right);
                    }
                }
            }
            dp.add(current);
        }

        return dp.get(n);
    }

    public static void main(String[] args) {
        System.out.println("Generate Parentheses\n");

        // Test for n = 1 to 4
        for (int n = 1; n <= 4; n++) {
            System.out.println("n = " + n);
            System.out.println("Backtracking: " + generateParenthesis(n));
            System.out.println("Iterative:    " + generateParenthesisIterative(n));
            System.out.println("Closure:      " + generateParenthesisClosure(n));
            System.out.println("DP:           " + generateParenthesisDP(n));
            System.out.println("Count: " + generateParenthesis(n).size() + 
                             " (Catalan number: " + catalan(n) + ")");
            System.out.println();
        }

        // Verify all approaches produce same results
        System.out.println("--- Verification ---");
        for (int n = 1; n <= 6; n++) {
            List<String> backtrack = generateParenthesis(n);
            List<String> iterative = generateParenthesisIterative(n);
            List<String> closure = generateParenthesisClosure(n);
            List<String> dp = generateParenthesisDP(n);

            Set<String> set1 = new HashSet<>(backtrack);
            Set<String> set2 = new HashSet<>(iterative);
            Set<String> set3 = new HashSet<>(closure);
            Set<String> set4 = new HashSet<>(dp);

            boolean allMatch = set1.equals(set2) && set2.equals(set3) && set3.equals(set4);
            System.out.println("n=" + n + ": All match=" + allMatch + 
                             ", Count=" + backtrack.size() + 
                             ", Catalan=" + catalan(n));
        }
    }

    private static long catalan(int n) {
        long result = 1;
        for (int i = 0; i < n; i++) {
            result = result * 2 * (2 * i + 1) / (i + 2);
        }
        return result;
    }
}
