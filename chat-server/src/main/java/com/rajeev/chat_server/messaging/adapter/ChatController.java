package com.rajeev.chat_server.messaging.adapter;

import com.rajeev.chat_server.messaging.model.Message;
import com.rajeev.chat_server.messaging.model.dto.PrivateMessageDto;
import com.rajeev.chat_server.messaging.service.ChatService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@SecurityRequirement(name = "Keycloak")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @PostMapping("/test/method")
    public ResponseEntity<?> sendMessage(@RequestBody PrivateMessageDto request) {
        // Logic to send a message
        return ResponseEntity.ok("Success");
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

}
