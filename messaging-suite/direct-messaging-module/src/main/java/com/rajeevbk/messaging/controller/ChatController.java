package com.rajeevbk.messaging.controller;

import com.rajeevbk.messaging.model.Message;
import com.rajeevbk.messaging.model.dto.ActiveChatDto;
import com.rajeevbk.messaging.model.dto.PrivateMessageDto;
import com.rajeevbk.messaging.model.dto.TypingStatusDto;
import com.rajeevbk.messaging.service.ChatService;
import com.rajeevbk.messaging.service.PresenceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.security.Principal;

@RestController
@SecurityRequirement(name = "Keycloak")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final PresenceService presenceService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService, PresenceService presenceService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.presenceService = presenceService;
    }

    @MessageMapping("/chat/private")
    public void sendPrivateMessage(@Payload PrivateMessageDto message, Principal principal) {
        String senderId = principal.getName();

        //Process message via service
        Message savedMessage = chatService.sendPrivateMessage(senderId, message.getRecipientId(), message.getContent());

        // Send the full, saved message object to the recipient
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/private",
                savedMessage
        );

        // Send a copy back to the sender
        messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/private",
                savedMessage
        );
    }

    /**
     * UPDATED: Now uses the 'typing' field.
     */
    @MessageMapping("/chat/typing")
    public void handleTypingStatus(@Payload TypingStatusDto status, Principal principal) {
        String senderId = principal.getName();
        TypingStatusDto outgoingStatus = new TypingStatusDto(senderId, status.isTyping());
        messagingTemplate.convertAndSendToUser(
                status.getRecipientId(),
                "/queue/typing",
                outgoingStatus
        );
    }

    /**
     * NEW: Receives an event from the client indicating which chat thread is active.
     * This information is stored in the PresenceService.
     */
    @MessageMapping("/chat/active")
    public void setActiveChat(@Payload ActiveChatDto activeChat, Principal principal) {
        presenceService.setActiveChat(principal.getName(), activeChat.getThreadId());
    }

}
