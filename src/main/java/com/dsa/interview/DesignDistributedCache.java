package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

/**
 * =====================================================
 * SYSTEM DESIGN: DISTRIBUTED CACHE (Redis-like)
 * =====================================================
 *
 * =====================================================
 * HIGH-LEVEL DESIGN (HLD)
 * =====================================================
 *
 * --- REQUIREMENTS ---
 * Functional:
 * 1. In-memory key-value store with rich data structures
 * 2. Low latency reads/writes (< 1ms)
 * 3. Distributed across multiple nodes
 * 4. High availability (no SPOF)
 * 5. Data persistence (optional)
 * 6. Eviction policies (LRU, LFU, TTL)
 *
 * Non-Functional:
 * - 100K+ ops/sec per node
 * - 99.999% availability
 * - Horizontal scalability
 * - Strong/eventual consistency
 *
 * --- SYSTEM ARCHITECTURE ---
 *
 *                    ┌─────────────────────┐
 *                    │  Client Application │
 *                    └──────────┬──────────┘
 *                               │
 *                    ┌──────────▼──────────┐
 *                    │   Cache Client SDK  │
 *                    │  (Consistent Hash)  │
 *                    └──────────┬──────────┘
 *                               │
 *         ┌─────────────────────┼─────────────────────┐
 *         │                     │                     │
 *   ┌─────▼─────┐        ┌─────▼─────┐        ┌─────▼─────┐
 *   │ Cache     │        │ Cache     │        │ Cache     │
 *   │ Node 1    │◄──────►│ Node 2    │◄──────►│ Node N    │
 *   │ (Primary) │        │ (Primary) │        │ (Primary) │
 *   └─────┬─────┘        └─────┬─────┘        └─────┬─────┘
 *         │                     │                     │
 *   ┌─────▼─────┐        ┌─────▼─────┐        ┌─────▼─────┐
 *   │ Replica 1 │        │ Replica 1 │        │ Replica 1 │
 *   │ (Slave)   │        │ (Slave)   │        │ (Slave)   │
 *   └───────────┘        └───────────┘        └───────────┘
 *
 * --- KEY DESIGN COMPONENTS ---
 *
 * 1. Consistent Hashing Ring:
 *    - Hash ring from 0 to 2^32 - 1
 *    - Virtual nodes for even distribution
 *    - Minimizes rehashing on node add/remove
 *
 * 2. Data Sharding:
 *    - Keys distributed across nodes via consistent hashing
 *    - Each node responsible for a range of hash ring
 *    - Automatic rebalancing
 *
 * 3. Replication:
 *    - Master-slave: each master has N replicas
 *    - Async replication for performance
 *    - Automatic failover via sentinel/consensus
 *
 * 4. Cluster Management:
 *    - Gossip protocol for node discovery
 *    - Raft/Paxos for configuration consensus
 *    - Health checks and heartbeat
 *
 * --- DATA STRUCTURES ---
 * String  → Simple key-value (SET/GET)
 * List    → Linked list (LPUSH/RPOP/LRANGE)
 * Set     → Unique elements (SADD/SREM/SMEMBERS)
 * Sorted  → Ordered by score (ZADD/ZRANGE/ZREM)
 * Hash    → Field-value pairs (HSET/HGET/HGETALL)
 *
 * --- PERSISTENCE ---
 * RDB: Periodic full snapshot (fork + write)
 * AOF: Append-only log of all write operations
 * Hybrid: RDB for base + AOF for incremental
 *
 * --- EVICTION POLICIES ---
 * noeviction       → Return errors on writes when full
 * allkeys-lru      → Evict LRU keys regardless of TTL
 * volatile-lru     → Evict LRU keys with TTL set
 * allkeys-random   → Evict random keys
 * volatile-ttl     → Evict keys with shortest TTL
 *
 * =====================================================
 * LOW-LEVEL DESIGN (LLD) - Java Implementation
 * =====================================================
 */
public class DesignDistributedCache {

    // =====================================================
    // 1. CACHE DATA STRUCTURES
    // =====================================================

    /**
     * Cache entry with metadata.
     */
    static class CacheEntry {
        final Object value;
        final long createdAt;
        volatile long lastAccessedAt;
        volatile long ttlMs;       // -1 = no expiry
        volatile int accessCount;  // For LFU

        CacheEntry(Object value, long ttlMs) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
            this.lastAccessedAt = this.createdAt;
            this.ttlMs = ttlMs;
            this.accessCount = 1;
        }

        boolean isExpired() {
            if (ttlMs < 0) return false;
            return (System.currentTimeMillis() - createdAt) > ttlMs;
        }

        void recordAccess() {
            this.lastAccessedAt = System.currentTimeMillis();
            this.accessCount++;
        }
    }

    // =====================================================
    // 2. EVICTION POLICIES
    // =====================================================

    public enum EvictionPolicy {
        NO_EVICTION,
        ALLKEYS_LRU,
        VOLATILE_LRU,
        ALLKEYS_RANDOM,
        VOLATILE_TTL,
        ALLKEYS_LFU
    }

    /**
     * LRU Cache using LinkedHashMap.
     * O(1) get/put with automatic eviction of least recently used.
     */
    static class LRUCache {
        private final LinkedHashMap<String, CacheEntry> map;
        private final int maxSize;
        private final Lock lock = new ReentrantLock();

        public LRUCache(int maxSize) {
            this.maxSize = maxSize;
            this.map = new LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    return size() > maxSize;
                }
            };
        }

        public CacheEntry get(String key) {
            lock.lock();
            try {
                CacheEntry entry = map.get(key);
                if (entry != null) {
                    entry.recordAccess();
                }
                return entry;
            } finally {
                lock.unlock();
            }
        }

        public void put(String key, CacheEntry entry) {
            lock.lock();
            try {
                map.put(key, entry);
            } finally {
                lock.unlock();
            }
        }

        public CacheEntry remove(String key) {
            lock.lock();
            try {
                return map.remove(key);
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            lock.lock();
            try {
                return map.size();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * LFU Cache implementation.
     * Evicts the least frequently used items first.
     * Uses frequency buckets for O(1) operations.
     */
    static class LFUCache {
        private final int maxSize;
        private final ConcurrentHashMap<String, CacheEntry> entries;
        private final ConcurrentHashMap<String, Integer> frequencies;
        // Freq → Set of keys at that frequency
        private final ConcurrentHashMap<Integer, LinkedHashSet<String>> freqBuckets;
        private volatile int minFreq;
        private final Lock lock = new ReentrantLock();

        public LFUCache(int maxSize) {
            this.maxSize = maxSize;
            this.entries = new ConcurrentHashMap<>();
            this.frequencies = new ConcurrentHashMap<>();
            this.freqBuckets = new ConcurrentHashMap<>();
            this.minFreq = 0;
        }

        public CacheEntry get(String key) {
            lock.lock();
            try {
                CacheEntry entry = entries.get(key);
                if (entry == null) return null;
                if (entry.isExpired()) {
                    removeInternal(key);
                    return null;
                }
                incrementFrequency(key);
                entry.recordAccess();
                return entry;
            } finally {
                lock.unlock();
            }
        }

        public void put(String key, CacheEntry entry) {
            lock.lock();
            try {
                if (entries.size() >= maxSize && !entries.containsKey(key)) {
                    evict();
                }
                entries.put(key, entry);
                frequencies.put(key, 1);
                freqBuckets.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
                minFreq = 1;
            } finally {
                lock.unlock();
            }
        }

        private void incrementFrequency(String key) {
            int freq = frequencies.getOrDefault(key, 0);
            frequencies.put(key, freq + 1);

            // Remove from old bucket
            LinkedHashSet<String> oldBucket = freqBuckets.get(freq);
            if (oldBucket != null) {
                oldBucket.remove(key);
                if (oldBucket.isEmpty() && freq == minFreq) {
                    minFreq++;
                }
            }

            // Add to new bucket
            freqBuckets.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
        }

        private void evict() {
            LinkedHashSet<String> minFreqBucket = freqBuckets.get(minFreq);
            if (minFreqBucket != null && !minFreqBucket.isEmpty()) {
                String keyToEvict = minFreqBucket.iterator().next();
                removeInternal(keyToEvict);
            }
        }

        private void removeInternal(String key) {
            entries.remove(key);
            Integer freq = frequencies.remove(key);
            if (freq != null) {
                LinkedHashSet<String> bucket = freqBuckets.get(freq);
                if (bucket != null) {
                    bucket.remove(key);
                }
            }
        }

        public int size() { return entries.size(); }
    }

    // =====================================================
    // 3. CONSISTENT HASHING
    // =====================================================

    /**
     * Consistent Hashing implementation.
     *
     * Maps keys to nodes on a hash ring.
     * Virtual nodes ensure even distribution.
     * When a node is added/removed, only K/N keys remap.
     */
    static class ConsistentHashRing {
        private final TreeMap<Integer, String> ring = new TreeMap<>();
        private final int virtualNodesPerNode;
        private final Set<String> nodes = ConcurrentHashMap.newKeySet();

        public ConsistentHashRing(int virtualNodesPerNode) {
            this.virtualNodesPerNode = virtualNodesPerNode;
        }

        /**
         * Add a node to the ring with virtual nodes.
         */
        public synchronized void addNode(String nodeId) {
            nodes.add(nodeId);
            for (int i = 0; i < virtualNodesPerNode; i++) {
                String virtualKey = nodeId + ":vnode:" + i;
                int hash = hash(virtualKey);
                ring.put(hash, nodeId);
            }
        }

        /**
         * Remove a node and all its virtual nodes.
         */
        public synchronized void removeNode(String nodeId) {
            nodes.remove(nodeId);
            ring.entrySet().removeIf(entry -> entry.getValue().equals(nodeId));
        }

        /**
         * Get the node responsible for a key.
         */
        public synchronized String getNode(String key) {
            if (ring.isEmpty()) return null;
            int hash = hash(key);
            Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
            if (entry == null) {
                // Wrap around to first node
                entry = ring.firstEntry();
            }
            return entry.getValue();
        }

        /**
         * Get the next N distinct nodes for replication.
         */
        public synchronized List<String> getNodes(String key, int count) {
            List<String> result = new ArrayList<>();
            int hash = hash(key);
            NavigableMap<Integer, String> tailMap = ring.tailMap(hash, false);

            for (Map.Entry<Integer, String> entry : tailMap.entrySet()) {
                if (result.size() >= count) break;
                if (!result.contains(entry.getValue())) {
                    result.add(entry.getValue());
                }
            }

            // Wrap around if needed
            if (result.size() < count) {
                for (Map.Entry<Integer, String> entry : ring.entrySet()) {
                    if (result.size() >= count) break;
                    if (!result.contains(entry.getValue())) {
                        result.add(entry.getValue());
                    }
                }
            }
            return result;
        }

        public Set<String> getAllNodes() { return Collections.unmodifiableSet(nodes); }
        public int getNodeCount() { return nodes.size(); }

        /**
         * Hash function (murmur-like).
         */
        private int hash(String key) {
            int h = 0;
            for (char c : key.toCharArray()) {
                h = 31 * h + c;
            }
            return h & 0x7FFFFFFF;
        }
    }

    // =====================================================
    // 4. CACHE NODE (Individual cache instance)
    // =====================================================

    /**
     * CacheNode - represents a single cache instance in the cluster.
     * Supports multiple data structures and eviction policies.
     */
    static class CacheNode {
        private final String nodeId;
        private final int maxMemory;
        private final EvictionPolicy evictionPolicy;
        private final ConcurrentHashMap<String, CacheEntry> store;
        private final LRUCache lruCache;
        private final LFUCache lfuCache;
        // TTL-based expiry scheduler
        private final ScheduledExecutorService expiryScheduler;
        // Replication handler (in production: sends to slave nodes)
        private final List<Consumer<WriteOperation>> replicationHandlers;

        @FunctionalInterface
        interface Consumer<T> {
            void accept(T operation);
        }

        static class WriteOperation {
            final String command;
            final String key;
            final Object value;
            final long ttlMs;
            final long timestamp;

            WriteOperation(String command, String key, Object value, long ttlMs) {
                this.command = command;
                this.key = key;
                this.value = value;
                this.ttlMs = ttlMs;
                this.timestamp = System.currentTimeMillis();
            }
        }

        public CacheNode(String nodeId, int maxMemory, EvictionPolicy evictionPolicy) {
            this.nodeId = nodeId;
            this.maxMemory = maxMemory;
            this.evictionPolicy = evictionPolicy;
            this.store = new ConcurrentHashMap<>();
            this.lruCache = new LRUCache(maxMemory);
            this.lfuCache = new LFUCache(maxMemory);
            this.replicationHandlers = new CopyOnWriteArrayList<>();
            this.expiryScheduler = Executors.newSingleThreadScheduledExecutor();
            // Run expiry check every second
            this.expiryScheduler.scheduleAtFixedRate(this::evictExpiredEntries,
                    1, 1, TimeUnit.SECONDS);
        }

        /**
         * SET command - store a key-value pair.
         */
        public void set(String key, Object value, long ttlMs) {
            CacheEntry entry = new CacheEntry(value, ttlMs);

            switch (evictionPolicy) {
                case ALLKEYS_LRU:
                case VOLATILE_LRU:
                    lruCache.put(key, entry);
                    break;
                case ALLKEYS_LFU:
                    lfuCache.put(key, entry);
                    break;
                default:
                    if (store.size() >= maxMemory && !store.containsKey(key)) {
                        handleEviction();
                    }
                    store.put(key, entry);
                    break;
            }

            // Replicate to slaves
            WriteOperation op = new WriteOperation("SET", key, value, ttlMs);
            replicate(op);

            System.out.println("[Node " + nodeId + "] SET " + key
                    + " (ttl=" + (ttlMs < 0 ? "∞" : ttlMs + "ms") + ")");
        }

        /**
         * GET command - retrieve a value by key.
         */
        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            CacheEntry entry;

            switch (evictionPolicy) {
                case ALLKEYS_LRU:
                case VOLATILE_LRU:
                    entry = lruCache.get(key);
                    break;
                case ALLKEYS_LFU:
                    entry = lfuCache.get(key);
                    break;
                default:
                    entry = store.get(key);
                    break;
            }

            if (entry == null) {
                System.out.println("[Node " + nodeId + "] GET " + key + " → MISS");
                return null;
            }

            if (entry.isExpired()) {
                deleteInternal(key);
                System.out.println("[Node " + nodeId + "] GET " + key + " → EXPIRED");
                return null;
            }

            entry.recordAccess();
            System.out.println("[Node " + nodeId + "] GET " + key + " → HIT");
            return (T) entry.value;
        }

        /**
         * DELETE command.
         */
        public boolean delete(String key) {
            boolean existed = deleteInternal(key);
            if (existed) {
                replicate(new WriteOperation("DEL", key, null, -1));
            }
            return existed;
        }

        private boolean deleteInternal(String key) {
            switch (evictionPolicy) {
                case ALLKEYS_LRU:
                case VOLATILE_LRU:
                    return lruCache.remove(key) != null;
                case ALLKEYS_LFU:
                    return lfuCache.get(key) != null && (lfuCache.get(key) != null);
                default:
                    return store.remove(key) != null;
            }
        }

        /**
         * EXPIRE - set TTL on an existing key.
         */
        public boolean expire(String key, long ttlMs) {
            CacheEntry entry = getEntry(key);
            if (entry == null) return false;
            entry.ttlMs = ttlMs;
            return true;
        }

        private CacheEntry getEntry(String key) {
            return switch (evictionPolicy) {
                case ALLKEYS_LRU, VOLATILE_LRU -> lruCache.get(key);
                case ALLKEYS_LFU -> lfuCache.get(key);
                default -> store.get(key);
            };
        }

        /**
         * Evict expired entries.
         */
        private void evictExpiredEntries() {
            long now = System.currentTimeMillis();
            store.forEach((key, entry) -> {
                if (entry.isExpired()) {
                    deleteInternal(key);
                }
            });
        }

        /**
         * Handle eviction based on policy.
         */
        private void handleEviction() {
            // In production, scan and evict based on policy
            // For NO_EVICTION, return error
        }

        /**
         * Register a replication handler (slave node).
         */
        public void addReplicationHandler(Consumer<WriteOperation> handler) {
            replicationHandlers.add(handler);
        }

        /**
         * Replicate write operations to slaves.
         */
        private void replicate(WriteOperation op) {
            for (Consumer<WriteOperation> handler : replicationHandlers) {
                handler.accept(op);
            }
        }

        /**
         * Apply a replicated write operation (slave receives this).
         */
        public void applyReplicatedOp(WriteOperation op) {
            switch (op.command) {
                case "SET" -> store.put(op.key, new CacheEntry(op.value, op.ttlMs));
                case "DEL" -> store.remove(op.key);
            }
        }

        /**
         * Get memory usage estimate.
         */
        public int getKeyCount() {
            return switch (evictionPolicy) {
                case ALLKEYS_LRU, VOLATILE_LRU -> lruCache.size();
                case ALLKEYS_LFU -> lfuCache.size();
                default -> store.size();
            };
        }

        public String getNodeId() { return nodeId; }
        public void shutdown() { expiryScheduler.shutdown(); }
    }

    // =====================================================
    // 5. DATA STRUCTURES (List, Set, SortedSet, Hash)
    // =====================================================

    /**
     * Redis-like List operations.
     */
    static class RedisList {
        private final ConcurrentLinkedDeque<String> deque = new ConcurrentLinkedDeque<>();
        private final Lock lock = new ReentrantLock();

        public void leftPush(String value) {
            lock.lock();
            try {
                deque.addFirst(value);
            } finally {
                lock.unlock();
            }
        }

        public void rightPush(String value) {
            lock.lock();
            try {
                deque.addLast(value);
            } finally {
                lock.unlock();
            }
        }

        public String leftPop() {
            lock.lock();
            try {
                return deque.pollFirst();
            } finally {
                lock.unlock();
            }
        }

        public String rightPop() {
            lock.lock();
            try {
                return deque.pollLast();
            } finally {
                lock.unlock();
            }
        }

        public List<String> range(int start, int end) {
            lock.lock();
            try {
                List<String> result = new ArrayList<>();
                int i = 0;
                for (String s : deque) {
                    if (i >= start && (end < 0 || i <= end)) {
                        result.add(s);
                    }
                    i++;
                    if (end >= 0 && i > end) break;
                }
                return result;
            } finally {
                lock.unlock();
            }
        }

        public int size() { return deque.size(); }
    }

    /**
     * Redis-like Sorted Set operations.
     */
    static class RedisSortedSet {
        // member → score mapping
        private final ConcurrentHashMap<String, Double> memberScores = new ConcurrentHashMap<>();
        // For range queries: sorted by score
        private final ConcurrentSkipListSet<ScoredMember> sortedSet = new ConcurrentSkipListSet<>(
                Comparator.comparingDouble(ScoredMember::score)
                        .thenComparing(ScoredMember::member)
        );

        record ScoredMember(String member, double score) {}

        public boolean add(String member, double score) {
            Double oldScore = memberScores.put(member, score);
            if (oldScore != null) {
                sortedSet.remove(new ScoredMember(member, oldScore));
            }
            sortedSet.add(new ScoredMember(member, score));
            return oldScore == null;
        }

        public Double getScore(String member) {
            return memberScores.get(member);
        }

        public boolean remove(String member) {
            Double score = memberScores.remove(member);
            if (score != null) {
                sortedSet.remove(new ScoredMember(member, score));
                return true;
            }
            return false;
        }

        public List<String> rangeByScore(double min, double max) {
            List<String> result = new ArrayList<>();
            ScoredMember from = new ScoredMember("", min);
            ScoredMember to = new ScoredMember("", max);
            for (ScoredMember sm : sortedSet.subSet(from, true, to, true)) {
                result.add(sm.member());
            }
            return result;
        }

        public List<String> reverseRange(int start, int end) {
            List<String> result = new ArrayList<>();
            int i = 0;
            for (ScoredMember sm : sortedSet.descendingSet()) {
                if (i >= start && (end < 0 || i <= end)) {
                    result.add(sm.member());
                }
                i++;
                if (end >= 0 && i > end) break;
            }
            return result;
        }

        public int size() { return sortedSet.size(); }
    }

    /**
     * Redis-like Hash operations.
     */
    static class RedisHash {
        private final ConcurrentHashMap<String, String> fields = new ConcurrentHashMap<>();

        public void set(String field, String value) {
            fields.put(field, value);
        }

        public String get(String field) {
            return fields.get(field);
        }

        public boolean delete(String field) {
            return fields.remove(field) != null;
        }

        public Map<String, String> getAll() {
            return new HashMap<>(fields);
        }

        public boolean exists(String field) {
            return fields.containsKey(field);
        }

        public int size() { return fields.size(); }
    }

    // =====================================================
    // 6. CACHE CLUSTER (Distributed cache manager)
    // =====================================================

    /**
     * CacheCluster - manages the distributed cache nodes.
     * Handles sharding, replication, and client requests.
     */
    static class CacheCluster {
        private final ConsistentHashRing hashRing;
        private final ConcurrentHashMap<String, CacheNode> nodes;
        private final int replicationFactor;
        private final int virtualNodes;

        public CacheCluster(int replicationFactor, int virtualNodes) {
            this.hashRing = new ConsistentHashRing(virtualNodes);
            this.nodes = new ConcurrentHashMap<>();
            this.replicationFactor = replicationFactor;
            this.virtualNodes = virtualNodes;
        }

        /**
         * Add a cache node to the cluster.
         */
        public void addNode(String nodeId, int maxMemory, EvictionPolicy policy) {
            CacheNode node = new CacheNode(nodeId, maxMemory, policy);
            nodes.put(nodeId, node);
            hashRing.addNode(nodeId);

            // Set up replication: each key is stored on N nodes
            // In production, we'd configure slaves per master
            System.out.println("[Cluster] Node added: " + nodeId
                    + " (total: " + nodes.size() + ")");
        }

        /**
         * Remove a cache node from the cluster.
         */
        public void removeNode(String nodeId) {
            CacheNode node = nodes.remove(nodeId);
            if (node != null) {
                hashRing.removeNode(nodeId);
                node.shutdown();
                System.out.println("[Cluster] Node removed: " + nodeId);
                // In production: rebalance data to remaining nodes
            }
        }

        /**
         * Find the primary node for a key.
         */
        private CacheNode getPrimaryNode(String key) {
            String nodeId = hashRing.getNode(key);
            return nodeId != null ? nodes.get(nodeId) : null;
        }

        /**
         * Find all replica nodes for a key.
         */
        private List<CacheNode> getReplicaNodes(String key) {
            List<String> nodeIds = hashRing.getNodes(key, replicationFactor);
            return nodeIds.stream()
                    .map(nodes::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        /**
         * SET operation - write to primary and replicas.
         */
        public void set(String key, Object value, long ttlMs) {
            CacheNode primary = getPrimaryNode(key);
            if (primary == null) {
                throw new IllegalStateException("No available cache node");
            }
            primary.set(key, value, ttlMs);

            // Async replication to replicas
            List<CacheNode> replicas = getReplicaNodes(key);
            for (CacheNode replica : replicas) {
                if (!replica.getNodeId().equals(primary.getNodeId())) {
                    CacheNode.WriteOperation op =
                            new CacheNode.WriteOperation("SET", key, value, ttlMs);
                    replica.applyReplicatedOp(op);
                }
            }
        }

        /**
         * GET operation - read from primary (could read from replica for load balancing).
         */
        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            CacheNode node = getPrimaryNode(key);
            if (node == null) return null;
            T value = node.get(key);
            if (value != null) return value;

            // If primary misses, try replicas
            List<CacheNode> replicas = getReplicaNodes(key);
            for (CacheNode replica : replicas) {
                if (!replica.getNodeId().equals(node.getNodeId())) {
                    value = replica.get(key);
                    if (value != null) return value;
                }
            }
            return null;
        }

        /**
         * DELETE operation.
         */
        public boolean delete(String key) {
            CacheNode primary = getPrimaryNode(key);
            if (primary == null) return false;
            return primary.delete(key);
        }

        /**
         * Get cluster statistics.
         */
        public Map<String, Object> getStats() {
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("nodeCount", nodes.size());
            stats.put("replicationFactor", replicationFactor);
            Map<String, Integer> nodeKeyCounts = new LinkedHashMap<>();
            for (Map.Entry<String, CacheNode> entry : nodes.entrySet()) {
                nodeKeyCounts.put(entry.getKey(), entry.getValue().getKeyCount());
            }
            stats.put("nodes", nodeKeyCounts);
            return stats;
        }

        public void shutdown() {
            nodes.values().forEach(CacheNode::shutdown);
        }
    }

    // =====================================================
    // 7. DEMONSTRATION
    // =====================================================

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("DISTRIBUTED CACHE DESIGN (Redis-like)");
        System.out.println("========================================\n");

        // Create a cache cluster with 3 nodes
        CacheCluster cluster = new CacheCluster(2, 100);
        cluster.addNode("cache-1", 1000, EvictionPolicy.ALLKEYS_LRU);
        cluster.addNode("cache-2", 1000, EvictionPolicy.ALLKEYS_LRU);
        cluster.addNode("cache-3", 1000, EvictionPolicy.ALLKEYS_LRU);

        // Basic SET/GET operations
        System.out.println("--- Basic Operations ---");
        cluster.set("user:100", "John Doe", -1);
        cluster.set("user:200", "Jane Smith", 5000); // 5 second TTL
        cluster.set("session:abc123", "token_data", 3600000); // 1 hour TTL

        System.out.println("  user:100 = " + cluster.get("user:100"));
        System.out.println("  user:200 = " + cluster.get("user:200"));
        System.out.println("  session:abc123 = " + cluster.get("session:abc123"));
        System.out.println("  unknown_key = " + cluster.get("unknown_key"));

        // TTL expiry demo
        System.out.println("\n--- TTL Expiry ---");
        System.out.println("  user:200 (before sleep) = " + cluster.get("user:200"));
        try { Thread.sleep(6000); } catch (InterruptedException e) {}
        System.out.println("  user:200 (after 6s sleep) = " + cluster.get("user:200"));

        // Data structure demos
        System.out.println("\n--- List Operations ---");
        RedisList list = new RedisList();
        list.leftPush("item1");
        list.leftPush("item2");
        list.rightPush("item3");
        System.out.println("  List range [0,2]: " + list.range(0, 2));
        System.out.println("  Left pop: " + list.leftPop());

        System.out.println("\n--- Sorted Set Operations ---");
        RedisSortedSet sortedSet = new RedisSortedSet();
        sortedSet.add("player1", 1500);
        sortedSet.add("player2", 2300);
        sortedSet.add("player3", 1800);
        System.out.println("  Top scores (reverse): " + sortedSet.reverseRange(0, 2));
        System.out.println("  Scores between 1500-2000: " + sortedSet.rangeByScore(1500, 2000));

        System.out.println("\n--- Hash Operations ---");
        RedisHash hash = new RedisHash();
        hash.set("name", "Alice");
        hash.set("email", "alice@example.com");
        hash.set("age", "28");
        System.out.println("  Hash fields: " + hash.getAll());
        System.out.println("  Hash get 'email': " + hash.get("email"));

        // Cluster stats
        System.out.println("\n--- Cluster Statistics ---");
        Map<String, Object> stats = cluster.getStats();
        stats.forEach((k, v) -> System.out.println("  " + k + ": " + v));

        // Consistent hashing demo
        System.out.println("\n--- Consistent Hashing ---");
        ConsistentHashRing ring = new ConsistentHashRing(3);
        ring.addNode("server-A");
        ring.addNode("server-B");
        ring.addNode("server-C");

        String[] testKeys = {"key1", "key2", "key3", "user:100", "session:abc"};
        for (String key : testKeys) {
            System.out.println("  " + key + " → " + ring.getNode(key));
        }

        cluster.shutdown();

        System.out.println("\n========================================");
        System.out.println("KEY DESIGN DECISIONS:");
        System.out.println("========================================");
        System.out.println("• Consistent hashing with virtual nodes for even distribution");
        System.out.println("• LRU/LFU/TTL eviction policies for memory management");
        System.out.println("• Master-slave replication with async replication");
        System.out.println("• RDB + AOF persistence for durability");
        System.out.println("• Rich data structures: List, Set, Sorted Set, Hash");
        System.out.println("• Gossip protocol for cluster discovery (in production)");
        System.out.println("• Sentinel/Cluster for automatic failover");
        System.out.println("• O(1) operations for all core data structures");
        System.out.println("========================================");
    }
}
