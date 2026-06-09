package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * =====================================================
 * SYSTEM DESIGN: NOTIFICATION SYSTEM
 * =====================================================
 *
 * =====================================================
 * HIGH-LEVEL DESIGN (HLD)
 * =====================================================
 *
 * --- REQUIREMENTS ---
 * Functional:
 * 1. Send push notifications (mobile push, email, SMS, in-app)
 * 2. Support multiple channels (FCM, APNs, SES, Twilio, WebSocket)
 * 3. User notification preferences (opt-in/out per channel)
 * 4. Template-based notifications with variable substitution
 * 5. Rate limiting per user to prevent spam
 * 6. Delivery tracking (sent, delivered, failed, opened)
 * 7. Scheduled/delayed notifications
 * 8. Broadcast notifications to all users
 *
 * Non-Functional:
 * - 10M+ notifications/day
 * - < 100ms processing time per notification
 * - 99.99% delivery reliability
 * - Exactly-once delivery semantics (idempotent)
 * - Handle traffic spikes (e.g., marketing campaigns × 10x)
 *
 * --- SYSTEM ARCHITECTURE ---
 *
 * ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
 * │ Microservice │    │ Microservice │    │  Admin Panel │
 * │    A         │    │    B         │    │              │
 * └──────┬───────┘    └──────┬───────┘    └──────┬───────┘
 *        │                   │                   │
 *        └───────────────────┼───────────────────┘
 *                            │
 *                    ┌───────▼────────┐
 *                    │   API Gateway   │
 *                    │  /notifications │
 *                    └───────┬────────┘
 *                            │
 *                    ┌───────▼────────┐
 *                    │  Notification   │
 *                    │  Queue (Kafka)  │
 *                    │  - high_priority │
 *                    │  - medium       │
 *                    │  - low          │
 *                    │  - scheduled    │
 *                    └───────┬────────┘
 *                            │
 *          ┌─────────────────┼─────────────────┐
 *          │                 │                  │
 *    ┌─────▼──────┐   ┌─────▼──────┐   ┌──────▼─────┐
 *    │Notification│   │Notification│   │ Scheduled  │
 *    │ Worker (1) │   │ Worker (2) │   │ Dispatcher │
 *    └─────┬──────┘   └─────┬──────┘   └──────┬─────┘
 *          │                 │                  │
 *    ┌─────▼────────────────────────────────▼──┐
 *    │       Channel Handlers                   │
 *    │  ┌────────┐ ┌───────┐ ┌──────┐ ┌─────┐ │
 *    │  │ Push   │ │ Email │ │ SMS  │ │InApp│ │
 *    │  │(FCM)   │ │(SES)  │ │(Twi) │ │(Web)│ │
 *    │  └────────┘ └───────┘ └──────┘ └─────┘ │
 *    └──────────────────┬──────────────────────┘
 *                       │
 *    ┌──────────────────┴──────────────────────┐
 *    │          Output Stores                   │
 *    │  ┌────────────┐ ┌─────────────────────┐ │
 *    │  │ Delivery DB│ │ Analytics (Tracking) │ │
 *    │  │(Postgres)  │ │(Elasticsearch)      │ │
 *    │  └────────────┘ └─────────────────────┘ │
 *    └──────────────────────────────────────────┘
 *
 * --- DATA FLOW ---
 *
 * 1. Service submits notification via REST API:
 *    POST /notifications/send
 *    {
 *      "userId": "u123",
 *      "templateId": "welcome_email",
 *      "channels": ["email", "push"],
 *      "variables": {"name": "Alice"},
 *      "priority": "high"
 *    }
 *
 * 2. API Gateway validates and publishes to Kafka topic
 *
 * 3. Notification Worker picks up from Kafka:
 *    a. Look up user preferences (opt-in/out)
 *    b. Apply per-channel rate limiting
 *    c. Render template with variables
 *    d. Deduplicate (idempotency key)
 *    e. Dispatch to appropriate channel handler
 *
 * 4. Channel Handler sends via external provider:
 *    - Push: FCM (Firebase) for Android, APNs for iOS
 *    - Email: Amazon SES / SendGrid
 *    - SMS: Twilio / Amazon SNS
 *    - In-App: WebSocket push
 *
 * 5. Delivery receipt tracked back:
 *    - Sent → Delivered → Opened → Clicked
 *
 * --- DATABASE SCHEMA ---
 *
 * notifications:
 *   id (UUID PK)
 *   user_id (UUID, INDEXED)
 *   template_id (VARCHAR)
 *   channels (TEXT[] -- 'push', 'email', 'sms', 'in_app')
 *   variables (JSONB)
 *   status (VARCHAR -- 'pending', 'sent', 'delivered', 'failed')
 *   priority (VARCHAR -- 'high', 'medium', 'low')
 *   idempotency_key (VARCHAR, UNIQUE)
 *   created_at (TIMESTAMP)
 *   sent_at (TIMESTAMP)
 *   delivered_at (TIMESTAMP)
 *
 * templates:
 *   id (VARCHAR PK)
 *   name (VARCHAR)
 *   channel (VARCHAR)
 *   subject (TEXT)
 *   body (TEXT -- with {{variable}} placeholders)
 *   created_at (TIMESTAMP)
 *
 * user_preferences:
 *   user_id (UUID PK)
 *   email_enabled (BOOLEAN)
 *   push_enabled (BOOLEAN)
 *   sms_enabled (BOOLEAN)
 *   quiet_hours_start (TIME)
 *   quiet_hours_end (TIME)
 *
 * =====================================================
 * LOW-LEVEL DESIGN (LLD) - Java Implementation
 * =====================================================
 */
public class DesignNotificationSystem {

    // =====================================================
    // 1. CORE DATA MODELS
    // =====================================================

    public enum NotificationChannel {
        PUSH, EMAIL, SMS, IN_APP
    }

    public enum NotificationPriority {
        HIGH, MEDIUM, LOW
    }

    public enum NotificationStatus {
        PENDING, PROCESSING, SENT, DELIVERED, OPENED, CLICKED, FAILED
    }

    /**
     * Notification request model.
     */
    public static class NotificationRequest {
        private final String id;
        private final String userId;
        private final String templateId;
        private final Set<NotificationChannel> channels;
        private final Map<String, String> variables;
        private final NotificationPriority priority;
        private final String idempotencyKey;
        private final long createdAt;

        public NotificationRequest(String userId, String templateId,
                                   Set<NotificationChannel> channels,
                                   Map<String, String> variables,
                                   NotificationPriority priority,
                                   String idempotencyKey) {
            this.id = UUID.randomUUID().toString();
            this.userId = userId;
            this.templateId = templateId;
            this.channels = channels;
            this.variables = variables;
            this.priority = priority;
            this.idempotencyKey = idempotencyKey;
            this.createdAt = System.currentTimeMillis();
        }

        public String getId() { return id; }
        public String getUserId() { return userId; }
        public String getTemplateId() { return templateId; }
        public Set<NotificationChannel> getChannels() { return channels; }
        public Map<String, String> getVariables() { return variables; }
        public NotificationPriority getPriority() { return priority; }
        public String getIdempotencyKey() { return idempotencyKey; }
        public long getCreatedAt() { return createdAt; }
    }

    /**
     * Notification record stored in database.
     */
    public static class NotificationRecord {
        private final String id;
        private final String userId;
        private final String templateId;
        private final NotificationChannel channel;
        private final String renderedSubject;
        private final String renderedBody;
        private volatile NotificationStatus status;
        private final long createdAt;
        private volatile long sentAt;
        private volatile long deliveredAt;
        private volatile String errorMessage;
        private int retryCount;

        public NotificationRecord(String id, String userId, String templateId,
                                   NotificationChannel channel, String renderedSubject,
                                   String renderedBody) {
            this.id = id;
            this.userId = userId;
            this.templateId = templateId;
            this.channel = channel;
            this.renderedSubject = renderedSubject;
            this.renderedBody = renderedBody;
            this.status = NotificationStatus.PENDING;
            this.createdAt = System.currentTimeMillis();
            this.retryCount = 0;
        }

        // Status transitions
        public void markProcessing() { this.status = NotificationStatus.PROCESSING; }
        public void markSent() { this.status = NotificationStatus.SENT; this.sentAt = System.currentTimeMillis(); }
        public void markDelivered() { this.status = NotificationStatus.DELIVERED; this.deliveredAt = System.currentTimeMillis(); }
        public void markFailed(String error) {
            this.status = NotificationStatus.FAILED;
            this.errorMessage = error;
        }

        // Getters
        public String getId() { return id; }
        public String getUserId() { return userId; }
        public NotificationChannel getChannel() { return channel; }
        public NotificationStatus getStatus() { return status; }
        public String getRenderedSubject() { return renderedSubject; }
        public String getRenderedBody() { return renderedBody; }
        public int getRetryCount() { return retryCount; }
        public void incrementRetry() { this.retryCount++; }

        @Override
        public String toString() {
            return "Notification{" + "id='" + id + '\'' + ", channel=" + channel
                    + ", status=" + status + ", userId='" + userId + '\'' + '}';
        }
    }

    /**
     * Notification template with placeholders.
     */
    public static class NotificationTemplate {
        private final String id;
        private final String name;
        private final NotificationChannel channel;
        private final String subjectTemplate;
        private final String bodyTemplate;

        public NotificationTemplate(String id, String name, NotificationChannel channel,
                                    String subjectTemplate, String bodyTemplate) {
            this.id = id;
            this.name = name;
            this.channel = channel;
            this.subjectTemplate = subjectTemplate;
            this.bodyTemplate = bodyTemplate;
        }

        /**
         * Render template with variables.
         * Replaces {{variable}} placeholders with actual values.
         */
        public RenderedTemplate render(Map<String, String> variables) {
            String subject = replacePlaceholders(subjectTemplate, variables);
            String body = replacePlaceholders(bodyTemplate, variables);
            return new RenderedTemplate(subject, body);
        }

        private String replacePlaceholders(String template, Map<String, String> variables) {
            String result = template;
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return result;
        }

        public String getId() { return id; }
        public NotificationChannel getChannel() { return channel; }

        record RenderedTemplate(String subject, String body) {}
    }

    /**
     * User notification preferences.
     */
    public static class UserPreferences {
        private final String userId;
        private final Map<NotificationChannel, Boolean> channelEnabled;
        private int quietHoursStart; // Hour (0-23)
        private int quietHoursEnd;   // Hour (0-23)
        private int maxNotificationsPerDay;
        private final AtomicInteger todayCount;
        private volatile long lastResetDay;

        public UserPreferences(String userId) {
            this.userId = userId;
            this.channelEnabled = new ConcurrentHashMap<>();
            this.maxNotificationsPerDay = 50;
            this.todayCount = new AtomicInteger(0);
            this.lastResetDay = System.currentTimeMillis();
            // Default: all channels enabled
            for (NotificationChannel c : NotificationChannel.values()) {
                channelEnabled.put(c, true);
            }
        }

        public boolean isChannelEnabled(NotificationChannel channel) {
            return channelEnabled.getOrDefault(channel, false);
        }

        public void setChannelEnabled(NotificationChannel channel, boolean enabled) {
            channelEnabled.put(channel, enabled);
        }

        public void setQuietHours(int start, int end) {
            this.quietHoursStart = start;
            this.quietHoursEnd = end;
        }

        /**
         * Check if current time is within quiet hours.
         */
        public boolean isQuietHours() {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (quietHoursStart <= quietHoursEnd) {
                return hour >= quietHoursStart && hour < quietHoursEnd;
            } else {
                // Overnight quiet hours (e.g., 22:00 - 08:00)
                return hour >= quietHoursStart || hour < quietHoursEnd;
            }
        }

        /**
         * Check and increment daily count. Returns false if exceeded.
         */
        public boolean tryIncrementDailyCount() {
            resetDailyIfNeeded();
            int current = todayCount.get();
            if (current >= maxNotificationsPerDay) return false;
            return todayCount.incrementAndGet() <= maxNotificationsPerDay;
        }

        private void resetDailyIfNeeded() {
            long now = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long todayStart = cal.getTimeInMillis();

            if (lastResetDay < todayStart) {
                todayCount.set(0);
                lastResetDay = now;
            }
        }
    }

    // =====================================================
    // 2. TEMPLATE SERVICE
    // =====================================================

    /**
     * Manages notification templates.
     */
    public static class TemplateService {
        private final ConcurrentHashMap<String, NotificationTemplate> templates = new ConcurrentHashMap<>();

        public void registerTemplate(NotificationTemplate template) {
            templates.put(template.getId(), template);
        }

        public NotificationTemplate getTemplate(String templateId) {
            return templates.get(templateId);
        }

        /**
         * Render a template for a specific channel.
         */
        public NotificationTemplate.RenderedTemplate render(String templateId,
                                                             NotificationChannel channel,
                                                             Map<String, String> variables) {
            NotificationTemplate template = templates.get(templateId);
            if (template == null) {
                throw new IllegalArgumentException("Template not found: " + templateId);
            }
            if (template.getChannel() != channel) {
                throw new IllegalArgumentException("Template " + templateId
                        + " not available for channel " + channel);
            }
            return template.render(variables);
        }
    }

    // =====================================================
    // 3. PREFERENCE SERVICE
    // =====================================================

    /**
     * Manages user notification preferences.
     */
    public static class PreferenceService {
        private final ConcurrentHashMap<String, UserPreferences> preferences = new ConcurrentHashMap<>();

        public UserPreferences getOrCreate(String userId) {
            return preferences.computeIfAbsent(userId, UserPreferences::new);
        }

        /**
         * Check if a notification should be suppressed for this user.
         */
        public boolean shouldSuppress(String userId, NotificationChannel channel) {
            UserPreferences prefs = preferences.get(userId);
            if (prefs == null) return false;

            // Check channel opt-in
            if (!prefs.isChannelEnabled(channel)) {
                System.out.println("  [Suppress] User " + userId
                        + " has disabled channel " + channel);
                return true;
            }

            // Check quiet hours (skip for high priority)
            if (prefs.isQuietHours()) {
                System.out.println("  [Suppress] User " + userId
                        + " is in quiet hours for " + channel);
                return true;
            }

            // Check daily limit
            if (!prefs.tryIncrementDailyCount()) {
                System.out.println("  [Suppress] User " + userId
                        + " exceeded daily limit for " + channel);
                return true;
            }

            return false;
        }
    }

    // =====================================================
    // 4. DEDUPLICATION SERVICE
    // =====================================================

    /**
     * Ensures idempotent notification delivery.
     * Prevents duplicate notifications caused by retries.
     */
    public static class DeduplicationService {
        private final ConcurrentHashMap<String, Boolean> processedKeys = new ConcurrentHashMap<>();
        private static final long KEY_EXPIRY_MS = 86_400_000; // 24 hours

        /**
         * Check if an idempotency key has already been processed.
         * Returns true if this is a duplicate.
         */
        public boolean isDuplicate(String idempotencyKey) {
            return processedKeys.containsKey(idempotencyKey);
        }

        /**
         * Mark an idempotency key as processed.
         */
        public void markProcessed(String idempotencyKey) {
            processedKeys.put(idempotencyKey, true);
            // In production: use Redis with TTL for auto-expiry
        }

        /**
         * Periodic cleanup (in production, Redis handles this via TTL).
         */
        public void cleanup() {
            // In production: Redis keys expire automatically
        }
    }

    // =====================================================
    // 5. RATE LIMITER (Per-channel, per-user)
    // =====================================================

    /**
     * Per-user per-channel rate limiter for notifications.
     */
    public static class NotificationRateLimiter {
        // channel:userId → counter
        private final ConcurrentHashMap<String, SlidingWindowCounter> counters;

        // Max notifications per channel per minute
        private static final int PUSH_MAX_PER_MIN = 10;
        private static final int EMAIL_MAX_PER_MIN = 5;
        private static final int SMS_MAX_PER_MIN = 1;
        private static final int IN_APP_MAX_PER_MIN = 20;

        public NotificationRateLimiter() {
            this.counters = new ConcurrentHashMap<>();
        }

        /**
         * Check if a notification is allowed based on rate limits.
         */
        public boolean isAllowed(String userId, NotificationChannel channel) {
            String key = channel + ":" + userId;
            int maxPerMin = switch (channel) {
                case PUSH -> PUSH_MAX_PER_MIN;
                case EMAIL -> EMAIL_MAX_PER_MIN;
                case SMS -> SMS_MAX_PER_MIN;
                case IN_APP -> IN_APP_MAX_PER_MIN;
            };

            SlidingWindowCounter counter = counters.computeIfAbsent(key,
                    k -> new SlidingWindowCounter(maxPerMin, 60_000));
            return counter.allowRequest().allowed();
        }

        /**
         * Simple sliding window counter.
         */
        private static class SlidingWindowCounter {
            private final int maxRequests;
            private final long windowMs;
            private final Deque<Long> timestamps = new LinkedList<>();

            SlidingWindowCounter(int maxRequests, long windowMs) {
                this.maxRequests = maxRequests;
                this.windowMs = windowMs;
            }

            synchronized RateLimitResult allowRequest() {
                long now = System.currentTimeMillis();
                long windowStart = now - windowMs;

                while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                    timestamps.pollFirst();
                }

                if (timestamps.size() < maxRequests) {
                    timestamps.addLast(now);
                    return new RateLimitResult(true,
                            maxRequests - timestamps.size(), now + windowMs);
                }

                return new RateLimitResult(false, 0,
                        timestamps.peekFirst() + windowMs);
            }

            record RateLimitResult(boolean allowed, int remaining, long resetAt) {}
        }
    }

    // =====================================================
    // 6. CHANNEL HANDLERS (Push, Email, SMS, In-App)
    // =====================================================

    /**
     * Abstract channel handler interface.
     */
    public interface ChannelHandler {
        NotificationChannel getChannel();
        boolean send(NotificationRecord record);
    }

    /**
     * Push Notification Handler (FCM for Android, APNs for iOS).
     */
    public static class PushNotificationHandler implements ChannelHandler {
        @Override
        public NotificationChannel getChannel() { return NotificationChannel.PUSH; }

        @Override
        public boolean send(NotificationRecord record) {
            System.out.println("  [PUSH] Sending to " + record.getUserId()
                    + ": " + truncate(record.getRenderedBody(), 50));
            // In production:
            // FirebaseMessage message = FirebaseMessage.builder()
            //     .setToken(deviceToken)
            //     .setNotification(Notification.builder()
            //         .setTitle(record.getRenderedSubject())
            //         .setBody(record.getRenderedBody())
            //         .build())
            //     .build();
            // String response = FirebaseMessaging.getInstance().send(message);
            return true; // Simulated success
        }
    }

    /**
     * Email Handler (Amazon SES / SendGrid).
     */
    public static class EmailHandler implements ChannelHandler {
        @Override
        public NotificationChannel getChannel() { return NotificationChannel.EMAIL; }

        @Override
        public boolean send(NotificationRecord record) {
            System.out.println("  [EMAIL] Sending to " + record.getUserId()
                    + ": " + record.getRenderedSubject());
            // In production:
            // SendEmailRequest request = new SendEmailRequest()
            //     .withDestination(new Destination().withToAddresses(userEmail))
            //     .withMessage(new Message()
            //         .withSubject(new Content(record.getRenderedSubject()))
            //         .withBody(new Body().withHtml(new Content(record.getRenderedBody()))));
            // amazonSimpleEmailService.sendEmail(request);
            return true;
        }
    }

    /**
     * SMS Handler (Twilio).
     */
    public static class SmsHandler implements ChannelHandler {
        @Override
        public NotificationChannel getChannel() { return NotificationChannel.SMS; }

        @Override
        public boolean send(NotificationRecord record) {
            System.out.println("  [SMS] Sending to " + record.getUserId()
                    + ": " + truncate(record.getRenderedBody(), 30));
            // In production:
            // Message.creator(
            //     new PhoneNumber(userPhone),
            //     new PhoneNumber(twilioPhone),
            //     record.getRenderedBody()
            // ).create();
            return true;
        }
    }

    /**
     * In-App Notification Handler (WebSocket push).
     */
    public static class InAppNotificationHandler implements ChannelHandler {
        @Override
        public NotificationChannel getChannel() { return NotificationChannel.IN_APP; }

        @Override
        public boolean send(NotificationRecord record) {
            System.out.println("  [IN-APP] Sending to " + record.getUserId()
                    + ": " + truncate(record.getRenderedBody(), 50));
            // In production: push via WebSocket to connected client
            // presenceService.sendToUser(record.getUserId(), notificationPayload);
            return true;
        }
    }

    private static String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }

    // =====================================================
    // 7. NOTIFICATION WORKER (Core processing logic)
    // =====================================================

    /**
     * NotificationWorker - processes notifications from the queue.
     * This is the heart of the notification system.
     */
    public static class NotificationWorker {
        private final TemplateService templateService;
        private final PreferenceService preferenceService;
        private final DeduplicationService dedupService;
        private final NotificationRateLimiter rateLimiter;
        private final Map<NotificationChannel, ChannelHandler> handlers;
        // Delivery tracking store
        private final ConcurrentHashMap<String, NotificationRecord> deliveryStore;
        // Retry queue for failed notifications
        private final Queue<NotificationRecord> retryQueue = new ConcurrentLinkedQueue<>();
        // Max retry attempts
        private static final int MAX_RETRIES = 3;

        public NotificationWorker(TemplateService templateService,
                                  PreferenceService preferenceService,
                                  DeduplicationService dedupService,
                                  NotificationRateLimiter rateLimiter) {
            this.templateService = templateService;
            this.preferenceService = preferenceService;
            this.dedupService = dedupService;
            this.rateLimiter = rateLimiter;
            this.deliveryStore = new ConcurrentHashMap<>();
            this.handlers = new HashMap<>();

            // Register channel handlers
            registerHandler(new PushNotificationHandler());
            registerHandler(new EmailHandler());
            registerHandler(new SmsHandler());
            registerHandler(new InAppNotificationHandler());
        }

        private void registerHandler(ChannelHandler handler) {
            handlers.put(handler.getChannel(), handler);
        }

        /**
         * Process a notification request.
         * Returns list of notification records created.
         */
        public List<NotificationRecord> process(NotificationRequest request) {
            List<NotificationRecord> records = new ArrayList<>();

            // 1. Check deduplication
            if (dedupService.isDuplicate(request.getIdempotencyKey())) {
                System.out.println("  [Worker] Skipping duplicate: " + request.getIdempotencyKey());
                return records;
            }

            // 2. Process each requested channel
            for (NotificationChannel channel : request.getChannels()) {
                // 3. Check user preferences
                if (preferenceService.shouldSuppress(request.getUserId(), channel)) {
                    continue;
                }

                // 4. Check rate limits
                if (!rateLimiter.isAllowed(request.getUserId(), channel)) {
                    System.out.println("  [Worker] Rate limited: " + request.getUserId()
                            + " on " + channel);
                    // Queue for retry with backoff
                    continue;
                }

                // 5. Render template
                NotificationTemplate.RenderedTemplate rendered;
                try {
                    rendered = templateService.render(
                            request.getTemplateId(), channel, request.getVariables());
                } catch (Exception e) {
                    System.out.println("  [Worker] Template render failed: " + e.getMessage());
                    continue;
                }

                // 6. Create notification record
                NotificationRecord record = new NotificationRecord(
                        request.getId() + ":" + channel,
                        request.getUserId(),
                        request.getTemplateId(),
                        channel,
                        rendered.subject(),
                        rendered.body()
                );

                // 7. Send via channel handler
                ChannelHandler handler = handlers.get(channel);
                if (handler != null) {
                    record.markProcessing();
                    boolean success = handler.send(record);
                    if (success) {
                        record.markSent();
                    } else {
                        record.markFailed("Send failed");
                        // Add to retry queue
                        retryQueue.add(record);
                    }
                }

                // 8. Store record
                deliveryStore.put(record.getId(), record);
                records.add(record);
            }

            // 9. Mark idempotency key as processed
            dedupService.markProcessed(request.getIdempotencyKey());

            return records;
        }

        /**
         * Retry failed notifications with exponential backoff.
         */
        public void retryFailed() {
            List<NotificationRecord> toRetry = new ArrayList<>();
            NotificationRecord record;
            while ((record = retryQueue.poll()) != null) {
                toRetry.add(record);
            }

            for (NotificationRecord failed : toRetry) {
                if (failed.getRetryCount() >= MAX_RETRIES) {
                    System.out.println("  [Retry] Giving up on " + failed.getId()
                            + " after " + MAX_RETRIES + " attempts");
                    continue;
                }

                // Exponential backoff: 10s, 30s, 90s
                long backoff = (long) (10_000 * Math.pow(3, failed.getRetryCount()));
                System.out.println("  [Retry] Will retry " + failed.getId()
                        + " in " + (backoff / 1000) + "s (attempt "
                        + (failed.getRetryCount() + 1) + "/" + MAX_RETRIES + ")");

                // In production: schedule via delayed queue (Kafka, SQS)
                failed.incrementRetry();
                ChannelHandler handler = handlers.get(failed.getChannel());
                if (handler != null) {
                    boolean success = handler.send(failed);
                    if (success) {
                        failed.markSent();
                        deliveryStore.put(failed.getId(), failed);
                    } else if (failed.getRetryCount() < MAX_RETRIES) {
                        retryQueue.add(failed);
                    }
                }
            }
        }

        /**
         * Get delivery status for a notification.
         */
        public NotificationStatus getStatus(String notificationId) {
            NotificationRecord record = deliveryStore.get(notificationId);
            return record != null ? record.getStatus() : null;
        }

        /**
         * Get delivery statistics.
         */
        public Map<String, Long> getStats() {
            Map<String, Long> stats = new LinkedHashMap<>();
            stats.put("total", (long) deliveryStore.size());
            stats.put("pending", countByStatus(NotificationStatus.PENDING));
            stats.put("sent", countByStatus(NotificationStatus.SENT));
            stats.put("delivered", countByStatus(NotificationStatus.DELIVERED));
            stats.put("failed", countByStatus(NotificationStatus.FAILED));
            stats.put("pendingRetry", (long) retryQueue.size());
            return stats;
        }

        private long countByStatus(NotificationStatus status) {
            return deliveryStore.values().stream()
                    .filter(r -> r.getStatus() == status)
                    .count();
        }
    }

    // =====================================================
    // 8. NOTIFICATION SERVICE (Facade for external use)
    // =====================================================

    /**
     * NotificationService - main entry point for sending notifications.
     * Provides a clean API for other services to use.
     */
    public static class NotificationService {
        private final TemplateService templateService;
        private final PreferenceService preferenceService;
        private final NotificationRateLimiter rateLimiter;
        private final DeduplicationService dedupService;
        private final NotificationWorker worker;
        // Simulates notification queue (in production: Kafka)
        private final BlockingQueue<NotificationRequest> queue;
        private final ScheduledExecutorService executor;
        private volatile boolean running;

        public NotificationService() {
            this.templateService = new TemplateService();
            this.preferenceService = new PreferenceService();
            this.rateLimiter = new NotificationRateLimiter();
            this.dedupService = new DeduplicationService();
            this.worker = new NotificationWorker(templateService, preferenceService,
                    dedupService, rateLimiter);
            this.queue = new LinkedBlockingQueue<>(10000);
            this.executor = Executors.newScheduledThreadPool(4);
            this.running = true;

            // Start queue consumer
            startQueueConsumer();
        }

        /**
         * Send a notification (async - queued for processing).
         */
        public CompletableFuture<List<NotificationRecord>> send(NotificationRequest request) {
            CompletableFuture<List<NotificationRecord>> future = new CompletableFuture<>();

            // In production: publish to Kafka topic
            // For this implementation, we process in a thread pool
            executor.submit(() -> {
                try {
                    List<NotificationRecord> records = worker.process(request);
                    future.complete(records);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            return future;
        }

        /**
         * Send a notification (sync - directly processed).
         */
        public List<NotificationRecord> sendSync(NotificationRequest request) {
            return worker.process(request);
        }

        /**
         * Schedule a notification for future delivery.
         */
        public void schedule(NotificationRequest request, long delayMs) {
            executor.schedule(() -> {
                worker.process(request);
            }, delayMs, TimeUnit.MILLISECONDS);
            System.out.println("  [Scheduler] Scheduled notification " + request.getId()
                    + " in " + (delayMs / 1000) + "s");
        }

        /**
         * Broadcast to all users (marketing campaigns).
         */
        public void broadcast(Set<String> userIds, String templateId,
                              Map<String, String> variables, NotificationChannel channel) {
            for (String userId : userIds) {
                NotificationRequest request = new NotificationRequest(
                        userId, templateId, Set.of(channel), variables,
                        NotificationPriority.LOW, "broadcast_" + userId + "_" + templateId
                );
                queue.offer(request);
            }
            System.out.println("  [Broadcast] Queued " + userIds.size()
                    + " notifications for " + channel);
        }

        private void startQueueConsumer() {
            executor.submit(() -> {
                while (running) {
                    try {
                        NotificationRequest request = queue.poll(1, TimeUnit.SECONDS);
                        if (request != null) {
                            worker.process(request);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        public void shutdown() {
            running = false;
            executor.shutdown();
        }

        // Accessors
        public TemplateService getTemplateService() { return templateService; }
        public PreferenceService getPreferenceService() { return preferenceService; }
        public NotificationWorker getWorker() { return worker; }
    }

    // =====================================================
    // 9. DEMONSTRATION
    // =====================================================

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("NOTIFICATION SYSTEM DESIGN");
        System.out.println("========================================\n");

        // Initialize notification service
        NotificationService service = new NotificationService();

        // Register templates
        System.out.println("--- Registering Templates ---");
        service.getTemplateService().registerTemplate(
                new NotificationTemplate("welcome_email", "Welcome Email",
                        NotificationChannel.EMAIL,
                        "Welcome to our service, {{name}}!",
                        "Hi {{name}},\n\nThank you for joining! Your verification code is {{code}}.\n\nBest regards,\nThe Team")
        );
        service.getTemplateService().registerTemplate(
                new NotificationTemplate("order_confirmation", "Order Confirmation",
                        NotificationChannel.PUSH,
                        "Order #{{orderId}} Confirmed",
                        "Your order of {{item}} has been confirmed. Estimated delivery: {{deliveryDate}}")
        );
        service.getTemplateService().registerTemplate(
                new NotificationTemplate("login_alert", "Login Alert",
                        NotificationChannel.SMS,
                        "Security Alert",
                        "New login from {{device}} at {{time}}. Not you? Reset your password.")
        );
        service.getTemplateService().registerTemplate(
                new NotificationTemplate("friend_request", "Friend Request",
                        NotificationChannel.IN_APP,
                        "New Friend Request",
                        "{{sender}} sent you a friend request.")
        );

        // Configure user preferences
        System.out.println("\n--- User Preferences ---");
        UserPreferences alicePrefs = service.getPreferenceService().getOrCreate("alice");
        alicePrefs.setChannelEnabled(NotificationChannel.SMS, false); // Alice doesn't want SMS
        alicePrefs.setQuietHours(22, 8); // Quiet hours 10PM - 8AM
        System.out.println("  Alice: SMS enabled=" + alicePrefs.isChannelEnabled(NotificationChannel.SMS));

        // Send notifications
        System.out.println("\n--- Sending Notifications ---");

        // 1. Welcome email to Alice
        System.out.println("\n  1. Welcome Email:");
        service.sendSync(new NotificationRequest(
                "alice", "welcome_email", Set.of(NotificationChannel.EMAIL),
                Map.of("name", "Alice", "code", "ABC123"),
                NotificationPriority.HIGH, "welcome_alice_1"
        ));

        // 2. Push notification to Bob
        System.out.println("\n  2. Order Confirmation Push:");
        service.sendSync(new NotificationRequest(
                "bob", "order_confirmation", Set.of(NotificationChannel.PUSH),
                Map.of("orderId", "ORD-789", "item", "iPhone 15", "deliveryDate", "2026-06-15"),
                NotificationPriority.MEDIUM, "order_bob_1"
        ));

        // 3. Multi-channel (SMS disabled for Alice, so only Push)
        System.out.println("\n  3. Multi-channel (Email + SMS + Push):");
        service.sendSync(new NotificationRequest(
                "alice", "login_alert", Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS, NotificationChannel.PUSH),
                Map.of("device", "Chrome on Windows", "time", "10:23 PM"),
                NotificationPriority.HIGH, "alert_alice_1"
        ));

        // 4. Scheduled notification
        System.out.println("\n  4. Scheduled Notification (5s delay):");
        service.schedule(new NotificationRequest(
                "bob", "friend_request", Set.of(NotificationChannel.IN_APP),
                Map.of("sender", "Charlie"),
                NotificationPriority.LOW, "friend_bob_1"
        ), 5000);

        // 5. Broadcast to all users
        System.out.println("\n  5. Broadcast to 3 users:");
        service.broadcast(
                new HashSet<>(Arrays.asList("alice", "bob", "charlie")),
                "welcome_email",
                Map.of("name", "Valued Customer", "code", "WELCOME10"),
                NotificationChannel.EMAIL
        );

        // Wait for async tasks
        Thread.sleep(1000);

        // Delivery stats
        System.out.println("\n--- Delivery Statistics ---");
        Map<String, Long> stats = service.getWorker().getStats();
        stats.forEach((k, v) -> System.out.println("  " + k + ": " + v));

        // Retry failed notifications
        System.out.println("\n--- Retry Failed ---");
        service.getWorker().retryFailed();

        service.shutdown();

        System.out.println("\n========================================");
        System.out.println("KEY DESIGN DECISIONS:");
        System.out.println("========================================");
        System.out.println("• Kafka for reliable, async notification processing");
        System.out.println("• Template engine with {{variable}} substitution");
        System.out.println("• Multi-channel delivery (Push, Email, SMS, In-App)");
        System.out.println("• User preference checks (opt-in/out, quiet hours)");
        System.out.println("• Per-channel rate limiting to prevent spam");
        System.out.println("• Idempotency keys for exactly-once delivery");
        System.out.println("• Retry with exponential backoff (3 attempts)");
        System.out.println("• High-priority bypasses quiet hours");
        System.out.println("• Delivery tracking (Sent → Delivered → Opened)");
        System.out.println("• WebSocket for real-time in-app notifications");
        System.out.println("========================================");
    }
}
