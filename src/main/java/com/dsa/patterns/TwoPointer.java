package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Two Pointer
 * 
 * Used when: Problems involving sorted arrays, finding pairs/triplets,
 * comparing elements from both ends, in-place array manipulation.
 * 
 * Key variations:
 * 1. Opposite direction (left/right from ends)
 * 2. Same direction (both from start, different speeds)
 * 3. Multiple pointers (3Sum, 4Sum)
 * 
 * Time Complexity: O(n) typically, O(n²) for k-sum variants
 * Space Complexity: O(1) typically
 */
public class TwoPointer {

    /**
     * Problem: Two Sum II - Input Array Is Sorted
     * Find two numbers that add up to target.
     * 
     * Approach: Left and right pointers from ends
     * Time: O(n), Space: O(1)
     */
    public static int[] twoSum(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[]{left + 1, right + 1}; // 1-indexed
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return new int[]{-1, -1};
    }

    /**
     * Problem: Three Sum
     * Find all unique triplets that sum to zero.
     * 
     * Approach: Sort + fix one element + two pointer for the other two
     * Time: O(n²), Space: O(1) excluding output
     */
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates for i
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int left = i + 1, right = nums.length - 1;
            int target = -nums[i];

            while (left < right) {
                int sum = nums[left] + nums[right];
                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    // Skip duplicates
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;

                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }

    /**
     * Problem: 4Sum
     * Find all unique quadruplets that sum to target.
     * 
     * Approach: Sort + nested loops + two pointer
     * Time: O(n³), Space: O(1) excluding output
     */
    public static List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            for (int j = i + 1; j < nums.length - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;

                int left = j + 1, right = nums.length - 1;

                while (left < right) {
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];
                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));

                        while (left < right && nums[left] == nums[left + 1]) left++;
                        while (left < right && nums[right] == nums[right - 1]) right--;

                        left++;
                        right--;
                    } else if (sum < target) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Problem: Container With Most Water
     * Find two lines that together with x-axis form a container holding max water.
     * 
     * Approach: Two pointers from ends, move the shorter line inward
     * Time: O(n), Space: O(1)
     */
    public static int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int maxWater = 0;

        while (left < right) {
            int width = right - left;
            int h = Math.min(height[left], height[right]);
            maxWater = Math.max(maxWater, width * h);

            // Move the pointer with the smaller height
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxWater;
    }

    /**
     * Problem: Trapping Rain Water
     * Calculate how much water can be trapped between bars.
     * 
     * Approach: Two pointers tracking left max and right max
     * Time: O(n), Space: O(1)
     */
    public static int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }

        return water;
    }

    /**
     * Problem: Move Zeroes
     * Move all zeros to the end while maintaining relative order of non-zero elements.
     * 
     * Approach: Same-direction two pointers (slow/fast)
     * Time: O(n), Space: O(1)
     */
    public static void moveZeroes(int[] nums) {
        int nonZeroIndex = 0;

        // Move all non-zero elements to the front
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[nonZeroIndex++] = nums[i];
            }
        }

        // Fill remaining positions with zeros
        while (nonZeroIndex < nums.length) {
            nums[nonZeroIndex++] = 0;
        }
    }

    /**
     * Problem: Sort Colors (Dutch National Flag)
     * Sort array containing 0, 1, 2 in-place.
     * 
     * Approach: Three pointers (low, mid, high)
     * Time: O(n), Space: O(1)
     */
    public static void sortColors(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;

        while (mid <= high) {
            switch (nums[mid]) {
                case 0:
                    // Swap with low pointer
                    int temp0 = nums[low];
                    nums[low] = nums[mid];
                    nums[mid] = temp0;
                    low++;
                    mid++;
                    break;
                case 1:
                    mid++;
                    break;
                case 2:
                    // Swap with high pointer
                    int temp2 = nums[high];
                    nums[high] = nums[mid];
                    nums[mid] = temp2;
                    high--;
                    break;
            }
        }
    }

    /**
     * Problem: Valid Palindrome
     * Check if a string is a palindrome considering only alphanumeric chars.
     * 
     * Approach: Two pointers from ends, skip non-alphanumeric
     * Time: O(n), Space: O(1)
     */
    public static boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            // Skip non-alphanumeric characters
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    /**
     * Problem: Remove Duplicates from Sorted Array
     * Remove duplicates in-place, return new length.
     * 
     * Approach: Same-direction two pointers
     * Time: O(n), Space: O(1)
     */
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;

        int insertPos = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) {
                nums[insertPos++] = nums[i];
            }
        }

        return insertPos;
    }

    /**
     * Problem: Remove Element
     * Remove all occurrences of val in-place, return new length.
     * 
     * Approach: Same-direction two pointers
     * Time: O(n), Space: O(1)
     */
    public static int removeElement(int[] nums, int val) {
        int insertPos = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[insertPos++] = nums[i];
            }
        }
        return insertPos;
    }

    /**
     * Problem: Squares of a Sorted Array
     * Return sorted array of squares of each number.
     * 
     * Approach: Two pointers from ends, compare absolute values
     * Time: O(n), Space: O(n)
     */
    public static int[] sortedSquares(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0, right = n - 1;

        for (int i = n - 1; i >= 0; i--) {
            if (Math.abs(nums[left]) > Math.abs(nums[right])) {
                result[i] = nums[left] * nums[left];
                left++;
            } else {
                result[i] = nums[right] * nums[right];
                right--;
            }
        }

        return result;
    }

    /**
     * Problem: 3Sum Closest
     * Find triplet sum closest to target.
     * 
     * Approach: Sort + two pointer
     * Time: O(n²), Space: O(1)
     */
    public static int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int closestSum = nums[0] + nums[1] + nums[2];

        for (int i = 0; i < nums.length - 2; i++) {
            int left = i + 1, right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (Math.abs(sum - target) < Math.abs(closestSum - target)) {
                    closestSum = sum;
                }

                if (sum < target) {
                    left++;
                } else if (sum > target) {
                    right--;
                } else {
                    return target; // Exact match
                }
            }
        }

        return closestSum;
    }

    public static void main(String[] args) {
        System.out.println("=== TWO POINTER PATTERN ===");
        System.out.println();

        // 1. Two Sum II
        System.out.println("1. Two Sum II:");
        int[] nums1 = {2, 7, 11, 15};
        System.out.println("   Input: numbers=[2,7,11,15], target=9");
        System.out.println("   Output: " + Arrays.toString(twoSum(nums1, 9)) + " (expected: [1,2])");
        System.out.println();

        // 2. Three Sum
        System.out.println("2. Three Sum:");
        int[] nums2 = {-1, 0, 1, 2, -1, -4};
        System.out.println("   Input: [-1,0,1,2,-1,-4]");
        System.out.println("   Output: " + threeSum(nums2) + " (expected: [[-1,-1,2],[-1,0,1]])");
        System.out.println();

        // 3. 4Sum
        System.out.println("3. 4Sum:");
        int[] nums3 = {1, 0, -1, 0, -2, 2};
        System.out.println("   Input: [1,0,-1,0,-2,2], target=0");
        System.out.println("   Output: " + fourSum(nums3, 0) + " (expected: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]])");
        System.out.println();

        // 4. Container With Most Water
        System.out.println("4. Container With Most Water:");
        int[] nums4 = {1, 8, 6, 2, 5, 4, 8, 3, 7};
        System.out.println("   Input: [1,8,6,2,5,4,8,3,7]");
        System.out.println("   Output: " + maxArea(nums4) + " (expected: 49)");
        System.out.println();

        // 5. Trapping Rain Water
        System.out.println("5. Trapping Rain Water:");
        int[] nums5 = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        System.out.println("   Input: [0,1,0,2,1,0,1,3,2,1,2,1]");
        System.out.println("   Output: " + trap(nums5) + " (expected: 6)");
        System.out.println();

        // 6. Move Zeroes
        System.out.println("6. Move Zeroes:");
        int[] nums6 = {0, 1, 0, 3, 12};
        System.out.print("   Input: [0,1,0,3,12] -> ");
        moveZeroes(nums6);
        System.out.println(Arrays.toString(nums6) + " (expected: [1,3,12,0,0])");
        System.out.println();

        // 7. Sort Colors
        System.out.println("7. Sort Colors:");
        int[] nums7 = {2, 0, 2, 1, 1, 0};
        System.out.print("   Input: [2,0,2,1,1,0] -> ");
        sortColors(nums7);
        System.out.println(Arrays.toString(nums7) + " (expected: [0,0,1,1,2,2])");
        System.out.println();

        // 8. Valid Palindrome
        System.out.println("8. Valid Palindrome:");
        System.out.println("   Input: \"A man, a plan, a canal: Panama\"");
        System.out.println("   Output: " + isPalindrome("A man, a plan, a canal: Panama") + " (expected: true)");
        System.out.println("   Input: \"race a car\"");
        System.out.println("   Output: " + isPalindrome("race a car") + " (expected: false)");
        System.out.println();

        // 9. Remove Duplicates
        System.out.println("9. Remove Duplicates:");
        int[] nums9 = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        System.out.println("   Input: [0,0,1,1,1,2,2,3,3,4]");
        System.out.println("   Output length: " + removeDuplicates(nums9) + " (expected: 5)");
        System.out.println();

        // 10. Squares of Sorted Array
        System.out.println("10. Squares of Sorted Array:");
        int[] nums10 = {-4, -1, 0, 3, 10};
        System.out.println("    Input: [-4,-1,0,3,10]");
        System.out.println("    Output: " + Arrays.toString(sortedSquares(nums10)) + " (expected: [0,1,9,16,100])");
        System.out.println();

        // 11. 3Sum Closest
        System.out.println("11. 3Sum Closest:");
        int[] nums11 = {-1, 2, 1, -4};
        System.out.println("    Input: [-1,2,1,-4], target=1");
        System.out.println("    Output: " + threeSumClosest(nums11, 1) + " (expected: 2)");
    }
}
