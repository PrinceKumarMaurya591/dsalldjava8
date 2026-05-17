package com.dsa.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

// Problem: Network Delay Time
// Link: https://leetcode.com/problems/network-delay-time/
//
// You are given a network of n nodes, labeled from 1 to n. You are also given times,
// a list of travel times as directed edges times[i] = (u_i, v_i, w_i), where u_i is
// the source node, v_i is the target node, and w_i is the time it takes for a signal
// to travel from source to target.
//
// We will send a signal from a given node k. Return the minimum time it takes for
// all the n nodes to receive the signal. If it is impossible for all nodes to receive
// the signal, return -1.
//
// Approach: Dijkstra's Algorithm (Shortest Path from single source)
// 1. Build adjacency list from the times array.
// 2. Use a min-heap (PriorityQueue) to always process the node with smallest distance.
// 3. Track the maximum distance among all reachable nodes.
// 4. If all nodes are reachable, return max distance; otherwise return -1.
//
// Time Complexity: O((V + E) log V) using priority queue
// Space Complexity: O(V + E)

public class NetworkDelayTime {

    public static void main(String[] args) {
        // Test case 1
        int[][] times1 = {{2, 1, 1}, {2, 3, 1}, {3, 4, 1}};
        int n1 = 4;
        int k1 = 2;
        System.out.println("Network delay time: " + networkDelayTime(times1, n1, k1));
        // Expected: 2

        // Test case 2: Impossible (node 4 unreachable)
        int[][] times2 = {{1, 2, 1}};
        int n2 = 4;
        int k2 = 1;
        System.out.println("Network delay time (unreachable): "
                + networkDelayTime(times2, n2, k2));
        // Expected: -1

        // Test case 3: Single node
        int[][] times3 = {};
        int n3 = 1;
        int k3 = 1;
        System.out.println("Network delay time (single node): "
                + networkDelayTime(times3, n3, k3));
        // Expected: 0
    }

    public static int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list (1-indexed nodes)
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] time : times) {
            int u = time[0];
            int v = time[1];
            int w = time[2];
            adj.get(u).add(new int[]{v, w});
        }

        // Distance array, initialize with infinity
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        // Min-heap: [node, distance]
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[]{k, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0];
            int time = current[1];

            // Skip if we already found a better path
            if (time > dist[node]) continue;

            for (int[] neighbor : adj.get(node)) {
                int nextNode = neighbor[0];
                int travelTime = neighbor[1];
                int newTime = time + travelTime;

                if (newTime < dist[nextNode]) {
                    dist[nextNode] = newTime;
                    pq.offer(new int[]{nextNode, newTime});
                }
            }
        }

        // Find the maximum distance among all reachable nodes
        int maxTime = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                return -1; // Unreachable node
            }
            maxTime = Math.max(maxTime, dist[i]);
        }

        return maxTime;
    }
}
