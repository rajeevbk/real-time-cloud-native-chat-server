package com.rajeevbk.messaging.repository;

import com.rajeevbk.messaging.model.MessageThread;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Spring Data Repository for the MessageThread entity stored in Redis.
 * Extending CrudRepository provides standard CRUD operations (save, findById, delete, etc.)
 * for our @RedisHash annotated MessageThread objects.
 */
@Repository
public interface RedisMessageThreadRepository extends CrudRepository<MessageThread, UUID> {
    // Spring Data Redis can also support more complex derived queries if needed.
    // For example:
    // List<MessageThread> findByThreadName(String threadName);
}