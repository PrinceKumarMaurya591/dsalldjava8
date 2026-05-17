package com.dsa.linkedlist;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem: LRU Cache (Least Recently Used Cache)
 * 
 * Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.
 * 
 * Implement the LRUCache class:
 * - LRUCache(int capacity): Initialize the LRU cache with positive size capacity.
 * - int get(int key): Return the value of the key if the key exists, otherwise return -1.
 * - void put(int key, int value): Update the value of the key if the key exists.
 *   Otherwise, add the key-value pair to the cache. If the number of keys exceeds
 *   the capacity from this operation, evict the least recently used key.
 * 
 * The functions get and put must each run in O(1) average time complexity.
 * 
 * Approach: HashMap + Doubly Linked List
 * - HashMap provides O(1) lookup from key to node
 * - Doubly Linked List maintains the order of usage
 * - Most recently used items are at the head
 * - Least recently used items are at the tail
 * - On get: move the accessed node to the head
 * - On put: if exists, update and move to head; if new, add to head and evict tail if needed
 * 
 * Time Complexity: O(1) for both get and put
 * Space Complexity: O(capacity)
 */
public class LRUCache {

    // Node class for doubly linked list
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node() {}

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head; // Dummy head (most recently used)
    private final Node tail; // Dummy tail (least recently used)

    /**
     * Initialize the LRU cache with the given capacity.
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node();
        this.tail = new Node();
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Get the value of the key if it exists, otherwise return -1.
     * Moves the accessed node to the head (most recently used).
     */
    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }

        // Move to head (most recently used)
        moveToHead(node);
        return node.value;
    }

    /**
     * Update or insert a key-value pair.
     * If the key exists, update the value and move to head.
     * If the key is new, add to head and evict LRU if at capacity.
     */
    public void put(int key, int value) {
        Node node = cache.get(key);

        if (node != null) {
            // Key exists: update value and move to head
            node.value = value;
            moveToHead(node);
        } else {
            // Key doesn't exist: create new node
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addToHead(newNode);

            // Evict least recently used if over capacity
            if (cache.size() > capacity) {
                Node lru = removeTail();
                cache.remove(lru.key);
            }
        }
    }

    /**
     * Returns the current size of the cache.
     */
    public int size() {
        return cache.size();
    }

    /**
     * Checks if the cache contains a key without affecting its recency.
     */
    public boolean containsKey(int key) {
        return cache.containsKey(key);
    }

    // ========== Doubly Linked List Operations ==========

    /**
     * Adds a node right after the head (most recently used position).
     */
    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    /**
     * Removes a node from the linked list.
     */
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /**
     * Moves a node to the head (most recently used position).
     * Combines removeNode and addToHead.
     */
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    /**
     * Removes and returns the node at the tail (least recently used).
     */
    private Node removeTail() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }

    /**
     * Prints the current state of the cache from most to least recently used.
     */
    public void printCache() {
        System.out.print("Cache (MRU -> LRU): ");
        Node current = head.next;
        while (current != tail) {
            System.out.print("[" + current.key + ":" + current.value + "]");
            if (current.next != tail) {
                System.out.print(" <-> ");
            }
            current = current.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("=== LRU Cache Demonstration ===");
        System.out.println();

        // Test case 1: Basic operations
        System.out.println("Test 1 - Basic operations:");
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.printCache();
        System.out.println("get(1): " + cache.get(1) + " (expected: 1)");
        cache.printCache(); // 1 should be MRU now
        cache.put(3, 3);    // Evicts key 2
        cache.printCache();
        System.out.println("get(2): " + cache.get(2) + " (expected: -1, evicted)");
        cache.put(4, 4);    // Evicts key 1
        cache.printCache();
        System.out.println("get(1): " + cache.get(1) + " (expected: -1, evicted)");
        System.out.println("get(3): " + cache.get(3) + " (expected: 3)");
        System.out.println("get(4): " + cache.get(4) + " (expected: 4)");
        System.out.println();

        // Test case 2: Update existing key
        System.out.println("Test 2 - Update existing key:");
        LRUCache cache2 = new LRUCache(2);
        cache2.put(1, 1);
        cache2.put(2, 2);
        cache2.printCache();
        cache2.put(1, 10); // Update key 1
        cache2.printCache();
        System.out.println("get(1): " + cache2.get(1) + " (expected: 10)");
        System.out.println("get(2): " + cache2.get(2) + " (expected: 2)");
        System.out.println();

        // Test case 3: Capacity of 1
        System.out.println("Test 3 - Capacity of 1:");
        LRUCache cache3 = new LRUCache(1);
        cache3.put(1, 1);
        System.out.println("get(1): " + cache3.get(1) + " (expected: 1)");
        cache3.put(2, 2); // Evicts key 1
        System.out.println("get(1): " + cache3.get(1) + " (expected: -1)");
        System.out.println("get(2): " + cache3.get(2) + " (expected: 2)");
        System.out.println();

        // Test case 4: Larger capacity
        System.out.println("Test 4 - Larger capacity (3):");
        LRUCache cache4 = new LRUCache(3);
        cache4.put(1, 1);
        cache4.put(2, 2);
        cache4.put(3, 3);
        cache4.put(4, 4); // Evicts key 1
        cache4.printCache();
        System.out.println("get(1): " + cache4.get(1) + " (expected: -1)");
        System.out.println("get(2): " + cache4.get(2) + " (expected: 2)");
        cache4.put(5, 5); // Evicts key 3 (since 2 was just accessed)
        cache4.printCache();
        System.out.println("get(3): " + cache4.get(3) + " (expected: -1)");
        System.out.println("get(4): " + cache4.get(4) + " (expected: 4)");
        System.out.println("get(5): " + cache4.get(5) + " (expected: 5)");
        System.out.println();

        // Test case 5: Get on non-existent key
        System.out.println("Test 5 - Non-existent key:");
        LRUCache cache5 = new LRUCache(2);
        System.out.println("get(99): " + cache5.get(99) + " (expected: -1)");
        System.out.println();

        // Test case 6: Sequence of puts and gets
        System.out.println("Test 6 - Sequence test:");
        LRUCache cache6 = new LRUCache(2);
        System.out.println("put(2,1), put(1,1), put(2,3), put(4,1)");
        cache6.put(2, 1);
        cache6.put(1, 1);
        cache6.put(2, 3);  // Update key 2
        cache6.put(4, 1);  // Evicts key 1
        System.out.println("get(1): " + cache6.get(1) + " (expected: -1)");
        System.out.println("get(2): " + cache6.get(2) + " (expected: 3)");
    }
}
