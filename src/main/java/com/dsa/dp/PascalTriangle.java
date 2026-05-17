package com.dsa.dp;

import java.util.ArrayList;
import java.util.List;

// Problem: Pascal's Triangle
// Link: https://leetcode.com/problems/pascals-triangle/
//
// Given an integer numRows, return the first numRows of Pascal's triangle.
// In Pascal's triangle, each number is the sum of the two numbers directly above it.
//
// Approach: DP (iterative)
// Each row[i][j] = row[i-1][j-1] + row[i-1][j]
//
// Time Complexity: O(numRows^2)
// Space Complexity: O(numRows^2)

public class PascalTriangle {

    public static void main(String[] args) {
        List<List<Integer>> result = generate(5);
        System.out.println("Pascal's Triangle (5 rows):");
        for (List<Integer> row : result) {
            System.out.println(row);
        }
        // Expected:
        // [1]
        // [1, 1]
        // [1, 2, 1]
        // [1, 3, 3, 1]
        // [1, 4, 6, 4, 1]
    }

    public static List<List<Integer>> generate(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();

        if (numRows <= 0) return triangle;

        // First row
        List<Integer> firstRow = new ArrayList<>();
        firstRow.add(1);
        triangle.add(firstRow);

        for (int i = 1; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();
            List<Integer> prevRow = triangle.get(i - 1);

            // First element is always 1
            row.add(1);

            // Middle elements
            for (int j = 1; j < i; j++) {
                row.add(prevRow.get(j - 1) + prevRow.get(j));
            }

            // Last element is always 1
            row.add(1);

            triangle.add(row);
        }

        return triangle;
    }
}
