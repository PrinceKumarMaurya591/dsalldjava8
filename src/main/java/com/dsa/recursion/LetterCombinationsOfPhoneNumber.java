package com.dsa.recursion;

import java.util.ArrayList;
import java.util.List;

// Problem: Letter Combinations of a Phone Number
// Link: https://leetcode.com/problems/letter-combinations-of-a-phone-number/
//
// Given a string containing digits from 2-9 inclusive, return all possible letter
// combinations that the number could represent. Return the answer in any order.
//
// A mapping of digits to letters (just like on the telephone buttons) is given below.
// Note that 1 does not map to any letters.
//
// 2: abc    3: def    4: ghi    5: jkl
// 6: mno    7: pqrs   8: tuv    9: wxyz
//
// Approach: Backtracking
// - Map each digit to its corresponding letters
// - Build combinations character by character
// - When current string length equals digits length, add to result
//
// Time Complexity: O(4^n * n) where n = number of digits, 4 = max letters per digit
// Space Complexity: O(n) - recursion depth

public class LetterCombinationsOfPhoneNumber {

    private static final String[] KEYPAD = {
            "",     // 0
            "",     // 1
            "abc",  // 2
            "def",  // 3
            "ghi",  // 4
            "jkl",  // 5
            "mno",  // 6
            "pqrs", // 7
            "tuv",  // 8
            "wxyz"  // 9
    };

    public static void main(String[] args) {
        System.out.println("=== Letter Combinations of Phone Number ===");
        System.out.println("Digits '23': " + letterCombinations("23"));
        // Expected: [ad, ae, af, bd, be, bf, cd, ce, cf]

        System.out.println("Digits '': " + letterCombinations(""));
        // Expected: []

        System.out.println("Digits '2': " + letterCombinations("2"));
        // Expected: [a, b, c]

        System.out.println("Digits '79': " + letterCombinations("79"));
        // Expected: [pw, px, py, pz, qw, qx, qy, qz, rw, rx, ry, rz, sw, sx, sy, sz]
    }

    public static List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty()) return result;

        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    private static void backtrack(String digits, int index, StringBuilder current,
                                   List<String> result) {
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }

        String letters = KEYPAD[digits.charAt(index) - '0'];
        for (char ch : letters.toCharArray()) {
            current.append(ch);
            backtrack(digits, index + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
