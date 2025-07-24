package com.rajeev.chat_server.messaging.repository;


import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.rajeev.chat_server.messaging.model.Message;

import java.util.UUID;

@Repository
// The repository signature is still correct, as MessageKey is the ID class.
public interface CassandraMessageRepository extends CassandraRepository<Message, MessageKey> {

    // This query method also remains correct. Spring Data can find "threadId"
    // on the Message entity itself now.
    Slice<Message> findByThreadId(UUID threadId, Pageable pageable);
}