package com.rajeevbk.messaging.service;

import com.rajeevbk.messaging.model.dto.PresenceStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class PresenceService {

    private static final String ONLINE_USERS_KEY = "online_users";
    private static final String ACTIVE_CHATS_KEY = "active_chats";
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PresenceService(RedisTemplate<String, Object> redisTemplate, SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Called when a user connects. Adds them to the online set and broadcasts the event.
     */
    public void userConnected(String username) {
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
        broadcastPresenceChange(username, true);
    }

    /**
     * Called when a user disconnects. Removes them from the online set and broadcasts the event.
     */
    public void userDisconnected(String username) {
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
        clearActiveChat(username);
        broadcastPresenceChange(username, false);
    }

    /**
     * NEW: Clears the active chat status for a user.
     */
    public void clearActiveChat(String username) {
        redisTemplate.opsForHash().delete(ACTIVE_CHATS_KEY, username);
    }

    public boolean isUserViewingThread(String username, UUID threadId) {
        Object activeThread = redisTemplate.opsForHash().get(ACTIVE_CHATS_KEY, username);
        return activeThread != null && activeThread.toString().equals(threadId.toString());
    }

    public void setActiveChat(String username, UUID threadId) {
        if (threadId != null) {
            redisTemplate.opsForHash().put(ACTIVE_CHATS_KEY, username, threadId.toString());
        } else {
            clearActiveChat(username);
        }
    }

    /**
     * Fetches the set of all currently online users from Redis.
     */
    public Set<Object> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }

    private void broadcastPresenceChange(String username, boolean isOnline) {
        PresenceStatusDto status = new PresenceStatusDto(username, isOnline);
        // Broadcast to a public topic that all connected clients can listen to
        messagingTemplate.convertAndSend("/topic/presence", status);
    }
}