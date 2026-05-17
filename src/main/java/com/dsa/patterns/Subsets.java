package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Subsets (Backtracking)
 * 
 * Used when: Problems involving generating all permutations, combinations,
 * subsets of a set, or all possible arrangements/solutions.
 * 
 * Core idea: Use recursion/backtracking to explore all possibilities.
 * At each step, decide to include or exclude an element.
 * 
 * Key variations:
 * 1. Subsets (all possible subsets)
 * 2. Subsets with duplicates
 * 3. Permutations
 * 4. Permutations with duplicates
 * 5. Combinations
 * 6. Combination Sum (unlimited use)
 * 7. Combination Sum II (each once)
 * 8. Letter Combinations of Phone Number
 * 9. Palindrome Partitioning
 * 10. Generate Parentheses
 * 
 * Time Complexity: O(2^n) for subsets, O(n!) for permutations
 * Space Complexity: O(n) for recursion stack
 */
public class Subsets {

    /**
     * Problem: Subsets
     * Return all possible subsets (power set).
     * 
     * Approach: Backtracking - at each index, decide to include or skip.
     * Time: O(n * 2^n), Space: O(n)
     */
    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSubsets(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackSubsets(int[] nums, int start, 
                                          List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrackSubsets(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Problem: Subsets II (with duplicates)
     * Return all possible unique subsets.
     * 
     * Approach: Sort first, skip duplicates at same recursion level.
     * Time: O(n * 2^n), Space: O(n)
     */
    public static List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        backtrackSubsetsWithDup(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackSubsetsWithDup(int[] nums, int start,
                                                 List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));

        for (int i = start; i < nums.length; i++) {
            // Skip duplicates at same recursion level
            if (i > start && nums[i] == nums[i - 1]) continue;

            current.add(nums[i]);
            backtrackSubsetsWithDup(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Problem: Permutations
     * Return all possible permutations of distinct integers.
     * 
     * Approach: Backtracking with used array.
     * Time: O(n * n!), Space: O(n)
     */
    public static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrackPermute(nums, used, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackPermute(int[] nums, boolean[] used,
                                          List<Integer> current, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            used[i] = true;
            current.add(nums[i]);
            backtrackPermute(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    /**
     * Problem: Permutations II (with duplicates)
     * Return all unique permutations.
     * 
     * Approach: Sort + skip duplicates at same recursion level.
     * Time: O(n * n!), Space: O(n)
     */
    public static List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        boolean[] used = new boolean[nums.length];
        backtrackPermuteUnique(nums, used, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackPermuteUnique(int[] nums, boolean[] used,
                                                List<Integer> current, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            // Skip duplicates: if same as previous and previous not used
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;

            used[i] = true;
            current.add(nums[i]);
            backtrackPermuteUnique(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    /**
     * Problem: Combinations
     * Return all combinations of k numbers from [1, n].
     * 
     * Time: O(C(n,k) * k), Space: O(k)
     */
    public static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombine(n, k, 1, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackCombine(int n, int k, int start,
                                          List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i <= n; i++) {
            current.add(i);
            backtrackCombine(n, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Problem: Combination Sum
     * Find all combinations that sum to target (unlimited use of each element).
     * 
     * Time: O(2^(target/min)), Space: O(target/min)
     */
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombinationSum(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackCombinationSum(int[] candidates, int remaining, int start,
                                                 List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) continue;

            current.add(candidates[i]);
            backtrackCombinationSum(candidates, remaining - candidates[i], i, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Problem: Combination Sum II
     * Find all unique combinations (each element used once).
     * 
     * Time: O(2^n), Space: O(n)
     */
    public static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrackCombinationSum2(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackCombinationSum2(int[] candidates, int remaining, int start,
                                                  List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;
            if (i > start && candidates[i] == candidates[i - 1]) continue;

            current.add(candidates[i]);
            backtrackCombinationSum2(candidates, remaining - candidates[i], i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Problem: Letter Combinations of a Phone Number
     * Return all possible letter combinations from digit string.
     * 
     * Time: O(4^n), Space: O(n)
     */
    public static List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) return new ArrayList<>();

        String[] mapping = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        List<String> result = new ArrayList<>();
        backtrackLetterCombinations(digits, 0, mapping, new StringBuilder(), result);
        return result;
    }

    private static void backtrackLetterCombinations(String digits, int index, String[] mapping,
                                                     StringBuilder current, List<String> result) {
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }

        String letters = mapping[digits.charAt(index) - '0'];
        for (char c : letters.toCharArray()) {
            current.append(c);
            backtrackLetterCombinations(digits, index + 1, mapping, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }

    /**
     * Problem: Palindrome Partitioning
     * Partition string into all possible palindrome substrings.
     * 
     * Time: O(n * 2^n), Space: O(n)
     */
    public static List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        backtrackPartition(s, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrackPartition(String s, int start,
                                            List<String> current, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                current.add(s.substring(start, end + 1));
                backtrackPartition(s, end + 1, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    private static boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left++) != s.charAt(right--)) return false;
        }
        return true;
    }

    /**
     * Problem: Generate Parentheses
     * Generate all combinations of well-formed parentheses.
     * 
     * Time: O(4^n / sqrt(n)), Space: O(n)
     */
    public static List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrackParenthesis(n, 0, 0, new StringBuilder(), result);
        return result;
    }

    private static void backtrackParenthesis(int n, int open, int close,
                                              StringBuilder current, List<String> result) {
        if (current.length() == 2 * n) {
            result.add(current.toString());
            return;
        }

        if (open < n) {
            current.append('(');
            backtrackParenthesis(n, open + 1, close, current, result);
            current.deleteCharAt(current.length() - 1);
        }

        if (close < open) {
            current.append(')');
            backtrackParenthesis(n, open, close + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== SUBSETS (BACKTRACKING) PATTERN ===");
        System.out.println();

        // 1. Subsets
        System.out.println("1. Subsets:");
        System.out.println("   Input: [1,2,3]");
        System.out.println("   Output: " + subsets(new int[]{1, 2, 3}));
        System.out.println("   Expected: [[],[1],[1,2],[1,2,3],[1,3],[2],[2,3],[3]]");
        System.out.println();

        // 2. Subsets II
        System.out.println("2. Subsets II (with duplicates):");
        System.out.println("   Input: [1,2,2]");
        System.out.println("   Output: " + subsetsWithDup(new int[]{1, 2, 2}));
        System.out.println("   Expected: [[],[1],[1,2],[1,2,2],[2],[2,2]]");
        System.out.println();

        // 3. Permutations
        System.out.println("3. Permutations:");
        System.out.println("   Input: [1,2,3]");
        System.out.println("   Output: " + permute(new int[]{1, 2, 3}));
        System.out.println("   Expected: 6 permutations");
        System.out.println();

        // 4. Permutations II
        System.out.println("4. Permutations II (with duplicates):");
        System.out.println("   Input: [1,1,2]");
        System.out.println("   Output: " + permuteUnique(new int[]{1, 1, 2}));
        System.out.println("   Expected: [[1,1,2],[1,2,1],[2,1,1]]");
        System.out.println();

        // 5. Combinations
        System.out.println("5. Combinations:");
        System.out.println("   Input: n=4, k=2");
        System.out.println("   Output: " + combine(4, 2));
        System.out.println("   Expected: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]");
        System.out.println();

        // 6. Combination Sum
        System.out.println("6. Combination Sum:");
        System.out.println("   Input: [2,3,6,7], target=7");
        System.out.println("   Output: " + combinationSum(new int[]{2, 3, 6, 7}, 7));
        System.out.println("   Expected: [[2,2,3],[7]]");
        System.out.println();

        // 7. Combination Sum II
        System.out.println("7. Combination Sum II:");
        System.out.println("   Input: [10,1,2,7,6,1,5], target=8");
        System.out.println("   Output: " + combinationSum2(new int[]{10, 1, 2, 7, 6, 1, 5}, 8));
        System.out.println("   Expected: [[1,1,6],[1,2,5],[1,7],[2,6]]");
        System.out.println();

        // 8. Letter Combinations
        System.out.println("8. Letter Combinations:");
        System.out.println("   Input: \"23\"");
        System.out.println("   Output: " + letterCombinations("23"));
        System.out.println("   Expected: [ad,ae,af,bd,be,bf,cd,ce,cf]");
        System.out.println();

        // 9. Palindrome Partitioning
        System.out.println("9. Palindrome Partitioning:");
        System.out.println("   Input: \"aab\"");
        System.out.println("   Output: " + partition("aab"));
        System.out.println("   Expected: [[a,a,b],[aa,b]]");
        System.out.println();

        // 10. Generate Parentheses
        System.out.println("10. Generate Parentheses:");
        System.out.println("    Input: n=3");
        System.out.println("    Output: " + generateParenthesis(3));
        System.out.println("    Expected: [((())),(()()),(())(),()(()),()()()]");
    }
}
