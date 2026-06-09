package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * =====================================================
 * SYSTEM DESIGN: VIDEO STREAMING PLATFORM (Netflix-like)
 * =====================================================
 *
 * =====================================================
 * HIGH-LEVEL DESIGN (HLD)
 * =====================================================
 *
 * --- REQUIREMENTS ---
 * Functional:
 * 1. Stream video content to millions of concurrent users
 * 2. Support multiple devices (mobile, tablet, TV, web)
 * 3. Adaptive bitrate streaming (auto-quality based on network)
 * 4. Multiple resolutions (360p, 480p, 720p, 1080p, 4K)
 * 5. Content recommendation engine
 * 6. User profiles, watch history, continue watching
 * 7. Search with auto-complete
 * 8. Download for offline viewing
 *
 * Non-Functional:
 * - 200M+ subscribers, 100M+ daily active users
 * - < 2s startup time (time-to-first-frame)
 * - 99.99% availability
 * - < 1% buffering ratio
 * - Global content delivery (CDN)
 *
 * --- SYSTEM ARCHITECTURE ---
 *
 * ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
 * │   Client     │────▶│   CDN Edge   │────▶│  Origin      │
 * │   (App/Brow) │     │  (Populated) │     │  Server      │
 * └──────────────┘     └──────────────┘     └──────┬───────┘
 *                                                    │
 * ┌──────────────────────────────────────────────────┼──────────────┐
 * │                                                  │              │
 * │  ┌─────────────┐  ┌──────────────┐  ┌──────────▼──────────┐  │
 * │  │ User/Subs   │  │ Content      │  │ Content Delivery    │  │
 * │  │ Service     │  │ Catalog Svc  │  │ Service (Streaming) │  │
 * │  └─────────────┘  └──────────────┘  └──────────┬──────────┘  │
 * │                                                  │              │
 * │  ┌─────────────┐  ┌──────────────┐  ┌──────────▼──────────┐  │
 * │  │ Recommend   │  │ Search       │  │ Encoding Pipeline   │  │
 * │  │ Service     │  │ Service      │  │ (Transcoding Farm)  │  │
 * │  └─────────────┘  └──────────────┘  └──────────┬──────────┘  │
 * │                                                  │              │
 * │  ┌─────────────┐  ┌──────────────┐  ┌──────────▼──────────┐  │
 * │  │ Analytics   │  │ Watch        │  │ Storage Layer       │  │
 * │  │ Service     │  │ History Svc  │  │ (S3/Blob)           │  │
 * │  └─────────────┘  └──────────────┘  └─────────────────────┘  │
 * └──────────────────────────────────────────────────────────────┘
 *
 * --- KEY COMPONENTS ---
 *
 * 1. CDN (Content Delivery Network):
 *    - Netflix Open Connect: own CDN appliances at ISP locations
 *    - Cache popular content at edge nodes (80% of traffic)
 *    - Long-tail content served from origin
 *    - Regional caching hierarchy
 *
 * 2. Encoding Pipeline (Media Processing):
 *    - Original master file → multiple bitrate versions
 *    - Each version split into chunks (2-10 seconds)
 *    - HLS (HTTP Live Streaming) or MPEG-DASH
 *    - Manifest files describe available qualities/segments
 *
 * 3. Content Delivery Service:
 *    - Handles streaming session management
 *    - Generates secure URLs (token-based auth, expiry)
 *    - Directs client to optimal CDN node
 *    - Tracks playback analytics
 *
 * 4. Recommendation Engine:
 *    - Offline: ML model training (Spark, TensorFlow)
 *    - Online: real-time personalization
 *    - Collaborative filtering + content-based filtering
 *    - A/B testing framework
 *
 * 5. Adaptive Bitrate (ABR) Algorithm:
 *    - Client-side: monitors buffer and bandwidth
 *    - Request next chunk at appropriate quality
 *    - Smooth transition: gradual quality changes
 *    - Buffer-based: keep 10-30s buffer to avoid rebuffering
 *
 * --- VIDEO ENCODING ---
 *
 * Input Master (4K, 50Mbps)
 * ├── 2160p (4K)  ─── 15-25 Mbps
 * ├── 1440p (2K)  ─── 8-12 Mbps
 * ├── 1080p (HD)  ─── 4-6 Mbps
 * ├── 720p        ─── 2-3 Mbps
 * ├── 480p        ─── 1-1.5 Mbps
 * └── 360p        ─── 0.5-0.8 Mbps
 *
 * Each resolution → segmented into 6-second chunks
 * Chunk naming: video_id/resolution/segment_{number}.ts
 * Manifest: video_id/manifest.m3u8 (HLS)
 *
 * --- DATABASE SCHEMA ---
 *
 * Content Catalog (MySQL/PostgreSQL):
 *   video_id (UUID PK)
 *   title (VARCHAR)
 *   description (TEXT)
 *   genre (VARCHAR)
 *   release_year (INT)
 *   duration_min (INT)
 *   maturity_rating (VARCHAR)
 *   cast (TEXT[])
 *   average_rating (FLOAT)
 *
 * User Watch History (Cassandra):
 *   user_id (UUID)
 *   video_id (UUID)
 *   progress_sec (INT)       -- where they left off
 *   completed (BOOLEAN)
 *   rating (INT)              -- user rating (1-5)
 *   watched_at (TIMESTAMP)
 *   device_type (VARCHAR)
 *   PRIMARY KEY ((user_id), watched_at)
 *
 * Video Metadata (Elasticsearch):
 *   Indexed for full-text search
 *   Fields: title, description, cast, genre
 *
 * =====================================================
 * LOW-LEVEL DESIGN (LLD) - Java Implementation
 * =====================================================
 */
public class DesignVideoStreamingPlatform {

    // =====================================================
    // 1. CORE DATA MODELS
    // =====================================================

    public enum VideoQuality {
        P360(360, "360p", 500_000),    // 500 Kbps
        P480(480, "480p", 1_000_000),   // 1 Mbps
        P720(720, "720p", 2_500_000),   // 2.5 Mbps
        P1080(1080, "1080p", 5_000_000), // 5 Mbps
        P1440(1440, "2K", 10_000_000),   // 10 Mbps
        P2160(2160, "4K", 20_000_000);   // 20 Mbps

        final int height;
        final String label;
        final int bitrateBps;

        VideoQuality(int height, String label, int bitrateBps) {
            this.height = height;
            this.label = label;
            this.bitrateBps = bitrateBps;
        }

        public int getHeight() { return height; }
        public String getLabel() { return label; }
        public int getBitrateBps() { return bitrateBps; }
    }

    /**
     * Video metadata (content catalog).
     */
    public static class VideoMetadata {
        private final String videoId;
        private final String title;
        private final String description;
        private final String genre;
        private final int releaseYear;
        private final int durationMinutes;
        private final List<String> cast;
        private final double averageRating;

        public VideoMetadata(String videoId, String title, String description,
                             String genre, int releaseYear, int durationMinutes,
                             List<String> cast, double averageRating) {
            this.videoId = videoId;
            this.title = title;
            this.description = description;
            this.genre = genre;
            this.releaseYear = releaseYear;
            this.durationMinutes = durationMinutes;
            this.cast = cast;
            this.averageRating = averageRating;
        }

        public String getVideoId() { return videoId; }
        public String getTitle() { return title; }
        public String getGenre() { return genre; }
        public int getDurationMinutes() { return durationMinutes; }
        public double getAverageRating() { return averageRating; }
        public List<String> getCast() { return cast; }
    }

    /**
     * Video segment (chunk) metadata.
     */
    public static class VideoSegment {
        private final String videoId;
        private final VideoQuality quality;
        private final int segmentNumber;
        private final long byteSize;
        private final long durationMs;

        public VideoSegment(String videoId, VideoQuality quality,
                            int segmentNumber, long byteSize, long durationMs) {
            this.videoId = videoId;
            this.quality = quality;
            this.segmentNumber = segmentNumber;
            this.byteSize = byteSize;
            this.durationMs = durationMs;
        }

        public VideoQuality getQuality() { return quality; }
        public int getSegmentNumber() { return segmentNumber; }

        public String getSegmentUrl() {
            return String.format("/content/%s/%s/seg_%04d.ts",
                    videoId, quality.getLabel(), segmentNumber);
        }

        public long getByteSize() { return byteSize; }
        public long getDurationMs() { return durationMs; }
    }

    /**
     * Playback session - tracks a user's viewing session.
     */
    public static class PlaybackSession {
        private final String sessionId;
        private final String userId;
        private final String videoId;
        private final String deviceType;
        private volatile VideoQuality currentQuality;
        private volatile int currentSegment;
        private volatile long lastHeartbeat;
        private volatile long startedAt;
        private volatile int totalSegmentsWatched;

        public PlaybackSession(String sessionId, String userId, String videoId,
                               String deviceType, VideoQuality startQuality) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.videoId = videoId;
            this.deviceType = deviceType;
            this.currentQuality = startQuality;
            this.currentSegment = 0;
            this.lastHeartbeat = System.currentTimeMillis();
            this.startedAt = System.currentTimeMillis();
            this.totalSegmentsWatched = 0;
        }

        public void recordHeartbeat() { this.lastHeartbeat = System.currentTimeMillis(); }
        public void advanceSegment(int segmentNum) {
            this.currentSegment = segmentNum;
            this.totalSegmentsWatched++;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public int getWatchProgressSeconds() {
            return currentSegment * 6; // 6 seconds per segment
        }

        // Getters
        public String getSessionId() { return sessionId; }
        public String getUserId() { return userId; }
        public String getVideoId() { return videoId; }
        public VideoQuality getCurrentQuality() { return currentQuality; }
        public void setCurrentQuality(VideoQuality q) { this.currentQuality = q; }
        public String getDeviceType() { return deviceType; }
    }

    // =====================================================
    // 2. VIDEO CATALOG SERVICE
    // =====================================================

    /**
     * Content Catalog Service - manages video metadata and search.
     */
    public static class CatalogService {
        private final ConcurrentHashMap<String, VideoMetadata> catalog = new ConcurrentHashMap<>();
        // Inverted index for simple search
        private final ConcurrentHashMap<String, Set<String>> searchIndex = new ConcurrentHashMap<>();

        public void addVideo(VideoMetadata video) {
            catalog.put(video.getVideoId(), video);
            // Index for search
            indexVideo(video);
        }

        public VideoMetadata getVideo(String videoId) {
            return catalog.get(videoId);
        }

        /**
         * Simple full-text search (in production: Elasticsearch).
         */
        public List<VideoMetadata> search(String query) {
            Set<String> resultIds = new HashSet<>();
            String lowerQuery = query.toLowerCase();

            // Check if we have exact index match
            Set<String> indexed = searchIndex.get(lowerQuery);
            if (indexed != null) {
                resultIds.addAll(indexed);
            }

            // Fallback: linear scan (for demo only)
            for (VideoMetadata video : catalog.values()) {
                if (video.getTitle().toLowerCase().contains(lowerQuery)
                        || video.getGenre().toLowerCase().contains(lowerQuery)
                        || video.getCast().stream().anyMatch(c -> c.toLowerCase().contains(lowerQuery))) {
                    resultIds.add(video.getVideoId());
                }
            }

            return resultIds.stream()
                    .map(catalog::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        /**
         * Get recommendations based on genre/cast (simplified).
         */
        public List<VideoMetadata> getRecommendations(String videoId, int limit) {
            VideoMetadata current = catalog.get(videoId);
            if (current == null) return Collections.emptyList();

            return catalog.values().stream()
                    .filter(v -> !v.getVideoId().equals(videoId))
                    .filter(v -> v.getGenre().equals(current.getGenre())
                            || v.getCast().stream().anyMatch(c -> current.getCast().contains(c)))
                    .sorted(Comparator.comparingDouble(VideoMetadata::getAverageRating).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        /**
         * Get popular videos (by rating).
         */
        public List<VideoMetadata> getPopular(int limit) {
            return catalog.values().stream()
                    .sorted(Comparator.comparingDouble(VideoMetadata::getAverageRating).reversed())
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        private void indexVideo(VideoMetadata video) {
            String[] terms = (video.getTitle() + " " + video.getGenre() + " "
                    + String.join(" ", video.getCast())).toLowerCase().split("\\s+");
            for (String term : terms) {
                searchIndex.computeIfAbsent(term, k -> ConcurrentHashMap.newKeySet())
                        .add(video.getVideoId());
            }
        }
    }

    // =====================================================
    // 3. ENCODING PIPELINE (Transcoding)
    // =====================================================

    /**
     * EncodingService - simulates video transcoding pipeline.
     * In production: distributed transcoding farm using FFmpeg.
     */
    public static class EncodingService {
        private final ConcurrentHashMap<String, Map<VideoQuality, List<VideoSegment>>> encodedVideos;
        private static final int SEGMENT_DURATION_MS = 6000; // 6-second chunks

        public EncodingService() {
            this.encodedVideos = new ConcurrentHashMap<>();
        }

        /**
         * "Transcode" a video into multiple qualities with segments.
         * In production, this is a distributed job:
         * 1. Split video into segments (ffmpeg -f segment)
         * 2. Encode each segment at each quality level
         * 3. Generate manifest file (.m3u8 or .mpd)
         * 4. Upload to CDN origin
         */
        public void transcode(String videoId, int durationMinutes) {
            System.out.println("  [Encoder] Transcoding " + videoId
                    + " (" + durationMinutes + " min)...");

            Map<VideoQuality, List<VideoSegment>> qualityMap = new ConcurrentHashMap<>();
            int totalSegments = (durationMinutes * 60 * 1000) / SEGMENT_DURATION_MS;

            for (VideoQuality quality : VideoQuality.values()) {
                List<VideoSegment> segments = new ArrayList<>();
                for (int i = 0; i < totalSegments; i++) {
                    // Simulated byte size based on bitrate and duration
                    long byteSize = (quality.getBitrateBps() * SEGMENT_DURATION_MS / 1000) / 8;
                    segments.add(new VideoSegment(videoId, quality, i, byteSize, SEGMENT_DURATION_MS));
                }
                qualityMap.put(quality, segments);
            }

            encodedVideos.put(videoId, qualityMap);
            System.out.println("  [Encoder] Completed: " + videoId
                    + " → " + VideoQuality.values().length + " qualities × "
                    + totalSegments + " segments = "
                    + (VideoQuality.values().length * totalSegments) + " chunks");
        }

        /**
         * Get manifest for a video (available qualities and segments).
         * In production, returns .m3u8 (HLS) or .mpd (DASH) format.
         */
        public String getManifest(String videoId) {
            Map<VideoQuality, List<VideoSegment>> qualityMap = encodedVideos.get(videoId);
            if (qualityMap == null) return null;

            StringBuilder manifest = new StringBuilder();
            manifest.append("#EXTM3U\n#EXT-X-VERSION:6\n\n");

            for (VideoQuality quality : VideoQuality.values()) {
                List<VideoSegment> segments = qualityMap.get(quality);
                if (segments != null && !segments.isEmpty()) {
                    manifest.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                            .append(quality.getBitrateBps())
                            .append(",RESOLUTION=1920x")
                            .append(quality.getHeight())
                            .append("\n")
                            .append(videoId).append("/").append(quality.getLabel())
                            .append("/playlist.m3u8\n\n");
                }
            }
            return manifest.toString();
        }

        /**
         * Get segment for streaming.
         */
        public VideoSegment getSegment(String videoId, VideoQuality quality, int segmentNum) {
            Map<VideoQuality, List<VideoSegment>> qualityMap = encodedVideos.get(videoId);
            if (qualityMap == null) return null;
            List<VideoSegment> segments = qualityMap.get(quality);
            if (segments == null || segmentNum >= segments.size()) return null;
            return segments.get(segmentNum);
        }

        /**
         * Get total segments for a video at a given quality.
         */
        public int getTotalSegments(String videoId, VideoQuality quality) {
            Map<VideoQuality, List<VideoSegment>> qualityMap = encodedVideos.get(videoId);
            if (qualityMap == null) return 0;
            List<VideoSegment> segments = qualityMap.get(quality);
            return segments != null ? segments.size() : 0;
        }

        public boolean isReady(String videoId) {
            return encodedVideos.containsKey(videoId);
        }
    }

    // =====================================================
    // 4. ADAPTIVE BITRATE (ABR) ALGORITHM
    // =====================================================

    /**
     * AdaptiveBitrateController - client-side ABR algorithm.
     *
     * Strategy: Buffer-based ABR (BOLA-like)
     * - Maintain target buffer of 20 seconds
     * - Monitor download speed of last N segments
     * - If buffer < 10s: step down quality
     * - If buffer > 30s: step up quality
     * - Smooth transitions: max 1 step change per segment
     */
    public static class AdaptiveBitrateController {
        // Client state
        private VideoQuality currentQuality;
        private final Deque<Long> downloadTimes = new LinkedList<>();
        private final Deque<Long> segmentSizes = new LinkedList<>();
        private volatile int bufferLevelSeconds;
        private static final int TARGET_BUFFER_SECONDS = 20;
        private static final int LOW_BUFFER_THRESHOLD = 10;
        private static final int HIGH_BUFFER_THRESHOLD = 30;
        // Moving average window
        private static final int THROUGHPUT_WINDOW = 5;

        // Available qualities (sorted lowest to highest)
        private final List<VideoQuality> availableQualities;

        public AdaptiveBitrateController(VideoQuality startQuality,
                                         List<VideoQuality> availableQualities) {
            this.currentQuality = startQuality;
            this.availableQualities = new ArrayList<>(availableQualities);
            this.availableQualities.sort(Comparator.comparingInt(VideoQuality::getHeight));
            this.bufferLevelSeconds = 0;
        }

        /**
         * Called after each segment download.
         * Determines the quality for the NEXT segment.
         */
        public VideoQuality onSegmentDownloaded(long segmentSizeBytes,
                                                 long downloadDurationMs,
                                                 int currentBufferSeconds) {
            this.bufferLevelSeconds = currentBufferSeconds;

            // Record throughput sample
            long throughputBps = (segmentSizeBytes * 8 * 1000) / Math.max(1, downloadDurationMs);
            downloadTimes.addLast(downloadDurationMs);
            segmentSizes.addLast(segmentSizeBytes);
            if (downloadTimes.size() > THROUGHPUT_WINDOW) {
                downloadTimes.pollFirst();
                segmentSizes.pollFirst();
            }

            // Calculate average throughput
            double avgThroughputBps = calculateAverageThroughput();

            // Make quality decision
            int currentIdx = getQualityIndex(currentQuality);

            // Buffer-based decision
            if (bufferLevelSeconds < LOW_BUFFER_THRESHOLD) {
                // Buffer too low - step DOWN one level
                currentIdx = Math.max(0, currentIdx - 1);
            } else if (bufferLevelSeconds > HIGH_BUFFER_THRESHOLD) {
                // Buffer healthy - try step UP
                int targetIdx = selectQualityByThroughput(avgThroughputBps);
                // Only step up one level at a time (smooth transitions)
                currentIdx = Math.min(targetIdx, currentIdx + 1);
            }

            // Ensure selected quality is within throughput capacity
            while (currentIdx > 0
                    && availableQualities.get(currentIdx).getBitrateBps() > avgThroughputBps * 0.8) {
                currentIdx--;
            }

            currentQuality = availableQualities.get(currentIdx);
            return currentQuality;
        }

        /**
         * Select best quality based on measured throughput.
         */
        private int selectQualityByThroughput(double throughputBps) {
            int bestIdx = 0;
            for (int i = 0; i < availableQualities.size(); i++) {
                // Need 80% of bitrate as safety margin
                if (availableQualities.get(i).getBitrateBps() <= throughputBps * 0.8) {
                    bestIdx = i;
                }
            }
            return bestIdx;
        }

        private int getQualityIndex(VideoQuality quality) {
            for (int i = 0; i < availableQualities.size(); i++) {
                if (availableQualities.get(i) == quality) return i;
            }
            return 0;
        }

        private double calculateAverageThroughput() {
            if (segmentSizes.isEmpty()) return 0;
            long totalBytes = segmentSizes.stream().mapToLong(Long::longValue).sum();
            long totalTime = downloadTimes.stream().mapToLong(Long::longValue).sum();
            return (totalBytes * 8 * 1000.0) / Math.max(1, totalTime);
        }

        public VideoQuality getCurrentQuality() { return currentQuality; }
        public int getBufferLevelSeconds() { return bufferLevelSeconds; }
    }

    // =====================================================
    // 5. STREAMING SERVICE
    // =====================================================

    /**
     * StreamingService - manages video streaming sessions.
     * Handles CDN routing, session management, and DRM.
     */
    public static class StreamingService {
        private final EncodingService encodingService;
        private final CatalogService catalogService;
        private final ConcurrentHashMap<String, PlaybackSession> activeSessions;
        // CDN simulation: node → list of cached content
        private final ConcurrentHashMap<String, Set<String>> cdnCache;
        private final Set<String> popularContent;
        private static final int POPULAR_THRESHOLD = 100; // views

        // CDN nodes
        private static final String[] CDN_NODES = {
                "us-east", "us-west", "eu-west", "eu-central",
                "ap-southeast", "ap-northeast", "sa-east"
        };

        public StreamingService(EncodingService encodingService,
                                CatalogService catalogService) {
            this.encodingService = encodingService;
            this.catalogService = catalogService;
            this.activeSessions = new ConcurrentHashMap<>();
            this.cdnCache = new ConcurrentHashMap<>();
            this.popularContent = ConcurrentHashMap.newKeySet();

            // Initialize CDN cache
            for (String node : CDN_NODES) {
                cdnCache.put(node, ConcurrentHashMap.newKeySet());
            }
        }

        /**
         * Start a streaming session.
         * Returns the optimal CDN node and initial segment URL.
         */
        public StreamSessionInfo startSession(String userId, String videoId,
                                               String deviceType, String region) {
            // Check if video is encoded and ready
            if (!encodingService.isReady(videoId)) {
                throw new IllegalStateException("Video not ready: " + videoId);
            }

            // Create session
            String sessionId = "session_" + UUID.randomUUID().toString().substring(0, 8);
            VideoQuality startQuality = getStartQuality(deviceType);
            PlaybackSession session = new PlaybackSession(sessionId, userId, videoId,
                    deviceType, startQuality);
            activeSessions.put(sessionId, session);

            // Find optimal CDN node
            String cdnNode = selectCDNNode(region, videoId);

            // Cache popular content at edge
            trackView(videoId, cdnNode);

            // Generate secure streaming URL (with token)
            String streamToken = generateStreamToken(sessionId, videoId, System.currentTimeMillis() + 3600000);

            System.out.println("  [Stream] Session started: " + sessionId
                    + " | User: " + userId + " | Video: " + videoId
                    + " | CDN: " + cdnNode + " | Quality: " + startQuality.getLabel());

            return new StreamSessionInfo(sessionId, cdnNode, streamToken, startQuality,
                    encodingService.getTotalSegments(videoId, startQuality));
        }

        /**
         * Get the next segment in the stream, potentially upgrading/downgrading quality.
         */
        public VideoSegment getNextSegment(String sessionId, int currentSegment,
                                            int currentBufferSeconds) {
            PlaybackSession session = activeSessions.get(sessionId);
            if (session == null) return null;

            // Get available qualities
            List<VideoQuality> qualities = Arrays.asList(VideoQuality.values());

            // Run ABR algorithm
            AdaptiveBitrateController abr = new AdaptiveBitrateController(
                    session.getCurrentQuality(), qualities);

            // Simulate segment download metrics (in production, reported by client)
            long simulatedSize = 500_000;
            long simulatedTime = 200;
            VideoQuality nextQuality = abr.onSegmentDownloaded(
                    simulatedSize, simulatedTime, currentBufferSeconds);

            session.setCurrentQuality(nextQuality);
            session.advanceSegment(currentSegment);

            return encodingService.getSegment(session.getVideoId(), nextQuality, currentSegment);
        }

        /**
         * End a streaming session and save watch progress.
         */
        public void endSession(String sessionId) {
            PlaybackSession session = activeSessions.remove(sessionId);
            if (session != null) {
                System.out.println("  [Stream] Session ended: " + sessionId
                        + " | Watched: " + session.getWatchProgressSeconds() + "s"
                        + " | Final quality: " + session.getCurrentQuality().getLabel());
                // In production: save watch progress to Cassandra
            }
        }

        /**
         * Select the best CDN node based on region and content popularity.
         */
        private String selectCDNNode(String region, String videoId) {
            // Map region to closest CDN node
            Map<String, String> regionMapping = new HashMap<>();
            regionMapping.put("us", "us-east");
            regionMapping.put("eu", "eu-west");
            regionMapping.put("asia", "ap-southeast");
            regionMapping.put("sa", "sa-east");

            String preferred = regionMapping.getOrDefault(region, "us-east");

            // For popular content, use edge cache
            if (popularContent.contains(videoId)) {
                return preferred; // Likely cached at edge
            }

            // For less popular, route to less loaded node
            return preferred;
        }

        /**
         * Track a view and promote to popular if threshold met.
         */
        private void trackView(String videoId, String cdnNode) {
            cdnCache.computeIfAbsent(cdnNode, k -> ConcurrentHashMap.newKeySet()).add(videoId);
            // Simplified popularity tracking
            if (Math.random() < 0.2) { // Simulate: 20% chance to become popular
                popularContent.add(videoId);
            }
        }

        /**
         * Determine initial quality based on device type.
         */
        private VideoQuality getStartQuality(String deviceType) {
            return switch (deviceType.toLowerCase()) {
                case "tv" -> VideoQuality.P2160;
                case "desktop" -> VideoQuality.P1080;
                case "tablet" -> VideoQuality.P720;
                case "mobile" -> VideoQuality.P480;
                default -> VideoQuality.P720;
            };
        }

        /**
         * Generate a secure streaming token (simplified JWT-like).
         */
        private String generateStreamToken(String sessionId, String videoId, long expiresAt) {
            String payload = sessionId + ":" + videoId + ":" + expiresAt;
            return Base64.getEncoder().encodeToString(payload.getBytes());
        }

        /**
         * Get content popularity statistics.
         */
        public Map<String, Object> getCDNStats() {
            Map<String, Object> stats = new LinkedHashMap<>();
            for (String node : CDN_NODES) {
                stats.put(node, cdnCache.getOrDefault(node, Collections.emptySet()).size() + " videos cached");
            }
            stats.put("popularContent", popularContent.size() + " titles");
            stats.put("activeSessions", activeSessions.size());
            return stats;
        }

        /**
         * Streaming session info returned to client.
         */
        public record StreamSessionInfo(
                String sessionId,
                String cdnNode,
                String streamToken,
                VideoQuality initialQuality,
                int totalSegments
        ) {}
    }

    // =====================================================
    // 6. WATCH HISTORY SERVICE
    // =====================================================

    /**
     * WatchHistoryService - tracks what users watch.
     * In production: backed by Cassandra (time-series).
     */
    public static class WatchHistoryService {
        // user_id → list of watch records (most recent first)
        private final ConcurrentHashMap<String, List<WatchRecord>> history = new ConcurrentHashMap<>();

        public record WatchRecord(
                String videoId,
                int progressSeconds,
                boolean completed,
                int userRating,
                long watchedAt
        ) {}

        /**
         * Record a watch event.
         */
        public void recordWatch(String userId, String videoId,
                                int progressSeconds, boolean completed, int rating) {
            history.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                    .add(0, new WatchRecord(videoId, progressSeconds, completed,
                            rating, System.currentTimeMillis()));

            // Keep only last 500 records per user
            List<WatchRecord> userHistory = history.get(userId);
            if (userHistory.size() > 500) {
                userHistory.subList(500, userHistory.size()).clear();
            }
        }

        /**
         * Get watch history for a user.
         */
        public List<WatchRecord> getHistory(String userId, int limit) {
            List<WatchRecord> userHistory = history.get(userId);
            if (userHistory == null) return Collections.emptyList();
            return userHistory.stream().limit(limit).collect(Collectors.toList());
        }

        /**
         * Get "Continue Watching" - videos not completed.
         */
        public List<WatchRecord> getContinueWatching(String userId) {
            List<WatchRecord> userHistory = history.get(userId);
            if (userHistory == null) return Collections.emptyList();
            return userHistory.stream()
                    .filter(r -> !r.completed() && r.progressSeconds() > 0)
                    .distinct()
                    .limit(20)
                    .collect(Collectors.toList());
        }
    }

    // =====================================================
    // 7. DEMONSTRATION
    // =====================================================

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("VIDEO STREAMING PLATFORM DESIGN (Netflix-like)");
        System.out.println("========================================\n");

        // Initialize services
        CatalogService catalog = new CatalogService();
        EncodingService encoder = new EncodingService();
        WatchHistoryService watchHistory = new WatchHistoryService();
        StreamingService streaming = new StreamingService(encoder, catalog);

        // Add content to catalog
        System.out.println("--- Content Catalog ---");
        catalog.addVideo(new VideoMetadata("v1", "Stranger Things", "Kids disappear in small town",
                "Sci-Fi", 2016, 50, Arrays.asList("Millie Brown", "Finn Wolfhard"), 8.7));
        catalog.addVideo(new VideoMetadata("v2", "The Crown", "Royal family drama",
                "Drama", 2016, 55, Arrays.asList("Claire Foy", "Matt Smith"), 8.6));
        catalog.addVideo(new VideoMetadata("v3", "Dark", "Time travel mystery",
                "Sci-Fi", 2017, 55, Arrays.asList("Louis Hofmann", "Lisa Vicari"), 8.8));
        catalog.addVideo(new VideoMetadata("v4", "Money Heist", "Bank heist thriller",
                "Thriller", 2017, 45, Arrays.asList("Ursula Corbero", "Alvaro Morte"), 8.3));
        catalog.addVideo(new VideoMetadata("v5", "Squid Game", "Survival game drama",
                "Thriller", 2021, 50, Arrays.asList("Lee Jung-jae", "Park Hae-soo"), 8.0));

        for (VideoMetadata v : catalog.search("")) {
            System.out.println("  " + v.getVideoId() + ": " + v.getTitle()
                    + " (" + v.getGenre() + ", " + v.getAverageRating() + "★)");
        }

        // Encode videos (simulated transcoding)
        System.out.println("\n--- Transcoding Pipeline ---");
        encoder.transcode("v1", 50);
        encoder.transcode("v2", 55);
        encoder.transcode("v3", 55);

        // Search demo
        System.out.println("\n--- Search ---");
        List<VideoMetadata> results = catalog.search("Sci-Fi");
        System.out.println("  Search 'Sci-Fi': " + results.stream()
                .map(VideoMetadata::getTitle).collect(Collectors.joining(", ")));

        // Recommendations demo
        System.out.println("\n--- Recommendations ---");
        List<VideoMetadata> recs = catalog.getRecommendations("v1", 3);
        System.out.println("  Because you watched 'Stranger Things':");
        for (VideoMetadata rec : recs) {
            System.out.println("    - " + rec.getTitle() + " (" + rec.getGenre() + ")");
        }

        // Start streaming session
        System.out.println("\n--- Streaming Session ---");
        StreamingService.StreamSessionInfo sessionInfo =
                streaming.startSession("user_alice", "v1", "tv", "us");

        System.out.println("  Session: " + sessionInfo.sessionId());
        System.out.println("  CDN Node: " + sessionInfo.cdnNode());
        System.out.println("  Initial Quality: " + sessionInfo.initialQuality().getLabel());
        System.out.println("  Total Segments: " + sessionInfo.totalSegments());

        // Simulate streaming segments with ABR
        System.out.println("\n--- Adaptive Bitrate Streaming ---");
        int bufferSeconds = 25; // Start with healthy buffer
        for (int seg = 0; seg < 5; seg++) {
            VideoSegment segment = streaming.getNextSegment(
                    sessionInfo.sessionId(), seg, bufferSeconds);

            if (segment != null) {
                System.out.println("  Segment " + seg + ": "
                        + segment.getQuality().getLabel()
                        + " | URL: " + segment.getSegmentUrl()
                        + " | Size: " + (segment.getByteSize() / 1024) + " KB");
            }

            // Simulate buffer changes
            bufferSeconds = Math.max(5, bufferSeconds + (int)(Math.random() * 10) - 3);
        }

        // End session and record watch history
        System.out.println("\n--- Watch History ---");
        streaming.endSession(sessionInfo.sessionId());
        watchHistory.recordWatch("user_alice", "v1", 120, false, 0);

        // Continue watching
        List<WatchHistoryService.WatchRecord> continueWatching =
                watchHistory.getContinueWatching("user_alice");
        System.out.println("  Continue Watching: " + continueWatching.size() + " videos");

        // HLS Manifest demo
        System.out.println("\n--- HLS Manifest (v1) ---");
        String manifest = encoder.getManifest("v1");
        System.out.println(manifest);

        // CDN Stats
        System.out.println("--- CDN Cache Status ---");
        Map<String, Object> stats = streaming.getCDNStats();
        stats.forEach((k, v) -> System.out.println("  " + k + ": " + v));

        System.out.println("\n========================================");
        System.out.println("KEY DESIGN DECISIONS:");
        System.out.println("========================================");
        System.out.println("• HLS/DASH adaptive streaming with segmented video (6s chunks)");
        System.out.println("• ABR algorithm: buffer-based (BOLA) + throughput-based");
        System.out.println("• CDN caching: popular content at edge (80% cache hit)");
        System.out.println("• Open Connect: Netflix's ISP-level CDN appliances");
        System.out.println("• Distributed transcoding farm (FFmpeg + Spark)");
        System.out.println("• Multiple resolutions: 360p → 4K (6 quality levels)");
        System.out.println("• Secure token-based streaming URLs with expiry");
        System.out.println("• Cassandra for watch history (time-series data)");
        System.out.println("• Elasticsearch for full-text content search");
        System.out.println("• Collaborative + content-based recommendation engine");
        System.out.println("========================================");
    }
}
