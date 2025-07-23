package com.rajeev.chat_server.messaging.repository;

import com.rajeev.chat_server.messaging.model.MessageThread;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data Repository for the MessageThread entity stored in Redis.
 * Extending CrudRepository provides standard CRUD operations (save, findById, delete, etc.)
 * for our @RedisHash annotated MessageThread objects.
 */
@Repository
public interface MessageThreadRepository extends CrudRepository<MessageThread, UUID> {
    // Spring Data Redis can also support more complex derived queries if needed.
    // For example:
    // List<MessageThread> findByThreadName(String threadName);
}