package com.rajeev.chat_server.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents the composite primary key for the 'messages_by_thread' table.
 * A dedicated primary key class is best practice for composite keys in Spring Data Cassandra.
 */
@PrimaryKeyClass
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageKey implements Serializable {

    @PrimaryKeyColumn(name = "thread_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID threadId;

    @PrimaryKeyColumn(name = "message_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID messageId;
}