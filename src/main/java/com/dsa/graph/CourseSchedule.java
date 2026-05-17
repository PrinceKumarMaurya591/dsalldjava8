package com.dsa.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Problem: Course Schedule
// Link: https://leetcode.com/problems/course-schedule/
//
// There are a total of numCourses courses you have to take, labeled from 0 to numCourses-1.
// You are given an array prerequisites where prerequisites[i] = [a_i, b_i] indicates
// that you must take course b_i first if you want to take course a_i.
//
// Return true if you can finish all courses, otherwise return false.
//
// Approach: Topological Sort (Kahn's Algorithm - BFS)
// 1. Build adjacency list and indegree array from prerequisites.
// 2. Add all courses with indegree 0 to the queue.
// 3. Process each course, reducing indegree of its neighbors.
// 4. If all courses are processed (count == numCourses), return true.
//
// Time Complexity: O(V + E) where V = numCourses, E = prerequisites.length
// Space Complexity: O(V + E)

public class CourseSchedule {

    public static void main(String[] args) {
        // Test case 1: Possible
        int numCourses1 = 2;
        int[][] prerequisites1 = {{1, 0}};
        System.out.println("Can finish (2 courses, [[1,0]]): "
                + canFinish(numCourses1, prerequisites1));
        // Expected: true

        // Test case 2: Impossible (cycle)
        int numCourses2 = 2;
        int[][] prerequisites2 = {{1, 0}, {0, 1}};
        System.out.println("Can finish (2 courses, [[1,0],[0,1]]): "
                + canFinish(numCourses2, prerequisites2));
        // Expected: false

        // Test case 3: More complex
        int numCourses3 = 4;
        int[][] prerequisites3 = {{1, 0}, {2, 1}, {3, 2}};
        System.out.println("Can finish (4 courses, [[1,0],[2,1],[3,2]]): "
                + canFinish(numCourses3, prerequisites3));
        // Expected: true
    }

    public static boolean canFinish(int numCourses, int[][] prerequisites) {
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
        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processed++;

            for (int neighbor : adj.get(course)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // If we processed all courses, no cycle exists
        return processed == numCourses;
    }
}
