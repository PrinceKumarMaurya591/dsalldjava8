package com.dsa.graph;

import java.util.Arrays;

// Problem: Cheapest Flights Within K Stops
// Link: https://leetcode.com/problems/cheapest-flights-within-k-stops/
//
// There are n cities connected by some number of flights. You are given an array
// flights where flights[i] = [from_i, to_i, price_i] indicates that there is a flight.
//
// You are also given an integer k (max stops). Return the cheapest price from src to dst
// with at most k stops. If there is no such route, return -1.
//
// Approach: Bellman-Ford Algorithm (DP)
// 1. Initialize distance array with INF, set dist[src] = 0.
// 2. Relax all edges up to k+1 times (k stops = k+1 edges).
// 3. Use a temporary array to ensure we only use at most k stops.
//
// Time Complexity: O(k * E) where E = number of flights
// Space Complexity: O(n)

public class CheapestFlightsWithinKStops {

    public static void main(String[] args) {
        // Test case 1
        int n1 = 4;
        int[][] flights1 = {{0, 1, 100}, {1, 2, 100}, {2, 3, 100}, {0, 2, 500}};
        int src1 = 0, dst1 = 3, k1 = 1;
        System.out.println("Cheapest price: " + findCheapestPrice(n1, flights1, src1, dst1, k1));
        // Expected: 500 (0 -> 2 -> 3 with 1 stop, cost 500)
        // Note: 0 -> 1 -> 2 -> 3 would be 2 stops, cost 300, but k=1

        // Test case 2
        int n2 = 3;
        int[][] flights2 = {{0, 1, 100}, {1, 2, 100}, {0, 2, 500}};
        int src2 = 0, dst2 = 2, k2 = 1;
        System.out.println("Cheapest price: " + findCheapestPrice(n2, flights2, src2, dst2, k2));
        // Expected: 200 (0 -> 1 -> 2 with 1 stop, cost 200)

        // Test case 3: No route within k stops
        int n3 = 3;
        int[][] flights3 = {{0, 1, 100}, {1, 2, 100}};
        int src3 = 0, dst3 = 2, k3 = 0;
        System.out.println("Cheapest price: " + findCheapestPrice(n3, flights3, src3, dst3, k3));
        // Expected: -1 (no direct flight from 0 to 2)
    }

    public static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int INF = Integer.MAX_VALUE / 2; // Use /2 to avoid overflow on addition
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // Relax edges up to k+1 times (k stops = k+1 edges)
        for (int i = 0; i <= k; i++) {
            int[] temp = Arrays.copyOf(dist, n);

            for (int[] flight : flights) {
                int from = flight[0];
                int to = flight[1];
                int price = flight[2];

                if (dist[from] != INF && dist[from] + price < temp[to]) {
                    temp[to] = dist[from] + price;
                }
            }

            dist = temp;
        }

        return dist[dst] == INF ? -1 : dist[dst];
    }
}
