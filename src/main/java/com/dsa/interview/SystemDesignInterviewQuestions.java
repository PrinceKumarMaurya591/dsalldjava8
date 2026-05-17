package com.dsa.interview;

import java.util.*;

/**
 * System Design Interview Questions - Concepts & Patterns
 * 
 * Covers: Design Fundamentals, Scalability, Databases, Caching,
 * Load Balancing, Microservices, CAP Theorem, Consistent Hashing,
 * Design Patterns (URL Shortener, Chat System, Rate Limiter,
 * Distributed Cache, Notification System, etc.)
 */
public class SystemDesignInterviewQuestions {

    // =============================================
    // 1. DESIGN FUNDAMENTALS
    // =============================================

    /**
     * Q1: Key Characteristics of Distributed Systems
     * 
     * 1. Scalability - Handle growing load
     *    - Vertical Scaling: bigger machine (limited)
     *    - Horizontal Scaling: more machines (preferred)
     * 
     * 2. Reliability - System continues to work despite failures
     *    - Redundancy, failover, replication
     * 
     * 3. Availability - System is accessible when needed
     *    - Measured as uptime percentage (99.9%, 99.99%, 99.999%)
     * 
     * 4. Performance - Response time, throughput, latency
     *    - Latency: time to process one request
     *    - Throughput: requests per second
     * 
     * 5. Consistency - All nodes see the same data
     *    - Strong, Eventual, Causal consistency
     * 
     * 6. Maintainability - Easy to operate, debug, update
     *    - Monitoring, logging, CI/CD, documentation
     */

    /**
     * Q2: CAP Theorem
     * 
     * A distributed system can only guarantee 2 of 3:
     * - Consistency (C): All nodes see the same data at the same time
     * - Availability (A): Every request gets a response (success/failure)
     * - Partition Tolerance (P): System continues despite network failures
     * 
     * Since network partitions are inevitable, we choose between CP and AP:
     * 
     * CP (Consistency + Partition Tolerance):
     * - Bank systems, payment processing
     * - Block writes during partition until consistency restored
     * - Examples: HBase, MongoDB (default), Zookeeper
     * 
     * AP (Availability + Partition Tolerance):
     * - Social media, content delivery
     * - Accept stale reads during partition
     * - Examples: Cassandra, DynamoDB, CouchDB
     * 
     * CA (Consistency + Availability) - Not possible in distributed systems
     * - Single-node databases (MySQL, PostgreSQL without replication)
     */

    /**
     * Q3: Consistent Hashing
     * 
     * Problem: In a distributed cache with N nodes, when a node is added/removed,
     * simple hashing (hash(key) % N) causes most keys to remap.
     * 
     * Solution: Consistent Hashing
     * - Hash ring: 0 to 2^32-1 (circular space)
     * - Each server is placed on the ring (hash of server IP)
     * - Each key is placed on the ring (hash of key)
     * - Key is stored on the nearest server clockwise
     * 
     * When a server is added/removed:
     * - Only keys in the affected range need to move
     * - Average: only K/N keys remap (K = total keys, N = servers)
     * 
     * Virtual Nodes:
     * - Each physical server has multiple virtual nodes on the ring
     * - Better load distribution
     * - Handles heterogeneous server capacities
     * 
     * Used by: DynamoDB, Cassandra, Discord, Akamai CDN
     */

    // =============================================
    // 2. SCALABILITY PATTERNS
    // =============================================

    /**
     * Q4: Load Balancing Algorithms
     * 
     * 1. Round Robin - Requests distributed sequentially
     *    - Simple, but doesn't account for server load
     * 
     * 2. Least Connections - Send to server with fewest active connections
     *    - Good for varying request processing times
     * 
     * 3. Least Response Time - Send to fastest responding server
     *    - Requires monitoring response times
     * 
     * 4. IP Hash - Hash of client IP determines server
     *    - Ensures same client goes to same server (session affinity)
     * 
     * 5. Weighted Round Robin - Servers with higher capacity get more requests
     *    - Good for heterogeneous servers
     * 
     * 6. Random - Random selection
     *    - Simple, works well with large server pools
     */

    /**
     * Q5: Caching Strategies
     * 
     * 1. Cache-Aside (Lazy Loading):
     *    - Application checks cache first
     *    - On miss, loads from DB, updates cache
     *    - Pros: only cached data that's requested
     *    - Cons: cache miss penalty, stale data
     * 
     * 2. Write-Through:
     *    - Write to cache and DB simultaneously
     *    - Pros: cache always consistent with DB
     *    - Cons: higher write latency, cache churn
     * 
     * 3. Write-Behind (Write-Back):
     *    - Write to cache first, async write to DB
     *    - Pros: very fast writes
     *    - Cons: data loss risk if cache fails
     * 
     * 4. Read-Through:
     *    - Cache sits between app and DB
     *    - Cache loads data from DB on miss
     *    - Pros: transparent to application
     * 
     * Cache Eviction Policies:
     * - LRU (Least Recently Used) - Most common
     * - LFU (Least Frequently Used)
     * - FIFO (First In First Out)
     * - TTL (Time To Live) - Expire after fixed time
     */

    /**
     * Q6: Database Scaling Strategies
     * 
     * 1. Read Replicas:
     *    - Master handles writes, replicas handle reads
     *    - Improves read throughput
     *    - Replication lag can cause stale reads
     * 
     * 2. Sharding (Horizontal Partitioning):
     *    - Split data across multiple databases
     *    - Shard key determines which database
     *    - Challenges: resharding, complex queries, joins
     * 
     * 3. Vertical Partitioning:
     *    - Split table by columns (frequently vs rarely accessed)
     *    - Example: user profile in one DB, user photos in another
     * 
     * 4. Denormalization:
     *    - Store redundant data to avoid joins
     *    - Improves read performance
     *    - Challenges: data consistency, update complexity
     * 
     * 5. Database Federation:
     *    - Split by function (users DB, orders DB, products DB)
     *    - Each DB handles its own domain
     *    - Challenges: joins across domains
     */

    // =============================================
    // 3. SYSTEM DESIGN PROBLEMS
    // =============================================

    /**
     * Q7: Design URL Shortener (like TinyURL)
     * 
     * Requirements:
     * - Generate short, unique URLs
     * - Redirect short URL to original
     * - Handle 100M URLs/month, 100:1 read/write ratio
     * 
     * Key Components:
     * - Web servers (load balanced)
     * - Cache (Redis) for hot URLs
     * - Database for URL mappings
     * 
     * URL Encoding:
     * - Base62 (a-z, A-Z, 0-9) = 62 characters
     * - 7 chars → 62^7 ≈ 3.5 trillion combinations
     * 
     * Approaches for generating short keys:
     * 1. Hash + collision resolution (MD5, Base62 encode)
     * 2. Distributed ID generator (Snowflake, Redis INCR)
     * 3. Pre-generated keys (KGS - Key Generation Service)
     * 
     * API Design:
     * POST /shorten { url: "https://example.com/very-long-url" }
     *   → { short_url: "https://short.ly/abc123" }
     * GET /{short_key}
     *   → 301 Redirect to original URL
     * 
     * Database Schema:
     * id (BIGINT PK)
     * short_key (VARCHAR, UNIQUE, INDEXED)
     * original_url (TEXT)
     * created_at (TIMESTAMP)
     * expires_at (TIMESTAMP, nullable)
     * click_count (BIGINT)
     * 
     * Scaling:
     * - Cache frequently accessed URLs (LRU)
     * - Database sharding by short_key
     * - CDN for static content
     */

    /**
     * Q8: Design WhatsApp / Chat System
     * 
     * Requirements:
     * - One-on-one and group chat
     * - Real-time delivery (low latency)
     * - Support 1B users, 100M DAU
     * - Messages: text, images, files
     * - Online/offline status
     * - Message history
     * 
     * Key Components:
     * - Chat servers (WebSocket connections)
     * - Message queue (Kafka) for async processing
     * - Database for message persistence
     * - CDN for media files
     * - Presence service (Redis)
     * 
     * WebSocket Connection Flow:
     * 1. Client connects to Chat Server via WebSocket
     * 2. Chat Server registers connection in Redis (user → server mapping)
     * 3. When User A sends message to User B:
     *    a. Chat Server checks if B is online (Redis)
     *    b. If online, forward to B's Chat Server
     *    c. If offline, store in DB, push notification
     * 
     * Message Flow:
     * Sender → Chat Server → Message Queue → 
     *   → Recipient's Chat Server (if online)
     *   → Database (persist)
     *   → Push Notification (if offline)
     * 
     * Database Schema:
     * messages: id, sender_id, receiver_id, group_id, content, 
     *           message_type, created_at, read_at
     * conversations: id, participant_ids, last_message, last_message_at
     * 
     * Key Design Decisions:
     * - Use WebSocket for real-time bidirectional communication
     * - Use Kafka for reliable message delivery
     * - Use Cassandra for message storage (write-optimized)
     * - Use Redis for presence and typing indicators
     * - Use S3/CDN for media files
     * 
     * For Group Chat:
     * - Fan-out on write: store message once, read by all
     * - Fan-out on read: store message per recipient
     * - Hybrid: small groups fan-out on write, large groups on read
     */

    /**
     * Q9: Design Rate Limiter
     * 
     * Requirements:
     * - Limit API requests per user/IP within time window
     * - Low latency (adds < 1ms overhead)
     * - Distributed (works across multiple servers)
     * 
     * Algorithms:
     * 
     * 1. Token Bucket:
     *    - Bucket holds tokens (max capacity)
     *    - Tokens added at fixed rate
     *    - Request consumes one token
     *    - If no tokens, request is rejected
     *    - Pros: smooth traffic, handles bursts
     * 
     * 2. Leaky Bucket:
     *    - Queue of requests processed at fixed rate
     *    - If queue full, request is rejected
     *    - Pros: smooth output, no bursts
     * 
     * 3. Fixed Window Counter:
     *    - Count requests in fixed time window (e.g., 1 minute)
     *    - Reset counter at window boundary
     *    - Problem: traffic spike at window boundary
     * 
     * 4. Sliding Window Log:
     *    - Track timestamps of all requests
     *    - Count requests in sliding window
     *    - Pros: accurate, Cons: memory intensive
     * 
     * 5. Sliding Window Counter:
     *    - Hybrid of fixed window + sliding log
     *    - Weighted count based on overlap
     *    - Used by: Kong, Redis
     * 
     * Distributed Rate Limiting:
     * - Use Redis with Lua scripting (atomic operations)
     * - INCR + EXPIRE for counter
     * - Sorted sets for sliding window
     * 
     * // Redis Lua script for sliding window counter
     * // KEYS[1] = rate limiter key
     * // ARGV[1] = window size (ms)
     * // ARGV[2] = max requests
     * // ARGV[3] = current timestamp
     * 
     * local current = redis.call('INCR', KEYS[1])
     * if current == 1 then
     *     redis.call('PEXPIRE', KEYS[1], ARGV[1])
     * end
     * return current <= tonumber(ARGV[2])
     */

    /**
     * Q10: Design Distributed Cache (like Redis)
     * 
     * Requirements:
     * - Fast read/write (microsecond latency)
     * - Distributed across multiple nodes
     * - High availability (no single point of failure)
     * - Data persistence (optional)
     * 
     * Key Components:
     * - Cache nodes (in-memory data store)
     * - Consistent hashing for data distribution
     * - Replication for fault tolerance
     * - Cluster manager for node coordination
     * 
     * Data Structures:
     * - String: simple key-value
     * - List: ordered collection (LPUSH, RPOP)
     * - Set: unique elements (SADD, SISMEMBER)
     * - Sorted Set: ordered by score (ZADD, ZRANGE)
     * - Hash: field-value pairs (HSET, HGET)
     * 
     * Replication:
     * - Master-slave: master handles writes, slaves replicate
     * - Sentinel: automatic failover if master fails
     * - Cluster: automatic sharding + replication
     * 
     * Persistence Options:
     * - RDB (snapshot): periodic full snapshots
     * - AOF (append-only file): every write operation logged
     * - Both: RDB for recovery, AOF for durability
     * 
     * Eviction Policies:
     * - noeviction: return errors on write when memory full
     * - allkeys-lru: evict least recently used keys
     * - volatile-lru: evict LRU keys with TTL set
     * - allkeys-random: evict random keys
     * - volatile-ttl: evict keys with shortest TTL
     */

    /**
     * Q11: Design Notification System
     * 
     * Requirements:
     * - Send push notifications, emails, SMS
     * - Support 10M+ notifications/day
     * - Reliable delivery (retry on failure)
     * - Real-time delivery
     * 
     * Components:
     * - API Gateway: receive notification requests
     * - Notification Queue: decouple processing
     * - Notification Workers: process different channels
     * - Template Service: manage notification templates
     * - Preference Service: user notification preferences
     * - Rate Limiter: prevent spam
     * 
     * Flow:
     * 1. Service sends notification request via API
     * 2. Request goes to Notification Queue (Kafka)
     * 3. Workers pick up and process:
     *    - Check user preferences (opt-in/out)
     *    - Apply rate limiting
     *    - Render template
     *    - Send via appropriate channel
     * 4. Update notification status (sent/failed)
     * 
     * Database Schema:
     * notifications: id, user_id, channel, title, body,
     *                status, created_at, sent_at
     * templates: id, name, channel, content (with placeholders)
     * user_preferences: user_id, channel, enabled, quiet_hours
     * 
     * Reliability:
     * - Retry queue for failed notifications
     * - Dead letter queue for persistent failures
     * - Idempotency keys to prevent duplicates
     * 
     * Real-time Delivery:
     * - WebSocket for in-app notifications
     * - Firebase Cloud Messaging (FCM) for mobile push
     * - Amazon SNS for email/SMS
     */

    /**
     * Q12: Design Uber / Ride-Hailing Service
     * 
     * Requirements:
     * - Real-time driver location tracking
     * - Match riders with nearby drivers
     * - Handle 100M+ users, 10M+ rides/day
     * - ETA calculation, pricing, payment
     * 
     * Key Components:
     * - Location Service: receives GPS updates from drivers
     * - Dispatch Service: matches riders with drivers
     * - Map Service: route calculation, ETA
     * - Pricing Service: surge pricing, fare calculation
     * - Payment Service: process payments
     * - Trip Service: manage ride lifecycle
     * 
     * Location Updates:
     * - Drivers send GPS every 3-5 seconds via WebSocket
     * - Store latest location in Redis (geospatial index)
     * - GEOADD driver_id longitude latitude
     * - GEORADIUS longitude latitude radius_meters
     * 
     * Matching Algorithm:
     * 1. Rider requests ride with pickup location
     * 2. Query Redis for nearby drivers (GEORADIUS)
     * 3. Filter by driver availability and preferences
     * 4. Calculate ETA for each candidate
     * 5. Send ride request to top N drivers
     * 6. First to accept gets the ride
     * 
     * QuadTree for Location Indexing:
     * - Divide map into grid cells
     * - Each cell stores drivers in that area
     * - Efficient for range queries (nearby drivers)
     * - Dynamic grid sizing based on density
     * 
     * Database Schema:
     * users: id, name, phone, payment_method
     * drivers: id, user_id, vehicle_type, license, status
     * trips: id, rider_id, driver_id, pickup, dropoff,
     *        status, fare, created_at, completed_at
     * locations: driver_id, latitude, longitude, updated_at
     * 
     * Scaling Considerations:
     * - Shard by region/city
     * - Use Kafka for trip events
     * - Use Redis for real-time location
     * - Use Cassandra for trip history (write-heavy)
     */

    /**
     * Q13: Design Netflix / Video Streaming Platform
     * 
     * Requirements:
     * - Stream video to millions of concurrent users
     * - Support multiple devices and resolutions
     * - Low buffering, adaptive bitrate
     * - Content recommendation
     * 
     * Key Components:
     * - CDN: serve video content from edge locations
     * - Encoding Pipeline: transcode videos to multiple formats
     * - Content Delivery Service: manage streaming sessions
     * - Recommendation Service: personalized suggestions
     * - User Service: profiles, watch history
     * 
     * Video Encoding:
     * - Original video → multiple resolutions (360p, 480p, 720p, 1080p, 4K)
     * - Each resolution split into small chunks (2-10 seconds)
     * - HLS (HTTP Live Streaming) or MPEG-DASH protocol
     * 
     * Adaptive Bitrate Streaming:
     * - Client monitors network conditions
     * - Requests appropriate quality chunk
     * - Smooth transition between qualities
     * - No buffering if network degrades
     * 
     * CDN Strategy:
     * - Store popular content at edge (cache)
     * - Long-tail content served from origin
     * - Open Connect: Netflix's own CDN appliances at ISP locations
     * 
     * Recommendation System:
     * - Collaborative filtering: users with similar taste
     * - Content-based filtering: similar content attributes
     * - Matrix factorization: SVD, ALS
     * - Real-time: view history, ratings, time of day
     * - Batch: daily model training
     * 
     * Database:
     * - Cassandra: user metadata, watch history
     * - MySQL: billing, user accounts
     * - Elasticsearch: content search
     * - S3: video storage
     */

    /**
     * Q14: Design Twitter / Social Media Feed
     * 
     * Requirements:
     * - Post tweets (text, images, video)
     * - View timeline (home feed)
     * - Follow/unfollow users
     * - 500M users, 200M DAU, 500M tweets/day
     * 
     * Feed Generation Approaches:
     * 
     * 1. Fan-out on Write (Push):
     *    - When user tweets, pre-compute feeds for all followers
     *    - Insert tweet into each follower's feed cache
     *    - Pros: fast read (O(1) to get feed)
     *    - Cons: slow for celebrities (millions of followers)
     * 
     * 2. Fan-out on Read (Pull):
     *    - When user views feed, fetch tweets from followed users
     *    - Merge and rank in real-time
     *    - Pros: works for celebrities
     *    - Cons: slow read (O(N) to merge)
     * 
     * 3. Hybrid:
     *    - Regular users: fan-out on write
     *    - Celebrities (>10K followers): fan-out on read
     *    - Best of both approaches
     * 
     * Timeline Cache (Redis):
     * - Each user has a sorted set (timeline)
     * - Key: timeline:{user_id}
     * - Members: tweet_ids with score = timestamp
     * - ZREVRANGE timeline:{user_id} 0 99 → get latest 100 tweets
     * 
     * Database Schema:
     * users: id, name, handle, bio, created_at
     * tweets: id, user_id, content, media_urls, created_at
     * follows: follower_id, followee_id, created_at
     * likes: user_id, tweet_id, created_at
     * 
     * Key Design Decisions:
     * - Use Redis for timeline cache
     * - Use Cassandra for tweet storage (write-optimized)
     * - Use Kafka for async feed fan-out
     * - Use CDN for media content
     * - Use search index (Elasticsearch) for tweet search
     */

    /**
     * Q15: Design Distributed ID Generator
     * 
     * Requirements:
     * - Generate unique IDs across distributed system
     * - IDs should be sortable by time
     * - High availability, low latency
     * - Handle 10K+ IDs/second
     * 
     * Approaches:
     * 
     * 1. UUID:
     *    - 128-bit, universally unique
     *    - Pros: simple, no coordination
     *    - Cons: not sortable, large (36 chars), bad for DB indexing
     * 
     * 2. Database Auto-Increment:
     *    - Use DB sequence or AUTO_INCREMENT
     *    - Pros: simple, sequential
     *    - Cons: single point of failure, bottleneck
     * 
     * 3. Snowflake (Twitter):
     *    - 64-bit ID composed of:
     *      - 1 bit: sign (always 0)
     *      - 41 bits: timestamp (milliseconds, ~69 years)
     *      - 10 bits: worker/machine ID (1024 machines)
     *      - 12 bits: sequence number (4096 IDs/ms per machine)
     *    - Pros: sortable, compact, distributed
     *    - Cons: clock skew issues
     * 
     * 4. Redis INCR:
     *    - Use Redis atomic increment
     *    - Pros: fast, sequential
     *    - Cons: Redis is single point (use Redis Cluster)
     * 
     * 5. Database Range:
     *    - Allocate ranges to each server
     *    - Server 1: 1-1000, Server 2: 1001-2000
     *    - When range exhausted, request new range
     *    - Used by: Flickr, Instagram
     */

    // =============================================
    // 4. DESIGN PATTERNS IN SYSTEM DESIGN
    // =============================================

    /**
     * Q16: Common Architectural Patterns
     * 
     * 1. Event-Driven Architecture:
     *    - Components communicate via events
     *    - Loose coupling, scalability
     *    - Event sourcing, CQRS
     * 
     * 2. Microservices Architecture:
     *    - Small, independent services
     *    - Each service has its own database
     *    - API Gateway for routing
     *    - Service discovery, circuit breakers
     * 
     * 3. Saga Pattern (Distributed Transactions):
     *    - Choreography: each service publishes events
     *    - Orchestration: central coordinator manages steps
     *    - Compensating transactions for rollback
     * 
     * 4. CQRS (Command Query Responsibility Segregation):
     *    - Separate read and write models
     *    - Optimize each for its purpose
     *    - Eventual consistency between models
     * 
     * 5. Strangler Fig Pattern:
     *    - Gradually replace legacy system
     *    - Route functionality piece by piece
     *    - Eventually strangle the old system
     * 
     * 6. Circuit Breaker:
     *    - Prevent cascading failures
     *    - States: Closed → Open → Half-Open
     *    - Fail fast when downstream is down
     * 
     * 7. Bulkhead:
     *    - Isolate resources per service/tenant
     *    - One failure doesn't bring down entire system
     *    - Thread pools, connection pools per service
     */

    // =============================================
    // MAIN METHOD
    // =============================================

    public static void main(String[] args) {
        System.out.println("SYSTEM DESIGN INTERVIEW QUESTIONS\n");
        System.out.println("This file contains conceptual system design interview questions.\n");
        System.out.println("Topics covered:");
        System.out.println("1. Distributed Systems Fundamentals (Scalability, CAP Theorem)");
        System.out.println("2. Consistent Hashing");
        System.out.println("3. Load Balancing Algorithms");
        System.out.println("4. Caching Strategies (Cache-Aside, Write-Through, Write-Behind)");
        System.out.println("5. Database Scaling (Sharding, Replication, Federation)");
        System.out.println("6. Design URL Shortener (TinyURL)");
        System.out.println("7. Design Chat System (WhatsApp)");
        System.out.println("8. Design Rate Limiter (Token Bucket, Sliding Window)");
        System.out.println("9. Design Distributed Cache (Redis-like)");
        System.out.println("10. Design Notification System");
        System.out.println("11. Design Ride-Hailing Service (Uber)");
        System.out.println("12. Design Video Streaming Platform (Netflix)");
        System.out.println("13. Design Social Media Feed (Twitter)");
        System.out.println("14. Design Distributed ID Generator (Snowflake)");
        System.out.println("15. Architectural Patterns (Event-Driven, CQRS, Saga, Circuit Breaker)");

        System.out.println("\n================================================");
        System.out.println("DEMONSTRATION COMPLETE");
        System.out.println("================================================");
    }
}
