package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Binary Search
 * 
 * Used when: Problems involving sorted arrays, searching in rotated arrays,
 * finding boundaries (first/last occurrence), finding peak element,
 * searching in 2D matrix, finding square root.
 * 
 * Key variations:
 * 1. Classic binary search
 * 2. First/Last occurrence (lower/upper bound)
 * 3. Search in rotated sorted array
 * 4. Find peak element
 * 5. Search in 2D matrix
 * 6. Find minimum in rotated sorted array
 * 7. Find square root
 * 8. Search in unknown size array
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */
public class BinarySearch {

    /**
     * Problem: Classic Binary Search
     * Find target in sorted array. Return index or -1.
     * 
     * Time: O(log n), Space: O(1)
     */
    public static int binarySearch(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    /**
     * Problem: First and Last Position of Element in Sorted Array
     * Find the first and last occurrence of target.
     * 
     * Approach: Two binary searches - one for first, one for last.
     * Time: O(log n), Space: O(1)
     */
    public static int[] searchRange(int[] nums, int target) {
        return new int[]{findFirst(nums, target), findLast(nums, target)};
    }

    private static int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] >= target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
            if (nums[mid] == target) result = mid;
        }

        return result;
    }

    private static int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
            if (nums[mid] == target) result = mid;
        }

        return result;
    }

    /**
     * Problem: Search in Rotated Sorted Array
     * Search in a rotated sorted array with distinct values.
     * 
     * Approach: Find which half is sorted, check if target is in that half.
     * Time: O(log n), Space: O(1)
     */
    public static int searchRotated(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return mid;

            // Left half is sorted
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            // Right half is sorted
            else {
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return -1;
    }

    /**
     * Problem: Search in Rotated Sorted Array II (with duplicates)
     * 
     * Approach: Same as above but handle duplicates by shrinking bounds.
     * Time: O(log n) average, O(n) worst case, Space: O(1)
     */
    public static boolean searchRotatedWithDuplicates(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) return true;

            // Handle duplicates: can't determine which half is sorted
            if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                left++;
                right--;
            } else if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return false;
    }

    /**
     * Problem: Find Minimum in Rotated Sorted Array
     * Find minimum element in rotated sorted array.
     * 
     * Approach: Binary search comparing mid with right.
     * Time: O(log n), Space: O(1)
     */
    public static int findMin(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1; // Min is in right half
            } else {
                right = mid; // Min is in left half (including mid)
            }
        }

        return nums[left];
    }

    /**
     * Problem: Find Peak Element
     * Find a peak element (greater than its neighbors).
     * 
     * Approach: Binary search - if mid < mid+1, peak is on right; else on left.
     * Time: O(log n), Space: O(1)
     */
    public static int findPeakElement(int[] nums) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < nums[mid + 1]) {
                left = mid + 1; // Peak is on the right
            } else {
                right = mid; // Peak is on the left (including mid)
            }
        }

        return left;
    }

    /**
     * Problem: Search a 2D Matrix
     * Search in a matrix where each row is sorted and first element of each row
     * is greater than last element of previous row.
     * 
     * Approach: Treat as flattened sorted array, binary search on indices.
     * Time: O(log(m*n)), Space: O(1)
     */
    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix.length == 0) return false;

        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int row = mid / n;
            int col = mid % n;

            if (matrix[row][col] == target) {
                return true;
            } else if (matrix[row][col] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    /**
     * Problem: Search a 2D Matrix II
     * Search in a matrix where each row and column is sorted separately.
     * 
     * Approach: Start from top-right corner, move left/down.
     * Time: O(m + n), Space: O(1)
     */
    public static boolean searchMatrixII(int[][] matrix, int target) {
        if (matrix.length == 0) return false;

        int row = 0, col = matrix[0].length - 1;

        while (row < matrix.length && col >= 0) {
            if (matrix[row][col] == target) {
                return true;
            } else if (matrix[row][col] < target) {
                row++; // Move down
            } else {
                col--; // Move left
            }
        }

        return false;
    }

    /**
     * Problem: Find Square Root
     * Find square root of x rounded down to nearest integer.
     * 
     * Approach: Binary search between 0 and x.
     * Time: O(log x), Space: O(1)
     */
    public static int mySqrt(int x) {
        if (x < 2) return x;

        int left = 1, right = x / 2;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            long square = (long) mid * mid;

            if (square == x) {
                return mid;
            } else if (square < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return right;
    }

    /**
     * Problem: Find Smallest Letter Greater Than Target
     * Find smallest character in array that is larger than target.
     * 
     * Approach: Binary search for upper bound.
     * Time: O(log n), Space: O(1)
     */
    public static char nextGreatestLetter(char[] letters, char target) {
        int left = 0, right = letters.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (letters[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return letters[left % letters.length];
    }

    /**
     * Problem: Time Based Key-Value Store
     * Store multiple values per key with timestamps, retrieve by timestamp.
     */
    static class TimeMap {
        private Map<String, List<Pair>> map;

        static class Pair {
            String value;
            int timestamp;
            Pair(String v, int t) { value = v; timestamp = t; }
        }

        public TimeMap() {
            map = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            map.computeIfAbsent(key, k -> new ArrayList<>())
               .add(new Pair(value, timestamp));
        }

        public String get(String key, int timestamp) {
            List<Pair> list = map.get(key);
            if (list == null) return "";

            // Binary search for largest timestamp <= target
            int left = 0, right = list.size() - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (list.get(mid).timestamp <= timestamp) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return right >= 0 ? list.get(right).value : "";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== BINARY SEARCH PATTERN ===");
        System.out.println();

        // 1. Classic Binary Search
        System.out.println("1. Classic Binary Search:");
        int[] nums1 = {-1, 0, 3, 5, 9, 12};
        System.out.println("   Input: [-1,0,3,5,9,12], target=9");
        System.out.println("   Output: " + binarySearch(nums1, 9) + " (expected: 4)");
        System.out.println("   Input: target=2");
        System.out.println("   Output: " + binarySearch(nums1, 2) + " (expected: -1)");
        System.out.println();

        // 2. First and Last Position
        System.out.println("2. First and Last Position:");
        int[] nums2 = {5, 7, 7, 8, 8, 10};
        System.out.println("   Input: [5,7,7,8,8,10], target=8");
        System.out.println("   Output: " + Arrays.toString(searchRange(nums2, 8)) + " (expected: [3,4])");
        System.out.println();

        // 3. Search in Rotated Sorted Array
        System.out.println("3. Search in Rotated Sorted Array:");
        int[] nums3 = {4, 5, 6, 7, 0, 1, 2};
        System.out.println("   Input: [4,5,6,7,0,1,2], target=0");
        System.out.println("   Output: " + searchRotated(nums3, 0) + " (expected: 4)");
        System.out.println("   Input: target=3");
        System.out.println("   Output: " + searchRotated(nums3, 3) + " (expected: -1)");
        System.out.println();

        // 4. Find Minimum in Rotated Sorted Array
        System.out.println("4. Find Minimum in Rotated Sorted Array:");
        int[] nums4 = {3, 4, 5, 1, 2};
        System.out.println("   Input: [3,4,5,1,2]");
        System.out.println("   Output: " + findMin(nums4) + " (expected: 1)");
        System.out.println();

        // 5. Find Peak Element
        System.out.println("5. Find Peak Element:");
        int[] nums5 = {1, 2, 3, 1};
        System.out.println("   Input: [1,2,3,1]");
        System.out.println("   Output: " + findPeakElement(nums5) + " (expected: 2)");
        System.out.println();

        // 6. Search 2D Matrix
        System.out.println("6. Search 2D Matrix:");
        int[][] matrix = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}};
        System.out.println("   Input: matrix=[[1,3,5,7],[10,11,16,20],[23,30,34,60]], target=3");
        System.out.println("   Output: " + searchMatrix(matrix, 3) + " (expected: true)");
        System.out.println("   Input: target=13");
        System.out.println("   Output: " + searchMatrix(matrix, 13) + " (expected: false)");
        System.out.println();

        // 7. Search 2D Matrix II
        System.out.println("7. Search 2D Matrix II:");
        int[][] matrix2 = {
            {1, 4, 7, 11, 15},
            {2, 5, 8, 12, 19},
            {3, 6, 9, 16, 22},
            {10, 13, 14, 17, 24},
            {18, 21, 23, 26, 30}
        };
        System.out.println("   Input: target=5");
        System.out.println("   Output: " + searchMatrixII(matrix2, 5) + " (expected: true)");
        System.out.println("   Input: target=20");
        System.out.println("   Output: " + searchMatrixII(matrix2, 20) + " (expected: false)");
        System.out.println();

        // 8. Square Root
        System.out.println("8. Square Root:");
        System.out.println("   Input: 8 -> " + mySqrt(8) + " (expected: 2)");
        System.out.println("   Input: 16 -> " + mySqrt(16) + " (expected: 4)");
        System.out.println();

        // 9. Next Greatest Letter
        System.out.println("9. Next Greatest Letter:");
        char[] letters = {'c', 'f', 'j'};
        System.out.println("   Input: letters=[c,f,j], target='a'");
        System.out.println("   Output: " + nextGreatestLetter(letters, 'a') + " (expected: c)");
        System.out.println("   Input: target='c'");
        System.out.println("   Output: " + nextGreatestLetter(letters, 'c') + " (expected: f)");
        System.out.println("   Input: target='z'");
        System.out.println("   Output: " + nextGreatestLetter(letters, 'z') + " (expected: c)");
        System.out.println();

        // 10. Time Based Key-Value Store
        System.out.println("10. Time Based Key-Value Store:");
        TimeMap timeMap = new TimeMap();
        timeMap.set("foo", "bar", 1);
        System.out.println("    get(foo, 1): " + timeMap.get("foo", 1) + " (expected: bar)");
        System.out.println("    get(foo, 3): " + timeMap.get("foo", 3) + " (expected: bar)");
        timeMap.set("foo", "bar2", 4);
        System.out.println("    get(foo, 4): " + timeMap.get("foo", 4) + " (expected: bar2)");
        System.out.println("    get(foo, 5): " + timeMap.get("foo", 5) + " (expected: bar2)");
    }
}
