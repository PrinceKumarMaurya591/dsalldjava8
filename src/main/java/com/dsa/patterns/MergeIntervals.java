package com.dsa.patterns;

import java.util.*;

/**
 * Pattern: Merge Intervals
 * 
 * Used when: Problems involving overlapping intervals, meeting rooms,
 * interval intersections, free time, insert interval.
 * 
 * Key variations:
 * 1. Merge overlapping intervals
 * 2. Insert new interval into sorted intervals
 * 3. Find intersection of two interval lists
 * 4. Find non-overlapping intervals (meeting rooms)
 * 5. Minimum meeting rooms required
 * 
 * Core approach: Sort by start time, then compare end time with next start time.
 * Time Complexity: O(n log n) due to sorting
 * Space Complexity: O(n) for output
 */
public class MergeIntervals {

    /**
     * Problem: Merge Intervals
     * Merge all overlapping intervals.
     * 
     * Approach: Sort by start, merge if current end >= next start.
     * Time: O(n log n), Space: O(n)
     */
    public static int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            if (current[1] >= intervals[i][0]) {
                // Overlapping: merge by taking max end
                current[1] = Math.max(current[1], intervals[i][1]);
            } else {
                result.add(current);
                current = intervals[i];
            }
        }
        result.add(current);

        return result.toArray(new int[result.size()][]);
    }

    /**
     * Problem: Insert Interval
     * Insert a new interval into sorted non-overlapping intervals.
     * 
     * Approach: Three phases - add non-overlapping before, merge overlapping, add after.
     * Time: O(n), Space: O(n)
     */
    public static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;

        // Add all intervals ending before newInterval starts
        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Merge overlapping intervals
        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);

        // Add remaining intervals
        while (i < intervals.length) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }

    /**
     * Problem: Non-overlapping Intervals
     * Find minimum intervals to remove to make rest non-overlapping.
     * 
     * Approach: Sort by end, greedily keep intervals with earliest end.
     * Time: O(n log n), Space: O(1)
     */
    public static int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length == 0) return 0;

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int count = 0;
        int end = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < end) {
                count++; // Overlapping, remove this interval
            } else {
                end = intervals[i][1];
            }
        }

        return count;
    }

    /**
     * Problem: Meeting Rooms
     * Determine if a person can attend all meetings.
     * 
     * Approach: Sort by start, check if any meeting overlaps with next.
     * Time: O(n log n), Space: O(1)
     */
    public static boolean canAttendMeetings(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i - 1][1] > intervals[i][0]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Problem: Minimum Meeting Rooms
     * Find minimum number of meeting rooms required.
     * 
     * Approach: Separate start and end times, sort both, use two pointers.
     * Time: O(n log n), Space: O(n)
     */
    public static int minMeetingRooms(int[][] intervals) {
        if (intervals.length == 0) return 0;

        int[] start = new int[intervals.length];
        int[] end = new int[intervals.length];

        for (int i = 0; i < intervals.length; i++) {
            start[i] = intervals[i][0];
            end[i] = intervals[i][1];
        }

        Arrays.sort(start);
        Arrays.sort(end);

        int rooms = 0;
        int endIdx = 0;

        for (int i = 0; i < start.length; i++) {
            if (start[i] < end[endIdx]) {
                rooms++; // Need a new room
            } else {
                endIdx++; // Free up a room
            }
        }

        return rooms;
    }

    /**
     * Problem: Interval List Intersections
     * Find intersection of two lists of intervals.
     * 
     * Approach: Two pointers, check overlap, advance the one ending earlier.
     * Time: O(m + n), Space: O(min(m, n))
     */
    public static int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        List<int[]> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < firstList.length && j < secondList.length) {
            int start = Math.max(firstList[i][0], secondList[j][0]);
            int end = Math.min(firstList[i][1], secondList[j][1]);

            if (start <= end) {
                result.add(new int[]{start, end});
            }

            // Advance the interval that ends earlier
            if (firstList[i][1] < secondList[j][1]) {
                i++;
            } else {
                j++;
            }
        }

        return result.toArray(new int[result.size()][]);
    }

    /**
     * Problem: Employee Free Time
     * Find common free time intervals across all employees.
     * 
     * Approach: Flatten all intervals, sort by start, merge, find gaps.
     * Time: O(n log n), Space: O(n)
     */
    public static List<int[]> employeeFreeTime(int[][][] schedule) {
        // Flatten all intervals
        List<int[]> allIntervals = new ArrayList<>();
        for (int[][] employee : schedule) {
            for (int[] interval : employee) {
                allIntervals.add(interval);
            }
        }

        // Sort by start time
        allIntervals.sort((a, b) -> Integer.compare(a[0], b[0]));

        // Merge intervals
        List<int[]> merged = new ArrayList<>();
        int[] current = allIntervals.get(0);

        for (int i = 1; i < allIntervals.size(); i++) {
            if (current[1] >= allIntervals.get(i)[0]) {
                current[1] = Math.max(current[1], allIntervals.get(i)[1]);
            } else {
                merged.add(current);
                current = allIntervals.get(i);
            }
        }
        merged.add(current);

        // Find gaps (free time)
        List<int[]> freeTime = new ArrayList<>();
        for (int i = 1; i < merged.size(); i++) {
            freeTime.add(new int[]{merged.get(i - 1)[1], merged.get(i)[0]});
        }

        return freeTime;
    }

    public static void main(String[] args) {
        System.out.println("=== MERGE INTERVALS PATTERN ===");
        System.out.println();

        // 1. Merge Intervals
        System.out.println("1. Merge Intervals:");
        int[][] intervals1 = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        System.out.println("   Input: [[1,3],[2,6],[8,10],[15,18]]");
        System.out.println("   Output: " + Arrays.deepToString(merge(intervals1)) + " (expected: [[1,6],[8,10],[15,18]])");
        System.out.println();

        // 2. Insert Interval
        System.out.println("2. Insert Interval:");
        int[][] intervals2 = {{1, 3}, {6, 9}};
        System.out.println("   Input: [[1,3],[6,9]], new=[2,5]");
        System.out.println("   Output: " + Arrays.deepToString(insert(intervals2, new int[]{2, 5})) + " (expected: [[1,5],[6,9]])");
        System.out.println();

        // 3. Non-overlapping Intervals
        System.out.println("3. Non-overlapping Intervals:");
        int[][] intervals3 = {{1, 2}, {2, 3}, {3, 4}, {1, 3}};
        System.out.println("   Input: [[1,2],[2,3],[3,4],[1,3]]");
        System.out.println("   Min to remove: " + eraseOverlapIntervals(intervals3) + " (expected: 1)");
        System.out.println();

        // 4. Meeting Rooms
        System.out.println("4. Meeting Rooms:");
        int[][] intervals4 = {{0, 30}, {5, 10}, {15, 20}};
        System.out.println("   Input: [[0,30],[5,10],[15,20]]");
        System.out.println("   Can attend: " + canAttendMeetings(intervals4) + " (expected: false)");
        int[][] intervals4b = {{7, 10}, {2, 4}};
        System.out.println("   Input: [[7,10],[2,4]]");
        System.out.println("   Can attend: " + canAttendMeetings(intervals4b) + " (expected: true)");
        System.out.println();

        // 5. Minimum Meeting Rooms
        System.out.println("5. Minimum Meeting Rooms:");
        int[][] intervals5 = {{0, 30}, {5, 10}, {15, 20}};
        System.out.println("   Input: [[0,30],[5,10],[15,20]]");
        System.out.println("   Rooms needed: " + minMeetingRooms(intervals5) + " (expected: 2)");
        System.out.println();

        // 6. Interval List Intersections
        System.out.println("6. Interval List Intersections:");
        int[][] first = {{0, 2}, {5, 10}, {13, 23}, {24, 25}};
        int[][] second = {{1, 5}, {8, 12}, {15, 24}, {25, 26}};
        System.out.println("   First: [[0,2],[5,10],[13,23],[24,25]]");
        System.out.println("   Second: [[1,5],[8,12],[15,24],[25,26]]");
        System.out.println("   Intersection: " + Arrays.deepToString(intervalIntersection(first, second)));
        System.out.println("   Expected: [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]]");
        System.out.println();

        // 7. Employee Free Time
        System.out.println("7. Employee Free Time:");
        int[][][] schedule = {
            {{1, 2}, {5, 6}},
            {{1, 3}},
            {{4, 10}}
        };
        System.out.println("   Schedule: [[[1,2],[5,6]], [[1,3]], [[4,10]]]");
        List<int[]> free = employeeFreeTime(schedule);
        System.out.print("   Free time: ");
        for (int[] f : free) System.out.print(Arrays.toString(f) + " ");
        System.out.println("(expected: [3,4])");
    }
}
