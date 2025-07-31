package com.rajeevbk.messaging.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.rajeevbk.messaging.exception.ResourceNotFoundException;
import com.rajeevbk.messaging.model.Message;
import com.rajeevbk.messaging.model.MessageThread;
import com.rajeevbk.messaging.model.Participant;
import com.rajeevbk.messaging.model.dto.MessageThreadDto;
import com.rajeevbk.messaging.model.dto.ParticipantDto;
import com.rajeevbk.messaging.model.dto.UserDetailDto;
import com.rajeevbk.messaging.repository.CassandraMessageRepository;
import com.rajeevbk.messaging.repository.RedisMessageThreadRepository;
import com.rajeevbk.messaging.util.ThreadType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.function.Function;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);


    private final CassandraMessageRepository cassandraMessageRepository;
    private final RedisMessageThreadRepository redisMessageThreadRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PresenceService presenceService;
    private final RestTemplate restTemplate;


    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Autowired
    public ChatService(CassandraMessageRepository messageRepository, RedisMessageThreadRepository messageThreadRepository, RedisTemplate<String, String> redisTemplate, PresenceService presenceService, RestTemplate restTemplate) {
        this.cassandraMessageRepository = messageRepository;
        this.redisMessageThreadRepository = messageThreadRepository;
        this.redisTemplate = redisTemplate;
        this.presenceService = presenceService;
        this.restTemplate = restTemplate;
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

            MessageThread savedThread = redisMessageThreadRepository.save(newThread);

            // THIS IS THE NEW LOGIC: Update the lookup index for both users
            String user1Key = "user:" + userId1 + ":threads";
            String user2Key = "user:" + userId2 + ":threads";
            redisTemplate.opsForSet().add(user1Key, threadId.toString());
            redisTemplate.opsForSet().add(user2Key, threadId.toString());

            return savedThread;
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

    private void updateThreadWithLatestMessageAndUnreadCount(UUID threadId, Message message, String recipientId) {
        MessageThread thread = redisMessageThreadRepository.findById(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("Thread not found"));
        thread.setLastMessage(message);
        thread.setLastActivityAt(message.getTimestamp());
        // THIS IS THE FIX: Only increment the count if the recipient is NOT viewing this specific thread.
        if (!presenceService.isUserViewingThread(recipientId, threadId)) {
            thread.getParticipants().stream()
                    .filter(p -> p.getUserId().equals(recipientId))
                    .findFirst()
                    .ifPresent(participant -> {
                        participant.setUnreadCount(participant.getUnreadCount() + 1);
                    });
        }
        redisMessageThreadRepository.save(thread);
    }

    @Transactional
    public Message sendPrivateMessage(String senderId, String recipientId, String content) {
        UUID threadId = findOrCreateDirectThread(senderId, recipientId);
        Message savedMessage = createAndSaveMessage(threadId, senderId, content);
        updateThreadWithLatestMessageAndUnreadCount(threadId, savedMessage, recipientId);
        return savedMessage;
    }

    public List<MessageThreadDto> findThreadsForUser(String username) {
        String userThreadsKey = "user:" + username + ":threads";
        Set<String> threadIdsAsObject = redisTemplate.opsForSet().members(userThreadsKey);

        if (threadIdsAsObject == null || threadIdsAsObject.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Get all thread IDs for the user
        Set<UUID> threadIds = threadIdsAsObject.stream()
                .map(obj -> UUID.fromString(String.valueOf(obj)))
                .collect(Collectors.toSet());

        // 2. Fetch all the full thread objects from Redis in one go
        List<MessageThread> threads = new ArrayList<>();
        redisMessageThreadRepository.findAllById(threadIds).forEach(threads::add);

        if (threads.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Collect all unique participant usernames from all the threads
        Set<String> participantUsernames = threads.stream()
                .flatMap(thread -> thread.getParticipants().stream())
                .map(Participant::getUserId)
                .collect(Collectors.toSet());

        // --- GRACEFUL DEGRADATION LOGIC ---
        try {
            // Try to fetch user details from the user service
            UserDetailDto[] userDetails = restTemplate.postForObject(
                    userServiceUrl + "/api/users/details",
                    participantUsernames,
                    UserDetailDto[].class
            );

            Map<String, UserDetailDto> userDetailsMap = Arrays.stream(userDetails)
                    .collect(Collectors.toMap(UserDetailDto::getUsername, Function.identity()));

            // Enrich the participant objects with the fetched names
            threads.forEach(thread ->
                    thread.getParticipants().forEach(p -> {
                        UserDetailDto detail = userDetailsMap.get(p.getUserId());
                        if (detail != null) {
                            p.setFirstName(detail.getFirstName());
                            p.setLastName(detail.getLastName());
                        }
                    })
            );

        } catch (RestClientException e) {
            // If the user service is down, log a warning and proceed without the full names.
            log.warn("User service is unavailable. Proceeding without enriching user details. Error: {}", e.getMessage());
            // No further action is needed; the names will simply be null.
        }
        // --- END OF GRACEFUL DEGRADATION LOGIC ---

        // This final step works whether enrichment succeeded or failed.
        return threads.stream()
                .sorted(Comparator.comparing(MessageThread::getLastActivityAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::convertToDto)
                .collect(Collectors.toList());


    }

    // NEW: Helper method to convert entity to DTO
    private MessageThreadDto convertToDto(MessageThread thread) {
        MessageThreadDto dto = new MessageThreadDto();
        dto.setThreadId(thread.getThreadId());
        dto.setLastActivityAt(thread.getLastActivityAt());

        if (thread.getLastMessage() != null) {
            dto.setLastMessagePreview(thread.getLastMessage().getContent());
            dto.setLastMessageSender(thread.getLastMessage().getSenderId());
        }

        // Manually map the enriched Participant objects to ParticipantDto objects
        Set<ParticipantDto> participantDtos = thread.getParticipants().stream()
                .map(p -> {
                    ParticipantDto pDto = new ParticipantDto();
                    pDto.setUserId(p.getUserId());
                    pDto.setJoinedAt(p.getJoinedAt());
                    pDto.setUnreadCount(p.getUnreadCount());
                    pDto.setFirstName(p.getFirstName()); // This is now a non-transient field on the DTO
                    pDto.setLastName(p.getLastName());   // This is now a non-transient field on the DTO
                    return pDto;
                })
                .collect(Collectors.toSet());

        dto.setParticipants(participantDtos);

        return dto;
    }

    public List<Message> getMessagesForThread(UUID threadId, Pageable pageable) {
        return cassandraMessageRepository.findByThreadId(threadId, pageable);
    }


    /**
     * NEW: Marks a thread as read for a specific user by resetting their unread count.
     */
    @Transactional
    public void markThreadAsRead(UUID threadId, String username) {
        redisMessageThreadRepository.findById(threadId).ifPresent(thread -> {
            thread.getParticipants().stream()
                    .filter(p -> p.getUserId().equals(username))
                    .findFirst()
                    .ifPresent(participant -> {
                        participant.setUnreadCount(0);
                        redisMessageThreadRepository.save(thread);
                    });
        });
    }
}