package com.dsa.array;

import java.util.*;

/**
 * Problem 22: Group Anagrams
 */
public class GroupAnagrams {
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
        }
        return new ArrayList<>(map.values());
    }
    
    public static void main(String[] args) {
        System.out.println("Group Anagrams:");
        String[] strs = {"eat","tea","tan","ate","nat","bat"};
        System.out.println("Input: [\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"]");
        System.out.println("Output: " + groupAnagrams(strs));
    }
}