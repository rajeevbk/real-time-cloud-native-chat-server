package com.rajeevbk.messaging.controller;

import com.rajeevbk.messaging.model.Message;
import com.rajeevbk.messaging.model.dto.MessageThreadDto;
import com.rajeevbk.messaging.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing and retrieving chat threads.
 */
@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    private final ChatService chatService;

    @Autowired
    public ThreadController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Fetches a list of all chat threads for the currently authenticated user,
     * sorted by the most recent message.
     * @param principal The authenticated user.
     * @return A list of message thread DTOs.
     */
    @GetMapping
    public List<MessageThreadDto> getMyThreads(Principal principal) {
        return chatService.findThreadsForUser(principal.getName());
    }

    @GetMapping("/{threadId}/messages")
    public List<Message> getMessagesForThread(@PathVariable UUID threadId, Pageable pageable) {
        return chatService.getMessagesForThread(threadId, pageable);
    }

    @PostMapping("/{threadId}/read")
    public ResponseEntity<Void> markThreadAsRead(@PathVariable UUID threadId, Principal principal) {
        chatService.markThreadAsRead(threadId, principal.getName());
        return ResponseEntity.ok().build();
    }


}