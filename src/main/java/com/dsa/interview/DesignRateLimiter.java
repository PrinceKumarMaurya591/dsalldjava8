package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * =====================================================
 * SYSTEM DESIGN: RATE LIMITER
 * (Token Bucket, Sliding Window, Distributed)
 * =====================================================
 *
 * =====================================================
 * HIGH-LEVEL DESIGN (HLD)
 * =====================================================
 *
 * --- REQUIREMENTS ---
 * Functional:
 * 1. Limit API requests per user/IP/API key within time window
 * 2. Configurable limits per client
 * 3. Block requests exceeding limits (HTTP 429 Too Many Requests)
 * 4. Clear rate limit headers in response
 *
 * Non-Functional:
 * - < 1ms latency overhead per request
 * - Distributed across multiple servers
 * - Fault-tolerant (no single point of failure)
 * - Eventually consistent rate limit counters
 *
 * --- ALGORITHMS ---
 *
 * 1. TOKEN BUCKET:
 *    ┌─────────────────────────┐
 *    │        Bucket           │
 *    │  ┌───┐ ┌───┐ ┌───┐    │
 *    │  │ T │ │ T │ │ T │ ... │  ← Tokens added at rate r
 *    │  └───┘ └───┘ └───┘    │
 *    │  Max capacity = b      │
 *    └─────────────────────────┘
 *    When request arrives:
 *    - If bucket has token → consume one → ALLOW
 *    - If bucket empty → DENY
 *    - Tokens refill at r per second (up to b max)
 *
 * 2. LEAKY BUCKET:
 *    ┌─────────────────────┐
 *    │     Request Queue    │
 *    │  ┌──┬──┬──┬──┬──┐  │  ← Fixed rate processing
 *    │  │R1│R2│R3│R4│R5│  │
 *    │  └──┴──┴──┴──┴──┘  │
 *    │  Max queue = q      │
 *    └─────────────────────┘
 *
 * 3. FIXED WINDOW COUNTER:
 *    Time: |---1 min---||---1 min---||---1 min---|
 *    Count: [10 req]     [10 req]     [10 req]
 *    Problem: Spike at boundary
 *
 * 4. SLIDING WINDOW LOG:
 *    Time: ◄──1 min──►
 *    Log: [t1, t2, t3, ..., tn]
 *    Count timestamps within window
 *    Memory: O(window_size × rate)
 *
 * 5. SLIDING WINDOW COUNTER (Hybrid - RECOMMENDED):
 *    |---prev window---|---curr window---|
 *    Count = prev_count * overlap + curr_count
 *    Used by: Kong API Gateway, Redis
 *
 * --- DISTRIBUTED ARCHITECTURE ---
 *
 *                    ┌──────────────────────┐
 *                    │   API Gateway / LB    │
 *                    │  (Rate Limit Check)   │
 *                    └──────┬──────────┬─────┘
 *                           │          │
 *              ┌────────────▼──┐  ┌───▼────────────┐
 *              │  App Server 1 │  │  App Server 2  │
 *              │  (local cache)│  │  (local cache) │
 *              └───────┬───────┘  └───────┬────────┘
 *                      │                  │
 *                      └────────┬─────────┘
 *                               │
 *                    ┌──────────▼──────────┐
 *                    │    Redis Cluster    │
 *                    │  (Atomic Counters)  │
 *                    └─────────────────────┘
 *
 * Key Design Decisions:
 * - Local cache (near) for fast check → sync with Redis async
 * - Redis Lua scripting for atomic operations
 * - Sliding window counter for memory efficiency + accuracy
 * - Rate limit rules stored in configuration service
 *
 * --- RATE LIMIT HEADERS ---
 * X-RateLimit-Limit: 100
 * X-RateLimit-Remaining: 87
 * X-RateLimit-Reset: 1620000000
 * Retry-After: 45
 *
 * =====================================================
 * LOW-LEVEL DESIGN (LLD) - Java Implementation
 * =====================================================
 */
public class DesignRateLimiter {

    // =====================================================
    // 1. RATE LIMIT CONFIGURATION
    // =====================================================

    /**
     * Represents a rate limit rule for a specific client/resource.
     */
    public static class RateLimitRule {
        private final String key;        // e.g., "user:123", "ip:192.168.1.1"
        private final int maxRequests;   // Maximum requests allowed
        private final long windowMs;     // Time window in milliseconds
        private final Algorithm algorithm;

        public enum Algorithm {
            TOKEN_BUCKET,
            LEAKY_BUCKET,
            FIXED_WINDOW,
            SLIDING_WINDOW_LOG,
            SLIDING_WINDOW_COUNTER
        }

        public RateLimitRule(String key, int maxRequests, long windowMs, Algorithm algorithm) {
            this.key = key;
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.algorithm = algorithm;
        }

        public String getKey() { return key; }
        public int getMaxRequests() { return maxRequests; }
        public long getWindowMs() { return windowMs; }
        public Algorithm getAlgorithm() { return algorithm; }
    }

    // =====================================================
    // 2. RATE LIMIT RESULT
    // =====================================================

    /**
     * Result of a rate limit check.
     */
    public static class RateLimitResult {
        private final boolean allowed;
        private final int remaining;
        private final long resetAtMs;
        private final int limit;

        public RateLimitResult(boolean allowed, int remaining, long resetAtMs, int limit) {
            this.allowed = allowed;
            this.remaining = remaining;
            this.resetAtMs = resetAtMs;
            this.limit = limit;
        }

        public boolean isAllowed() { return allowed; }
        public int getRemaining() { return remaining; }
        public long getResetAtMs() { return resetAtMs; }
        public int getLimit() { return limit; }

        /**
         * Convert to standard HTTP rate limit headers.
         */
        public Map<String, String> toHeaders() {
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("X-RateLimit-Limit", String.valueOf(limit));
            headers.put("X-RateLimit-Remaining", String.valueOf(remaining));
            headers.put("X-RateLimit-Reset", String.valueOf(resetAtMs / 1000));
            if (!allowed) {
                long retryAfter = Math.max(1, (resetAtMs - System.currentTimeMillis()) / 1000);
                headers.put("Retry-After", String.valueOf(retryAfter));
            }
            return headers;
        }
    }

    // =====================================================
    // 3. TOKEN BUCKET ALGORITHM
    // =====================================================

    /**
     * Token Bucket implementation.
     *
     * Properties:
     * - Bucket capacity (b): max tokens bucket can hold
     * - Refill rate (r): tokens added per second
     * - Each request consumes 1 token
     * - Supports bursts up to capacity
     */
    public static class TokenBucket {
        private final int capacity;
        private final double refillRatePerMs;
        private final AtomicLong currentTokens;
        private volatile long lastRefillTimestamp;

        public TokenBucket(int capacity, double refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRatePerMs = refillRatePerSecond / 1000.0;
            this.currentTokens = new AtomicLong(capacity);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        /**
         * Try to consume a token. Returns true if allowed.
         */
        public synchronized boolean tryConsume() {
            refill();
            if (currentTokens.get() > 0) {
                currentTokens.decrementAndGet();
                return true;
            }
            return false;
        }

        /**
         * Refill tokens based on elapsed time.
         */
        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTimestamp;
            if (elapsed > 0) {
                double tokensToAdd = elapsed * refillRatePerMs;
                long newTokens = Math.min(capacity,
                        currentTokens.get() + (long) tokensToAdd);
                currentTokens.set(newTokens);
                lastRefillTimestamp = now;
            }
        }

        /**
         * Get estimated time until next token is available.
         */
        public long getTimeUntilNextToken() {
            if (currentTokens.get() > 0) return 0;
            return (long) (1.0 / refillRatePerMs);
        }

        public int getAvailableTokens() { return (int) Math.min(currentTokens.get(), capacity); }
    }

    // =====================================================
    // 4. FIXED WINDOW COUNTER
    // =====================================================

    /**
     * Fixed Window Counter implementation.
     *
     * Simple counter reset at fixed intervals.
     * Pro: Low memory, simple
     * Con: Traffic spike at window boundary (can double limit)
     */
    public static class FixedWindowCounter {
        private final int maxRequests;
        private final long windowMs;
        private final AtomicInteger counter;
        private volatile long windowStart;

        public FixedWindowCounter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.counter = new AtomicInteger(0);
            this.windowStart = System.currentTimeMillis();
        }

        /**
         * Check if request is allowed.
         */
        public synchronized RateLimitResult allowRequest() {
            long now = System.currentTimeMillis();
            // Reset window if expired
            if (now - windowStart >= windowMs) {
                counter.set(0);
                windowStart = now;
            }

            int count = counter.incrementAndGet();
            boolean allowed = count <= maxRequests;
            long resetAt = windowStart + windowMs;

            return new RateLimitResult(
                    allowed,
                    Math.max(0, maxRequests - count),
                    resetAt,
                    maxRequests
            );
        }
    }

    // =====================================================
    // 5. SLIDING WINDOW LOG
    // =====================================================

    /**
     * Sliding Window Log implementation.
     *
     * Maintains a sorted set of timestamps for each request.
     * Counts requests within the sliding window.
     * Pro: Very accurate
     * Con: Memory intensive (O(n) per key)
     */
    public static class SlidingWindowLog {
        private final int maxRequests;
        private final long windowMs;
        private final Deque<Long> timestamps;
        private final Lock lock = new ReentrantLock();

        public SlidingWindowLog(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.timestamps = new LinkedList<>();
        }

        /**
         * Check if request is allowed.
         */
        public RateLimitResult allowRequest() {
            lock.lock();
            try {
                long now = System.currentTimeMillis();
                long windowStart = now - windowMs;

                // Remove expired timestamps
                while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                    timestamps.pollFirst();
                }

                // Check if under limit
                if (timestamps.size() < maxRequests) {
                    timestamps.addLast(now);
                    return new RateLimitResult(true,
                            maxRequests - timestamps.size(),
                            now + windowMs,
                            maxRequests);
                }

                // Denied
                long oldestTimestamp = timestamps.peekFirst();
                long resetAt = oldestTimestamp + windowMs;
                return new RateLimitResult(false, 0, resetAt, maxRequests);
            } finally {
                lock.unlock();
            }
        }
    }

    // =====================================================
    // 6. SLIDING WINDOW COUNTER (HYBRID)
    // =====================================================

    /**
     * Sliding Window Counter implementation.
     *
     * Hybrid approach: splits time into windows, uses weighted count.
     * Pro: Memory efficient, smooth rate limiting
     * Con: Slightly less accurate than sliding log
     *
     * count = prev_window_count * (1 - overlap_ratio) + curr_window_count
     *
     * Used by: Kong API Gateway, Redis rate limiting
     */
    public static class SlidingWindowCounter {
        private final int maxRequests;
        private final long windowMs;
        // Previous complete window count
        private final AtomicInteger prevCount;
        // Current window count
        private final AtomicInteger currCount;
        // Current window start timestamp
        private volatile long currWindowStart;
        private final Lock lock = new ReentrantLock();

        public SlidingWindowCounter(int maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
            this.prevCount = new AtomicInteger(0);
            this.currCount = new AtomicInteger(0);
            this.currWindowStart = System.currentTimeMillis();
        }

        /**
         * Check if request is allowed.
         */
        public RateLimitResult allowRequest() {
            lock.lock();
            try {
                long now = System.currentTimeMillis();
                slideWindow(now);

                int currentCount = currCount.incrementAndGet();
                long elapsedInCurrWindow = now - currWindowStart;
                double overlapRatio = (double) elapsedInCurrWindow / windowMs;

                // Weighted estimate of total requests in sliding window
                double estimatedCount = prevCount.get() * (1 - overlapRatio) + currentCount;

                boolean allowed = estimatedCount <= maxRequests;

                long resetAt = currWindowStart + windowMs;
                int remaining = Math.max(0, (int) (maxRequests - estimatedCount));

                return new RateLimitResult(allowed, remaining, resetAt, maxRequests);
            } finally {
                lock.unlock();
            }
        }

        /**
         * Slide window if needed.
         * - If we're in a new window, shift counts
         * - If we've skipped a window, reset both
         */
        private void slideWindow(long now) {
            long elapsed = now - currWindowStart;
            if (elapsed >= windowMs) {
                // We've moved to a new window
                prevCount.set(currCount.get());
                currCount.set(0);
                currWindowStart = now;
            } else if (elapsed >= windowMs * 2) {
                // We've skipped more than one window
                prevCount.set(0);
                currCount.set(0);
                currWindowStart = now;
            }
        }
    }

    // =====================================================
    // 7. DISTRIBUTED RATE LIMITER (Redis-backed)
    // =====================================================

    /**
     * Distributed Rate Limiter using Redis-like atomic operations.
     *
     * In production, this uses Redis Lua scripting for atomicity:
     *
     * -- Lua script for sliding window counter
     * local key = KEYS[1]
     * local max = tonumber(ARGV[1])
     * local window = tonumber(ARGV[2])
     * local now = tonumber(ARGV[3])
     *
     * local current = redis.call('INCR', key)
     * if current == 1 then
     *     redis.call('PEXPIRE', key, window)
     * end
     * return current <= max
     */
    public static class DistributedRateLimiter {
        private final ConcurrentHashMap<String, SlidingWindowCounter> localCounters;
        private final int maxRequests;
        private final long windowMs;

        // In production: Redis client
        // private final JedisCluster redis;

        public DistributedRateLimiter(int maxRequests, long windowMs) {
            this.localCounters = new ConcurrentHashMap<>();
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
        }

        /**
         * Check if request is allowed for a given key.
         * Uses local counter (fast path) with async Redis sync.
         */
        public RateLimitResult allowRequest(String key) {
            SlidingWindowCounter counter = localCounters.computeIfAbsent(key,
                    k -> new SlidingWindowCounter(maxRequests, windowMs));
            return counter.allowRequest();
        }

        /**
         * In production, the Redis Lua script would be:
         *
         * String luaScript =
         *     "local key = KEYS[1]\n" +
         *     "local limit = tonumber(ARGV[1])\n" +
         *     "local window = tonumber(ARGV[2])\n" +
         *     "local now = tonumber(ARGV[3])\n" +
         *     "local current = redis.call('INCR', key)\n" +
         *     "if current == 1 then\n" +
         *     "    redis.call('PEXPIRE', key, window)\n" +
         *     "end\n" +
         *     "return current";
         *
         * // Atomic eval
         * // Long result = (Long) redis.eval(luaScript, 1, key, limit, window, now);
         */

        /**
         * Clean up stale counters to prevent memory leak.
         */
        public void cleanup() {
            long now = System.currentTimeMillis();
            // Remove counters not accessed recently
            // In production: use expiry-based eviction
        }
    }

    // =====================================================
    // 8. LEAKY BUCKET ALGORITHM
    // =====================================================

    /**
     * Leaky Bucket implementation.
     *
     * Requests flow in at variable rate, leak out at fixed rate.
     * Useful for smoothing out bursts for downstream services.
     */
    public static class LeakyBucket {
        private final int capacity;
        private final long leakIntervalMs;  // Time between leak operations
        private final AtomicInteger waterLevel;
        private volatile long lastLeakTime;
        private final Lock lock = new ReentrantLock();

        public LeakyBucket(int capacity, double leakRatePerSecond) {
            this.capacity = capacity;
            this.leakIntervalMs = (long) (1000.0 / leakRatePerSecond);
            this.waterLevel = new AtomicInteger(0);
            this.lastLeakTime = System.currentTimeMillis();
        }

        /**
         * Try to add a request to the bucket.
         */
        public boolean tryConsume() {
            lock.lock();
            try {
                leak();
                if (waterLevel.get() < capacity) {
                    waterLevel.incrementAndGet();
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Leak water at fixed rate.
         */
        private void leak() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastLeakTime;
            if (elapsed >= leakIntervalMs) {
                int leaksToRemove = (int) (elapsed / leakIntervalMs);
                int newLevel = Math.max(0, waterLevel.get() - leaksToRemove);
                waterLevel.set(newLevel);
                lastLeakTime = now;
            }
        }
    }

    // =====================================================
    // 9. RATE LIMITER FACTORY & MANAGER
    // =====================================================

    /**
     * Rate Limiter Manager - manages multiple rate limiters.
     * Supports different algorithms and configurations per client/resource.
     */
    public static class RateLimiterManager {
        private final ConcurrentHashMap<String, Object> limiters = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, RateLimitRule> rules = new ConcurrentHashMap<>();

        /**
         * Configure a rate limit rule.
         */
        public void addRule(RateLimitRule rule) {
            rules.put(rule.getKey(), rule);
            createLimiter(rule);
        }

        /**
         * Check if a request is allowed for the given key.
         */
        public RateLimitResult allowRequest(String key) {
            RateLimitRule rule = rules.get(key);
            if (rule == null) {
                // No rule = no rate limit
                return new RateLimitResult(true, Integer.MAX_VALUE,
                        System.currentTimeMillis() + 3600000, Integer.MAX_VALUE);
            }
            return allowRequest(key, rule);
        }

        /**
         * Check request against a specific rule.
         */
        private RateLimitResult allowRequest(String key, RateLimitRule rule) {
            Object limiter = limiters.get(key);

            return switch (rule.getAlgorithm()) {
                case TOKEN_BUCKET -> {
                    TokenBucket tb = (TokenBucket) limiter;
                    boolean allowed = tb.tryConsume();
                    yield new RateLimitResult(allowed, tb.getAvailableTokens(),
                            System.currentTimeMillis() + tb.getTimeUntilNextToken(),
                            rule.getMaxRequests());
                }
                case FIXED_WINDOW -> {
                    FixedWindowCounter fwc = (FixedWindowCounter) limiter;
                    yield fwc.allowRequest();
                }
                case SLIDING_WINDOW_LOG -> {
                    SlidingWindowLog swl = (SlidingWindowLog) limiter;
                    yield swl.allowRequest();
                }
                case SLIDING_WINDOW_COUNTER -> {
                    SlidingWindowCounter swc = (SlidingWindowCounter) limiter;
                    yield swc.allowRequest();
                }
                case LEAKY_BUCKET -> {
                    LeakyBucket lb = (LeakyBucket) limiter;
                    boolean allowed = lb.tryConsume();
                    yield new RateLimitResult(allowed, allowed ? 1 : 0,
                            System.currentTimeMillis() + 1000, rule.getMaxRequests());
                }
            };
        }

        /**
         * Create the appropriate limiter based on rule.
         */
        private void createLimiter(RateLimitRule rule) {
            double ratePerSecond = (double) rule.getMaxRequests() / (rule.getWindowMs() / 1000.0);
            Object limiter = switch (rule.getAlgorithm()) {
                case TOKEN_BUCKET -> new TokenBucket(rule.getMaxRequests(), ratePerSecond);
                case FIXED_WINDOW -> new FixedWindowCounter(rule.getMaxRequests(), rule.getWindowMs());
                case SLIDING_WINDOW_LOG -> new SlidingWindowLog(rule.getMaxRequests(), rule.getWindowMs());
                case SLIDING_WINDOW_COUNTER -> new SlidingWindowCounter(rule.getMaxRequests(), rule.getWindowMs());
                case LEAKY_BUCKET -> new LeakyBucket(rule.getMaxRequests(), ratePerSecond);
            };
            limiters.put(rule.getKey(), limiter);
        }
    }

    // =====================================================
    // 10. DEMONSTRATION
    // =====================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("RATE LIMITER DESIGN");
        System.out.println("========================================\n");

        // --- Token Bucket Demo ---
        System.out.println("--- Token Bucket (10 tokens, 5/sec refill) ---");
        TokenBucket tokenBucket = new TokenBucket(10, 5);
        for (int i = 0; i < 15; i++) {
            boolean allowed = tokenBucket.tryConsume();
            System.out.println("  Request " + (i + 1) + ": " + (allowed ? "✅ ALLOWED" : "❌ DENIED")
                    + " (available: " + tokenBucket.getAvailableTokens() + ")");
            Thread.sleep(50);
        }

        Thread.sleep(500);

        // --- Sliding Window Counter Demo ---
        System.out.println("\n--- Sliding Window Counter (5 req/min) ---");
        SlidingWindowCounter swc = new SlidingWindowCounter(5, 60_000);
        for (int i = 0; i < 8; i++) {
            RateLimitResult result = swc.allowRequest();
            System.out.println("  Request " + (i + 1) + ": " + (result.isAllowed() ? "✅ ALLOWED" : "❌ DENIED")
                    + " (remaining: " + result.getRemaining() + ")");
        }

        // --- Fixed Window Demo ---
        System.out.println("\n--- Fixed Window Counter (3 req/10sec) ---");
        FixedWindowCounter fwc = new FixedWindowCounter(3, 10_000);
        for (int i = 0; i < 5; i++) {
            RateLimitResult result = fwc.allowRequest();
            System.out.println("  Request " + (i + 1) + ": " + (result.isAllowed() ? "✅ ALLOWED" : "❌ DENIED")
                    + " (remaining: " + result.getRemaining() + ")");
        }

        // --- Rate Limiter Manager Demo ---
        System.out.println("\n--- Rate Limiter Manager (Multiple Rules) ---");
        RateLimiterManager manager = new RateLimiterManager();

        // Rule: user_123 can make 100 requests per minute (Token Bucket)
        manager.addRule(new RateLimitRule("user:123", 100, 60_000,
                RateLimitRule.Algorithm.TOKEN_BUCKET));

        // Rule: IP 192.168.1.1 can make 10 requests per second (Sliding Window Counter)
        manager.addRule(new RateLimitRule("ip:192.168.1.1", 10, 1000,
                RateLimitRule.Algorithm.SLIDING_WINDOW_COUNTER));

        // Simulate requests
        for (int i = 0; i < 5; i++) {
            RateLimitResult result = manager.allowRequest("user:123");
            System.out.println("  user:123 req " + (i + 1) + ": "
                    + (result.isAllowed() ? "✅" : "❌")
                    + " remaining=" + result.getRemaining());
        }

        for (int i = 0; i < 12; i++) {
            RateLimitResult result = manager.allowRequest("ip:192.168.1.1");
            System.out.println("  ip:192.168.1.1 req " + (i + 1) + ": "
                    + (result.isAllowed() ? "✅" : "❌")
                    + " remaining=" + result.getRemaining());
            Thread.sleep(50);
        }

        // --- Distributed Rate Limiter Demo ---
        System.out.println("\n--- Distributed Rate Limiter ---");
        DistributedRateLimiter distributedLimiter = new DistributedRateLimiter(5, 10_000);
        for (int i = 0; i < 8; i++) {
            RateLimitResult result = distributedLimiter.allowRequest("api:my-service");
            System.out.println("  Request " + (i + 1) + ": " + (result.isAllowed() ? "✅ ALLOWED" : "❌ DENIED")
                    + " (X-RateLimit-Remaining: " + result.getRemaining() + ")");
        }

        System.out.println("\n========================================");
        System.out.println("KEY DESIGN DECISIONS:");
        System.out.println("========================================");
        System.out.println("• Token Bucket: Best for API rate limiting (allows bursts)");
        System.out.println("• Sliding Window Counter: Memory efficient + accurate");
        System.out.println("• Fixed Window: Simple but has boundary spike problem");
        System.out.println("• Sliding Window Log: Most accurate but memory heavy");
        System.out.println("• Leaky Bucket: Best for smoothing outgoing traffic");
        System.out.println("• Redis Lua scripting for atomic distributed operations");
        System.out.println("• Local cache + async sync for sub-millisecond latency");
        System.out.println("• Standard rate limit headers (X-RateLimit-*)");
        System.out.println("========================================");
    }
}
