package com.rajeev.chat_server.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a single chat message, designed for storage in Cassandra.
 * The annotations map this class to a Cassandra table.
 */
@Table("messages_by_thread") // Defines the Cassandra table name
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {

    @PrimaryKey // This is correct: it annotates the field that holds the primary key.
    private MessageKey key;

    /**
     * The ID of the user who sent the message.
     */
    private String senderId;

    /**
     * The actual text content of the message.
     */
    private String content;

    /**
     * The timestamp when the message was sent.
     * While the messageId is the primary time sort, this is useful for display.
     */
    private Instant timestamp;
}