package com.rajeevbk.messaging.model;

import com.rajeevbk.messaging.util.ThreadType;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a conversation thread's metadata.
 * This object is designed to be stored in Redis for fast access. It holds
 * information about the participants and a preview of the last message.
 * The 'Serializable' interface is crucial for Redis serialization.
 */
@RedisHash("MessageThread") // Defines the Redis hash name for this object
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageThread implements Serializable {

    /**
     * The unique identifier for the thread. Using UUID is a good practice.
     * This will be the primary key in Redis.
     */
    @Id
    private UUID threadId;

    /**
     * A set of participants in this thread.
     * This is an improvement inspired by the reference code, using a dedicated
     * object to hold more context about each user in the thread.
     */
    private Set<Participant> participants;

    /**
     * The display name of the thread (useful for group chats).
     */
    private String threadName;

    /**
     * The user ID of the person who created the thread.
     */
    private String createdBy;

    /**
     * Timestamp of when the thread was created.
     */
    private Instant createdAt;

    /**
     * Timestamp of the last activity, used for sorting threads.
     * This should be updated every time a new message is sent.
     */
    private Instant lastActivityAt;

    /**
     * A copy of the most recent message for UI previews. This is a key
     * performance optimization, avoiding a query to Cassandra for the chat list.
     */
    private Message lastMessage;

    /**
     * Enum to distinguish between a direct message and a group chat.
     */
    private ThreadType threadType;

}