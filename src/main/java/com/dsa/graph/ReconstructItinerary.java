package com.dsa.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// Problem: Reconstruct Itinerary
// Link: https://leetcode.com/problems/reconstruct-itinerary/
//
// You are given a list of airline tickets where tickets[i] = [from_i, to_i]
// represent the departure and arrival airports. Reconstruct the itinerary in order
// and return it.
//
// All of the tickets belong to a man who departs from "JFK". The itinerary must
// begin with "JFK". If there are multiple valid itineraries, return the one that
// has the smallest lexical order when read as a single string.
//
// Approach: Hierholzer's Algorithm (Eulerian Path)
// 1. Build adjacency list using a min-heap (PriorityQueue) for lexical ordering.
// 2. Perform DFS from "JFK", visiting edges in lexical order.
// 3. Post-order add nodes to the result (reverse of visit order).
//
// Time Complexity: O(E log E) where E = number of tickets
// Space Complexity: O(V + E)

public class ReconstructItinerary {

    public static void main(String[] args) {
        // Test case 1
        List<List<String>> tickets1 = new ArrayList<>();
        tickets1.add(List.of("MUC", "LHR"));
        tickets1.add(List.of("JFK", "MUC"));
        tickets1.add(List.of("SFO", "SJC"));
        tickets1.add(List.of("LHR", "SFO"));

        System.out.println("Itinerary: " + findItinerary(tickets1));
        // Expected: [JFK, MUC, LHR, SFO, SJC]

        // Test case 2
        List<List<String>> tickets2 = new ArrayList<>();
        tickets2.add(List.of("JFK", "SFO"));
        tickets2.add(List.of("JFK", "ATL"));
        tickets2.add(List.of("SFO", "ATL"));
        tickets2.add(List.of("ATL", "JFK"));
        tickets2.add(List.of("ATL", "SFO"));

        System.out.println("Itinerary: " + findItinerary(tickets2));
        // Expected: [JFK, ATL, JFK, SFO, ATL, SFO]
    }

    public static List<String> findItinerary(List<List<String>> tickets) {
        // Build adjacency list with min-heap for lexical ordering
        Map<String, PriorityQueue<String>> adj = new HashMap<>();

        for (List<String> ticket : tickets) {
            String from = ticket.get(0);
            String to = ticket.get(1);
            adj.putIfAbsent(from, new PriorityQueue<>());
            adj.get(from).offer(to);
        }

        LinkedList<String> result = new LinkedList<>();
        dfs("JFK", adj, result);

        return result;
    }

    private static void dfs(String airport, Map<String, PriorityQueue<String>> adj,
                             LinkedList<String> result) {
        PriorityQueue<String> destinations = adj.get(airport);

        while (destinations != null && !destinations.isEmpty()) {
            String next = destinations.poll();
            dfs(next, adj, result);
        }

        // Post-order: add to the front of the result
        result.addFirst(airport);
    }
}
