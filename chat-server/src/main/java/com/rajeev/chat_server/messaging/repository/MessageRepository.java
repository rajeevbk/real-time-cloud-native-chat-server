package com.rajeev.chat_server.messaging.repository;



import com.rajeev.chat_server.messaging.model.Message;
import com.rajeev.chat_server.messaging.model.MessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Repository for the Message entity.
 * This interface provides the standard CRUD operations and allows for custom
 * query methods to be defined.
 */
@Repository
public interface MessageRepository extends CassandraRepository<Message, MessageKey> {
    /**
     * Finds a slice of messages for a given thread, ordered by messageId descending.
     * This is the primary method for fetching a chat history page.
     *
     * Spring Data automatically generates the query from the method name.
     * It queries by the 'threadId' field within the composite 'key'.
     *5
     * @param threadId The UUID of the thread to fetch messages for.
     * @param pageable The pagination information (page size).
     * @return A Slice of messages, which includes the content and a flag indicating if there's a next page.
     */
    Slice<Message> findByKeyThreadId(UUID threadId, Pageable pageable);
}