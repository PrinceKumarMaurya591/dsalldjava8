package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Top K Elements (Heap Pattern)
 * 
 * Used when: Problems involving finding top/smallest/frequent K elements,
 * merging K sorted lists, K-way merge, median from data stream.
 * 
 * Core idea: Use a Min-Heap (for top K largest) or Max-Heap (for top K smallest)
 * to efficiently track K elements.
 * 
 * Key variations:
 * 1. Kth Largest/Smallest element
 * 2. Top K Frequent elements
 * 3. K Closest Points to Origin
 * 4. Kth Largest in a stream
 * 5. Sort K-sorted array (nearly sorted)
 * 6. Median from data stream
 * 
 * Time Complexity: O(n log k) typically
 * Space Complexity: O(k) or O(n)
 */
public class TopKElements {

    /**
     * Problem: Kth Largest Element in an Array
     * Find the kth largest element.
     * 
     * Approach: Min-heap of size k.
     * Time: O(n log k), Space: O(k)
     */
    public static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll(); // Remove smallest, keep k largest
            }
        }

        return minHeap.peek();
    }

    /**
     * Problem: Kth Smallest Element in an Array
     * 
     * Approach: Max-heap of size k.
     * Time: O(n log k), Space: O(k)
     */
    public static int findKthSmallest(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        for (int num : nums) {
            maxHeap.offer(num);
            if (maxHeap.size() > k) {
                maxHeap.poll(); // Remove largest, keep k smallest
            }
        }

        return maxHeap.peek();
    }

    /**
     * Problem: Top K Frequent Elements
     * Return k most frequent elements.
     * 
     * Approach: Frequency map + min-heap ordered by frequency.
     * Time: O(n log k), Space: O(n)
     */
    public static int[] topKFrequent(int[] nums, int k) {
        // Count frequencies
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        // Min-heap ordered by frequency
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
            (a, b) -> a.getValue() - b.getValue()
        );

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = minHeap.poll().getKey();
        }

        return result;
    }

    /**
     * Problem: Top K Frequent Words
     * Return k most frequent words (sorted by frequency desc, then lexicographically).
     * 
     * Time: O(n log k), Space: O(n)
     */
    public static List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }

        // Min-heap: if frequencies differ, lower frequency first
        // If same frequency, higher lexicographical order first (so we pop it)
        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
            (a, b) -> a.getValue() == b.getValue() ?
                      b.getKey().compareTo(a.getKey()) :
                      a.getValue() - b.getValue()
        );

        for (Map.Entry<String, Integer> entry : freq.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll().getKey());
        }
        Collections.reverse(result);

        return result;
    }

    /**
     * Problem: K Closest Points to Origin
     * Find k closest points to origin (0, 0).
     * 
     * Approach: Max-heap of size k, ordered by distance.
     * Time: O(n log k), Space: O(k)
     */
    public static int[][] kClosest(int[][] points, int k) {
        // Max-heap: keep k closest, pop farthest
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(
                b[0] * b[0] + b[1] * b[1],
                a[0] * a[0] + a[1] * a[1]
            )
        );

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }

        return result;
    }

    /**
     * Problem: Kth Largest Element in a Stream
     * Design class to find kth largest element in a stream.
     */
    static class KthLargest {
        private final PriorityQueue<Integer> minHeap;
        private final int k;

        public KthLargest(int k, int[] nums) {
            this.k = k;
            this.minHeap = new PriorityQueue<>();
            for (int num : nums) {
                add(num);
            }
        }

        public int add(int val) {
            minHeap.offer(val);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
            return minHeap.peek();
        }
    }

    /**
     * Problem: Sort K-Sorted Array (Nearly Sorted)
     * Sort an array where each element is at most k positions away from its sorted position.
     * 
     * Approach: Min-heap of size k+1.
     * Time: O(n log k), Space: O(k)
     */
    public static int[] sortKSortedArray(int[] arr, int k) {
        int n = arr.length;
        int[] result = new int[n];
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        // Add first k+1 elements
        for (int i = 0; i <= Math.min(k, n - 1); i++) {
            minHeap.offer(arr[i]);
        }

        int idx = 0;
        for (int i = k + 1; i < n; i++) {
            result[idx++] = minHeap.poll();
            minHeap.offer(arr[i]);
        }

        while (!minHeap.isEmpty()) {
            result[idx++] = minHeap.poll();
        }

        return result;
    }

    /**
     * Problem: Median from Data Stream
     * Find median of a stream of numbers.
     * 
     * Approach: Two heaps - max-heap for left half, min-heap for right half.
     * Time: O(log n) per add, O(1) for median
     */
    static class MedianFinder {
        private final PriorityQueue<Integer> maxHeap; // Left half (smaller numbers)
        private final PriorityQueue<Integer> minHeap; // Right half (larger numbers)

        public MedianFinder() {
            maxHeap = new PriorityQueue<>((a, b) -> b - a);
            minHeap = new PriorityQueue<>();
        }

        public void addNum(int num) {
            if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
                maxHeap.offer(num);
            } else {
                minHeap.offer(num);
            }

            // Balance heaps: maxHeap can have at most 1 more element than minHeap
            if (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            } else if (minHeap.size() > maxHeap.size()) {
                maxHeap.offer(minHeap.poll());
            }
        }

        public double findMedian() {
            if (maxHeap.size() == minHeap.size()) {
                return (maxHeap.peek() + minHeap.peek()) / 2.0;
            }
            return maxHeap.peek();
        }
    }

    /**
     * Problem: Reorganize String
     * Rearrange string so no two adjacent chars are same.
     * 
     * Approach: Max-heap by frequency, always place most frequent remaining char.
     * Time: O(n log k), Space: O(k)
     */
    public static String reorganizeString(String s) {
        // Count frequencies
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        // Max-heap by frequency
        PriorityQueue<Map.Entry<Character, Integer>> maxHeap = new PriorityQueue<>(
            (a, b) -> b.getValue() - a.getValue()
        );
        maxHeap.addAll(freq.entrySet());

        // If most frequent char > (n+1)/2, impossible
        if (maxHeap.peek().getValue() > (s.length() + 1) / 2) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        Map.Entry<Character, Integer> prev = null;

        while (!maxHeap.isEmpty()) {
            Map.Entry<Character, Integer> current = maxHeap.poll();
            result.append(current.getKey());
            current.setValue(current.getValue() - 1);

            if (prev != null && prev.getValue() > 0) {
                maxHeap.offer(prev);
            }

            prev = current;
        }

        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== TOP K ELEMENTS (HEAP) PATTERN ===");
        System.out.println();

        // 1. Kth Largest
        System.out.println("1. Kth Largest Element:");
        int[] nums1 = {3, 2, 1, 5, 6, 4};
        System.out.println("   Input: [3,2,1,5,6,4], k=2");
        System.out.println("   Output: " + findKthLargest(nums1, 2) + " (expected: 5)");
        System.out.println();

        // 2. Kth Smallest
        System.out.println("2. Kth Smallest Element:");
        System.out.println("   Input: [3,2,1,5,6,4], k=2");
        System.out.println("   Output: " + findKthSmallest(nums1, 2) + " (expected: 2)");
        System.out.println();

        // 3. Top K Frequent Elements
        System.out.println("3. Top K Frequent Elements:");
        int[] nums3 = {1, 1, 1, 2, 2, 3};
        System.out.println("   Input: [1,1,1,2,2,3], k=2");
        System.out.println("   Output: " + Arrays.toString(topKFrequent(nums3, 2)) + " (expected: [1,2])");
        System.out.println();

        // 4. Top K Frequent Words
        System.out.println("4. Top K Frequent Words:");
        String[] words = {"i", "love", "leetcode", "i", "love", "coding"};
        System.out.println("   Input: [i,love,leetcode,i,love,coding], k=2");
        System.out.println("   Output: " + topKFrequent(words, 2) + " (expected: [i,love])");
        System.out.println();

        // 5. K Closest Points to Origin
        System.out.println("5. K Closest Points to Origin:");
        int[][] points = {{1, 3}, {-2, 2}};
        System.out.println("   Input: [[1,3],[-2,2]], k=1");
        System.out.println("   Output: " + Arrays.deepToString(kClosest(points, 1)) + " (expected: [[-2,2]])");
        System.out.println();

        // 6. Kth Largest in a Stream
        System.out.println("6. Kth Largest in a Stream:");
        KthLargest kthLargest = new KthLargest(3, new int[]{4, 5, 8, 2});
        System.out.println("   add(3): " + kthLargest.add(3) + " (expected: 4)");
        System.out.println("   add(5): " + kthLargest.add(5) + " (expected: 5)");
        System.out.println("   add(10): " + kthLargest.add(10) + " (expected: 5)");
        System.out.println("   add(9): " + kthLargest.add(9) + " (expected: 8)");
        System.out.println("   add(4): " + kthLargest.add(4) + " (expected: 8)");
        System.out.println();

        // 7. Sort K-Sorted Array
        System.out.println("7. Sort K-Sorted Array:");
        int[] nums7 = {3, 2, 1, 5, 6, 4};
        System.out.println("   Input: [3,2,1,5,6,4], k=2");
        System.out.println("   Output: " + Arrays.toString(sortKSortedArray(nums7, 2)) + " (expected: [1,2,3,4,5,6])");
        System.out.println();

        // 8. Median from Data Stream
        System.out.println("8. Median from Data Stream:");
        MedianFinder mf = new MedianFinder();
        mf.addNum(1);
        mf.addNum(2);
        System.out.println("   After [1,2]: median=" + mf.findMedian() + " (expected: 1.5)");
        mf.addNum(3);
        System.out.println("   After [1,2,3]: median=" + mf.findMedian() + " (expected: 2.0)");
        System.out.println();

        // 9. Reorganize String
        System.out.println("9. Reorganize String:");
        System.out.println("   Input: \"aab\"");
        System.out.println("   Output: \"" + reorganizeString("aab") + "\" (expected: \"aba\")");
        System.out.println("   Input: \"aaab\"");
        System.out.println("   Output: \"" + reorganizeString("aaab") + "\" (expected: \"\")");
    }
}
