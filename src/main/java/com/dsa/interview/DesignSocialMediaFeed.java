package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * =====================================================
 * SYSTEM DESIGN: SOCIAL MEDIA FEED (Twitter-like)
 * =====================================================
 *
 * =====================================================
 * HIGH-LEVEL DESIGN (HLD)
 * =====================================================
 *
 * --- REQUIREMENTS ---
 * Functional:
 * 1. Users can post tweets (text, images, video)
 * 2. Users can follow/unfollow other users
 * 3. Home timeline shows tweets from followed users (chronological)
 * 4. Like, retweet, reply to tweets
 * 5. Trending topics / hashtags
 * 6. Search tweets
 * 7. User profiles with tweet history
 *
 * Non-Functional:
 * - 500M+ users, 200M DAU, 500M+ tweets/day
 * - Home timeline loads in < 500ms
 * - 99.99% availability
 * - Eventually consistent (it's OK if feed is slightly stale)
 * - Handle celebrity users (100M+ followers)
 *
 * --- SYSTEM ARCHITECTURE ---
 *
 * ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
 * │   Client     │────▶│   API        │────▶│  Write Path  │
 * │   (App/Web)  │     │   Gateway    │     │              │
 * └──────────────┘     └──────────────┘     └──────┬───────┘
 *                                                    │
 * ┌──────────────────────────────────────────────────┼──────────────┐
 * │                    FAN-OUT WRITE PATH            │              │
 * │  ┌──────────┐  ┌──────────┐  ┌─────────────────▼──────────┐  │
 * │  │ Tweet    │──▶│ Kafka    │──▶│  Fan-out Service         │  │
 * │  │ Service  │  │ (Queue)  │  │  (Pre-compute timelines)  │  │
 * │  └──────────┘  └──────────┘  └──────┬───────────────────┬┘  │
 * │                                      │                   │    │
 * │            ┌─────────────────────────┘                   │    │
 * │            │   Celebrities (>10K followers)              │    │
 * │            │   → Fan-out on READ                         │    │
 * │            │                                             │    │
 * │            │   Regular users (<10K followers)            │    │
 * │            │   → Fan-out on WRITE                        │    │
 * │            ▼                                             │    │
 * │  ┌──────────────────┐          ┌──────────────────────┐  │    │
 * │  │ Timeline Cache   │          │ Tweet Store          │  │    │
 * │  │ (Redis)          │          │ (Cassandra)          │  │    │
 * │  │ user:{id}:timeline│          │ tweets:{id}          │  │    │
 * │  └──────────────────┘          └──────────────────────┘  │    │
 * │                                                           │    │
 * │                    READ PATH                              │    │
 * │  ┌──────────┐  ┌──────────────────┐  ┌────────────────┐  │    │
 * │  │  Feed    │──▶│ Timeline Cache   │──▶│  Merge + Rank  │  │    │
 * │  │ Service  │  │  + Celeb Tweets   │  │  (If fan-out   │  │    │
 * │  └──────────┘  │  (Redis + Pull)   │  │   on read)     │  │    │
 * │                 └──────────────────┘  └────────────────┘  │    │
 * └──────────────────────────────────────────────────────────────┘
 *
 * --- FEED GENERATION APPROACHES ---
 *
 * 1. FAN-OUT ON WRITE (PUSH):
 *    When User A tweets:
 *    → Insert tweet into timeline of ALL followers
 *    Pros: O(1) read, fast timeline loading
 *    Cons: Slow writes for celebrities
 *
 * 2. FAN-OUT ON READ (PULL):
 *    When User B views timeline:
 *    → Fetch recent tweets from all followed users
 *    → Merge and sort
 *    Pros: Works for any number of followers
 *    Cons: Slow reads, complex merging
 *
 * 3. HYBRID (RECOMMENDED):
 *    Regular users (< 10K followers): Fan-out on write
 *    Celebrities (≥ 10K followers): Fan-out on read
 *    → Best of both worlds
 *
 * --- TIMELINE CACHE (Redis) ---
 * Key: timeline:{user_id}
 * Type: Sorted Set
 * Members: tweet_id
 * Score: tweet_timestamp
 *
 * ZADD timeline:{user_id} timestamp tweet_id
 * ZREVRANGE timeline:{user_id} 0 99 → Get 100 latest tweets
 * ZREMRANGEBYSCORE timeline:{user_id} 0 cutoff → Remove old tweets
 *
 * --- DATABASE SCHEMA ---
 *
 * Users (PostgreSQL/MySQL):
 *   id (UUID PK)
 *   username (VARCHAR, UNIQUE)
 *   display_name (VARCHAR)
 *   bio (TEXT)
 *   created_at (TIMESTAMP)
 *
 * Tweets (Cassandra):
 *   tweet_id (TIMEUUID PK)
 *   user_id (UUID)
 *   content (TEXT)
 *   media_urls (TEXT[])
 *   like_count (INT)
 *   retweet_count (INT)
 *   reply_to_id (UUID, nullable)
 *   created_at (TIMESTAMP)
 *   PRIMARY KEY ((user_id), created_at) -- clustering for user timelines
 *
 * Follows (MySQL/PostgreSQL):
 *   follower_id (UUID)
 *   followee_id (UUID)
 *   created_at (TIMESTAMP)
 *   PRIMARY KEY (follower_id, followee_id)
 *   INDEX on followee_id for follower count
 *
 * Likes (Cassandra):
 *   user_id (UUID)
 *   tweet_id (UUID)
 *   created_at (TIMESTAMP)
 *   PRIMARY KEY ((user_id), tweet_id)
 *
 * =====================================================
 * LOW-LEVEL DESIGN (LLD) - Java Implementation
 * =====================================================
 */
public class DesignSocialMediaFeed {

    // =====================================================
    // 1. CORE DATA MODELS
    // =====================================================

    /**
     * Tweet model.
     */
    public static class Tweet {
        private final String tweetId;
        private final String userId;
        private final String content;
        private final List<String> mediaUrls;
        private final String replyToTweetId;

        private volatile int likeCount;
        private volatile int retweetCount;
        private volatile int replyCount;
        private final long createdAt;

        public Tweet(String tweetId, String userId, String content,
                     List<String> mediaUrls, String replyToTweetId) {
            this.tweetId = tweetId;
            this.userId = userId;
            this.content = content;
            this.mediaUrls = mediaUrls;
            this.replyToTweetId = replyToTweetId;
            this.likeCount = 0;
            this.retweetCount = 0;
            this.replyCount = 0;
            this.createdAt = System.currentTimeMillis();
        }

        public String getTweetId() { return tweetId; }
        public String getUserId() { return userId; }
        public String getContent() { return content; }
        public List<String> getMediaUrls() { return mediaUrls; }
        public long getCreatedAt() { return createdAt; }
        public int getLikeCount() { return likeCount; }
        public int getRetweetCount() { return retweetCount; }
        public int getReplyCount() { return replyCount; }

        public void incrementLikes() { this.likeCount++; }
        public void incrementRetweets() { this.retweetCount++; }

        @Override
        public String toString() {
            return "@" + userId + ": " + content.substring(0, Math.min(30, content.length())) + "...";
        }
    }

    /**
     * User profile.
     */
    public static class User {
        private final String userId;
        private final String username;
        private final String displayName;
        private int followerCount;
        private int followingCount;
        private final long createdAt;

        public User(String userId, String username, String displayName) {
            this.userId = userId;
            this.username = username;
            this.displayName = displayName;
            this.followerCount = 0;
            this.followingCount = 0;
            this.createdAt = System.currentTimeMillis();
        }

        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getDisplayName() { return displayName; }
        public int getFollowerCount() { return followerCount; }
        public int getFollowingCount() { return followingCount; }
        public void incrementFollowers() { this.followerCount++; }
        public void decrementFollowers() { this.followerCount--; }
        public void incrementFollowing() { this.followingCount++; }
        public void decrementFollowing() { this.followingCount--; }

        /**
         * Considered a "celebrity" if over 10K followers.
         */
        public boolean isCelebrity() {
            return followerCount >= 10_000;
        }
    }

    // =====================================================
    // 2. FOLLOW GRAPH
    // =====================================================

    /**
     * FollowGraph - manages follow relationships.
     * In production: backed by MySQL/PostgreSQL with denormalized Redis caches.
     */
    public static class FollowGraph {
        // follower_id → set of followee_ids (who I follow)
        private final ConcurrentHashMap<String, Set<String>> following = new ConcurrentHashMap<>();
        // followee_id → set of follower_ids (who follows me)
        private final ConcurrentHashMap<String, Set<String>> followers = new ConcurrentHashMap<>();
        // user_id → User object
        private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

        public void addUser(User user) {
            users.put(user.getUserId(), user);
            following.put(user.getUserId(), ConcurrentHashMap.newKeySet());
            followers.put(user.getUserId(), ConcurrentHashMap.newKeySet());
        }

        /**
         * User 'follower' follows 'followee'.
         */
        public void follow(String followerId, String followeeId) {
            if (followerId.equals(followeeId)) return;

            boolean added = following.computeIfAbsent(followerId, k -> ConcurrentHashMap.newKeySet())
                    .add(followeeId);
            if (added) {
                followers.computeIfAbsent(followeeId, k -> ConcurrentHashMap.newKeySet())
                        .add(followerId);

                User follower = users.get(followerId);
                User followee = users.get(followeeId);
                if (follower != null) follower.incrementFollowing();
                if (followee != null) followee.incrementFollowers();
            }
        }

        /**
         * User 'follower' unfollows 'followee'.
         */
        public void unfollow(String followerId, String followeeId) {
            Set<String> followingSet = following.get(followerId);
            if (followingSet != null && followingSet.remove(followeeId)) {
                Set<String> followersSet = followers.get(followeeId);
                if (followersSet != null) followersSet.remove(followerId);

                User follower = users.get(followerId);
                User followee = users.get(followeeId);
                if (follower != null) follower.decrementFollowing();
                if (followee != null) followee.decrementFollowers();
            }
        }

        /**
         * Get all users that 'userId' follows.
         */
        public Set<String> getFollowing(String userId) {
            return following.getOrDefault(userId, Collections.emptySet());
        }

        /**
         * Get all followers of 'userId'.
         */
        public Set<String> getFollowers(String userId) {
            return followers.getOrDefault(userId, Collections.emptySet());
        }

        /**
         * Check if 'follower' follows 'followee'.
         */
        public boolean isFollowing(String followerId, String followeeId) {
            Set<String> followingSet = following.get(followerId);
            return followingSet != null && followingSet.contains(followeeId);
        }

        public User getUser(String userId) { return users.get(userId); }
        public int getFollowerCount(String userId) {
            return followers.getOrDefault(userId, Collections.emptySet()).size();
        }
        public int getFollowingCount(String userId) {
            return following.getOrDefault(userId, Collections.emptySet()).size();
        }
    }

    // =====================================================
    // 3. TWEET STORE
    // =====================================================

    /**
     * TweetStore - persists tweets and supports retrieval.
     * In production: backed by Cassandra for write-optimized time-series.
     */
    public static class TweetStore {
        // tweet_id → Tweet
        private final ConcurrentHashMap<String, Tweet> tweets = new ConcurrentHashMap<>();
        // user_id → sorted list of tweet IDs (newest first)
        private final ConcurrentHashMap<String, List<String>> userTweets = new ConcurrentHashMap<>();
        // Max tweets per user to keep in memory
        private static final int MAX_TWEETS_PER_USER = 1000;

        /**
         * Save a tweet.
         */
        public void saveTweet(Tweet tweet) {
            tweets.put(tweet.getTweetId(), tweet);
            userTweets.computeIfAbsent(tweet.getUserId(), k -> new CopyOnWriteArrayList<>())
                    .add(0, tweet.getTweetId());

            // Trim to limit
            List<String> userTweetList = userTweets.get(tweet.getUserId());
            if (userTweetList.size() > MAX_TWEETS_PER_USER) {
                List<String> toRemove = userTweetList.subList(MAX_TWEETS_PER_USER, userTweetList.size());
                for (String oldId : toRemove) {
                    tweets.remove(oldId);
                }
                toRemove.clear();
            }
        }

        /**
         * Get a tweet by ID.
         */
        public Tweet getTweet(String tweetId) {
            return tweets.get(tweetId);
        }

        /**
         * Get tweets by a specific user (for profile page).
         */
        public List<Tweet> getUserTweets(String userId, int limit) {
            List<String> tweetIds = userTweets.get(userId);
            if (tweetIds == null) return Collections.emptyList();
            return tweetIds.stream()
                    .limit(limit)
                    .map(tweets::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        /**
         * Get tweets by multiple users (for timeline feed).
         */
        public List<Tweet> getTweetsByUsers(Set<String> userIds, int limit) {
            List<Tweet> result = new ArrayList<>();
            for (String userId : userIds) {
                List<String> tweetIds = userTweets.get(userId);
                if (tweetIds != null) {
                    for (String tweetId : tweetIds) {
                        Tweet tweet = tweets.get(tweetId);
                        if (tweet != null) {
                            result.add(tweet);
                        }
                        if (result.size() >= limit * userIds.size()) break;
                    }
                }
            }
            // Sort by time (newest first)
            result.sort(Comparator.comparingLong(Tweet::getCreatedAt).reversed());
            return result.stream().limit(limit).collect(Collectors.toList());
        }

        /**
         * Get tweets by specific tweet IDs.
         */
        public List<Tweet> getTweetsByIds(List<String> tweetIds) {
            return tweetIds.stream()
                    .map(tweets::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        public void incrementLikes(String tweetId) {
            Tweet tweet = tweets.get(tweetId);
            if (tweet != null) tweet.incrementLikes();
        }

        public void incrementRetweets(String tweetId) {
            Tweet tweet = tweets.get(tweetId);
            if (tweet != null) tweet.incrementRetweets();
        }
    }

    // =====================================================
    // 4. FAN-OUT SERVICE (Timeline generation)
    // =====================================================

    /**
     * FanOutService - pre-computes timelines using hybrid approach.
     *
     * - Regular users (< 10K followers): Fan-out on write (push to followers)
     * - Celebrities (≥ 10K followers): Fan-out on read (stored separately)
     */
    public static class FanOutService {
        private final FollowGraph followGraph;
        private final TweetStore tweetStore;
        // Timeline cache: user_id → sorted list of tweet IDs
        private final ConcurrentHashMap<String, List<String>> timelineCache = new ConcurrentHashMap<>();
        // Celebrity tweet cache: list of recent celebrity tweets
        private final LinkedList<Tweet> celebrityTweetCache = new LinkedList<>();
        // Max tweets in celebrity cache
        private static final int CELEBRITY_CACHE_SIZE = 10_000;
        // Max timeline size per user
        private static final int TIMELINE_SIZE = 500;

        // Simulated async queue (in production: Kafka)
        private final ExecutorService fanOutExecutor = Executors.newFixedThreadPool(8);

        public FanOutService(FollowGraph followGraph, TweetStore tweetStore) {
            this.followGraph = followGraph;
            this.tweetStore = tweetStore;
        }

        /**
         * Handle a new tweet - fan out to followers.
         */
        public void onNewTweet(Tweet tweet) {
            String userId = tweet.getUserId();
            User user = followGraph.getUser(userId);

            // Persist tweet
            tweetStore.saveTweet(tweet);

            if (user != null && user.isCelebrity()) {
                // CELEBRITY: Fan-out on read
                // Store tweet in celebrity cache, followers pull on read
                handleCelebrityTweet(tweet);
            } else {
                // REGULAR USER: Fan-out on write
                // Push tweet to all followers' timelines
                handleRegularUserTweet(tweet);
            }
        }

        /**
         * Fan-out on write: push tweet to all followers.
         * In production: done asynchronously via Kafka.
         */
        private void handleRegularUserTweet(Tweet tweet) {
            String userId = tweet.getUserId();
            Set<String> followers = followGraph.getFollowers(userId);

            System.out.println("  [FanOut] Pushing tweet " + tweet.getTweetId()
                    + " by " + userId + " to " + followers.size() + " followers");

            // Async fan-out (in production: Kafka consumer processes this)
            fanOutExecutor.submit(() -> {
                for (String followerId : followers) {
                    addToTimeline(followerId, tweet.getTweetId());
                }
            });
        }

        /**
         * Fan-out on read: store in celebrity cache.
         * Followers will pull these when they load their timeline.
         */
        private void handleCelebrityTweet(Tweet tweet) {
            synchronized (celebrityTweetCache) {
                celebrityTweetCache.addFirst(tweet);
                while (celebrityTweetCache.size() > CELEBRITY_CACHE_SIZE) {
                    celebrityTweetCache.removeLast();
                }
            }
            System.out.println("  [FanOut] Celebrity tweet " + tweet.getTweetId()
                    + " by " + tweet.getUserId() + " → fan-out on read");
        }

        /**
         * Get timeline for a user (merge push + pull).
         */
        public List<Tweet> getTimeline(String userId, int offset, int limit) {
            List<String> cachedIds = timelineCache.get(userId);
            List<Tweet> timeline = new ArrayList<>();

            // 1. Get tweets from push-based cache
            if (cachedIds != null) {
                List<String> pageIds = cachedIds.stream()
                        .skip(offset)
                        .limit(limit)
                        .collect(Collectors.toList());
                timeline.addAll(tweetStore.getTweetsByIds(pageIds));
            }

            // 2. Check if user follows any celebrities (pull-based)
            Set<String> following = followGraph.getFollowing(userId);
            for (String followeeId : following) {
                User followee = followGraph.getUser(followeeId);
                if (followee != null && followee.isCelebrity()) {
                    // Pull recent tweets from this celebrity
                    List<Tweet> celebTweets = tweetStore.getUserTweets(followeeId, 10);
                    timeline.addAll(celebTweets);
                }
            }

            // 3. Sort by time and deduplicate
            timeline.sort(Comparator.comparingLong(Tweet::getCreatedAt).reversed());
            timeline = timeline.stream()
                    .distinct()
                    .limit(limit)
                    .collect(Collectors.toList());

            return timeline;
        }

        /**
         * Add a tweet to a user's timeline cache.
         */
        private void addToTimeline(String userId, String tweetId) {
            timelineCache.compute(userId, (key, existingList) -> {
                if (existingList == null) {
                    existingList = new CopyOnWriteArrayList<>();
                }
                existingList.add(0, tweetId);
                // Trim to max size
                while (existingList.size() > TIMELINE_SIZE) {
                    existingList.remove(existingList.size() - 1);
                }
                return existingList;
            });
        }

        /**
         * Like a tweet.
         */
        public void likeTweet(String userId, String tweetId) {
            tweetStore.incrementLikes(tweetId);
        }

        /**
         * Retweet a tweet.
         */
        public void retweet(String userId, String tweetId) {
            tweetStore.incrementRetweets(tweetId);
            // In production: create a retweet entity
        }
    }

    // =====================================================
    // 5. TRENDING TOPICS
    // =====================================================

    /**
     * TrendingTopics - tracks trending hashtags and topics.
     * In production: uses sliding window counters in Redis.
     */
    public static class TrendingTopics {
        // hashtag → count in current window
        private final ConcurrentHashMap<String, AtomicInteger> hashtagCounts = new ConcurrentHashMap<>();
        // Window reset timestamp
        private volatile long windowStartMs;
        private static final long WINDOW_DURATION_MS = 3600_000; // 1 hour

        public TrendingTopics() {
            this.windowStartMs = System.currentTimeMillis();
            // Periodic reset
            Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(this::maybeResetWindow, 5, 5, TimeUnit.MINUTES);
        }

        /**
         * Extract hashtags from tweet content and record them.
         */
        public void extractAndRecord(String content) {
            // Simple regex-free extraction
            int idx = 0;
            while ((idx = content.indexOf('#', idx)) != -1) {
                int end = idx + 1;
                while (end < content.length() && Character.isLetterOrDigit(content.charAt(end))) {
                    end++;
                }
                if (end > idx + 1) {
                    String hashtag = content.substring(idx, end).toLowerCase();
                    recordHashtag(hashtag);
                }
                idx = end;
            }
        }

        /**
         * Record a hashtag occurrence.
         */
        public void recordHashtag(String hashtag) {
            hashtagCounts.computeIfAbsent(hashtag, k -> new AtomicInteger()).incrementAndGet();
        }

        /**
         * Get trending topics (sorted by count).
         */
        public List<String> getTrending(int limit) {
            return hashtagCounts.entrySet().stream()
                    .sorted(Map.Entry.<String, AtomicInteger>comparingByValue(
                            Comparator.comparingInt(AtomicInteger::get)).reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        private void maybeResetWindow() {
            long now = System.currentTimeMillis();
            if (now - windowStartMs >= WINDOW_DURATION_MS) {
                hashtagCounts.clear();
                windowStartMs = now;
            }
        }
    }

    // =====================================================
    // 6. SOCIAL MEDIA SERVICE (Main facade)
    // =====================================================

    /**
     * SocialMediaService - main entry point for the Twitter-like system.
     */
    public static class SocialMediaService {
        private final FollowGraph followGraph;
        private final TweetStore tweetStore;
        private final FanOutService fanOutService;
        private final TrendingTopics trendingTopics;
        private final AtomicLong tweetIdCounter = new AtomicLong(System.currentTimeMillis());

        public SocialMediaService() {
            this.followGraph = new FollowGraph();
            this.tweetStore = new TweetStore();
            this.fanOutService = new FanOutService(followGraph, tweetStore);
            this.trendingTopics = new TrendingTopics();
        }

        /**
         * Register a new user.
         */
        public User registerUser(String userId, String username, String displayName) {
            User user = new User(userId, username, displayName);
            followGraph.addUser(user);
            return user;
        }

        /**
         * Follow a user.
         */
        public void follow(String followerId, String followeeId) {
            followGraph.follow(followerId, followeeId);
            User followee = followGraph.getUser(followeeId);
            System.out.println("  [Social] User " + followerId + " followed " + followeeId
                    + " (followers: " + (followee != null ? followee.getFollowerCount() : "?") + ")");
        }

        /**
         * Unfollow a user.
         */
        public void unfollow(String followerId, String followeeId) {
            followGraph.unfollow(followerId, followeeId);
        }

        /**
         * Post a tweet.
         */
        public Tweet postTweet(String userId, String content, List<String> mediaUrls) {
            String tweetId = generateTweetId();
            Tweet tweet = new Tweet(tweetId, userId, content,
                    mediaUrls != null ? mediaUrls : Collections.emptyList(), null);

            fanOutService.onNewTweet(tweet);

            // Extract hashtags for trending
            trendingTopics.extractAndRecord(content);

            System.out.println("  [Social] @" + userId + " tweeted: "
                    + content.substring(0, Math.min(40, content.length())) + "...");
            return tweet;
        }

        /**
         * Reply to a tweet.
         */
        public Tweet replyToTweet(String userId, String content, String replyToTweetId) {
            String tweetId = generateTweetId();
            Tweet reply = new Tweet(tweetId, userId, content, Collections.emptyList(), replyToTweetId);
            tweetStore.saveTweet(reply);
            return reply;
        }

        /**
         * Get home timeline for a user.
         */
        public List<Tweet> getHomeTimeline(String userId, int offset, int limit) {
            long startTime = System.nanoTime();
            List<Tweet> timeline = fanOutService.getTimeline(userId, offset, limit);
            long duration = (System.nanoTime() - startTime) / 1_000_000;

            System.out.println("  [Social] Timeline for @" + userId
                    + ": " + timeline.size() + " tweets loaded in " + duration + "ms");
            return timeline;
        }

        /**
         * Get user profile tweets.
         */
        public List<Tweet> getUserTweets(String userId, int limit) {
            return tweetStore.getUserTweets(userId, limit);
        }

        /**
         * Like a tweet.
         */
        public void likeTweet(String userId, String tweetId) {
            fanOutService.likeTweet(userId, tweetId);
            Tweet tweet = tweetStore.getTweet(tweetId);
            if (tweet != null) {
                System.out.println("  [Social] @" + userId + " liked tweet "
                        + tweetId + " (now " + tweet.getLikeCount() + " likes)");
            }
        }

        /**
         * Get trending topics.
         */
        public List<String> getTrendingTopics(int limit) {
            return trendingTopics.getTrending(limit);
        }

        private String generateTweetId() {
            return "tweet_" + tweetIdCounter.incrementAndGet();
        }

        public FollowGraph getFollowGraph() { return followGraph; }
        public TweetStore getTweetStore() { return tweetStore; }
    }

    // =====================================================
    // 7. DEMONSTRATION
    // =====================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("SOCIAL MEDIA FEED DESIGN (Twitter-like)");
        System.out.println("========================================\n");

        // Initialize service
        SocialMediaService twitter = new SocialMediaService();

        // Create users
        System.out.println("--- Creating Users ---");
        User alice = twitter.registerUser("alice", "alice123", "Alice Johnson");
        User bob = twitter.registerUser("bob", "bob_smith", "Bob Smith");
        User charlie = twitter.registerUser("charlie", "charlie_dev", "Charlie Brown");
        User celebrity = twitter.registerUser("celebrity", "star_power", "Famous Star");

        // Make celebrity have many followers (for demo purposes)
        // We'll simulate by directly setting follower count
        celebrity.followerCount = 15_000; // Mark as celebrity (>10K)
        System.out.println("  Created: @" + alice.getUsername() + ", @" + bob.getUsername()
                + ", @" + charlie.getUsername() + ", @" + celebrity.getUsername()
                + " (celebrity: " + celebrity.isCelebrity() + ")");

        // Follow relationships
        System.out.println("\n--- Follow Actions ---");
        twitter.follow("alice", "bob");
        twitter.follow("alice", "charlie");
        twitter.follow("alice", "celebrity");
        twitter.follow("bob", "alice");
        twitter.follow("bob", "celebrity");
        twitter.follow("charlie", "alice");

        // Post tweets
        System.out.println("\n--- Posting Tweets ---");
        twitter.postTweet("alice", "Hello world! My first tweet #introduction", null);
        Thread.sleep(100);
        twitter.postTweet("bob", "Working on a new project. #coding #java", null);
        Thread.sleep(100);
        twitter.postTweet("charlie", "Just discovered consistent hashing - game changer! #systemdesign", null);
        Thread.sleep(100);

        // Celebrity tweets (fan-out on read)
        twitter.postTweet("celebrity", "Excited to announce my new movie coming out! #exclusive #movies", null);
        Thread.sleep(100);
        twitter.postTweet("celebrity", "Thanks for all the love and support! #grateful", null);

        // Wait for async fan-out
        Thread.sleep(500);

        // View home timelines
        System.out.println("\n--- Alice's Home Timeline ---");
        List<Tweet> aliceTimeline = twitter.getHomeTimeline("alice", 0, 10);
        for (Tweet tweet : aliceTimeline) {
            System.out.println("  @" + tweet.getUserId() + ": " + tweet.getContent()
                    + " (❤️ " + tweet.getLikeCount() + ")");
        }

        System.out.println("\n--- Bob's Home Timeline ---");
        List<Tweet> bobTimeline = twitter.getHomeTimeline("bob", 0, 10);
        for (Tweet tweet : bobTimeline) {
            System.out.println("  @" + tweet.getUserId() + ": " + tweet.getContent());
        }

        // Like tweets
        System.out.println("\n--- Interactions ---");
        twitter.likeTweet("alice", "tweet_" + (System.currentTimeMillis() - 400));
        twitter.likeTweet("bob", "tweet_" + (System.currentTimeMillis() - 300));

        // Trending topics
        System.out.println("\n--- Trending Topics ---");
        List<String> trending = twitter.getTrendingTopics(5);
        System.out.println("  Trending now:");
        for (String topic : trending) {
            System.out.println("    " + topic);
        }

        // User profile
        System.out.println("\n--- User Profile: @alice123 ---");
        User aliceProfile = twitter.getFollowGraph().getUser("alice");
        System.out.println("  Display: " + aliceProfile.getDisplayName());
        System.out.println("  Followers: " + aliceProfile.getFollowerCount());
        System.out.println("  Following: " + aliceProfile.getFollowingCount());
        List<Tweet> aliceTweets = twitter.getUserTweets("alice", 5);
        System.out.println("  Tweets:");
        for (Tweet tweet : aliceTweets) {
            System.out.println("    • " + tweet.getContent());
        }

        System.out.println("\n========================================");
        System.out.println("KEY DESIGN DECISIONS:");
        System.out.println("========================================");
        System.out.println("• Hybrid fan-out: push for regular users, pull for celebrities");
        System.out.println("• Redis sorted sets for timeline cache (O(log N) operations)");
        System.out.println("• Kafka for async fan-out processing (decouple writes)");
        System.out.println("• Cassandra for tweet storage (write-optimized time-series)");
        System.out.println("• PostgreSQL for follow graph (relational, ACID)");
        System.out.println("• Timeline loaded in < 500ms via pre-computed cache");
        System.out.println("• Trending topics with sliding window counters");
        System.out.println("• Eventually consistent: feed may lag by few seconds");
        System.out.println("• Fan-out on read for celebrities avoids write amplification");
        System.out.println("========================================");
    }
}
