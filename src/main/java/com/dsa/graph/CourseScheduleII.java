package com.dsa.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Problem: Course Schedule II
// Link: https://leetcode.com/problems/course-schedule-ii/
//
// There are a total of numCourses courses you have to take, labeled from 0 to numCourses-1.
// You are given an array prerequisites where prerequisites[i] = [a_i, b_i] indicates
// that you must take course b_i first if you want to take course a_i.
//
// Return the ordering of courses you should take to finish all courses.
// If it's impossible, return an empty array.
//
// Approach: Topological Sort (Kahn's Algorithm - BFS)
// 1. Build adjacency list and indegree array from prerequisites.
// 2. Add all courses with indegree 0 to the queue.
// 3. Process each course, adding to result and reducing indegree of neighbors.
// 4. If all courses are processed, return the order; otherwise return empty array.
//
// Time Complexity: O(V + E) where V = numCourses, E = prerequisites.length
// Space Complexity: O(V + E)

public class CourseScheduleII {

    public static void main(String[] args) {
        // Test case 1
        int numCourses1 = 4;
        int[][] prerequisites1 = {{1, 0}, {2, 0}, {3, 1}, {3, 2}};
        int[] order1 = findOrder(numCourses1, prerequisites1);
        System.out.print("Course order (4 courses): ");
        for (int course : order1) {
            System.out.print(course + " ");
        }
        System.out.println();
        // Expected: [0, 1, 2, 3] or [0, 2, 1, 3]

        // Test case 2: Impossible (cycle)
        int numCourses2 = 2;
        int[][] prerequisites2 = {{1, 0}, {0, 1}};
        int[] order2 = findOrder(numCourses2, prerequisites2);
        System.out.print("Course order (2 courses with cycle): ");
        for (int course : order2) {
            System.out.print(course + " ");
        }
        System.out.println(order2.length == 0 ? "(empty - impossible)" : "");
        // Expected: []

        // Test case 3: No prerequisites
        int numCourses3 = 3;
        int[][] prerequisites3 = {};
        int[] order3 = findOrder(numCourses3, prerequisites3);
        System.out.print("Course order (3 courses, no prereqs): ");
        for (int course : order3) {
            System.out.print(course + " ");
        }
        System.out.println();
        // Expected: [0, 1, 2] (any order)
    }

    public static int[] findOrder(int numCourses, int[][] prerequisites) {
        // Build adjacency list and indegree array
        List<List<Integer>> adj = new ArrayList<>();
        int[] indegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prerequisite = prereq[1];
            adj.get(prerequisite).add(course);
            indegree[course]++;
        }

        // Add all courses with no prerequisites to the queue
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        // Process courses in topological order
        int[] order = new int[numCourses];
        int index = 0;

        while (!queue.isEmpty()) {
            int course = queue.poll();
            order[index++] = course;

            for (int neighbor : adj.get(course)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // If we processed all courses, return the order
        if (index == numCourses) {
            return order;
        }

        // Cycle detected, impossible to finish all courses
        return new int[0];
    }
}
