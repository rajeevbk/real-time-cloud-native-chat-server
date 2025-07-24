package com.rajeev.chat_server.messaging.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rajeev.chat_server.messaging.model.MessageThread;

import java.util.UUID;


@Repository
public interface RedisMessageThreadRepository extends CrudRepository<MessageThread, UUID> {
    // Spring Data Redis can also support more complex derived queries if needed.
    // For example:
    // List<MessageThread> findByThreadName(String threadName);
}