package com.dsa.greedy;

import java.util.Arrays;

// Problem: Assign Cookies
// Link: https://leetcode.com/problems/assign-cookies/
//
// Assume you are an awesome parent and want to give your children some cookies.
// But you should give each child at most one cookie.
//
// Each child i has a greed factor g[i], which is the minimum size of a cookie
// that the child will be content with. Each cookie j has a size s[j].
// If s[j] >= g[i], we can assign the cookie j to the child i, and the child i
// will be content. Your goal is to maximize the number of your content children
// and output the maximum number.
//
// Approach: Greedy (Two Pointers)
// - Sort both arrays
// - Assign the smallest cookie that satisfies each child's greed
// - Move pointers accordingly
//
// Time Complexity: O(n log n + m log m) - sorting
// Space Complexity: O(1)

public class AssignCookies {

    public static void main(String[] args) {
        System.out.println("=== Assign Cookies ===");
        int[] g1 = {1, 2, 3};
        int[] s1 = {1, 1};
        System.out.println("Content children: " + findContentChildren(g1, s1));
        // Expected: 1

        int[] g2 = {1, 2};
        int[] s2 = {1, 2, 3};
        System.out.println("Content children: " + findContentChildren(g2, s2));
        // Expected: 2

        int[] g3 = {10, 9, 8, 7};
        int[] s3 = {5, 6, 7, 8};
        System.out.println("Content children: " + findContentChildren(g3, s3));
        // Expected: 2
    }

    public static int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);

        int child = 0;
        int cookie = 0;

        while (child < g.length && cookie < s.length) {
            if (s[cookie] >= g[child]) {
                child++; // This child is content
            }
            cookie++; // Move to next cookie (used or too small)
        }

        return child;
    }
}
