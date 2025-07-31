package com.rajeevbk.messaging.repository;

import com.rajeevbk.messaging.model.Message;
import com.rajeevbk.messaging.model.MessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
// The repository signature is still correct, as MessageKey is the ID class.
public interface CassandraMessageRepository extends CassandraRepository<Message, MessageKey> {
    List<Message> findByThreadId(UUID threadId, Pageable pageable);
}