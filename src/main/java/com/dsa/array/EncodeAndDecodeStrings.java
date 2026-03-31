package com.dsa.array;

import java.util.*;

/**
 * Problem 24: Encode and Decode Strings
 */
public class EncodeAndDecodeStrings {
    public static String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str.length()).append("#").append(str);
        }
        return sb.toString();
    }
    
    public static List<String> decode(String s) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            int delim = s.indexOf("#", i);
            int len = Integer.parseInt(s.substring(i, delim));
            result.add(s.substring(delim + 1, delim + 1 + len));
            i = delim + 1 + len;
        }
        return result;
    }
    
    public static void main(String[] args) {
        System.out.println("Encode and Decode Strings:");
        List<String> original = Arrays.asList("hello", "world", "code");
        System.out.println("Original: " + original);
        String encoded = encode(original);
        System.out.println("Encoded: " + encoded);
        List<String> decoded = decode(encoded);
        System.out.println("Decoded: " + decoded);
        System.out.println("Match: " + original.equals(decoded));
    }
}