package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * =====================================================
 * SYSTEM DESIGN: CHAT SYSTEM (WhatsApp-like)
 * =====================================================
 *
 * =====================================================
 * HIGH-LEVEL DESIGN (HLD)
 * =====================================================
 *
 * --- REQUIREMENTS ---
 * Functional:
 * 1. One-on-one and group messaging
 * 2. Real-time message delivery (low latency)
 * 3. Online/offline presence
 * 4. Read receipts (delivered, read)
 * 5. Media sharing (images, files)
 * 6. Message history
 * 7. Typing indicators
 *
 * Non-Functional:
 * - Support 1B+ users, 100M DAU
 * - 99.999% availability
 * - < 100ms message delivery latency
 * - Messages never lost (durable)
 * - End-to-end encryption
 *
 * --- SYSTEM ARCHITECTURE ---
 *
 * ┌─────────────┐     ┌──────────────┐     ┌─────────────┐
 * │   Mobile     │────▶│  Load        │────▶│  WebSocket  │
 * │   Client     │     │  Balancer    │     │  Server     │
 * └─────────────┘     └──────────────┘     └──────┬──────┘
 *                                                  │
 *                    ┌─────────────────────────────┼─────────────────────┐
 *                    │                             │                     │
 *               ┌────▼────┐                  ┌────▼────┐          ┌────▼────┐
 *               │ Message │                  │ Presence│          │  Media  │
 *               │ Queue   │                  │ Service │          │ Service │
 *               │ (Kafka) │                  │ (Redis) │          │ (CDN)   │
 *               └────┬────┘                  └─────────┘          └─────────┘
 *                    │
 *          ┌─────────┼─────────┐
 *     ┌────▼────┐ ┌──▼───┐ ┌──▼───┐
 *     │ Message │ │Group │ │Push  │
 *     │ Store   │ │Svc   │ │Notif │
 *     │(Cassan) │ │      │ │(FCM) │
 *     └─────────┘ └──────┘ └──────┘
 *
 * --- COMPONENTS ---
 *
 * 1. WebSocket Server (Chat Server):
 *    - Maintains persistent TCP connections with clients
 *    - Handles real-time bidirectional communication
 *    - Each server handles N concurrent connections
 *    - Identified by unique server ID
 *
 * 2. Load Balancer:
 *    - Distributes WebSocket connections across servers
 *    - Sticky sessions via consistent hashing on user_id
 *    - Health checks for server liveness
 *
 * 3. Message Queue (Apache Kafka):
 *    - Decouples message sending from processing
 *    - Ensures reliable message delivery
 *    - Topics: user_messages, group_messages, media_processing
 *    - Partitions: sharded by conversation_id
 *
 * 4. Presence Service (Redis):
 *    - Tracks user online/offline status
 *    - Stores {user_id → server_id} mapping
 *    - Pub/Sub for presence updates across servers
 *    - Heartbeat timeout = 30s for auto-offline
 *
 * 5. Message Store (Apache Cassandra):
 *    - Write-optimized NoSQL database
 *    - Time-series data model for messages
 *    - TTL for auto-expiring old messages
 *    - Linear scalability via horizontal sharding
 *
 * 6. Group Service:
 *    - Manages group membership
 *    - Fan-out on write for small groups
 *    - Fan-out on read for large groups (>100 members)
 *    - Stores group metadata in MySQL
 *
 * 7. Push Notification Service (FCM/APNs):
 *    - Sends push notifications for offline users
 *    - Batches notifications to reduce cost
 *    - Rate-limited per device
 *
 * 8. Media Service (S3/CDN):
 *    - Stores images, videos, files
 *    - Generates thumbnails
 *    - CDN for fast content delivery
 *    - Compression and format conversion
 *
 * --- DATA FLOWS ---
 *
 * A) One-on-One Message Send:
 *    1. Sender connects via WebSocket to Chat Server A
 *    2. Server A receives message, assigns unique message_id (Snowflake)
 *    3. Server A persists message to Cassandra (async-acknowledged)
 *    4. Server A checks Presence Service: is receiver online?
 *    5. If online:
 *       a. Look up receiver's server_id from Presence Service
 *       b. Forward message to receiver's Chat Server B via internal RPC
 *       c. Server B pushes message to receiver's WebSocket
 *    6. If offline:
 *       a. Store in message queue for later delivery
 *       b. Send push notification via FCM/APNs
 *    7. Sender gets delivery receipt
 *
 * B) Group Message Send:
 *    1. Sender sends message to group
 *    2. For groups < 100 members (fan-out on write):
 *       a. Write message once to group_conversation partition
 *       b. Copy message metadata to each member's timeline (async)
 *    3. For groups > 100 members (fan-out on read):
 *       a. Write message once
 *       b. Members pull messages on read
 *    4. Online members receive real-time push
 *    5. Offline members get push notification
 *
 * C) Read Receipt:
 *    1. Receiver opens conversation
 *    2. Client sends read_receipt with last_read_message_id
 *    3. Server updates read_at timestamp in Cassandra
 *    4. Server publishes read event to sender (if online)
 *
 * --- DATABASE SCHEMA (Cassandra) ---
 *
 * -- Messages by conversation (time-ordered)
 * CREATE TABLE messages_by_conversation (
 *     conversation_id   UUID,
 *     message_id        TIMEUUID,   -- Clustering key, time-sorted
 *     sender_id         UUID,
 *     content           TEXT,
 *     message_type      TEXT,       -- 'text', 'image', 'file', 'system'
 *     media_url         TEXT,
 *     created_at        TIMESTAMP,
 *     edited_at         TIMESTAMP,
 *     deleted           BOOLEAN,
 *     PRIMARY KEY ((conversation_id), message_id)
 * ) WITH CLUSTERING ORDER BY (message_id DESC);
 *
 * -- User conversations index
 * CREATE TABLE user_conversations (
 *     user_id           UUID,
 *     conversation_id   UUID,
 *     last_message_at   TIMESTAMP,
 *     unread_count      INT,
 *     PRIMARY KEY ((user_id), conversation_id)
 * ) WITH CLUSTERING ORDER BY (conversation_id DESC);
 *
 * -- Group membership
 * CREATE TABLE group_members (
 *     group_id          UUID,
 *     user_id           UUID,
 *     role              TEXT,       -- 'admin', 'member'
 *     joined_at         TIMESTAMP,
 *     PRIMARY KEY ((group_id), user_id)
 * );
 *
 * =====================================================
 * LOW-LEVEL DESIGN (LLD) - Java Implementation
 * =====================================================
 */
public class DesignChatSystem {

    // =====================================================
    // 1. CORE DATA MODELS
    // =====================================================

    public enum MessageType {
        TEXT, IMAGE, FILE, AUDIO, VIDEO, SYSTEM, LOCATION
    }

    public enum MessageStatus {
        SENDING, SENT, DELIVERED, READ, FAILED
    }

    public enum UserStatus {
        ONLINE, OFFLINE, AWAY, DO_NOT_DISTURB
    }

    /**
     * Message model - core entity in the chat system.
     */
    public static class Message {
        private final String messageId;
        private final String conversationId;
        private final String senderId;
        private final String content;
        private final MessageType type;
        private final String mediaUrl;
        private final long createdAt;
        private long deliveredAt;
        private long readAt;
        private MessageStatus status;
        private boolean edited;
        private boolean deleted;

        public Message(String messageId, String conversationId, String senderId,
                       String content, MessageType type, String mediaUrl) {
            this.messageId = messageId;
            this.conversationId = conversationId;
            this.senderId = senderId;
            this.content = content;
            this.type = type;
            this.mediaUrl = mediaUrl;
            this.createdAt = System.currentTimeMillis();
            this.status = MessageStatus.SENDING;
        }

        // Getters
        public String getMessageId() { return messageId; }
        public String getConversationId() { return conversationId; }
        public String getSenderId() { return senderId; }
        public String getContent() { return content; }
        public MessageType getType() { return type; }
        public String getMediaUrl() { return mediaUrl; }
        public long getCreatedAt() { return createdAt; }
        public MessageStatus getStatus() { return status; }

        public void markDelivered() {
            this.status = MessageStatus.DELIVERED;
            this.deliveredAt = System.currentTimeMillis();
        }

        public void markRead() {
            this.status = MessageStatus.READ;
            this.readAt = System.currentTimeMillis();
        }

        public void markFailed() {
            this.status = MessageStatus.FAILED;
        }
    }

    /**
     * Conversation - represents a chat between 2+ users.
     */
    public static class Conversation {
        private final String conversationId;
        private final Set<String> participantIds;
        private final boolean isGroup;
        private String groupName;
        private String lastMessage;
        private long lastMessageAt;
        private final long createdAt;

        public Conversation(String conversationId, Set<String> participantIds, boolean isGroup) {
            this.conversationId = conversationId;
            this.participantIds = new ConcurrentHashMap<>().newKeySet();
            this.participantIds.addAll(participantIds);
            this.isGroup = isGroup;
            this.createdAt = System.currentTimeMillis();
        }

        public String getConversationId() { return conversationId; }
        public Set<String> getParticipantIds() { return participantIds; }
        public boolean isGroup() { return isGroup; }
        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
    }

    /**
     * User session - tracks a connected user's state.
     */
    public static class UserSession {
        private final String userId;
        private final String serverId;
        private final String connectionId;
        private final long connectedAt;
        private volatile long lastHeartbeat;
        private volatile UserStatus status;

        public UserSession(String userId, String serverId, String connectionId) {
            this.userId = userId;
            this.serverId = serverId;
            this.connectionId = connectionId;
            this.connectedAt = System.currentTimeMillis();
            this.lastHeartbeat = System.currentTimeMillis();
            this.status = UserStatus.ONLINE;
        }

        public String getUserId() { return userId; }
        public String getServerId() { return serverId; }
        public String getConnectionId() { return connectionId; }
        public UserStatus getStatus() { return status; }
        public void setStatus(UserStatus status) { this.status = status; }

        public void updateHeartbeat() {
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public boolean isExpired(long timeoutMs) {
            return (System.currentTimeMillis() - lastHeartbeat) > timeoutMs;
        }
    }

    // =====================================================
    // 2. PRESENCE SERVICE
    // =====================================================

    /**
     * Presence Service - manages user online/offline status.
     * In production, backed by Redis for fast reads/writes.
     */
    public static class PresenceService {
        // user_id → UserSession mapping
        private final ConcurrentHashMap<String, UserSession> sessions = new ConcurrentHashMap<>();
        // user_id → server_id mapping (for routing)
        private final ConcurrentHashMap<String, String> userServerMap = new ConcurrentHashMap<>();
        // Heartbeat timeout (30 seconds)
        private static final long HEARTBEAT_TIMEOUT_MS = 30_000;
        // Cleanup interval (10 seconds)
        private static final long CLEANUP_INTERVAL_MS = 10_000;

        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public PresenceService() {
            // Start periodic cleanup of expired sessions
            scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions,
                    CLEANUP_INTERVAL_MS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS);
        }

        /**
         * Register a user as online.
         */
        public void userConnected(String userId, String serverId, String connectionId) {
            UserSession session = new UserSession(userId, serverId, connectionId);
            sessions.put(userId, session);
            userServerMap.put(userId, serverId);
            publishPresenceChange(userId, UserStatus.ONLINE);
        }

        /**
         * Mark a user as offline.
         */
        public void userDisconnected(String userId) {
            UserSession session = sessions.remove(userId);
            userServerMap.remove(userId);
            if (session != null) {
                publishPresenceChange(userId, UserStatus.OFFLINE);
            }
        }

        /**
         * Check if a user is online.
         */
        public boolean isOnline(String userId) {
            UserSession session = sessions.get(userId);
            return session != null && !session.isExpired(HEARTBEAT_TIMEOUT_MS);
        }

        /**
         * Get the server handling a user's connection.
         */
        public String getUserServer(String userId) {
            return userServerMap.get(userId);
        }

        /**
         * Update heartbeat for a user.
         */
        public void updateHeartbeat(String userId) {
            UserSession session = sessions.get(userId);
            if (session != null) {
                session.updateHeartbeat();
            }
        }

        /**
         * Get user's current status.
         */
        public UserStatus getUserStatus(String userId) {
            UserSession session = sessions.get(userId);
            if (session == null) return UserStatus.OFFLINE;
            if (session.isExpired(HEARTBEAT_TIMEOUT_MS)) return UserStatus.OFFLINE;
            return session.getStatus();
        }

        /**
         * Clean up sessions that haven't sent heartbeat.
         */
        private void cleanupExpiredSessions() {
            long now = System.currentTimeMillis();
            sessions.entrySet().removeIf(entry -> {
                if (entry.getValue().isExpired(HEARTBEAT_TIMEOUT_MS)) {
                    userServerMap.remove(entry.getKey());
                    publishPresenceChange(entry.getKey(), UserStatus.OFFLINE);
                    return true;
                }
                return false;
            });
        }

        /**
         * Publish presence change event (in production, via Redis Pub/Sub or Kafka).
         */
        private void publishPresenceChange(String userId, UserStatus status) {
            // In production: redis.publish("presence_changes", payload)
            System.out.println("[Presence] User " + userId + " is now " + status);
        }

        /**
         * Graceful shutdown.
         */
        public void shutdown() {
            scheduler.shutdown();
        }
    }

    // =====================================================
    // 3. MESSAGE STORE (Cassandra-like implementation)
    // =====================================================

    /**
     * Message Store - persists messages.
     * In production, backed by Apache Cassandra or ScyllaDB.
     * Uses time-series model for efficient range queries.
     */
    public static class MessageStore {
        // conversation_id → sorted list of messages (by time)
        private final ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Message>> messageIndex;
        // user_id → list of conversation_ids the user belongs to
        private final ConcurrentHashMap<String, Set<String>> userConversations;

        // Maximum messages per conversation before eviction
        private static final int MAX_MESSAGES_PER_CONVERSATION = 10_000;

        public MessageStore() {
            this.messageIndex = new ConcurrentHashMap<>();
            this.userConversations = new ConcurrentHashMap<>();
        }

        /**
         * Store a new message.
         */
        public void storeMessage(Message message) {
            String conversationId = message.getConversationId();

            messageIndex.compute(conversationId, (key, existingMap) -> {
                if (existingMap == null) {
                    existingMap = new ConcurrentSkipListMap<>(Comparator.reverseOrder()); // newest first
                }
                existingMap.put(message.getCreatedAt(), message);

                // Evict oldest messages if limit exceeded
                while (existingMap.size() > MAX_MESSAGES_PER_CONVERSATION) {
                    Long oldestKey = existingMap.lastKey();
                    existingMap.remove(oldestKey);
                }
                return existingMap;
            });
        }

        /**
         * Get messages for a conversation, paginated.
         */
        public List<Message> getMessages(String conversationId, int limit, Long beforeTimestamp) {
            ConcurrentSkipListMap<Long, Message> messages = messageIndex.get(conversationId);
            if (messages == null) return Collections.emptyList();

            List<Message> result = new ArrayList<>();
            if (beforeTimestamp == null) {
                // Get newest messages
                for (Message msg : messages.values()) {
                    if (result.size() >= limit) break;
                    result.add(msg);
                }
            } else {
                // Get messages older than beforeTimestamp
                Map<Long, Message> tailMap = messages.tailMap(beforeTimestamp, false);
                for (Message msg : tailMap.values()) {
                    if (result.size() >= limit) break;
                    result.add(msg);
                }
            }
            return result;
        }

        /**
         * Update message status (delivered, read).
         */
        public void updateMessageStatus(String messageId, MessageStatus status) {
            // In production: UPDATE messages_by_conversation SET status=? WHERE ...
            // For in-memory, we'd need a secondary index
        }

        /**
         * Register a user's conversation.
         */
        public void addUserConversation(String userId, String conversationId) {
            userConversations.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                    .add(conversationId);
        }

        /**
         * Get all conversations for a user.
         */
        public Set<String> getUserConversations(String userId) {
            return userConversations.getOrDefault(userId, Collections.emptySet());
        }
    }

    // =====================================================
    // 4. MESSAGE ROUTER (Core message delivery logic)
    // =====================================================

    /**
     * Message Router - routes messages between users and servers.
     * Handles fan-out for group messages.
     */
    public static class MessageRouter {
        private final PresenceService presenceService;
        private final MessageStore messageStore;
        // Simulates network of chat servers: server_id → message handler
        private final ConcurrentHashMap<String, Consumer<Message>> serverHandlers;
        // Group membership: group_id → set of member_ids
        private final ConcurrentHashMap<String, Set<String>> groupMembership;
        // Small group threshold for fan-out strategy
        private static final int SMALL_GROUP_THRESHOLD = 100;

        @FunctionalInterface
        public interface Consumer<T> {
            void accept(T message);
        }

        public MessageRouter(PresenceService presenceService, MessageStore messageStore) {
            this.presenceService = presenceService;
            this.messageStore = messageStore;
            this.serverHandlers = new ConcurrentHashMap<>();
            this.groupMembership = new ConcurrentHashMap<>();
        }

        /**
         * Register a server's message handler.
         */
        public void registerServer(String serverId, Consumer<Message> handler) {
            serverHandlers.put(serverId, handler);
        }

        /**
         * Handle sending a message from one user to another (one-on-one).
         */
        public void routeDirectMessage(Message message, String receiverId) {
            // 1. Persist message
            messageStore.storeMessage(message);

            // 2. Check if receiver is online
            if (presenceService.isOnline(receiverId)) {
                // 3. Forward to receiver's server
                String receiverServerId = presenceService.getUserServer(receiverId);
                Consumer<Message> handler = serverHandlers.get(receiverServerId);
                if (handler != null) {
                    message.markDelivered();
                    handler.accept(message);
                }
            } else {
                // 4. Receiver offline - will be delivered when they come online
                queueForOfflineDelivery(message, receiverId);
            }
        }

        /**
         * Handle group message with smart fan-out.
         */
        public void routeGroupMessage(Message message, String groupId) {
            Set<String> members = groupMembership.get(groupId);
            if (members == null || members.isEmpty()) return;

            // Persist message
            messageStore.storeMessage(message);

            if (members.size() <= SMALL_GROUP_THRESHOLD) {
                // Fan-out on write: deliver to all online members immediately
                for (String memberId : members) {
                    if (!memberId.equals(message.getSenderId())) {
                        routeDirectMessage(message, memberId);
                    }
                }
            } else {
                // Fan-out on read: store once, members pull on read
                // For online members, still push real-time notification
                for (String memberId : members) {
                    if (!memberId.equals(message.getSenderId()) && presenceService.isOnline(memberId)) {
                        // Send lightweight notification (message summary)
                        String serverId = presenceService.getUserServer(memberId);
                        Consumer<Message> handler = serverHandlers.get(serverId);
                        if (handler != null) {
                            handler.accept(message);
                        }
                    }
                }
            }
        }

        /**
         * Queue message for delivery when user comes online.
         * In production: store in Kafka or a pending_messages table in Cassandra.
         */
        private void queueForOfflineDelivery(Message message, String userId) {
            // In production: INSERT INTO pending_messages (user_id, message_id, ...)
            System.out.println("[Queue] Message " + message.getMessageId()
                    + " queued for user " + userId + " (offline)");
        }

        /**
         * Deliver all pending messages when a user comes online.
         */
        public void deliverPendingMessages(String userId) {
            // In production: SELECT * FROM pending_messages WHERE user_id = ?
            // Then deliver each message
            System.out.println("[Delivery] Delivering pending messages for user " + userId);
        }

        /**
         * Create a group.
         */
        public String createGroup(Set<String> memberIds) {
            String groupId = "group_" + UUID.randomUUID().toString();
            groupMembership.put(groupId, ConcurrentHashMap.newKeySet());
            groupMembership.get(groupId).addAll(memberIds);
            return groupId;
        }

        /**
         * Add member to group.
         */
        public void addGroupMember(String groupId, String userId) {
            groupMembership.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet())
                    .add(userId);
        }

        /**
         * Remove member from group.
         */
        public void removeGroupMember(String groupId, String userId) {
            Set<String> members = groupMembership.get(groupId);
            if (members != null) {
                members.remove(userId);
            }
        }
    }

    // =====================================================
    // 5. CHAT SERVER (Handles client connections)
    // =====================================================

    /**
     * ChatServer - simulates a chat server node.
     * In production, this would manage thousands of WebSocket connections.
     */
    public static class ChatServer {
        private final String serverId;
        private final PresenceService presenceService;
        private final MessageRouter messageRouter;
        private final MessageStore messageStore;
        // Connected clients on this server: connectionId → userId
        private final ConcurrentHashMap<String, String> connections = new ConcurrentHashMap<>();
        // userId → connectionId
        private final ConcurrentHashMap<String, String> userConnections = new ConcurrentHashMap<>();

        public ChatServer(String serverId, PresenceService presenceService,
                          MessageRouter messageRouter, MessageStore messageStore) {
            this.serverId = serverId;
            this.presenceService = presenceService;
            this.messageRouter = messageRouter;
            this.messageStore = messageStore;

            // Register this server's message handler
            messageRouter.registerServer(serverId, this::handleIncomingMessage);
        }

        /**
         * Client connects to this server.
         */
        public void clientConnect(String userId, String connectionId) {
            connections.put(connectionId, userId);
            userConnections.put(userId, connectionId);
            presenceService.userConnected(userId, serverId, connectionId);
            System.out.println("[Server " + serverId + "] User " + userId + " connected (" + connectionId + ")");

            // Deliver any pending messages
            messageRouter.deliverPendingMessages(userId);
        }

        /**
         * Client disconnects from this server.
         */
        public void clientDisconnect(String connectionId) {
            String userId = connections.remove(connectionId);
            if (userId != null) {
                userConnections.remove(userId);
                presenceService.userDisconnected(userId);
                System.out.println("[Server " + serverId + "] User " + userId + " disconnected");
            }
        }

        /**
         * Send a message from a client (called by client's WebSocket handler).
         */
        public void sendMessage(String senderId, String conversationId,
                                String content, MessageType type, String mediaUrl) {
            String messageId = generateMessageId();
            Message message = new Message(messageId, conversationId, senderId, content, type, mediaUrl);

            if (conversationId.startsWith("group_")) {
                messageRouter.routeGroupMessage(message, conversationId);
            } else {
                // For one-on-one, the other participant is encoded in conversation_id
                // In production, look up from conversation metadata
                String receiverId = getOtherParticipant(conversationId, senderId);
                messageRouter.routeDirectMessage(message, receiverId);
            }
        }

        /**
         * Handle incoming message from another server.
         */
        private void handleIncomingMessage(Message message) {
            String userId = getUserIdForDelivery(message);
            String connectionId = userConnections.get(userId);
            if (connectionId != null) {
                // Push to client via WebSocket
                System.out.println("[Server " + serverId + "] Delivering message "
                        + message.getMessageId() + " to user " + userId
                        + " via connection " + connectionId);
                // In production: connection.send(message.toJson())
            }
        }

        /**
         * Generate a unique message ID (Snowflake-style).
         */
        private String generateMessageId() {
            long timestamp = System.currentTimeMillis();
            long random = ThreadLocalRandom.current().nextLong(10000);
            return timestamp + "-" + serverId + "-" + random;
        }

        /**
         * Extract receiver ID from conversation (simplified).
         * In production, conversation metadata stores participant list.
         */
        private String getOtherParticipant(String conversationId, String senderId) {
            // Simplified: conversation_id format is "user1_user2"
            String[] parts = conversationId.split("_");
            return parts[0].equals(senderId) ? parts[1] : parts[0];
        }

        /**
         * Determine which user should receive this message.
         */
        private String getUserIdForDelivery(Message message) {
            // In production, look up conversation participants and find the one
            // connected to THIS server
            return ""; // Simplified
        }

        /**
         * Get count of active connections on this server.
         */
        public int getConnectionCount() {
            return connections.size();
        }

        public String getServerId() { return serverId; }
    }

    // =====================================================
    // 6. END-TO-END ENCRYPTION STRATEGY
    // =====================================================

    /**
     * E2EE Strategy (conceptual):
     *
     * 1. Key Exchange (X3DH - Extended Triple Diffie-Hellman):
     *    - Each client generates a identity key pair (IK) and signed pre-key (SPK)
     *    - Pre-key bundle uploaded to server
     *    - Sender downloads receiver's pre-key bundle
     *    - Shared secret derived via DH key exchange
     *
     * 2. Encryption (AES-256-GCM + HMAC-SHA256):
     *    - Each message encrypted with unique message key
     *    - Message key derived from root key + ratchet
     *    - Double Ratchet Algorithm for forward secrecy
     *
     * 3. Perfect Forward Secrecy:
     *    - If long-term key is compromised, past messages remain secure
     *    - Keys are rotated on every message (symmetric ratchet)
     *
     * 4. Server never has access to message content
     *    - Server only sees encrypted ciphertext and metadata (sender, receiver, timestamp)
     */

    // =====================================================
    // 7. DEMONSTRATION
    // =====================================================

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========================================");
        System.out.println("CHAT SYSTEM DESIGN (WhatsApp-like)");
        System.out.println("========================================\n");

        // Initialize components
        PresenceService presenceService = new PresenceService();
        MessageStore messageStore = new MessageStore();
        MessageRouter messageRouter = new MessageRouter(presenceService, messageStore);

        // Create chat servers
        ChatServer server1 = new ChatServer("server-1", presenceService, messageRouter, messageStore);
        ChatServer server2 = new ChatServer("server-2", presenceService, messageRouter, messageStore);

        // Simulate users connecting
        System.out.println("--- User Connections ---");
        server1.clientConnect("alice", "conn-alice-1");
        server2.clientConnect("bob", "conn-bob-1");

        Thread.sleep(500);

        // Send a direct message
        System.out.println("\n--- One-on-One Message ---");
        String conversationId = "alice_bob";
        messageStore.addUserConversation("alice", conversationId);
        messageStore.addUserConversation("bob", conversationId);

        server1.sendMessage("alice", conversationId,
                "Hey Bob! How are you?", MessageType.TEXT, null);

        Thread.sleep(500);

        // Send a group message
        System.out.println("\n--- Group Message ---");
        Set<String> groupMembers = new HashSet<>(Arrays.asList("alice", "bob", "charlie"));
        String groupId = messageRouter.createGroup(groupMembers);
        messageStore.addUserConversation("alice", groupId);
        messageStore.addUserConversation("bob", groupId);

        server1.sendMessage("alice", groupId,
                "Hello everyone!", MessageType.TEXT, null);

        Thread.sleep(500);

        // Retrieve message history
        System.out.println("\n--- Message History ---");
        List<Message> history = messageStore.getMessages(conversationId, 10, null);
        for (Message msg : history) {
            System.out.println("  [" + msg.getType() + "] " + msg.getSenderId()
                    + ": " + msg.getContent() + " (" + msg.getStatus() + ")");
        }

        // Disconnect
        System.out.println("\n--- Disconnection ---");
        server1.clientDisconnect("conn-alice-1");
        server2.clientDisconnect("conn-bob-1");

        presenceService.shutdown();

        System.out.println("\n========================================");
        System.out.println("KEY DESIGN DECISIONS:");
        System.out.println("========================================");
        System.out.println("• WebSocket for real-time bidirectional communication");
        System.out.println("• Cassandra for write-optimized message storage (time-series)");
        System.out.println("• Redis for presence tracking and server routing");
        System.out.println("• Kafka for async message processing and durability");
        System.out.println("• Hybrid fan-out: write for small groups, read for large groups");
        System.out.println("• End-to-end encryption with Double Ratchet Algorithm");
        System.out.println("• CDN (S3/CloudFront) for media file delivery");
        System.out.println("• Push notifications (FCM/APNs) for offline users");
        System.out.println("• Consistent hashing for WebSocket server assignment");
        System.out.println("========================================");
    }
}
