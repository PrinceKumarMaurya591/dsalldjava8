package com.dsa.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ArrayProblemsTest {

    // =========================================================================
    // 1. TWO SUM TEST
    // =========================================================================
    @Test
    public void testTwoSum() {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = ArrayProblems.twoSum(nums, target);
        assertArrayEquals(new int[]{0, 1}, result);
        
        // Additional test case
        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        int[] result2 = ArrayProblems.twoSum(nums2, target2);
        assertArrayEquals(new int[]{1, 2}, result2);
    }

    // =========================================================================
    // 2. BEST TIME TO BUY AND SELL STOCK TEST
    // =========================================================================
    @Test
    public void testMaxProfit() {
        int[] prices = {7, 1, 5, 3, 6, 4};
        assertEquals(5, ArrayProblems.maxProfit(prices));
        
        int[] prices2 = {7, 6, 4, 3, 1};
        assertEquals(0, ArrayProblems.maxProfit(prices2));
    }

    // =========================================================================
    // 3. CONTAINS DUPLICATE TEST
    // =========================================================================
    @Test
    public void testContainsDuplicate() {
        int[] nums = {1, 2, 3, 1};
        assertTrue(ArrayProblems.containsDuplicate(nums));
        
        int[] nums2 = {1, 2, 3, 4};
        assertFalse(ArrayProblems.containsDuplicate(nums2));
    }

    // =========================================================================
    // 4. CONTAINS DUPLICATE II TEST
    // =========================================================================
    @Test
    public void testContainsNearbyDuplicate() {
        int[] nums = {1, 2, 3, 1};
        int k = 3;
        assertTrue(ArrayProblems.containsNearbyDuplicate(nums, k));
        
        int[] nums2 = {1, 2, 3, 1, 2, 3};
        int k2 = 2;
        assertFalse(ArrayProblems.containsNearbyDuplicate(nums2, k2));
    }

    // =========================================================================
    // 5. PRODUCT OF ARRAY EXCEPT SELF TEST
    // =========================================================================
    @Test
    public void testProductExceptSelf() {
        int[] nums = {1, 2, 3, 4};
        int[] expected = {24, 12, 8, 6};
        assertArrayEquals(expected, ArrayProblems.productExceptSelf(nums));
        
        int[] nums2 = {-1, 1, 0, -3, 3};
        int[] expected2 = {0, 0, 9, 0, 0};
        assertArrayEquals(expected2, ArrayProblems.productExceptSelf(nums2));
    }

    // =========================================================================
    // 6. MAXIMUM SUBARRAY TEST
    // =========================================================================
    @Test
    public void testMaxSubArray() {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        assertEquals(6, ArrayProblems.maxSubArray(nums));
        
        int[] nums2 = {1};
        assertEquals(1, ArrayProblems.maxSubArray(nums2));
    }

    // =========================================================================
    // 7. MAXIMUM PRODUCT SUBARRAY TEST
    // =========================================================================
    @Test
    public void testMaxProduct() {
        int[] nums = {2, 3, -2, 4};
        assertEquals(6, ArrayProblems.maxProduct(nums));
        
        int[] nums2 = {-2, 0, -1};
        assertEquals(0, ArrayProblems.maxProduct(nums2));
    }

    // =========================================================================
    // 8. FIND MINIMUM IN ROTATED SORTED ARRAY TEST
    // =========================================================================
    @Test
    public void testFindMin() {
        int[] nums = {4, 5, 6, 7, 0, 1, 2};
        assertEquals(0, ArrayProblems.findMin(nums));
        
        int[] nums2 = {3, 4, 5, 1, 2};
        assertEquals(1, ArrayProblems.findMin(nums2));
    }

    // =========================================================================
    // 9. SEARCH IN ROTATED SORTED ARRAY TEST
    // =========================================================================
    @Test
    public void testSearchInRotatedSortedArray() {
        int[] nums = {4, 5, 6, 7, 0, 1, 2};
        assertEquals(4, ArrayProblems.searchInRotatedSortedArray(nums, 0));
        assertEquals(-1, ArrayProblems.searchInRotatedSortedArray(nums, 3));
    }

    // =========================================================================
    // 10. TWO SUM II TEST
    // =========================================================================
    @Test
    public void testTwoSumII() {
        int[] numbers = {2, 7, 11, 15};
        int target = 9;
        int[] result = ArrayProblems.twoSumII(numbers, target);
        assertArrayEquals(new int[]{1, 2}, result);
    }

    // =========================================================================
    // 11. 3 SUM TEST
    // =========================================================================
    @Test
    public void testThreeSum() {
        int[] nums = {-1, 0, 1, 2, -1, -4};
        List<List<Integer>> result = ArrayProblems.threeSum(nums);
        List<List<Integer>> expected = new ArrayList<>();
        expected.add(Arrays.asList(-1, -1, 2));
        expected.add(Arrays.asList(-1, 0, 1));
        
        assertEquals(expected.size(), result.size());
        for (List<Integer> triplet : expected) {
            assertTrue(result.contains(triplet));
        }
    }

    // =========================================================================
    // 12. MERGE SORTED ARRAY TEST
    // =========================================================================
    @Test
    public void testMergeSortedArray() {
        int[] nums1 = {1, 2, 3, 0, 0, 0};
        int[] nums2 = {2, 5, 6};
        ArrayProblems.mergeSortedArray(nums1, 3, nums2, 3);
        assertArrayEquals(new int[]{1, 2, 2, 3, 5, 6}, nums1);
    }

    // =========================================================================
    // 13. CONTAINER WITH MOST WATER TEST
    // =========================================================================
    @Test
    public void testMaxArea() {
        int[] height = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        assertEquals(49, ArrayProblems.maxArea(height));
    }

    // =========================================================================
    // 14. VERIFYING AN ALIEN DICTIONARY TEST
    // =========================================================================
    @Test
    public void testIsAlienSorted() {
        String[] words = {"hello", "leetcode"};
        String order = "hlabcdefgijkmnopqrstuvwxyz";
        assertTrue(ArrayProblems.isAlienSorted(words, order));
        
        String[] words2 = {"word", "world", "row"};
        String order2 = "worldabcefghijkmnpqstuvxyz";
        assertFalse(ArrayProblems.isAlienSorted(words2, order2));
    }

    // =========================================================================
    // 15. NEXT PERMUTATION TEST
    // =========================================================================
    @Test
    public void testNextPermutation() {
        int[] nums = {1, 2, 3};
        ArrayProblems.nextPermutation(nums);
        assertArrayEquals(new int[]{1, 3, 2}, nums);
        
        int[] nums2 = {3, 2, 1};
        ArrayProblems.nextPermutation(nums2);
        assertArrayEquals(new int[]{1, 2, 3}, nums2);
    }

    // =========================================================================
    // 16. REMOVE DUPLICATES FROM SORTED ARRAY TEST
    // =========================================================================
    @Test
    public void testRemoveDuplicates() {
        int[] nums = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        int length = ArrayProblems.removeDuplicates(nums);
        assertEquals(5, length);
        // Check first 5 elements
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, Arrays.copyOfRange(nums, 0, 5));
    }

    // =========================================================================
    // 17. FIND FIRST AND LAST POSITION OF ELEMENT IN SORTED ARRAY TEST
    // =========================================================================
    @Test
    public void testSearchRange() {
        int[] nums = {5, 7, 7, 8, 8, 10};
        int[] result = ArrayProblems.searchRange(nums, 8);
        assertArrayEquals(new int[]{3, 4}, result);
        
        int[] result2 = ArrayProblems.searchRange(nums, 6);
        assertArrayEquals(new int[]{-1, -1}, result2);
    }

    // =========================================================================
    // 18. TRAPPING RAIN WATER TEST
    // =========================================================================
    @Test
    public void testTrap() {
        int[] height = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        assertEquals(6, ArrayProblems.trap(height));
    }

    // =========================================================================
    // 19. MEDIAN OF TWO SORTED ARRAYS TEST
    // =========================================================================
    @Test
    public void testFindMedianSortedArrays() {
        int[] nums1 = {1, 3};
        int[] nums2 = {2};
        assertEquals(2.0, ArrayProblems.findMedianSortedArrays(nums1, nums2), 0.001);
        
        int[] nums3 = {1, 2};
        int[] nums4 = {3, 4};
        assertEquals(2.5, ArrayProblems.findMedianSortedArrays(nums3, nums4), 0.001);
    }

    // =========================================================================
    // 20. VALID ANAGRAM TEST
    // =========================================================================
    @Test
    public void testIsAnagram() {
        assertTrue(ArrayProblems.isAnagram("anagram", "nagaram"));
        assertFalse(ArrayProblems.isAnagram("rat", "car"));
    }

    // =========================================================================
    // 21. TOP K FREQUENT ELEMENTS TEST
    // =========================================================================
    @Test
    public void testTopKFrequent() {
        int[] nums = {1, 1, 1, 2, 2, 3};
        int k = 2;
        int[] result = ArrayProblems.topKFrequent(nums, k);
        // Result can be in any order
        Arrays.sort(result);
        assertArrayEquals(new int[]{1, 2}, result);
    }

    // =========================================================================
    // 22. GROUP ANAGRAMS TEST
    // =========================================================================
    @Test
    public void testGroupAnagrams() {
        String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> result = ArrayProblems.groupAnagrams(strs);
        
        // Check that we have 3 groups
        assertEquals(3, result.size());
        
        // Check each group
        boolean foundEatGroup = false;
        boolean foundTanGroup = false;
        boolean foundBatGroup = false;
        
        for (List<String> group : result) {
            if (group.contains("eat")) {
                foundEatGroup = true;
                assertEquals(3, group.size());
                assertTrue(group.containsAll(Arrays.asList("eat", "tea", "ate")));
            } else if (group.contains("tan")) {
                foundTanGroup = true;
                assertEquals(2, group.size());
                assertTrue(group.containsAll(Arrays.asList("tan", "nat")));
            } else if (group.contains("bat")) {
                foundBatGroup = true;
                assertEquals(1, group.size());
                assertTrue(group.contains("bat"));
            }
        }
        
        assertTrue(foundEatGroup && foundTanGroup && foundBatGroup);
    }

    // =========================================================================
    // 23. VALID SUDOKU TEST
    // =========================================================================
    @Test
    public void testIsValidSudoku() {
        char[][] board = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        assertTrue(ArrayProblems.isValidSudoku(board));
        
        // Invalid board with duplicate in first row
        char[][] invalidBoard = {
            {'5','3','3','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        assertFalse(ArrayProblems.isValidSudoku(invalidBoard));
    }

    // =========================================================================
    // 24. ENCODE AND DECODE STRINGS TEST
    // =========================================================================
    @Test
    public void testEncodeAndDecode() {
        List<String> original = Arrays.asList("hello", "world", "code");
        String encoded = ArrayProblems.encode(original);
        List<String> decoded = ArrayProblems.decode(encoded);
        assertEquals(original, decoded);
        
        // Test with empty strings
        List<String> original2 = Arrays.asList("", "test", "");
        String encoded2 = ArrayProblems.encode(original2);
        List<String> decoded2 = ArrayProblems.decode(encoded2);
        assertEquals(original2, decoded2);
    }

    // =========================================================================
    // 25. LONGEST CONSECUTIVE SEQUENCE TEST
    // =========================================================================
    @Test
    public void testLongestConsecutive() {
        int[] nums = {100, 4, 200, 1, 3, 2};
        assertEquals(4, ArrayProblems.longestConsecutive(nums));
        
        int[] nums2 = {0, 3, 7, 2, 5, 8, 4, 6, 0, 1};
        assertEquals(9, ArrayProblems.longestConsecutive(nums2));
    }

    // =========================================================================
    // COMPREHENSIVE TEST RUN
    // =========================================================================
    @Test
    public void testAllProblems() {
        System.out.println("Running comprehensive tests for all 25 array problems...");
        
        // Run all individual tests
        testTwoSum();
        testMaxProfit();
        testContainsDuplicate();
        testContainsNearbyDuplicate();
        testProductExceptSelf();
        testMaxSubArray();
        testMaxProduct();
        testFindMin();
        testSearchInRotatedSortedArray();
        testTwoSumII();
        testThreeSum();
        testMergeSortedArray();
        testMaxArea();
        testIsAlienSorted();
        testNextPermutation();
        testRemoveDuplicates();
        testSearchRange();
        testTrap();
        testFindMedianSortedArrays();
        testIsAnagram();
        testTopKFrequent();
        testGroupAnagrams();
        testIsValidSudoku();
        testEncodeAndDecode();
        testLongestConsecutive();
        
        System.out.println("All 25 array problem tests passed!");
    }
}