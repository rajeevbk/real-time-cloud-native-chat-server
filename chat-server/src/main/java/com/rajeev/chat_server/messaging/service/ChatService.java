package com.rajeev.chat_server.messaging.service;
import com.datastax.oss.driver.api.core.uuid.Uuids;

import com.rajeev.chat_server.messaging.model.Message;
import com.rajeev.chat_server.messaging.repository.CassandraMessageRepository;
import com.rajeev.chat_server.messaging.repository.RedisMessageThreadRepository;
import com.rajeev.chat_server.util.ThreadType;
import com.rajeev.chat_server.messaging.model.Participant;
import com.rajeev.chat_server.messaging.model.MessageThread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Service
public class ChatService {

    private final CassandraMessageRepository cassandraMessageRepository;
    private final RedisMessageThreadRepository redisMessageThreadRepository;

    @Autowired
    public ChatService(CassandraMessageRepository messageRepository, RedisMessageThreadRepository messageThreadRepository) {
        this.cassandraMessageRepository = messageRepository;
        this.redisMessageThreadRepository = messageThreadRepository;
    }

    private UUID createCanonicalThreadId(String userId1, String userId2) {
        String[] ids = {userId1, userId2};
        Arrays.sort(ids);
        return UUID.nameUUIDFromBytes((ids[0] + ids[1]).getBytes(StandardCharsets.UTF_8));
    }

    private UUID findOrCreateDirectThread(String userId1, String userId2) {
        UUID threadId = createCanonicalThreadId(userId1, userId2);
        return redisMessageThreadRepository.findById(threadId).orElseGet(() -> {
            Set<Participant> participants = new HashSet<>();
            participants.add(Participant.builder().userId(userId1).joinedAt(Instant.now()).build());
            participants.add(Participant.builder().userId(userId2).joinedAt(Instant.now()).build());
            MessageThread newThread = MessageThread.builder()
                    .threadId(threadId).participants(participants).createdBy(userId1)
                    .createdAt(Instant.now()).lastActivityAt(Instant.now())
                    .threadType(ThreadType.DIRECT).build();
            return redisMessageThreadRepository.save(newThread);
        }).getThreadId();
    }

    private Message createAndSaveMessage(UUID threadId, String senderId, String content) {
        Message message = Message.builder()
                .threadId(threadId)
                .messageId(Uuids.timeBased())
                .senderId(senderId)
                .content(content)
                .timestamp(Instant.now())
                .build();
        return cassandraMessageRepository.save(message);
    }

    private void updateThreadWithLatestMessage(UUID threadId, Message message) {
        redisMessageThreadRepository.findById(threadId).ifPresent(thread -> {
            thread.setLastMessage(message);
            thread.setLastActivityAt(message.getTimestamp());
            redisMessageThreadRepository.save(thread);
        });
    }

    @Transactional
    public Message sendPrivateMessage(String senderId, String recipientId, String content) {
        UUID threadId = findOrCreateDirectThread(senderId, recipientId);
        Message savedMessage = createAndSaveMessage(threadId, senderId, content);
        updateThreadWithLatestMessage(threadId, savedMessage);
        return savedMessage;
    }
}